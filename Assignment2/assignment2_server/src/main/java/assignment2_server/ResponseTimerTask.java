package assignment2_server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Queue;

public class ResponseTimerTask implements Runnable{
    Queue<Long> responseQueue;
    File file;
    int chunkSize;

    public ResponseTimerTask(Queue<Long> responseQueue, File file, int chunkSize) {
        this.responseQueue = responseQueue;
        this.file = file;
        this.chunkSize = chunkSize;
    }

    @Override
    public void run() {
        int size = responseQueue.size();
        int count = Math.min(size, chunkSize);
        if (size != 0) {
            try{
                BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
                for (int i = 0; i < count; i++) {
                    bw.write(responseQueue.poll().toString() + "\n");
                }
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Successful loaded " + count + " response times");
        } else {
            System.out.println("Response Queue is empty...");
        }

    }
}
