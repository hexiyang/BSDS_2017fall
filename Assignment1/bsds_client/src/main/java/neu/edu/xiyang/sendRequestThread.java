package neu.edu.xiyang;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class sendRequestThread  implements Runnable{
    private WebTarget target1;
    private WebTarget target2;
    private int iterationNum;
    private long threadLatency;
    private CyclicBarrier cyclicBarrier;
    public int requestNum = 0;
    public int successNum = 0;
    public List<Long> requestLatency = new ArrayList<Long>();
    public List<Long> successLatency = new ArrayList<Long>();

    public sendRequestThread (WebTarget t1, WebTarget t2, int iterNum, CyclicBarrier cb) {
        target1 = t1;
        target2 = t2;
        iterationNum = iterNum;
        cyclicBarrier = cb;
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

            start = System.currentTimeMillis();
            try {
                Invocation.Builder invocationBuilder2 = target2.request(MediaType.TEXT_PLAIN);
                String response2 = invocationBuilder2.get(String.class);
                System.out.println("[" + i + "]" + "The target2 response of " + Thread.currentThread() + " is " + response2);
                successNum++;
                successLatency.add(System.currentTimeMillis() - start);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                requestNum++;
                requestLatency.add(System.currentTimeMillis() - start);
            }
        }

        threadLatency = System.currentTimeMillis() - startTime;
        System.out.println("The latency of " + Thread.currentThread() + " is " + threadLatency);

        try {
            System.out.println(Thread.currentThread() + " start waiting...");
            cyclicBarrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
/*
    private void sendRequest (WebTarget target, int iter) {
        long start = System.currentTimeMillis();
        try {
            Invocation.Builder invocationBuilder = target.request(MediaType.TEXT_PLAIN);
            Response response = invocationBuilder.get();
            String anwser = response.readEntity(String.class);
            response.close();
            System.out.println("[" + iter + "]" + "The target1 response of " + Thread.currentThread() + " is " + anwser);
            successNum++;
            successLatency.add(System.currentTimeMillis() - start);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            requestNum++;
            requestLatency.add(System.currentTimeMillis() - start);
        }
    }
*/
}
