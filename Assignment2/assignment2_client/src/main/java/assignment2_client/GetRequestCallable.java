package assignment2_client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class GetRequestCallable implements Callable<ResultData>{

    private HTTPRequests HTTPRequests;
    private int from;
    private int to;
    private int dayNum;

    public GetRequestCallable(HTTPRequests HTTPRequests, int from, int to, int dayNum) {
        this.HTTPRequests = HTTPRequests;
        this.from = from;
        this.to = to;
        this.dayNum = dayNum;
    }

    public ResultData call() {
        List<Long> latencies = new ArrayList<>();
        int totalNum = 0;
        int failedNum = 0;
        for (int i = from; i < to; i++) {
            long startTime = System.currentTimeMillis();
            int status = HTTPRequests.getMyVert(i, dayNum);
            if (status == 400) {
                failedNum++;
            }
            totalNum++;
            latencies.add(System.currentTimeMillis() - startTime);
        }
        return new ResultData(latencies, totalNum, failedNum);
    }
}
