package org.example;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.SimpleFormatter;

public class DateFormatting {
    static final int MAX_THREAD = 3;
    public static void main(String[] args) {
        Runnable r1 = new DateTask("Task 1");
        Runnable r2 = new DateTask("Task 2");
        Runnable r3 = new DateTask("Task 3");
        Runnable r4 = new DateTask("Task 4");
        Runnable r5 = new DateTask("Task 5");

        ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREAD);

        executorService.execute(r1);
        executorService.execute(r2);
        executorService.execute(r3);
        executorService.execute(r4);
        executorService.execute(r5);

        executorService.shutdown();

    }
}
class DateTask implements Runnable{
    private String name;
    public DateTask(String name){
        this.name = name;
    }
    @Override
    public void run() {
        for (int i = 0; i <= 5; i++){
            Date d = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
            if(i == 0) {
                System.out.println("Initialising time for task name: " + name + " is " + dateFormat.format(d));
            }else {
                System.out.println("Executing Time for task name: "+ name + " is " + dateFormat.format(d));
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println(name + " thread is completed");
    }
}
