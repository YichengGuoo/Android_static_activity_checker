package capreolus.staticanalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

import java.io.IOException;
import java.util.List;

/**
 * find activity到fragment的跳转
 */
public class activity2fragment {

    private static final String ACTIVITY_SUFFIX = "$Activity";
    private static final String FRAGMENT_SUFFIX = "$Fragment";

    private List<String> activityList;
    private List<String> fragmentList;
    private List<String> jumpList;

    public activity2fragment(List<String> activityList, List<String> fragmentList) {
        this.activityList = activityList;
        this.fragmentList = fragmentList;
        this.jumpList = new ArrayList<>();
    }

    public void analyze(File folder) throws IOException {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                analyze(file);
            } else if (file.getName().endsWith(".smali")) {
                analyzeFile(file);
            }
        }
    }

    private void analyzeFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        String currentActivity = null;
        while ((line = reader.readLine()) != null) {
            if (line.contains(".super L")) {
                String className = extractClassName(line);
                if (className.endsWith(ACTIVITY_SUFFIX) && activityList.contains(className)) {
                    currentActivity = className;
                }
            } else if (line.contains(".method public")) {
                String methodName = extractMethodName(line);
                if (methodName.equals("onCreate") && currentActivity != null) {
                    analyzeOnCreate(reader, currentActivity);
                    currentActivity = null;
                }
            }
        }
        reader.close();
    }

    private void analyzeOnCreate(BufferedReader reader, String currentActivity) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("Landroid/app/FragmentTransaction;->add(")) {
                String fragmentClassName = extractClassName(line, "(L", ";");
                if (fragmentClassName.endsWith(FRAGMENT_SUFFIX) && fragmentList.contains(fragmentClassName)) {
                    jumpList.add(currentActivity + "->" + fragmentClassName);
                }
            } else if (line.contains("Landroid/app/FragmentTransaction;->replace(") || line.contains("Landroid/app/FragmentTransaction;->replaceWithBackStack(")) {
                String fragmentClassName = extractClassName(line, "(L", ";");
                if (fragmentClassName.endsWith(FRAGMENT_SUFFIX) && fragmentList.contains(fragmentClassName)) {
                    jumpList.add(currentActivity + "->" + fragmentClassName);
                }
            } else if (line.contains("Landroid/os/Bundle;->putSerializable(")) {
                String argumentClassName = extractClassName(line, "(L", ";");
                if (argumentClassName != null && argumentClassName.startsWith("java/lang/")) {
                    // This is a Serializable argument
                    jumpList.add(fragmentClassName(currentActivity) + "->" + fragmentClassName(reader));
                }
            }
        }
    }

    private String extractClassName(String line) {
        return line.substring(line.indexOf("L") + 1, line.indexOf(";"));
    }

    private String extractMethodName(String line) {
        return line.substring(line.indexOf(" ") + 1, line.indexOf("("));
    }

    private String extractClassName(String line, String prefix, String suffix) {
        int start = line.indexOf(prefix) + prefix.length();
        int end = line.indexOf(suffix, start);
        return line.substring(start, end);
    }

    private String fragmentClassName(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains(".super Landroid/app/Fragment;")) {
                return extractClassName(line);
            }
        }
        return null;
    }

        private String fragmentClassName(String activityClassName) {
            return activityClassName.replace(ACTIVITY_SUFFIX, FRAGMENT_SUFFIX);
        }

        public List<String> getJumpList() {
            return jumpList;
        }

// Usage:

    public static void main(String[] args) throws IOException {
        List<String> activityList = Arrays.asList("com.example.MainActivity", "com.example.SecondActivity");
        List<String> fragmentList = Arrays.asList("com.example.MyFragment", "com.example.AnotherFragment");
        activity2fragment analyzer = new activity2fragment(activityList, fragmentList);
        analyzer.analyze(new File("G:\\test1temp\\test\\baidu"));
        List<String> jumpList = analyzer.getJumpList();
        for (String jump : jumpList) {
            System.out.println(jump);
        }

    }


}
