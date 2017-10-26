package assignment2_client;

import bsdsass2testdata.DataReader;
import bsdsass2testdata.RFIDLiftData;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
public class TestClient {

    static final String filePath = "/Users/Xiyang/Documents/Google Drive/Courses/" +
            "Distributed System/Assignments/Assignment2/BSDSAssignment2Day1.ser";

    public static void main(String[] args) {
        Measurement postMeasurement = multiThreadPost();
        Measurement getMeasurement = multiThreadGet();
        System.out.println("Now print out the post statistics:");
        postMeasurement.printStatistics();
        System.out.println("Now print out the get statistics:");
        getMeasurement.printStatistics();
    }

    private static Measurement multiThreadGet() {
        int threadNum = 100;
        int skierNum = 40000;
        int chunkSize = 40000/threadNum;
        List<GetRequestCallable> tasks = new ArrayList<>();
        SendRequest sendRequest = new SendRequest();
        int i = 0;
        while (i < chunkSize) {
            GetRequestCallable task = new GetRequestCallable(sendRequest, i, i + chunkSize, 1);
            tasks.add(task);
        }
        ExecutorService executor = Executors.newFixedThreadPool(threadNum);
        List<Future<ResultData>> futures = new ArrayList<>();
        System.out.println("ready to GET...");
        long startTime = System.currentTimeMillis();
        try {
            futures = executor.invokeAll(tasks, 15, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
        long wallTime = System.currentTimeMillis() - startTime;
        System.out.println("The wall time is: " + TimeUnit.MILLISECONDS.toMinutes(wallTime) + "min");
        return new Measurement(futures, wallTime);
    }
    private static Measurement multiThreadPost() {
        // writeToSer();
        int chunkSize = 10000;
        DataReader dataReader = new DataReader(filePath);
        List<RFIDLiftData> rfidLiftDataList = dataReader.readDataFile();
        int listSize = rfidLiftDataList.size();
        int ThreadNum = (int)Math.ceil((double)listSize/(double)chunkSize);
        System.out.println("The length of the list is " + listSize);

        // start the multiThread
        List<PostRequestCallable> tasks = new ArrayList<PostRequestCallable>();
        SendRequest sendRequest = new SendRequest();
        sendRequest.setAttributes(1, 5000);
        while (listSize > chunkSize) {
            List<RFIDLiftData> tempList = rfidLiftDataList.subList(listSize - chunkSize, listSize);
            PostRequestCallable task = new PostRequestCallable(sendRequest, tempList);
            tasks.add(task);
            listSize -= chunkSize;
        }
        if (listSize > 0) {
            tasks.add(new PostRequestCallable(sendRequest, rfidLiftDataList.subList(0,listSize)));
        }
        // Initiate the ThreadPool
        ExecutorService executor = Executors.newFixedThreadPool(ThreadNum);
        List<Future<ResultData>> futures = new ArrayList<Future<ResultData>>();
        System.out.println("ready to POST...");
        long startTime = System.currentTimeMillis();
        try {
            futures = executor.invokeAll(tasks, 15, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
        sendRequest.endLoading();
        long wallTime = System.currentTimeMillis() - startTime;
        System.out.println("The wall time is: " + TimeUnit.MILLISECONDS.toMinutes(wallTime) + "min");
        return new Measurement(futures, wallTime);
    }

}
