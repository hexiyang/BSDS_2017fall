package assignment2_client;

import bsdsass2testdata.RFIDLiftData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class TestClient {

    public static void main(String[] args) {
        //String filePath = "/Users/Xiyang/Documents/Google Drive/Courses/Distributed System/Assignments/Assignment3/BSDSAssignment2Day999.csv";
        //postData(filePath);

        //int dayNum = 999;
        //getData(dayNum);

        HTTPRequests requests = new HTTPRequests();
        printResponseStatistics(requests);

        /*
        Scheduler scheduler = new Scheduler(requests);
        Measurement postDay3 = scheduler.multiThreadPost("/Users/Xiyang/Documents/Google Drive/Courses/" +
                "Distributed System/Assignments/Assignment3/BSDSAssignment2Day3.csv");
        Measurement getDay3 = scheduler.multiThreadGet(3);
        Measurement postDay4 = scheduler.multiThreadPost("/Users/Xiyang/Documents/Google Drive/Courses/" +
                "Distributed System/Assignments/Assignment3/BSDSAssignment2Day4.csv");
        Measurement getDay4 = scheduler.multiThreadGet(4);
        Measurement postDay5 = scheduler.multiThreadPost("/Users/Xiyang/Documents/Google Drive/Courses/" +
                "Distributed System/Assignments/Assignment3/BSDSAssignment2Day5.csv");
        Measurement getDay5 = scheduler.multiThreadGet(5);

        System.out.println("\n----------------->>Print postDay3 stats<<-----------------");
        postDay3.printStatistics();
        System.out.println("----------------->>Print getDay3 stats<<-----------------");
        getDay3.printStatistics();
        System.out.println("----------------->>Print postDay4 stats<<-----------------");
        postDay4.printStatistics();
        System.out.println("----------------->>Print getDay4 stats<<-----------------");
        getDay4.printStatistics();
        System.out.println("----------------->>Print postDay5 stats<<-----------------");
        postDay5.printStatistics();
        System.out.println("----------------->>Print getDay5 stats<<-----------------");
        getDay5.printStatistics();
        */




    }

    private static void postData(String filePath) {
        Scheduler scheduler = new Scheduler(new HTTPRequests());
        Measurement postMeasurement = scheduler.multiThreadPost(filePath);
        postMeasurement.printStatistics();
    }
    private static void getData(int dayNum) {
        Scheduler scheduler = new Scheduler(new HTTPRequests());
        Measurement getMeasurement = scheduler.multiThreadGet(dayNum);
        getMeasurement.printStatistics();
    }

    /*
Functions for statistics about response time
 */
    private static void printResponseStatistics(HTTPRequests requests) {
        String stats = requests.getResponseTimeStats();
        String[] results = stats.split("::");
        System.out.println("\n" +
                "===================================================\n" +
                "*             ResponseTime Statistics             *\n" +
                "===================================================");
        // Start to print statistics
        System.out.println("[ Mean   ResponseTime]: " + results[0] + " ns ");
        System.out.println("[ Median ResponseTime]: " + results[1] + " ns ");
        System.out.println("[ 95th   ResponseTime]: " + results[2] + " ns ");
        System.out.println("[ 99th   ResponseTime]: " + results[3] + " ns ");
    }



    private static void postAndGet() {
        String filePath = "/Users/Xiyang/Documents/Google Drive/Courses/" +
                "Distributed System/Assignments/Assignment2/BSDSAssignment2Day2.ser";
        int dayNum = 1;

        Callable<Measurement> postCallable = () -> {
            HTTPRequests postHTTPRequests = new HTTPRequests();
            Scheduler scheduler = new Scheduler(postHTTPRequests);
            Measurement postMeasurement = scheduler.multiThreadPost(filePath);
            return postMeasurement;
        };
        Callable<Measurement> getCallable = () -> {
            HTTPRequests getHTTPRequests = new HTTPRequests();
            Scheduler scheduler = new Scheduler(getHTTPRequests);
            Measurement getMeasurement = scheduler.multiThreadGet(dayNum);
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
