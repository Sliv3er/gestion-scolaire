package com.school.gestion.util;

import com.school.gestion.model.Utilisateur;
import com.school.gestion.database.DatabaseConnection;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SessionManager {
    private static Utilisateur currentUser;
    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setCurrentUser(Utilisateur user) {
        currentUser = user;
    }

    public static Utilisateur getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    public static boolean isEnseignant() {
        return currentUser != null && currentUser.isEnseignant();
    }

    public static void logout() {
        currentUser = null;
        Platform.exit();
    }

    public static Utilisateur authenticate(String username, String password) {
        String query = "SELECT * FROM UTILISATEUR WHERE username = ? AND estActif = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                if (BCrypt.checkpw(password, hashedPassword)) {
                    Utilisateur user = new Utilisateur();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(hashedPassword);
                    user.setRole(rs.getString("role"));
                    user.setIdPersonne(rs.getInt("idPersonne"));
                    user.setEstActif(rs.getBoolean("estActif"));
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
