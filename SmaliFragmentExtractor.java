package capreolus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmaliFragmentExtractor {

    public static void main(String[] args) {
        String folderPath = "path";
        List<String> fragmentList = extractFragmentsFromFolder(folderPath);
        for (String fragment : fragmentList) {
            System.out.println(fragment);
        }
    }

    private static List<String> extractFragmentsFromFolder(String folderPath) {
        List<String> fragmentList = new ArrayList<>();
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    fragmentList.addAll(extractFragmentsFromFolder(file.getAbsolutePath()));
                } else {
                    if (file.getName().endsWith(".smali")) {
                        fragmentList.addAll(extractFragmentsFromFile(file));
                    }
                }
            }
        }
        return fragmentList;
    }

    private static List<String> extractFragmentsFromFile(File file) {
        List<String> fragmentList = new ArrayList<>();
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            String smaliCode = new String(data, "UTF-8");
            Pattern pattern = Pattern.compile("L[a-zA-Z0-9_/$]+;-><init>\\(.*Landroid/app/Fragment;.*\\)V");
            Matcher matcher = pattern.matcher(smaliCode);
            while (matcher.find()) {
                String matchedText = matcher.group();
                String fragmentName = matchedText.substring(matchedText.indexOf("L"), matchedText.indexOf(";"));
                fragmentList.add(fragmentName.replace("/", "."));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fragmentList;
    }
}