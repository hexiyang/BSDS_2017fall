package assignment2_server;

import bsdsass2testdata.RFIDLiftData;
import java.sql.*;
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

    public int loadRecords(List<RFIDLiftData> rfidLiftDataList) {
        String query = "INSERT INTO skierInfo (skierID, liftID, timeStamp, resortID, dayNum) " +
                "VALUES (?, ?, ?, ?, ?)";
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

}
