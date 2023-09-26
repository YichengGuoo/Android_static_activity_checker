package capreolus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AdbShellTest2 {

    public static void main(String[] args) {
        try {
            String packageName = "com.example.app"; // 替换为要测试的应用程序包名
            String activityName = "MainActivity"; // 替换为要测试的应用程序 Activity 名称
            String command1 = String.format("adb shell uiautomator dump /dev/tty; cat /sdcard/window_dump.xml | grep -E 'resource-id=\"%s\"|class=\"%s\"|text=\".*\"'", packageName + ":id/button1", "android.widget.Button");
            String command2 = String.format("adb shell uiautomator dump /dev/tty; cat /sdcard/window_dump.xml | grep -E 'resource-id=\"%s\"|class=\"%s\"|text=\".*\"'", packageName + ":id/button1", "android.widget.Button");

            String result1 = executeAdbShellCommand(command1);
            System.out.println("执行前页面信息：" + result1);



            String result2 = executeAdbShellCommand(command2);
            System.out.println("执行后页面信息：" + result2);

            if (result1.equals(result2)) {
                System.out.println("执行前后页面相同，返回:false");
//                return false;
            }

            String command3 = String.format("adb shell input swipe 500 1500 500 100"); // 模拟从屏幕中央向上滑动
            String result3 = executeAdbShellCommand(command3);
            System.out.println("滑动后页面信息：" + result3);

            if (!result3.contains(packageName + ":id/button1")) {
                System.out.println("页面中不存在按钮，返回:false");
//                return false;
            }

            System.out.println("测试通过，返回 true");
//            return true;
        } catch (Exception e) {
            e.printStackTrace();
//            return false;
        }
    }

    private static String executeAdbShellCommand(String command) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(command);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line + "\n");
        }
        reader.close();
        process.waitFor();
        return output.toString();
    }
}