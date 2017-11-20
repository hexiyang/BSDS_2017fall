package assignment2_client;

import bsdsass2testdata.DataReader;
import bsdsass2testdata.RFIDLiftData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Scheduler {
    private HTTPRequests httpRequests;
    public Scheduler(HTTPRequests httpRequests) {
        this.httpRequests = httpRequests;
    }

    public Measurement multiThreadPost(String filePath) {
        int chunkSize = 8000;
        DataReader dataReader = new DataReader(filePath);
        List<RFIDLiftData> rfidLiftDataList = dataReader.readDataFile();
        int listSize = rfidLiftDataList.size();
        int ThreadNum = (int)Math.ceil((double)listSize/chunkSize);
        // start the multiThread
        List<PostRequestCallable> tasks = new ArrayList<PostRequestCallable>();
        //httpRequests.setAttributes(1, 5000);
        while (listSize > chunkSize) {
            List<RFIDLiftData> tempList = rfidLiftDataList.subList(listSize - chunkSize, listSize);
            PostRequestCallable task = new PostRequestCallable(httpRequests, tempList);
            tasks.add(task);
            listSize -= chunkSize;
        }
        if (listSize > 0) {
            tasks.add(new PostRequestCallable(httpRequests, rfidLiftDataList.subList(0,listSize)));
        }
        // Initiate the ThreadPool
        ExecutorService executor = Executors.newFixedThreadPool(ThreadNum);
        List<Future<ResultData>> futures = new ArrayList<Future<ResultData>>();
        System.out.println("ready to POST...");
        long startTime = System.currentTimeMillis();
        try {
            futures = executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
        try {
            if (executor.awaitTermination(15, TimeUnit.MINUTES)) {
                //httpRequests.endLoading();
            } else {
                executor.shutdownNow();
                System.out.println("System time out");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            executor.shutdownNow();
        }
        long wallTime = System.currentTimeMillis() - startTime;
        System.out.println("The wall time is: " + TimeUnit.MILLISECONDS.toMinutes(wallTime) + "min");
        return new Measurement(futures, wallTime);
    }


    public Measurement multiThreadGet(int dayNum) {
        int threadNum = 10;
        int chunkSize = (int)Math.ceil((double)3000/threadNum);
        List<GetRequestCallable> tasks = new ArrayList<>();
        HTTPRequests HTTPRequests = new HTTPRequests();
        int i = 1;
        while (i < 3001) {
            GetRequestCallable task = new GetRequestCallable(HTTPRequests, i, i + chunkSize, dayNum);
            tasks.add(task);
            i += chunkSize;
        }
        ExecutorService executor = Executors.newFixedThreadPool(threadNum);
        List<Future<ResultData>> futures = new ArrayList<>();
        System.out.println("ready to GET...");
        long startTime = System.currentTimeMillis();
        try {
            futures = executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
        try {
            if (executor.awaitTermination(15, TimeUnit.MINUTES)) {
            } else {
                executor.shutdownNow();
                System.out.println("System time out");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            executor.shutdownNow();
        }
        long wallTime = System.currentTimeMillis() - startTime;
        System.out.println("The wall time is: " + TimeUnit.MILLISECONDS.toSeconds(wallTime) + "s");
        return new Measurement(futures, wallTime);
    }
}
