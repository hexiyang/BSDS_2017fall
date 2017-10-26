package assignment2_client;

import bsdsass2testdata.DataReader;
import bsdsass2testdata.RFIDLiftData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ThroughputTest {
    private String filePath;
    private HTTPRequests httpRequests;

    public ThroughputTest(String filePath, HTTPRequests httpRequests) {
        this.filePath = filePath;
        this.httpRequests = httpRequests;
    }

    public double postThroughput(int clientChunkSize, int serverChunckSize){
        DataReader dataReader = new DataReader(filePath);
        List<RFIDLiftData> rfidLiftDataList = dataReader.readDataFile();
        List<RFIDLiftData> testList = rfidLiftDataList.subList(0, 20000);
        int listSize = testList.size();
        int ThreadNum = (int)Math.ceil((double)listSize/(double)clientChunkSize);

        // start the multiThread
        List<PostRequestCallable> tasks = new ArrayList<PostRequestCallable>();
        httpRequests.setAttributes(1, serverChunckSize);
        while (listSize > clientChunkSize) {
            List<RFIDLiftData> tempList = rfidLiftDataList.subList(listSize - clientChunkSize, listSize);
            PostRequestCallable task = new PostRequestCallable(httpRequests, tempList);
            tasks.add(task);
            listSize -= clientChunkSize;
        }
        if (listSize > 0) {
            tasks.add(new PostRequestCallable(httpRequests, rfidLiftDataList.subList(0,listSize)));
        }
        // Initiate the ThreadPool
        ExecutorService executor = Executors.newFixedThreadPool(ThreadNum);
        List<Future<ResultData>> futures = new ArrayList<Future<ResultData>>();
        long startTime = System.currentTimeMillis();
        try {
            futures = executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
        try {
            if (executor.awaitTermination(10, TimeUnit.MINUTES)) {
                httpRequests.endLoading();
            } else {
                executor.shutdownNow();
                System.out.println("System times out");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            executor.shutdownNow();
        }
        httpRequests.endLoading();
        long wallTime = System.currentTimeMillis() - startTime;
//        System.out.println("The throughput for ThreadNum: " + 800000/clientChunkSize + ", ServerChunckSize: "
//        + serverChunckSize + " is " + (40000.00/(double)wallTime)*1000 + " req/s");
        return (40000.00/(double)wallTime)*1000;
    }
}
