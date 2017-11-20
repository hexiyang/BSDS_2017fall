package assignment2_server;

import bsdsass2testdata.RFIDLiftData;

import javax.servlet.ServletContext;
import java.util.*;

public class ScanTimerTask implements Runnable{
    Queue<RFIDLiftData> processQueue;
    SkierDAO skierDAO;
    int chunkSize;
    public ScanTimerTask (Queue<RFIDLiftData> processQueue, SkierDAO skierDAO, int chunkSize) {
        this.processQueue = processQueue;
        this.skierDAO = skierDAO;
        this.chunkSize = chunkSize;
    }
    @Override
    public void run() {
        int count = chunkSize;
        List<RFIDLiftData> list = new ArrayList<>();
        while (!processQueue.isEmpty() && count>0) {
            list.add(processQueue.poll());
            count--;
        }
        int recordsNum = chunkSize - count;
        int successNum = skierDAO.loadRecords(list);
        System.out.println("Successful loaded " + recordsNum + " records, successNum is " + successNum);
    }
}
