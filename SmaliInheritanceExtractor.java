package capreolus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * finished
 */
public class SmaliInheritanceExtractor {
    public static void main(String[] args) throws FileNotFoundException {
        String folderPath = "G:\\test1temp\\test\\baidu"; // 文件夹路径
        Map<String, ArrayList<String>> inheritanceMap = new HashMap<>(); // 继承关系映射表
        File newfile = new File("E:\\javafiles\\capreolus\\src\\capreolus\\file.txt");

        PrintStream ps = new PrintStream(new FileOutputStream(newfile));

        // 遍历文件夹，提取继承关系
        File folder = new File(folderPath);
        for (File file : folder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".smali")) {
                String className = getClassName(file);
                String parentClassName = getParentClassName(file);

                // 将子类添加到父类的列表中
                if (!parentClassName.isEmpty()) {
                    if (inheritanceMap.containsKey(parentClassName)) {
                        inheritanceMap.get(parentClassName).add(className);
                    } else {
                        ArrayList<String> childClasses = new ArrayList<>();
                        childClasses.add(className);
                        inheritanceMap.put(parentClassName, childClasses);
                    }
                }
            } else if (file.isDirectory()) {
                // 递归处理子文件夹
                String subFolderPath = file.getPath();
                Map<String, ArrayList<String>> subInheritanceMap = extractInheritance(subFolderPath);
                mergeInheritanceMaps(inheritanceMap, subInheritanceMap);
            }
        }
        StringBuilder sb2 = new StringBuilder();

        // 输出继承关系
        for (String parentClassName : inheritanceMap.keySet()) {
            sb2.append("Parent class: " + parentClassName+"\n");
            ArrayList<String> childClasses = inheritanceMap.get(parentClassName);
            for (String childClassName : childClasses) {
                sb2.append(" - Child class: " + childClassName+"\n");
            }
        }
        ps.println(sb2);// 往.txt文件里写入字符串
    }

    /**
     * 提取一个.smali文件的类名（即.class后面的内容）
     */
    private static String getClassName(File file) {
        String className = "";
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.startsWith(".class ")) {
                    className = line.substring(line.lastIndexOf(" ") + 1);
                    break;
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return className;
    }

    /**
     * 提取一个.smali文件的父类名（即.super后面的内容）
     */
    private static String getParentClassName(File file) {
        String parentClassName = "";
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.startsWith(".super ")) {
                    parentClassName = line.substring(line.lastIndexOf(" ") + 1);
                    break;
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return parentClassName;
    }

    /**
     * 递归提取一个文件夹中所有.smali文件的继承关系
     */
    private static Map<String, ArrayList<String>> extractInheritance(String folderPath) {
        Map<String, ArrayList<String>> inheritanceMap = new HashMap<>();

        File folder = new File(folderPath);
        for (File file : folder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".smali")) {
                String className = getClassName(file);
                String parentClassName = getParentClassName(file);

                // 将子类添加到父类的列表中
                if (!parentClassName.isEmpty()) {
                    if (inheritanceMap.containsKey(parentClassName)) {
                        inheritanceMap.get(parentClassName).add(className);
                    } else {
                        ArrayList<String> childClasses = new ArrayList<>();
                        childClasses.add(className);
                        inheritanceMap.put(parentClassName, childClasses);
                    }
                }
            } else if (file.isDirectory()) {
                // 递归处理子文件夹
                String subFolderPath = file.getPath();
                Map<String, ArrayList<String>> subInheritanceMap = extractInheritance(subFolderPath);
                mergeInheritanceMaps(inheritanceMap, subInheritanceMap);
            }
        }

        return inheritanceMap;
    }

    /**
     * 合并两个继承关系映射表
     */
    private static void mergeInheritanceMaps(Map<String, ArrayList<String>> map1, Map<String, ArrayList<String>> map2) {
        for (String key : map2.keySet()) {
            if (map1.containsKey(key)) {
                map1.get(key).addAll(map2.get(key));
            } else {
                map1.put(key, map2.get(key));
            }
        }
    }
}

