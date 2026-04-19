package com.school.gestion.controller;

import com.school.gestion.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs");
            return;
        }

        var user = SessionManager.authenticate(username, password);
        if (user != null) {
            SessionManager.setCurrentUser(user);
            navigateToDashboard(user.isAdmin());
        } else {
            showError("Identifiants incorrects");
        }
    }

    private void navigateToDashboard(boolean isAdmin) {
        try {
            String fxmlPath = isAdmin ? "/com/school/gestion/fxml/AdminDashboard.fxml"
                                     : "/com/school/gestion/fxml/TeacherDashboard.fxml";
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = SessionManager.getPrimaryStage();
            Scene scene = new Scene(root, 1200, 700);
            scene.getStylesheets().add(getClass().getResource("/com/school/gestion/css/styles.css").toExternalForm());
            stage.setTitle(isAdmin ? "Gestion Scolaire - Administrateur" : "Gestion Scolaire - Enseignant");
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
