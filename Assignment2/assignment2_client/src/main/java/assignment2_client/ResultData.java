package assignment2_client;

import java.util.ArrayList;
import java.util.List;

public class ResultData {
    public List<Long> latencies;
    public int totalNum;
    public int failNum;

    public ResultData() {
        latencies = new ArrayList<Long>();
        totalNum = 0;
        failNum = 0;
    }

    public ResultData(List<Long> latencies, int totalNum, int failNum) {
        this.latencies = latencies;
        this.totalNum = totalNum;
        this.failNum = failNum;
    }

}
