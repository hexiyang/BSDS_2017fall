package assignment2_server;

import bsdsass2testdata.RFIDLiftData;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import java.sql.*;
import java.util.HashMap;
import java.util.List;

public class SkierDAO {
    private String hostname;
    private String username;
    private String password;
    private Connection connection;
    private PreparedStatement batchStatement;

    public SkierDAO() {
        hostname = "jdbc:mysql://mydbinstance.cz2nl5t3sjuh.us-west-2.rds.amazonaws.com:3306/skierdb?rewriteBatchedStatements=true&relaxAutoCommit=true";
        username = "hexiyang";
        password = "12345678";
        buildConnection();
    }

    public void buildConnection() {
        try {
            Driver myDriver = new com.mysql.jdbc.Driver();
            DriverManager.registerDriver(myDriver);
            connection = DriverManager.getConnection(hostname, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("successfully connected!");
    }

    public void destroyConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createDayCache(ServletContext context, int dayNum) throws SQLException{
        String query = "select skierID, SUM(liftID) as totalLift, SUM(vertical) as totalVertical from skierInfo\n" +
                "where dayNum = " + dayNum + "\n" +
                "group by skierID;";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();
        HashMap<Integer, Integer> liftMap = new HashMap<>();
        HashMap<Integer, Integer> verticalMap = new HashMap<>();
        while(resultSet.next()) {
            int skierID = resultSet.getInt("skierID");
            int liftSum = resultSet.getInt("totalLift");
            int verticalSum = resultSet.getInt("totalVertical");
            liftMap.put(skierID, liftSum);
            verticalMap.put(skierID, verticalSum);
        }
        context.setAttribute("liftMap", liftMap);
        context.setAttribute("verticalMap", verticalMap);
        context.setAttribute("dayNum", dayNum);

    }

    public void prepareBatch() {
        String query = "INSERT INTO skierInfo (skierID, liftID, timeStamp, resortID, dayNum, vertical) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try{
            batchStatement = connection.prepareStatement(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addToBatch(RFIDLiftData rfidLiftData) {
        try {
            batchStatement.setInt(1, rfidLiftData.getSkierID());
            batchStatement.setInt(2, rfidLiftData.getLiftID());
            batchStatement.setInt(3, rfidLiftData.getTime());
            batchStatement.setInt(4, rfidLiftData.getResortID());
            batchStatement.setInt(5, rfidLiftData.getDayNum());
            batchStatement.setInt(6, getVertical(rfidLiftData.getLiftID()));
            batchStatement.addBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void doBatch() {
        try {
            batchStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        batchStatement = null;

    }

    private int loadRecords(List<RFIDLiftData> rfidLiftDataList) {
        String query = "INSERT INTO skierInfo (skierID, liftID, timeStamp, resortID, dayNum, vertical) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        int successCount = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            for (RFIDLiftData rfidLiftData : rfidLiftDataList) {
                try {
                    preparedStatement.setInt(1, rfidLiftData.getSkierID());
                    preparedStatement.setInt(2, rfidLiftData.getLiftID());
                    preparedStatement.setInt(3, rfidLiftData.getTime());
                    preparedStatement.setInt(4, rfidLiftData.getResortID());
                    preparedStatement.setInt(5, rfidLiftData.getDayNum());
                    preparedStatement.setInt(6, getVertical(rfidLiftData.getLiftID()));

                    // add to batch
                    preparedStatement.addBatch();
                    successCount++;
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return successCount;
    }

    private int getVertical (int liftID) {
        if(liftID < 11) {
            return 200;
        } else if (liftID < 21) {
            return 300;
        } else if (liftID < 31) {
            return 400;
        } else {
            return 500;
        }
    }



}
