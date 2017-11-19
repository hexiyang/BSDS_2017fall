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
        List<RFIDLiftData> RFIDDataList = new ArrayList<>();
        String csvSplitBy = ",";
        System.out.println("------------------>>Begin to read data<<---------------------\n");
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = br.readLine();
            while(line != null) {
                String[] items = line.split(csvSplitBy);
                RFIDLiftData item = new RFIDLiftData(
                        Integer.parseInt(items[0]),
                        Integer.parseInt(items[1]),
                        Integer.parseInt(items[2]),
                        Integer.parseInt(items[3]),
                        Integer.parseInt(items[4])
                );
                RFIDDataList.add(item);

                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return RFIDDataList;
        }
        System.out.println("---------------->>Finish reading, total size: " + RFIDDataList.size() + "<<--------------\n");
        return RFIDDataList;
    }

    public void printOutData(List<RFIDLiftData> RFIDDataIn) {
        // output contents to console
        int count = 0;
        System.out.println("==============>>Array List contents<<================");
        for(RFIDLiftData tmp: RFIDDataIn){
            System.out.print(String.valueOf (tmp.getResortID()) +  " " +
                    String.valueOf (tmp.getDayNum()) +  " " +
                    String.valueOf (tmp.getSkierID()) +  " " +
                    String.valueOf (tmp.getLiftID()) +  " " +
                    String.valueOf (tmp.getTime()) +   "\n"
            );
            count++;
        }
        System.out.println("Rec Count = " + count);
    }

    private List<RFIDLiftData> readDataFile_backup() {
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
