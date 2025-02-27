package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.*;

/**
 * Using ProcessBuilder and either the ProcessBuilder constructor or its command() method,
 * write a Java program that run a command (in Windows) or a shell script (Mac, Linux) and
 * ping a website of choice 10 times. Using BufferedReader, write the output (ping response) to
 * the console.
 * For windows
 * processBuilder.command("cmd.exe", "/c", "<ping command> -n <number of times to ping>
 * <website>");
 */
/* ============================================References===============================================================
========================================================================================================================
 * // https://www.baeldung.com/java-executor-service-tutorial
 * // https://stackabuse.com/convert-inputstream-into-a-string-in-java/
 * // https://stackoverflow.com/questions/1674122/how-to-write-into-input-stream-in-java
 * // https://stackoverflow.com/questions/28103040/how-to-capture-the-inputstream-while-executing-cmd-through-java
 * // https://stackoverflow.com/questions/7637290/get-command-prompt-output-to-string-in-java
 * // https://stackoverflow.com/questions/46964352/write-to-and-read-from-the-windows-command-prompt-by-java ---- This Worked!
 */
public class Main {
    String website = "facebook.com";
    Integer numberTime = 10;
    String cmd = "cmd.exe";
    String fileDir = "/C";

    /* ======================================================================
     * This will work on the command prompt and will display the ping result
     * but it will not display on the IDE output.
     * ======================================================================*/
//    String[] commandList = {cmd, fileDir, "start", "ping", "-n", numberTime.toString(), website};
    String[] commandList = {cmd, fileDir, "ping", "-n", numberTime.toString(), website};

    public Main() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(commandList);
        Process process = processBuilder.start();
        ExecutorService servicePool = Executors.newSingleThreadExecutor();

        this.serviceCommand(process, servicePool);
    }

    public void serviceCommand(Process process, ExecutorService service) throws IOException {
        String result; // -- This will hold the whole result of the output of the command prompt
        Callable<String> callableTask = () -> {
            StringBuilder output = new StringBuilder();
            try {
//                process.waitFor();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return output.toString();
        };

        Future<String> future = service.submit(callableTask);

        try {
            result = future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(result);
        process.destroy();
    }

    public static void main(String[] args) throws IOException {
        new Main();
    }
}