package test.edu.xiyang;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class SendRequestCallable implements Callable<Result>{
    private WebTarget target;
    private int iterationNum;

    public SendRequestCallable(ConfigInfo configInfo) {
        this.target = configInfo.webTarget;
        this.iterationNum = configInfo.iterationNum;
    }

    public Result call() {
        // Initialize variables
        List<Long> getLatenciesSuccess =  new ArrayList<Long>();
        List<Long> postLatenciesSuccess = new ArrayList<Long>();
        List<Long> getLatenciesFail =  new ArrayList<Long>();
        List<Long> postLatenciesFail = new ArrayList<Long>();
        int getFailNum = 0;
        int getSuccessNum = 0;
        int postFailNum = 0;
        int postSuccessNum = 0;

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < iterationNum; i++) {
            // Send the Get Request
            long start = System.currentTimeMillis();
            try {
                Invocation.Builder invocationBuilder = target.request(MediaType.TEXT_PLAIN);
                String response = invocationBuilder.get(String.class);
                System.out.println("[" + i + "]" + " Get response of " + Thread.currentThread() + " is \"" + response + "\"");
                getSuccessNum++;
                getLatenciesSuccess.add(System.currentTimeMillis() - start);
            } catch (Exception e) {
                getFailNum++;
                getLatenciesFail.add(System.currentTimeMillis() - start);
                e.printStackTrace();
            }

            // Send the Post Request
            start = System.currentTimeMillis();
            try {
                Invocation.Builder invocationBuilder = target.request(MediaType.TEXT_PLAIN);
                Response response = invocationBuilder.post(Entity.entity("post", MediaType.TEXT_PLAIN));
                String output = response.readEntity(String.class);
                response.close();
                System.out.println("[" + i + "]" + " Post response of " + Thread.currentThread() + " is \"" + output + "\"");
                postSuccessNum++;
                postLatenciesSuccess.add(System.currentTimeMillis() - start);
            } catch (Exception e) {
                postFailNum++;
                postLatenciesFail.add(System.currentTimeMillis() - start);
                e.printStackTrace();
            }
        }
        //System.out.println("The latency of " + Thread.currentThread() + " is " + threadLatency);
        return new Result(getLatenciesSuccess, postLatenciesSuccess,
                getLatenciesFail, postLatenciesFail,
                getFailNum, postFailNum, getSuccessNum, postSuccessNum);
    }


}
