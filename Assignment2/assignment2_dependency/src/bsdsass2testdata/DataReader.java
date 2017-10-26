package bsdsass2testdata;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataReader {
    private String path;

    public DataReader(String path) {
        this.path = path;
    }

    public List<RFIDLiftData> readDataFile() {
        // Read the original file
        List<RFIDLiftData> RFIDDataIn;
        try (FileInputStream fis = new FileInputStream(path);
             ObjectInputStream ois = new ObjectInputStream(fis))
        {
            // read data from serialized file
            System.out.println("===Reading array list===");
            RFIDDataIn = (ArrayList) ois.readObject();
            System.out.println("Rec Count = " + RFIDDataIn.size());
            ois.close();
            fis.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
            return null;
        }catch(ClassNotFoundException c){
            System.out.println("Class not found");
            c.printStackTrace();
            return null;
        }
        return RFIDDataIn;
    }

    public List<DummyClass> readDummyFile() {
        // Read the original file
        List<DummyClass> dummyClassList;
        try (FileInputStream fis = new FileInputStream(path);
             ObjectInputStream ois = new ObjectInputStream(fis))
        {
            // read data from serialized file
            System.out.println("===Reading array list===");
            dummyClassList = (ArrayList) ois.readObject();
            System.out.println("Rec Count = " + dummyClassList.size());
            ois.close();
            fis.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
            return null;
        }catch(ClassNotFoundException c){
            System.out.println("Class not found");
            c.printStackTrace();
            return null;
        }
        return dummyClassList;
    }

    public void writeDummyToFile(List<RFIDLiftData> RFIDDataIn) {
        List<DummyClass> dummyClassList = new ArrayList<>();
        for (RFIDLiftData temp : RFIDDataIn) {
            dummyClassList.add(new DummyClass(
                    temp.getResortID(),
                    temp.getDayNum(),
                    temp.getTime(),
                    temp.getSkierID(),
                    temp.getLiftID()));
        }
        try (FileOutputStream fos = new FileOutputStream(new File(
                "/Users/Xiyang/Documents/Google Drive/Courses/" +
                        "Distributed System/Assignments/Assignment2/BSDSAssignment2Day2-custom.ser"));
             ObjectOutputStream oos = new ObjectOutputStream(fos))
        {
            oos.writeObject(dummyClassList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Down with writing");
    }

    public void writeRFIDLiftDataToFile(List<DummyClass> DummyIn) {
        List<RFIDLiftData> rfidLiftDataList = new ArrayList<>();
        for (DummyClass temp : DummyIn) {
            rfidLiftDataList.add(new RFIDLiftData(
                    temp.getResortID(),
                    temp.getDayNum(),
                    temp.getTime(),
                    temp.getSkierID(),
                    temp.getLiftID()));
        }
        try (FileOutputStream fos = new FileOutputStream(new File(
                "/Users/Xiyang/Documents/Google Drive/Courses/" +
                        "Distributed System/Assignments/Assignment2/BSDSAssignment2Day2.ser"));
             ObjectOutputStream oos = new ObjectOutputStream(fos))
        {
            oos.writeObject(rfidLiftDataList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Down with writing");
    }
}
