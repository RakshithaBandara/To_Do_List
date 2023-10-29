package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static DBConnection dbConnection;
    private Connection connection;

    private DBConnection(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/todolist","root","root");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }

    public static DBConnection getInstance(){

//        if(dbConnection == null){
//            dbConnection = new DBConnection();
//        }
//        return dbConnection;

//        How to write this if in single line -->
        return (dbConnection == null) ? dbConnection = new DBConnection():dbConnection;
    }

    public Connection getConnection(){
        return connection;
    }
}