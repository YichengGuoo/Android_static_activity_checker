package capreolus;

import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SmaliHttpFunctionExtractor {
    public static void main(String[] args) throws IOException {
        String rootDirectory = "G:\\test1temp\\test\\ali";
        List<String> functionList = new ArrayList<String>() {{
            add("Ljava/net/URLConnection;->setRequestMethod(");
            add("Ljava/net/URLConnection;->getInputStream(");
            add("Ljava/net/HttpURLConnection;->getResponseCode(");
        }};
        List<File> smaliFileList = new ArrayList<>();
        Files.walk(Paths.get(rootDirectory))
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".smali"))
                .forEach(path -> smaliFileList.add(path.toFile()));
        for (File file : smaliFileList) {
            List<String> functionMatches = new ArrayList<>();
            String fileName = file.getName();
            String filePath = file.getAbsolutePath();
            List<String> lines = Files.readAllLines(Path.of(filePath));
            String linetemp = null;
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                for (String function : functionList) {
                    if (line.contains(function)) {
                        linetemp= line;
                        String functionName = lines.get(i - 1).split(":")[0];
                        functionMatches.add(functionName);
                    }
                }
            }
            if (!functionMatches.isEmpty()) {
                System.out.println(linetemp);
                System.out.println("Functions in " + fileName + ":\n" + String.join("\n", functionMatches));
                System.out.println("Location: " + filePath);
                System.out.println("-----------------------");
            }
        }
    }
}



