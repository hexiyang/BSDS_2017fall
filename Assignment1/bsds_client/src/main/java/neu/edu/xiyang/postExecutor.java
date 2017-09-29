package neu.edu.xiyang;

import javax.ws.rs.client.WebTarget;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class postExecutor {
    public static void main(String[] args) {
        configInfo config = new configInfo();
        int threadNum = config.threadNum;
        int iterationNum = config.iterationNum;
        WebTarget target1 = config.getWebTarget("target1");
        WebTarget target2 = config.getWebTarget("target2");
        // Create Tasks
        List<postRequestThread> tasks = new ArrayList<postRequestThread>();
        for (int i = 0; i < threadNum; i++) {
            // Submit runnable task
            postRequestThread task = new postRequestThread(target1, target2, iterationNum);
            tasks.add(task);
        }
        resultMeasurement measurements = new resultMeasurement();
        measurements.setPostTasks(tasks);
        // Start Threads
        long startTime = System.currentTimeMillis();
        measurements.setStartTime(startTime);
        System.out.println("Ready to roll.....");
        // Initiate the Thread Pool
        ExecutorService executor = Executors.newFixedThreadPool(threadNum);
        for (postRequestThread task : tasks) {
            executor.submit(task);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Get all the statistics
        if (executor.isShutdown()) {
            measurements.measurePOST();
        } else {
            System.out.println("The executor has not shutdown!");
        }
    }
}
