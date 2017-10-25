package assignment2_client;


import Dependencies.SkierInfo;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
public class TestClient {

    public static void main(String[] args) {
        multiThread();
    }

    private static void multiThread() {
        // writeToSer();
        int chunkSize = 8000;
        List<SkierInfo> skierInfoList = readFile();
        int listSize = skierInfoList.size();
        int ThreadNum = (int)Math.ceil((double)listSize/(double)chunkSize);
        System.out.println("The length of the list is " + skierInfoList.size());

        // start the multiThread
        List<PostRequestCallable> tasks = new ArrayList<PostRequestCallable>();
        SendRequest sendRequest = new SendRequest();
        while (listSize > chunkSize) {
            List<SkierInfo> tempList = skierInfoList.subList(listSize - chunkSize, listSize);
            PostRequestCallable task = new PostRequestCallable(sendRequest, tempList);
            tasks.add(task);
            listSize -= chunkSize;
        }
        if (listSize > 0) {
            tasks.add(new PostRequestCallable(sendRequest, skierInfoList.subList(0,listSize)));
        }
        // Initiate the ThreadPool
        ExecutorService executor = Executors.newFixedThreadPool(ThreadNum);
        List<Future<ResultData>> futures = new ArrayList<Future<ResultData>>();
        System.out.println("ready to roll...");
        long startTime = System.currentTimeMillis();
        try {
            futures = executor.invokeAll(tasks, 40, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendRequest.endLoading();
        long wallTime = System.currentTimeMillis() - startTime;
        System.out.println("The wall time is: " + TimeUnit.MILLISECONDS.toMinutes(wallTime) + "min");
        Measurement measurement = new Measurement(futures);
        measurement.printStatistics();
    }




    private static List<SkierInfo> readFile() {
        String newLine = System.getProperty("line.separator");
        List<SkierInfo> skierInfosIn;
        try (FileInputStream fis = new FileInputStream(
                "/Users/Xiyang/Documents/Google Drive/Courses/" +
                        "Distributed System/Assignments/Assignment2/BSDSAssignment2Day1-custom.ser");
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            // read data from serialized file
            System.out.println("====Reading List====");
            skierInfosIn = (ArrayList)ois.readObject();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
            return null;
        }
        return skierInfosIn;
    }

}
