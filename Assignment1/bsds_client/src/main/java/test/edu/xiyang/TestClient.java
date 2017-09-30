package test.edu.xiyang;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class TestClient {
    public static void main(String[] args) {
        ConfigInfo config = new ConfigInfo();
        int threadNum = config.threadNum;
        // Create Tasks
        List<SendRequestCallable> tasks = new ArrayList<SendRequestCallable>();
        for (int i = 0; i < threadNum; i++) {
            // Submit runnable task
            SendRequestCallable task = new SendRequestCallable(config);
            tasks.add(task);
        }
        // Start Threads
        System.out.println("Ready to roll.....");
        // Initiate the Thread Pool
        ExecutorService executor = Executors.newFixedThreadPool(threadNum);
        long startTime = System.currentTimeMillis();
        List<Future<Result>> futures = new ArrayList<Future<Result>>();
        try {
            futures = executor.invokeAll(tasks, 10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
        if (executor.isShutdown()) {
            long wallTime = System.currentTimeMillis() - startTime;
            System.out.println("The runtime for all threads to complete is: " + wallTime + " ms");
            ResultsMeasurement measurement = new ResultsMeasurement(futures);
            measurement.generateStatistics(wallTime);
        } else {
            System.out.println("The executor has not shutdown!");
        }

    }
}
