package assignment2_server;

import bsdsass2testdata.RFIDLiftData;
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
        int size = processQueue.size();
        int count = Math.min(size, chunkSize);
        if(size != 0) {
            skierDAO.prepareBatch();
            for (int i = 0; i < count; i ++) {
                skierDAO.addToBatch(processQueue.poll());
            }
            skierDAO.doBatch();
        }
        System.out.println("Successful loaded " + count + " records");
    }
}
