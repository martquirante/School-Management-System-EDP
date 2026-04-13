package com.mycompany.schoolmanagementssytem_edp;

import java.sql.Connection;

public class SchoolManagementSsytem_EDP {

    public static void main(String[] args) {
        
        // 🔹 Connect to database
        Connection conn = DBConnection.connect();
        
        if (conn != null) {
            System.out.println("Database Connected!");
        } else {
            System.out.println("Connection Failed!");
        }

        // 🔹 Open Admin GUI
        Admin admin = new Admin();
        admin.setVisible(true);
    }
}