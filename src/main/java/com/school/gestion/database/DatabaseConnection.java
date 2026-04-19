package com.school.gestion.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DB_URL = System.getenv("GESTION_DB_URL") != null
        ? System.getenv("GESTION_DB_URL")
        : "jdbc:mysql://localhost:3306/gestion_scolaire?serverTimezone=UTC";
    private static final String DB_USER = System.getenv("GESTION_DB_USER") != null
        ? System.getenv("GESTION_DB_USER")
        : "root";
    private static final String DB_PASSWORD = System.getenv("GESTION_DB_PASSWORD") != null
        ? System.getenv("GESTION_DB_PASSWORD")
        : "";

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
