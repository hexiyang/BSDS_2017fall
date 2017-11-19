package bsdsass2testdata;

import java.util.List;

public class Test {
    public static void main(String[] args) {
        DataReader dataReader = new DataReader("/Users/Xiyang/Documents/Google Drive/Courses/Distributed System/Assignments/" +
                "Assignment3/BSDSAssignment2Day3.csv");
        List<RFIDLiftData> RFIDDataList = dataReader.readDataFile();
        dataReader.printOutData(RFIDDataList);
    }
}
