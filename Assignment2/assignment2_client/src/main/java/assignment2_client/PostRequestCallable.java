package assignment2_client;

import bsdsass2testdata.RFIDLiftData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class PostRequestCallable implements Callable<ResultData>{
    private SendRequest sendRequest;
    private List<RFIDLiftData> processList;

    public PostRequestCallable(SendRequest sendRequest, List<RFIDLiftData> processList) {
        this.sendRequest = sendRequest;
        this.processList = processList;
    }

    public ResultData call() {
        List<Long> latencies = new ArrayList<Long>();
        int totalNum = 0;
        int failNum = 0;
        for (RFIDLiftData rfidLiftData : processList) {
            long startTime = System.currentTimeMillis();
            try {
                sendRequest.postRecord(rfidLiftData);
            } catch (Exception e) {
                System.out.println(e);
                failNum++;
            }
            totalNum++;
            latencies.add(System.currentTimeMillis() - startTime);
        }
        return new ResultData(latencies, totalNum, failNum);
    }
}
