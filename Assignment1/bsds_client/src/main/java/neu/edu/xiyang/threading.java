package neu.edu.xiyang;

import javax.ws.rs.client.WebTarget;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

public class threading {
    public static void main(String[] args) {
        int threadNum = 100;
        int iterationNum = 100;
        String ipAddress = "18.221.145.100";
        String port = "8080";
        webTargetBuilder builder = new webTargetBuilder();
        WebTarget target1 = builder.getWebTarget(ipAddress, port, "myresource");
        WebTarget target2 = builder.getWebTarget(ipAddress, port, "test");
        // Build the threads
        Measurements measurements = new Measurements();
        CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNum, measurements);
        List<sendRequestThread> tasks = new ArrayList<sendRequestThread>();
        List<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < threadNum; i++) {
            sendRequestThread runnable = new sendRequestThread(target1, target2, iterationNum, cyclicBarrier);
            tasks.add(runnable);
            threads.add(new Thread(runnable));
        }
        measurements.tasks = tasks;
        // Start Threads
        long startTime = System.currentTimeMillis();
        measurements.setStartTime(startTime);
        System.out.println("Ready to roll.....");
        for (Thread thread : threads) {
            thread.start();
        }
    }
}
