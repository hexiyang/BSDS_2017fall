package neu.edu.xiyang;

import java.util.ArrayList;
import java.util.List;

public class Result {
    List<Long> getLatenciesSuccess;
    List<Long> postLatenciesSuccess;
    List<Long> getLatenciesFail;
    List<Long> postLatenciesFail;
    int getRequestFailNum;
    int postRequestFailNum;
    int getRequestSuccessNum;
    int postRequestSuccessNum;
//    List<Long> getTimestamps;
//    List<Long> postTimestamps;

    public Result() {
        getLatenciesSuccess = new ArrayList<Long>();
        getLatenciesFail = new ArrayList<Long>();
        postLatenciesSuccess = new ArrayList<Long>();
        postLatenciesFail = new ArrayList<Long>();
        getRequestFailNum = 0;
        postRequestFailNum = 0;
        getRequestSuccessNum = 0;
        postRequestSuccessNum = 0;
//        getTimestamps = new ArrayList<Long>();
//        postTimestamps = new ArrayList<Long>();
    }

    public Result(List<Long> getLatenciesSuccess, List<Long> postLatenciesSuccess,
                  List<Long> getLatenciesFail, List<Long> postLatenciesFail,
                  int getRequestFailNum, int postRequestFailNum,
                  int getRequestSuccessNum, int postRequestSuccessNum) {
        this.getLatenciesSuccess = getLatenciesSuccess;
        this.postLatenciesSuccess = postLatenciesSuccess;
        this.getLatenciesFail = getLatenciesFail;
        this.postLatenciesFail = postLatenciesFail;
        this.getRequestFailNum = getRequestFailNum;
        this.postRequestFailNum = postRequestFailNum;
        this.getRequestSuccessNum = getRequestSuccessNum;
        this.postRequestSuccessNum = postRequestSuccessNum;
        // Timestamps
//        this.getTimestamps = getTimestamp;
//        this.postTimestamps = postTimestamp;
    }
}
