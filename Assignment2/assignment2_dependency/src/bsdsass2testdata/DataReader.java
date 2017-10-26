package bsdsass2testdata;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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
}
