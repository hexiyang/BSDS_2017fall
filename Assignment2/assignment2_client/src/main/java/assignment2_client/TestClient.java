package assignment2_client;

import bsdsass2testdata.RFIDLiftData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class TestClient {

    public static void main(String[] args) {
        postData("/Users/Xiyang/Documents/Google Drive/Courses/" +
                "Distributed System/Assignments/Assignment3/BSDSAssignment2Day999.csv");
    }

    private static void postData(String filePath) {
        Scheduler scheduler = new Scheduler(filePath, new HTTPRequests());
        Measurement postMeasurement = scheduler.multiThreadPost();
        postMeasurement.printStatistics();
    }
    private static void getData(int DayNum) {

    }
    private static void postAndGet() {
        String filePath = "/Users/Xiyang/Documents/Google Drive/Courses/" +
                "Distributed System/Assignments/Assignment2/BSDSAssignment2Day2.ser";

        Callable<Measurement> postCallable = () -> {
            HTTPRequests postHTTPRequests = new HTTPRequests();
            Scheduler scheduler = new Scheduler(filePath, postHTTPRequests);
            Measurement postMeasurement = scheduler.multiThreadPost();
            return postMeasurement;
        };
        Callable<Measurement> getCallable = () -> {
            HTTPRequests getHTTPRequests = new HTTPRequests();
            Scheduler scheduler = new Scheduler(filePath, getHTTPRequests);
            Measurement getMeasurement = scheduler.multiThreadGet();
            return getMeasurement;
        };

        List<Callable<Measurement>> tasks = new ArrayList<>();
        tasks.add(postCallable);
        tasks.add(getCallable);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        List<Future<Measurement>> futures = new ArrayList<>();
        System.out.println("Ready to roll...");
        long startTime = System.currentTimeMillis();
        try {
            futures = executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
        try {
            if (executor.awaitTermination(30, TimeUnit.MINUTES)) {
            } else {
                executor.shutdownNow();
                System.out.println("System time out, exceed 30 mins");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            executor.shutdownNow();
        }
        long wallTime = System.currentTimeMillis() - startTime;
        System.out.println("The  total wall time is: " + TimeUnit.MILLISECONDS.toMinutes(wallTime) + "min");
        for (Future<Measurement> future : futures) {
            try {
                Measurement measurement = future.get();
                measurement.printStatistics();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
