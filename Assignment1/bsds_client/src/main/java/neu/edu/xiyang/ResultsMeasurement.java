package neu.edu.xiyang;

import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

public class ResultsMeasurement {
    private Result finalResult = new Result();
    public ResultsMeasurement (List<Future<Result>> futures) {
        for (Future<Result> future : futures) {
            try {
                Result result = future.get();
                finalResult.getLatenciesSuccess.addAll(result.getLatenciesSuccess);
                finalResult.getLatenciesFail.addAll(result.getLatenciesFail);
                finalResult.postLatenciesSuccess.addAll(result.postLatenciesSuccess);
                finalResult.postLatenciesFail.addAll(result.postLatenciesFail);
                finalResult.getRequestSuccessNum += result.getRequestSuccessNum;
                finalResult.getRequestFailNum += result.getRequestFailNum;
                finalResult.postRequestSuccessNum += result.postRequestSuccessNum;
                finalResult.postRequestFailNum += result.postRequestFailNum;
                // Timestamp
//                finalResult.getTimestamps.addAll(result.getTimestamps);
//                finalResult.postTimestamps.addAll(result.postTimestamps);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    public void generateStatistics(long wallTime) {
        System.out.println("\n\n" +
                "***************************************************\n" +
                "*                Statistic Results                *\n" +
                "***************************************************\n" +
                "Wall Time: " + wallTime + " ms ");
        measureGET();
        measurePOST();

    }

    private void measureGET() {
        System.out.println("===================================================\n" +
                "Statistics for GET requests:\n" +
                "---------------------------------------------------");
        measure(finalResult.getRequestSuccessNum, finalResult.getRequestFailNum,
                finalResult.getLatenciesSuccess, finalResult.getLatenciesFail);
        System.out.println("---------------------------------------------------\n");
    }

    private void measurePOST() {
        System.out.println("===================================================\n" +
                "Statistics for POST requests:\n" +
                "---------------------------------------------------");
        measure(finalResult.postRequestSuccessNum, finalResult.postRequestFailNum,
                finalResult.postLatenciesSuccess, finalResult.postLatenciesFail);
        System.out.println("---------------------------------------------------\n");
    }

    private void measure(int successNum, int failNum, List<Long> successLatencies, List<Long> failLatencies) {
        int requestNum = successNum + failNum;
        List<Long> totalLatencies = new ArrayList<Long>();
        totalLatencies.addAll(successLatencies);
        totalLatencies.addAll(failLatencies);
        // Start to print statistics
        System.out.println("[    Total Requests]: " + requestNum);
        System.out.println("[Successed Requests]: " + successNum);
        System.out.println("[   Failed Requests]: " + failNum);
        System.out.println("[    Total Mean   Latency]: " + meanLatency(totalLatencies) + " ms ");
        System.out.println("[Successed Mean   Latency]: " + meanLatency(successLatencies) + " ms ");
        System.out.println("[   Failed Mean   Latency]: " + meanLatency(failLatencies) + " ms ");
        System.out.println("[    Total Median Latency]: " + percentileLatency(totalLatencies, 0.50) + " ms ");
        System.out.println("[Successed Median Latency]: " + percentileLatency(successLatencies, 0.50) + " ms ");
        System.out.println("[   Failed Median Latency]: " + percentileLatency(failLatencies, 0.50) + " ms ");
        System.out.println("[    Total 99th Latency]: " + percentileLatency(totalLatencies, 0.99) + " ms ");
        System.out.println("[Successed 99th Latency]: " + percentileLatency(successLatencies, 0.99) + " ms ");
        System.out.println("[   Failed 99th Latency]: " + percentileLatency(failLatencies, 0.99) + " ms ");
        System.out.println("[    Total 95th Latency]: " + percentileLatency(totalLatencies, 0.95) + " ms ");
        System.out.println("[Successed 95th Latency]: " + percentileLatency(successLatencies, 0.95) + " ms ");
        System.out.println("[   Failed 95th Latency]: " + percentileLatency(failLatencies, 0.95) + " ms ");
    }
    /*
    // For timestamps
    public void writeData() throws IOException{
        FileWriter fw = new FileWriter("/Users/Xiyang/Downloads/Get.dat", false);
        fw.write("Timestamp::Latency\n");
        int i = 0;
        for (long timestamp : finalResult.getTimestamps) {
            Timestamp ts = new Timestamp(timestamp);
            String newline = ts.toString() + "::" + finalResult.getLatenciesSuccess.get(i).toString() + "\n";
            fw.write(newline);
            i++;
        }
        fw.close();
        FileWriter fw1 = new FileWriter("/Users/Xiyang/Downloads/Post.dat", false);
        fw1.write("Timestamp::Latency\n");
        i = 0;
        for (long timestamp : finalResult.postTimestamps) {
            Timestamp ts = new Timestamp(timestamp);
            String newline = ts.toString() + "::" + finalResult.postLatenciesSuccess.get(i).toString() + "\n";
            fw1.write(newline);
            i++;
        }
        fw1.close();
    }*/


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
