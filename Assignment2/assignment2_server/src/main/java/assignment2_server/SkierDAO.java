package assignment2_server;

import Dependencies.SkierInfo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SkierDAO {
    private String hostname;
    private String username;
    private String password;
    private Connection connection;

    public SkierDAO() {
        hostname = "jdbc:mysql://mydbinstance.cz2nl5t3sjuh.us-west-2.rds.amazonaws.com:3306/skierdb?rewriteBatchedStatements=true";
        username = "hexiyang";
        password = "12345678";
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

    public int loadRecords(List<SkierInfo> skierInfoList) {
        String query = "INSERT INTO skierInfo (skierID, liftID, timeStamp, resortID, dayNum) " +
                "VALUES (?, ?, ?, ?, ?)";
        int successCount = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            for (SkierInfo skierInfo : skierInfoList) {
                try {
                    preparedStatement.setString(1, skierInfo.getSkierID());
                    preparedStatement.setString(2, skierInfo.getLiftID());
                    preparedStatement.setString(3, skierInfo.getTimestamp());
                    preparedStatement.setString(4, skierInfo.getResortID());
                    preparedStatement.setInt(5, skierInfo.getDayNum());
                    // add to batch
                    preparedStatement.addBatch();
                    successCount++;
                } catch (SQLException e) {
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

    public int addToBatch(SkierInfo skierInfo) {
        String query = "INSERT INTO skierdb.skierInfo (skierID, liftID, timeStamp, resortID, dayNum) " +
                "VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, skierInfo.getSkierID());
            preparedStatement.setString(2, skierInfo.getLiftID());
            preparedStatement.setString(3, skierInfo.getTimestamp());
            preparedStatement.setString(4, skierInfo.getResortID());
            preparedStatement.setInt(5, skierInfo.getDayNum());
            // add to batch
            preparedStatement.addBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }


}
