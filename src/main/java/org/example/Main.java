package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * @Question Using ProcessBuilder and either the ProcessBuilder constructor or its command() method,
 * write a Java program that run a command (in Windows) or a shell script (Mac, Linux) and
 * ping a website of choice 10 times. Using BufferedReader, write the output (ping response) to
 * the console.
 * For windows
 * processBuilder.command("cmd.exe", "/c", "<ping command> -n <number of times to ping>
 * <website>");
 */
/* ============================================References===============================================================
 * =====================================================================================================================
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
    String[] arrayWebsites = {"facebook.com", "google.com", "rte.ie", "youtube.com", "tiktok.com", "instagram.com", "github.com", "tudublin.ie", "fiverr.com", "twitch.tv"};


    public Main() throws Exception {
        tenPing("facebook.com");

        tenWebsites(); // Test if I can ping 10 different sites
    }

    /** @Method tenPing
     *  @Description: This ping 10 times on 1 website
     * @param byVal_website Enter any website that you wanted
     * @throws IOException
     */
    public void tenPing(String byVal_website) throws Exception {
        String[] commandList = {cmd, fileDir, "ping", "-n", numberTime.toString(), byVal_website};

        System.out.println("Start 10 Pings on 1 Website");
        ProcessBuilder processBuilder = new ProcessBuilder(commandList);
        Process process = processBuilder.start();
        ExecutorService servicePool = Executors.newSingleThreadExecutor();

        Callables callables = new Callables(process, processBuilder, servicePool);

        System.out.println(callables.call());

        System.out.println("End 10 Ping Command on 1 websites");
    }

    /** @Method: tenWebsite
     *  @Description: This will ping 10 different websites that are in the global array that was declared above
     *  @throws IOException
     */
    public void tenWebsites() throws Exception {
        System.out.println("Start 1 ping to 10 different websites");
        for (int i =0; i < 10; i++){
            ProcessBuilder pb = new ProcessBuilder(cmdListWeb(i));
            Process process = pb.start();
            ExecutorService servicePool = Executors.newFixedThreadPool(10);
            Callables callables = new Callables(process, pb, servicePool);

            System.out.println("\t\t\t\tWebsite " + arrayWebsites[i] + "\n========================================================");
            System.out.println(callables.call());
        }
        System.out.println("End Ping Command on 10 different websites\n========================================================");
    }

    public ArrayList<String> cmdListWeb(int index){
        String[] cl = {cmd, fileDir, "ping", "-n", "1"};
        ArrayList<String> temp2 = new ArrayList<>(List.of(cl));
        temp2.add(arrayWebsites[index]);
        return temp2;
    }

    public static void main(String[] args) throws Exception {
        new Main();
    }
}
class Callables implements Callable<List<String>>{
    Process process;
    ProcessBuilder processBuilder;
    ExecutorService service;
    public Callables(Process p, ProcessBuilder pb, ExecutorService s){
        this.process = p;
        this.processBuilder = pb;
        this.service = s;
    }

    public String task(Process process){
        StringBuilder output = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    @Override
    public List<String> call() throws Exception {
        String result; // -- This will hold the whole result of the output of the command prompt
        Callable<String> callableTask = () -> task(process); // arrow or -> is a simplification of { return object };
        // or a lambda expression
        Future<String> future = service.submit(callableTask);
        try {
            result = future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        finally {
//            process.destroy();
            service.shutdown();
            // -- shutdown() is the proper to shut the thread down instead of destroy.
            // learned in class check Executor Class Documents
            // made the code much cleaner and readable by making methods -- 03/03/2025
        }
        return Collections.singletonList(result);
    }
}