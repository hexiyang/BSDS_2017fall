package neu.edu.xiyang;

import javax.ws.rs.client.WebTarget;
import java.util.ArrayList;
import java.util.List;

public class threading {
    public static void main(String[] args) {
        int threadNum = 1;
        int iterationNum = 10;
        String ipAddress = "18.221.145.100";
        String port = "8080";
        webTargetBuilder builder = new webTargetBuilder();
        WebTarget target1 = builder.getWebTarget(ipAddress, port, "myresource");
        WebTarget target2 = builder.getWebTarget(ipAddress, port, "test");
        // Build the threads
        List<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < threadNum; i++) {
            threads.add(new Thread(new sendRequestThread(target1, target2, iterationNum)));
        }
        long startTime = System.currentTimeMillis();
        System.out.println("Ready to roll.....");
        for (Thread thread : threads) {
            thread.start();
        }
        System.out.println ("main thread exiting " + Thread.currentThread());
        long endTime = System.currentTimeMillis();
        System.out.println("The runtime for all threads to complete is: " + (endTime - startTime) + " milliseconds");
    }
}
