package capreolus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class PageStructureComparison {

    public static void main(String[] args) {
        String pageStructure1 = getPageStructure(1);
        System.out.println("等待5秒钟...");
        sleep(3000); // 等待5秒钟
        String pageStructure2 = getPageStructure(2);
        assert pageStructure1 != null;
        assert pageStructure2 != null;
        if (issame()) {
            System.out.println("两个页面相同");
        } else {
            System.out.println("两个页面不同");
        }
    }

    public static boolean issame() {
        String file1 = "/sdcard/Pictures/compare1.xml";
        String file2 = "/sdcard/Pictures/compare2.xml";

        try {
            ProcessBuilder pb = new ProcessBuilder("adb", "shell", "diff", file1, file2);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("same:1");
                return true;
            } else {
                String tempfile1 = "/sdcard/Pictures/compare1_temp.xml";
                String tempfile2 = "/sdcard/Pictures/compare2_temp.xml";
                execAdbCmd("adb shell \"head -c 3000 /sdcard/Pictures/compare1.xml > /sdcard/Pictures/compare1_temp.xml\"");
                execAdbCmd("adb shell \"head -c 3000 /sdcard/Pictures/compare2.xml > /sdcard/Pictures/compare2_temp.xml\"");

                ProcessBuilder pb1 = new ProcessBuilder("adb", "shell", "diff", tempfile1, tempfile2);
                Process process1 = pb1.start();

                BufferedReader reader1 = new BufferedReader(new InputStreamReader(process1.getInputStream()));

                String line1;
                StringBuilder output1 = new StringBuilder();
                while ((line1 = reader1.readLine()) != null) {
                    output1.append(line1);
                }

                int exitCode1 = process1.waitFor();
                if (exitCode1 == 0) {
                    System.out.println("same:2");
                    return true;
                } else {
                    return false;
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    private static String getPageStructure(int time) {
        String filepath = null;
        if (time == 1) {
            filepath = "Pictures/compare1.xml";
        } else {
            filepath = "Pictures/compare2.xml";
        }
        try {
            Process process = Runtime.getRuntime().exec("adb shell uiautomator dump /sdcard/" + filepath + "\n");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static String execAdbCmd(String cmd) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(cmd);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append('\n');
        }
        Thread.sleep(500);

        return output.toString().trim();

    }
}