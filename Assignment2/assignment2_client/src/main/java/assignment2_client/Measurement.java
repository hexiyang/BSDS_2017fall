package assignment2_client;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Measurement {
    long wallTime;
    List<Future<ResultData>> results;
    ResultData finalResult = new ResultData();

    public Measurement(List<Future<ResultData>> results, long wallTime) {
        this.results = results;
        this.wallTime = wallTime;
        aggregateResults();
    }

    public void setResults(List<Future<ResultData>> results) {
        this.results = results;
        aggregateResults();
    }

    public void printStatistics() {
        System.out.println("\n" +
                "===================================================\n" +
                "*                Statistic Results                *\n" +
                "===================================================");
        // Start to print statistics
        System.out.println("[     Wall Time]: " + wallTime + " ms");
        System.out.println("[    Total Requests]: " + finalResult.totalNum);
        System.out.println("[   Failed Requests]: " + finalResult.failNum);
        System.out.println("[    Total Mean   Latency]: " + meanLatency(finalResult.latencies) + " ms ");
        System.out.println("[    Total Median Latency]: " + percentileLatency(finalResult.latencies, 0.50) + " ms ");
        System.out.println("[    Total 95th Latency]: " + percentileLatency(finalResult.latencies, 0.95) + " ms ");
        System.out.println("[    Total 99th Latency]: " + percentileLatency(finalResult.latencies, 0.99) + " ms ");
    }

    private void aggregateResults() {
        for (Future<ResultData> future: results) {
            try {
                ResultData resultData = future.get();
                finalResult.latencies.addAll(resultData.latencies);
                finalResult.failNum += resultData.failNum;
                finalResult.totalNum += resultData.totalNum;
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    // Help functions
    private long meanLatency (List<Long> list) {
        if (list.size() > 0) {
            long sum = 0;
            for (Long latency: list) {
                sum += latency;
            }
            return sum/list.size();
        } else {
            return 0;
        }

    }

    private long percentileLatency (List<Long> list, double percentile) {
        if (list.size() > 0) {
            Collections.sort(list);
            int index = (int)(percentile * list.size());
            return list.get(Math.max(index-1, 0));
        } else {
            return 0;
        }
    }
}
