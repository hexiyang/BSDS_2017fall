package neu.edu.xiyang;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

public class sendRequestThread  implements Runnable{
    private WebTarget target1;
    private WebTarget target2;
    private int iterationNum;
    private long threadLatency;
    public int requestNum = 0;
    public int successNum = 0;
    public List<Long> requestLatency = new ArrayList<Long>();
    public List<Long> successLatency = new ArrayList<Long>();

    public sendRequestThread (WebTarget t1, WebTarget t2, int iterNum) {
        target1 = t1;
        target2 = t2;
        iterationNum = iterNum;
    }

    public void run() {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < iterationNum; i++) {
            long start = System.currentTimeMillis();
            try {
                Invocation.Builder invocationBuilder1 = target1.request(MediaType.TEXT_PLAIN);
                String response1 = invocationBuilder1.get(String.class);
                System.out.println("[" + i + "]" + "The target1 response of " + Thread.currentThread() + " is " + response1);
                successNum++;
                successLatency.add(System.currentTimeMillis() - start);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                requestNum++;
                requestLatency.add(System.currentTimeMillis() - start);
            }

            Invocation.Builder invocationBuilder2 = target2.request(MediaType.TEXT_PLAIN);
            String response2 = invocationBuilder2.get(String.class);
            System.out.println("[" + i + "]" + "The target2 response of " + Thread.currentThread() + " is " + response2);
        }
        long endTime = System.currentTimeMillis();
        threadLatency = endTime - startTime;
        System.out.println("The latency of " + Thread.currentThread() + " is " + threadLatency);
    }
}
