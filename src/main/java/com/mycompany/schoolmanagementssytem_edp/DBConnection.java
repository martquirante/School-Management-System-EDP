package com.mycompany.schoolmanagementssytem_edp;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    
    public static Connection connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/school_management_system",
                "root",
                ""
            );

            System.out.println("Connected successfully!");
            return conn;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}