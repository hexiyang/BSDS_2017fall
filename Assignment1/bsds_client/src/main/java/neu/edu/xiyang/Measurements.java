package neu.edu.xiyang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Measurements implements Runnable{
    List<sendRequestThread> tasks;
    long startTime;

    public void setThreads (List<sendRequestThread> ts) {
        tasks = ts;
    }
    public void setStartTime (long start) {
        startTime = start;
    }
    public void run() {
        long endTime = System.currentTimeMillis();
        System.out.println ("Now switching to " + Thread.currentThread());
        System.out.println("The runtime for all threads to complete is: " + (endTime - startTime) + " ms");
        // Start calculating statistics
        int requestNumSum = 0;
        int successRequestNumSum = 0;
        List<Long> requestLatency = new ArrayList<Long>();
        List<Long> successRequestLatency = new ArrayList<Long>();
        // Total Number of requests sent
        for (sendRequestThread task: tasks) {
            requestNumSum += task.requestNum;
            successRequestNumSum += task.successNum;
            requestLatency.addAll(task.requestLatency);
            successRequestLatency.addAll(task.successLatency);
        }
        System.out.println("Total number of requests sent is: " + requestNumSum);
        System.out.println("Total number of successful requests is " + successRequestNumSum);
        System.out.println("Mean latencies for all requests is " + meanLatency(requestLatency) + " ms");
        System.out.println("Mean latencies for all successful requests is " + meanLatency(successRequestLatency) + " ms");
        System.out.println("Median latencies for all requests is "
                + percentileLatency(requestLatency, 50) + " ms");
        System.out.println("Median latencies for all successful requests is "
                + percentileLatency(successRequestLatency, 50) + " ms");
        System.out.println("99th percentile latencies for all requests is "
                + percentileLatency(requestLatency, 99) + " ms");
        System.out.println("99th percentile latencies for all successful requests is "
                + percentileLatency(successRequestLatency, 99) + " ms");
        System.out.println("95th percentile latencies for all requests is "
                + percentileLatency(requestLatency, 95) + " ms");
        System.out.println("95th percentile latencies for all successful requests is "
                + percentileLatency(successRequestLatency, 95) + " ms");
    }

    private long meanLatency (List<Long> list) {
        long sum = 0;
        int count = 0;
        for (Long latency: list) {
            sum += latency;
            count++;
        }
        return sum/count;
    }

    private long percentileLatency (List<Long> list, int percentile) {
        Collections.sort(list);
        int index = Math.round((percentile/100) * list.size());
        return list.get(Math.max(index-1, 0));
    }
}
