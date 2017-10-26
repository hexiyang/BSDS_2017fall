package bsdsass2testdata;

import java.util.List;

public class Test {
    public static void main(String[] args) {
        DataReader dataReader = new DataReader("/Users/Xiyang/Documents/Google Drive/Courses/" +
                "Distributed System/Assignments/Assignment2/BSDSAssignment2Day2-custom.ser");
        List<DummyClass> dataList = dataReader.readDummyFile();
        System.out.println("The size of the list is: " + dataList.size());
        System.out.println("The first skierID: " + dataList.get(0).getSkierID());
        dataReader.writeRFIDLiftDataToFile(dataList);
    }
}
