package com.school.gestion.controller;

import com.school.gestion.model.*;
import com.school.gestion.service.SchoolService;
import com.school.gestion.util.AlertUtils;
import com.school.gestion.util.SessionManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;

public class TeacherDashboardController {

    @FXML private Label headerTitle;
    @FXML private Label userLabel;
    @FXML private VBox contentArea;

    private String teacherMatricule;

    @FXML
    private void initialize() {
        teacherMatricule = "T001";
        showDashboard();
    }

    @FXML
    private void navigateToDashboard() {
        headerTitle.setText("Tableau de bord Enseignant");
        showDashboard();
    }

    @FXML
    private void navigateToNotes() {
        headerTitle.setText("Saisie des Notes");
        showNotesView();
    }

    @FXML
    private void navigateToMesEleves() {
        headerTitle.setText("Mes Élèves");
        showMesElevesView();
    }

    @FXML
    private void handleLogout() {
        SessionManager.logout();
    }

    private void showDashboard() {
        contentArea.getChildren().clear();
        VBox welcomeBox = new VBox(10);
        Label welcome = new Label("Bienvenue, Enseignant");
        welcome.setStyle("-fx-font-size: 24px; -fx-text-fill: #1a237e; -fx-font-weight: bold;");

        HBox statsRow = new HBox(20);
        statsRow.getChildren().addAll(
            createStatCard("🏫  Mes Classes", "3", "#4361ee"),
            createStatCard("📚  Mes Matières", "2", "#10b981"),
            createStatCard("🎓  Total Élèves", "45", "#f59e0b")
        );

        contentArea.getChildren().addAll(welcomeBox, statsRow);
    }

    private VBox createStatCard(String label, String value, String color) {
        VBox card = new VBox(10);
        card.getStyleClass().add("stat-card");
        card.setPadding(new Insets(20));
        Label lbl = new Label(label);
        lbl.getStyleClass().add("stat-label");
        Label val = new Label(value);
        val.getStyleClass().add("stat-value");
        val.setStyle("-fx-text-fill: " + color + ";");
        card.getChildren().addAll(lbl, val);
        return card;
    }

    private void showNotesView() {
        VBox box = new VBox(15);

        HBox topBar = new HBox(15);
        ComboBox<Integer> trimestreCombo = new ComboBox<>();
        trimestreCombo.getItems().addAll(1, 2, 3);
        trimestreCombo.setValue(1);
        trimestreCombo.setPromptText("Trimestre");

        ComboBox<Classe> classeCombo = new ComboBox<>();
        classeCombo.getItems().addAll(SchoolService.getAllClasses());
        classeCombo.setPromptText("Classe");

        ComboBox<Matiere> matiereCombo = new ComboBox<>();
        matiereCombo.getItems().addAll(SchoolService.getAllMatieres());
        matiereCombo.setPromptText("Matière");

        topBar.getChildren().addAll(
            new Label("Trimestre:"), trimestreCombo,
            new Label("Classe:"), classeCombo,
            new Label("Matière:"), matiereCombo
        );

        GridPane gradeGrid = new GridPane();
        gradeGrid.setHgap(10);
        gradeGrid.setVgap(10);
        gradeGrid.setPadding(new Insets(10));

        classeCombo.setOnAction(e -> {
            if (classeCombo.getValue() != null && matiereCombo.getValue() != null) {
                buildGradeEntryGrid(gradeGrid, classeCombo.getValue(), matiereCombo.getValue(),
                                   trimestreCombo.getValue(), SchoolService.getActiveAnneeScolaire());
            }
        });

        matiereCombo.setOnAction(e -> {
            if (classeCombo.getValue() != null && matiereCombo.getValue() != null) {
                buildGradeEntryGrid(gradeGrid, classeCombo.getValue(), matiereCombo.getValue(),
                                   trimestreCombo.getValue(), SchoolService.getActiveAnneeScolaire());
            }
        });

        trimestreCombo.setOnAction(e -> {
            if (classeCombo.getValue() != null && matiereCombo.getValue() != null) {
                buildGradeEntryGrid(gradeGrid, classeCombo.getValue(), matiereCombo.getValue(),
                                   trimestreCombo.getValue(), SchoolService.getActiveAnneeScolaire());
            }
        });

        Button saveBtn = new Button("Enregistrer les notes");
        saveBtn.getStyleClass().add("btn");
        saveBtn.setOnAction(e -> saveGrades(gradeGrid, classeCombo.getValue(), matiereCombo.getValue(),
                                           trimestreCombo.getValue()));

        box.getChildren().addAll(topBar, gradeGrid, saveBtn);
        contentArea.getChildren().setAll(box);
    }

    private void buildGradeEntryGrid(GridPane grid, Classe classe, Matiere matiere, int trimestre, AnneeScolaire annee) {
        grid.getChildren().clear();
        Label title = new Label("Saisie des notes - " + classe.getNomComplet() + " - " + matiere.getLibelle() + " - Trimestre " + trimestre);
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1a237e;");
        grid.add(title, 0, 0, 4, 1);

        Label h1 = new Label("Élève"); h1.setStyle("-fx-font-weight: bold;");
        Label h2 = new Label("Devoir (0-20)"); h2.setStyle("-fx-font-weight: bold;");
        Label h3 = new Label("Examen (0-20)"); h3.setStyle("-fx-font-weight: bold;");
        Label h4 = new Label("Composition (0-20)"); h4.setStyle("-fx-font-weight: bold;");
        grid.add(h1, 0, 1); grid.add(h2, 1, 1); grid.add(h3, 2, 1); grid.add(h4, 3, 1);

        AnneeScolaire anneeScolaire = SchoolService.getActiveAnneeScolaire();
        if (anneeScolaire == null) return;

        List<Inscription> inscriptions = new java.util.ArrayList<>();
        String query = "SELECT i.*, e.nom, e.prenom FROM INSCRIPTION i JOIN ELEVE e ON i.matricule = e.matricule " +
                      "WHERE i.idClasse = ? AND i.idAnnee = ? AND i.statut = 'ACTIF'";
        try (var conn = com.school.gestion.database.DatabaseConnection.getConnection();
             var stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, classe.getIdClasse());
            stmt.setInt(2, anneeScolaire.getIdAnnee());
            var rs = stmt.executeQuery();
            int row = 2;
            while (rs.next()) {
                Label nomLabel = new Label(rs.getString("prenom") + " " + rs.getString("nom"));
                TextField devoirField = new TextField();
                TextField examField = new TextField();
                TextField compField = new TextField();

                String matricule = rs.getString("matricule");
                Note existingNote = null;
                for (Note n : SchoolService.getNotesByClasseMatiereTrimestre(classe.getIdClasse(), matiere.getCode(), trimestre, anneeScolaire.getIdAnnee())) {
                    if (n.getMatricule().equals(matricule)) {
                        existingNote = n;
                        break;
                    }
                }
                if (existingNote != null) {
                    devoirField.setText(existingNote.getNoteDevoir() != null ? String.valueOf(existingNote.getNoteDevoir()) : "");
                    examField.setText(existingNote.getNoteExamens() != null ? String.valueOf(existingNote.getNoteExamens()) : "");
                    compField.setText(existingNote.getNoteComposition() != null ? String.valueOf(existingNote.getNoteComposition()) : "");
                }

                devoirField.setUserData(matricule);
                examField.setUserData(matricule);
                compField.setUserData(matricule);

                grid.add(nomLabel, 0, row);
                grid.add(devoirField, 1, row);
                grid.add(examField, 2, row);
                grid.add(compField, 3, row);
                row++;
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void saveGrades(GridPane grid, Classe classe, Matiere matiere, int trimestre) {
        AnneeScolaire annee = SchoolService.getActiveAnneeScolaire();
        if (annee == null || classe == null || matiere == null) {
            AlertUtils.showError("Erreur", "Veuillez sélectionner tous les champs");
            return;
        }

        int saved = 0;
        for (javafx.scene.Node node : grid.getChildren()) {
            if (node instanceof TextField tf && tf.getUserData() != null) {
                String matricule = (String) tf.getUserData();
                TextField devoirField = null, examField = null, compField = null;

                int col = GridPane.getColumnIndex(node) != null ? GridPane.getColumnIndex(node) : 0;
                int row = GridPane.getRowIndex(node) != null ? GridPane.getRowIndex(node) : 0;

                for (javafx.scene.Node sibling : grid.getChildren()) {
                    if (sibling instanceof TextField siblingTf &&
                        GridPane.getRowIndex(sibling) != null && GridPane.getRowIndex(sibling) == row &&
                        GridPane.getColumnIndex(sibling) != null) {
                        int siblingCol = GridPane.getColumnIndex(sibling);
                        if (siblingCol == 1) devoirField = siblingTf;
                        else if (siblingCol == 2) examField = siblingTf;
                        else if (siblingCol == 3) compField = siblingTf;
                    }
                }

                if (devoirField != null || examField != null || compField != null) {
                    Note note = new Note(matricule, annee.getIdAnnee(), classe.getIdClasse(),
                                        matiere.getCode(), trimestre);
                    note.setMatriculeEnseignant(teacherMatricule);

                    try {
                        if (devoirField != null && !devoirField.getText().isEmpty())
                            note.setNoteDevoir(Double.parseDouble(devoirField.getText()));
                        if (examField != null && !examField.getText().isEmpty())
                            note.setNoteExamens(Double.parseDouble(examField.getText()));
                        if (compField != null && !compField.getText().isEmpty())
                            note.setNoteComposition(Double.parseDouble(compField.getText()));
                    } catch (NumberFormatException ex) {
                        AlertUtils.showWarning("Avertissement", "Note invalide pour l'élève: " + matricule);
                        continue;
                    }

                    if (SchoolService.saveNote(note)) saved++;
                }
            }
        }
        AlertUtils.showInfo("Succès", saved + " note(s) enregistrée(s) avec succès");
    }

    private void showMesElevesView() {
        VBox box = new VBox(15);
        HBox topBar = new HBox(15);

        ComboBox<Classe> classeCombo = new ComboBox<>();
        classeCombo.getItems().addAll(SchoolService.getAllClasses());
        classeCombo.setPromptText("Sélectionner une classe");

        topBar.getChildren().addAll(new Label("Classe:"), classeCombo);

        TableView<Eleve> table = new TableView<>();
        table.getColumns().addAll(
            createColumn("Matricule", "matricule", 100),
            createColumn("Nom", "nom", 120),
            createColumn("Prénom", "prenom", 120),
            createColumn("Date Naissance", "dateNaissance", 120)
        );

        classeCombo.setOnAction(e -> {
            if (classeCombo.getValue() != null) {
                List<Eleve> eleves = new java.util.ArrayList<>();
                AnneeScolaire annee = SchoolService.getActiveAnneeScolaire();
                if (annee != null) {
                    String query = "SELECT e.* FROM ELEVE e JOIN INSCRIPTION i ON e.matricule = i.matricule " +
                                  "WHERE i.idClasse = ? AND i.idAnnee = ? AND i.statut = 'ACTIF'";
                    try (var conn = com.school.gestion.database.DatabaseConnection.getConnection();
                         var stmt = conn.prepareStatement(query)) {
                        stmt.setInt(1, classeCombo.getValue().getIdClasse());
                        stmt.setInt(2, annee.getIdAnnee());
                        var rs = stmt.executeQuery();
                        while (rs.next()) {
                            Eleve eleve = new Eleve();
                            eleve.setMatricule(rs.getString("matricule"));
                            eleve.setNom(rs.getString("nom"));
                            eleve.setPrenom(rs.getString("prenom"));
                            eleve.setDateNaissance(rs.getDate("dateNaissance").toLocalDate());
                            eleves.add(eleve);
                        }
                    } catch (Exception ex) { ex.printStackTrace(); }
                }
                table.setItems(javafx.collections.FXCollections.observableArrayList(eleves));
            }
        });

        box.getChildren().addAll(topBar, table);
        contentArea.getChildren().setAll(box);
    }

    private <T> TableColumn<T, ?> createColumn(String title, String property, double width) {
        TableColumn<T, ?> col = new TableColumn<>(title);
        col.setCellValueFactory(data -> {
            try {
                var method = data.getValue().getClass().getMethod("get" + property.substring(0,1).toUpperCase() + property.substring(1));
                Object val = method.invoke(data.getValue());
                return new javafx.beans.property.SimpleObjectProperty(val != null ? val : "");
            } catch (Exception e) { return new javafx.beans.property.SimpleObjectProperty(""); }
        });
        col.setPrefWidth(width);
        return col;
    }
}
