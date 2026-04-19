package com.school.gestion;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.school.gestion.util.SessionManager;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        SessionManager.setPrimaryStage(stage);
        Parent root = FXMLLoader.load(getClass().getResource("/com/school/gestion/fxml/LoginView.fxml"));
        Scene scene = new Scene(root, 400, 350);
        scene.getStylesheets().add(getClass().getResource("/com/school/gestion/css/styles.css").toExternalForm());
        stage.setTitle("Gestion Scolaire - Connexion");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
