package com.school.gestion.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DB_URL = System.getenv("GESTION_DB_URL") != null
        ? System.getenv("GESTION_DB_URL")
        : "jdbc:sqlserver://localhost:1433;databaseName=GestionScolaire;encrypt=true;trustServerCertificate=true";
    private static final String DB_USER = System.getenv("GESTION_DB_USER") != null
        ? System.getenv("GESTION_DB_USER")
        : "sa";
    private static final String DB_PASSWORD = System.getenv("GESTION_DB_PASSWORD") != null
        ? System.getenv("GESTION_DB_PASSWORD")
        : "admin123";

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
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
