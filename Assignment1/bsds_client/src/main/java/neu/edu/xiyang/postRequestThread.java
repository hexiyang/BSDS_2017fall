package neu.edu.xiyang;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public class postRequestThread implements Runnable{
    private WebTarget target1;
    private WebTarget target2;
    private int iterationNum;
    private long threadLatency;
    public int requestNum = 0;
    public int successNum = 0;
    public List<Long> requestLatency = new ArrayList<Long>();
    public List<Long> successLatency = new ArrayList<Long>();

    public postRequestThread(WebTarget t1, WebTarget t2, int iterNum) {
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
                Response response = invocationBuilder1.post(Entity.entity("post", MediaType.TEXT_PLAIN));
                String output = response.readEntity(String.class);
                response.close();
                System.out.println("[" + i + "]" + "Target1 response of " + Thread.currentThread() + " is \"" + output + "\"");
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
                Invocation.Builder invocationBuilder1 = target1.request(MediaType.TEXT_PLAIN);
                Response response = invocationBuilder1.post(Entity.entity("post", MediaType.TEXT_PLAIN));
                String output = response.readEntity(String.class);
                response.close();
                System.out.println("[" + i + "]" + "Target2 response of " + Thread.currentThread() + " is \"" + output + "\"");
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

    }
}
