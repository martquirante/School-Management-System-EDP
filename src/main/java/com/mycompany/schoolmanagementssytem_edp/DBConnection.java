package com.mycompany.schoolmanagementssytem_edp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {

    private static final String DEFAULT_DATABASE_NAME = "school_management_system";
    private static final String BASE_URL =
            "jdbc:mysql://localhost:3306/"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Manila";
    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/" + DEFAULT_DATABASE_NAME
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Manila";
    private static final String DEFAULT_USERNAME = "root";
    private static final String DEFAULT_PASSWORD = "";
    private static volatile boolean databaseEnsured;

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException("MySQL JDBC driver is not available.", exception);
        }

        ensureDatabaseExists();

        return DriverManager.getConnection(
                System.getProperty("sms.db.url", DEFAULT_URL),
                System.getProperty("sms.db.user", DEFAULT_USERNAME),
                System.getProperty("sms.db.password", DEFAULT_PASSWORD)
        );
    }

    public static Connection connect() {
        try {
            return getConnection();
        } catch (Exception exception) {
            return null;
        }
    }

    private static synchronized void ensureDatabaseExists() throws SQLException {
        if (databaseEnsured) {
            return;
        }

        try (Connection connection = DriverManager.getConnection(
                System.getProperty("sms.db.baseUrl", BASE_URL),
                System.getProperty("sms.db.user", DEFAULT_USERNAME),
                System.getProperty("sms.db.password", DEFAULT_PASSWORD));
             java.sql.Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DEFAULT_DATABASE_NAME);
            databaseEnsured = true;
        }
    }
}
