package capreolus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 查找同名变量的调用，也就是当前变量还在那些地方被用到
 */
public class samenameuse {

    public static void main(String[] args) throws IOException {
        String folderPath = args[0];
        String variableName = args[1];
        String variableFilePath = args[2];

        List<File> smaliFiles = new ArrayList<>();
        getSmaliFiles(new File(folderPath), smaliFiles);

        List<File> filesUsingVariable = new ArrayList<>();

        Pattern pattern = Pattern.compile(".*\\b" + variableName + "\\b.*");
        String variableFileContent = Files.readString(new File(variableFilePath).toPath());

        for (File smaliFile : smaliFiles) {
            String fileContent = Files.readString(smaliFile.toPath());
            Matcher matcher = pattern.matcher(fileContent);
            if (matcher.find() && !smaliFile.getAbsolutePath().equals(variableFilePath)) {
                // Check if the variable is used in a valid context
                String[] lines = fileContent.split("\\r?\\n");
                for (int i = 0; i < lines.length; i++) {
                    String line = lines[i];
                    if (line.contains(variableName)) {
                        if (line.startsWith("    const")) {
                            if (line.contains(" " + variableName)) {
                                filesUsingVariable.add(smaliFile);
                                break;
                            }
                        } else if (line.startsWith("    sget") || line.startsWith("    iget")) {
                            if (line.contains(variableName)) {
                                filesUsingVariable.add(smaliFile);
                                break;
                            }
                        } else if (line.startsWith("    invoke")) {
                            // Check if the method signature contains the variable name
                            int invokeEndIndex = fileContent.indexOf("\n", fileContent.indexOf(line) + line.length()) + 1;
                            String invokeContent = fileContent.substring(fileContent.indexOf(line), invokeEndIndex);
                            if (invokeContent.contains(variableName)) {
                                filesUsingVariable.add(smaliFile);
                                break;
                            }
                        }
                    }
                }
            }
        }

        System.out.println("Files using variable " + variableName + ":");
        for (File file : filesUsingVariable) {
            System.out.println(file.getAbsolutePath());
        }
    }

    private static void getSmaliFiles(File file, List<File> smaliFiles) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    getSmaliFiles(f, smaliFiles);
                }
            }
        } else if (file.getName().endsWith(".smali")) {
            smaliFiles.add(file);
        }
    }
}