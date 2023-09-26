package capreolus;

import java.io.*;
import java.util.*;


/**
 * finished 但不一定有用
 */
public class SmaliInterfaceExtractor {
    public static void main(String[] args) throws Exception {
        File newfile = new File("E:\\javafiles\\capreolus\\src\\capreolus\\fileinterface.txt");
        PrintStream ps = new PrintStream(new FileOutputStream(newfile));
        String folderPath = "G:\\test1temp\\test\\baidu"; // 文件夹路径
        File folder = new File(folderPath);
        Map<String, List<String>> interfaceMap = extractInterfaceHierarchy(folder);
        StringBuilder sb2 = new StringBuilder();
        for (String parent : interfaceMap.keySet()) {
            sb2.append(parent + " is implemented by:"+"\n");
            for (String child : interfaceMap.get(parent)) {
                sb2.append("\t" + child+"\n");
            }
        }
        ps.println(sb2);// 往.txt文件里写入字符串

    }

    private static Map<String, List<String>> extractInterfaceHierarchy(File folder) throws Exception {
        Map<String, List<String>> interfaceMap = new HashMap<>();
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                interfaceMap.putAll(extractInterfaceHierarchy(file));
            } else if (file.getName().endsWith(".smali")) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                String currentClass = null;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith(".class")) {
                        currentClass = line.substring(line.lastIndexOf(" ") + 1);
                    } else if (line.startsWith(".super")) {
                        String parentClass = line.substring(line.lastIndexOf(" ") + 1);
                        addParentChildRelationship(parentClass, currentClass, interfaceMap);
                    } else if (line.startsWith(".implements")) {
                        String parentInterface = line.substring(line.lastIndexOf(" ") + 1);
                        addParentChildRelationship(parentInterface, currentClass, interfaceMap);
                    } else if (line.contains("invoke-interface")) {
                        String invokedInterface = line.substring(line.indexOf("L") + 1, line.indexOf(";"));
                        addParentChildRelationship(invokedInterface, currentClass, interfaceMap);
                    }
                }
                reader.close();
            }
        }
        return interfaceMap;
    }

    private static void addParentChildRelationship(String parent, String child, Map<String, List<String>> interfaceMap) {
        List<String> childClasses = interfaceMap.getOrDefault(parent, new ArrayList<>());
        childClasses.add(child);
        interfaceMap.put(parent, childClasses);
    }
}