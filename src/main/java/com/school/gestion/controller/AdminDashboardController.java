package com.school.gestion.controller;

import com.school.gestion.model.*;
import com.school.gestion.service.SchoolService;
import com.school.gestion.util.AlertUtils;
import com.school.gestion.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.school.gestion.database.DatabaseConnection;
import javafx.collections.FXCollections;

public class AdminDashboardController {

    @FXML private Label headerTitle;
    @FXML private Label userLabel;
    @FXML private VBox contentArea;

    @FXML
    private void initialize() {
        userLabel.setText("Administrateur: " + SessionManager.getCurrentUser().getUsername());
        showDashboard();
    }

    private void setActiveNav(Button btn) {
        contentArea.getChildren().clear();
    }

    @FXML
    private void navigateToDashboard() {
        headerTitle.setText("Tableau de bord");
        showDashboard();
    }

    @FXML
    private void navigateToEleves() {
        headerTitle.setText("Gestion des Élèves");
        showElevesView();
    }

    @FXML
    private void navigateToEnseignants() {
        headerTitle.setText("Gestion des Enseignants");
        showEnseignantsView();
    }

    @FXML
    private void navigateToClasses() {
        headerTitle.setText("Niveaux et Classes");
        showClassesView();
    }

    @FXML
    private void navigateToMatieres() {
        headerTitle.setText("Matières");
        showMatieresView();
    }

    @FXML
    private void navigateToInscriptions() {
        headerTitle.setText("Inscriptions et Affectations");
        showInscriptionsView();
    }

    @FXML
    private void navigateToNotes() {
        headerTitle.setText("Saisie des Notes");
        showNotesView();
    }

    @FXML
    private void navigateToBulletins() {
        headerTitle.setText("Génération des Bulletins");
        showBulletinsView();
    }

    @FXML
    private void handleLogout() {
        SessionManager.logout();
    }

    private void showDashboard() {
        contentArea.getChildren().clear();
        AnneeScolaire annee = SchoolService.getActiveAnneeScolaire();

        HBox statsRow = new HBox(20);
        Label elevesCount = new Label("0");
        Label classesCount = new Label("0");
        Label enseignantsCount = new Label("0");

        for (Eleve e : SchoolService.getAllEleves()) elevesCount.setText(String.valueOf(Integer.parseInt(elevesCount.getText()) + 1));
        for (Classe c : SchoolService.getAllClasses()) classesCount.setText(String.valueOf(Integer.parseInt(classesCount.getText()) + 1));
        for (Enseignant ens : SchoolService.getAllEnseignants()) enseignantsCount.setText(String.valueOf(Integer.parseInt(enseignantsCount.getText()) + 1));

        statsRow.getChildren().addAll(
            createStatCard("🎓  Total Élèves", elevesCount.getText(), "#4361ee"),
            createStatCard("🏫  Total Classes", classesCount.getText(), "#10b981"),
            createStatCard("👨‍🏫  Total Enseignants", enseignantsCount.getText(), "#f59e0b")
        );

        VBox welcomeBox = new VBox(10);
        Label welcome = new Label("Bienvenue dans le système de gestion scolaire");
        welcome.setStyle("-fx-font-size: 24px; -fx-text-fill: #1a237e; -fx-font-weight: bold;");
        Label subLabel = new Label("Année scolaire: " + (annee != null ? annee.getAnnee() : "Non définie"));
        subLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #757575;");
        welcomeBox.getChildren().addAll(welcome, subLabel);

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

    private void showElevesView() {
        VBox box = new VBox(15);
        HBox topBar = new HBox(10);
        Button addBtn = new Button("Ajouter un élève");
        addBtn.getStyleClass().add("btn");
        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher un élève...");
        searchField.setPrefWidth(300);
        topBar.getChildren().addAll(addBtn, searchField);

        TableView<Eleve> table = new TableView<>();
        table.getColumns().addAll(
            createColumn("Matricule", "matricule", 100),
            createColumn("Nom", "nom", 120),
            createColumn("Prénom", "prenom", 120),
            createColumn("Date Naissance", "dateNaissance", 120),
            createColumn("Sexe", "sexe", 50),
            createColumn("Niveau", "niveau", 100)
        );
        table.setItems(javafx.collections.FXCollections.observableArrayList(SchoolService.getAllEleves()));

        addBtn.setOnAction(e -> showAddEleveDialog());
        box.getChildren().addAll(topBar, table);
        contentArea.getChildren().setAll(box);
    }

    private void showEnseignantsView() {
        VBox box = new VBox(15);
        HBox topBar = new HBox(10);
        Button addBtn = new Button("Ajouter un enseignant");
        addBtn.getStyleClass().add("btn");
        addBtn.setOnAction(e -> showAddEnseignantDialog());
        topBar.getChildren().addAll(addBtn);

        TableView<Enseignant> table = new TableView<>();
        table.getColumns().addAll(
            createColumn("Matricule", "matricule", 100),
            createColumn("Nom", "nom", 120),
            createColumn("Prénom", "prenom", 120),
            createColumn("Grade", "grade", 150),
            createColumn("Téléphone", "telephone", 120)
        );
        table.setItems(javafx.collections.FXCollections.observableArrayList(SchoolService.getAllEnseignants()));
        box.getChildren().addAll(topBar, table);
        contentArea.getChildren().setAll(box);
    }

    private void showClassesView() {
        VBox box = new VBox(15);
        HBox topBar = new HBox(10);
        ComboBox<String> niveauCombo = new ComboBox<>();
        niveauCombo.getItems().addAll(SchoolService.getAllNiveaux());
        niveauCombo.setPromptText("Sélectionner un niveau");
        Button addClasseBtn = new Button("Ajouter une classe");
        addClasseBtn.getStyleClass().add("btn");
        addClasseBtn.setOnAction(e -> showAddClasseDialog(niveauCombo.getValue()));

        Button addNiveauBtn = new Button("+ Nouveau Niveau");
        addNiveauBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-size: 12px;");
        addNiveauBtn.setOnAction(e -> showAddNiveauDialog());

        topBar.getChildren().addAll(new Label("Niveau:"), niveauCombo, addClasseBtn, addNiveauBtn);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        niveauCombo.setOnAction(e -> {
            if (niveauCombo.getValue() != null) {
                addClasseBtn.setOnAction(ev -> showAddClasseDialog(niveauCombo.getValue()));
                List<Classe> classes = SchoolService.getClassesByNiveau(niveauCombo.getValue());
                grid.getChildren().clear();
                int col = 0, row = 0;
                for (Classe c : classes) {
                    VBox card = createClasseCard(c);
                    grid.add(card, col, row);
                    col++;
                    if (col == 3) { col = 0; row++; }
                }
            }
        });

        box.getChildren().addAll(topBar, grid);
        contentArea.getChildren().setAll(box);
    }

    private void showAddClasseDialog(String niveauDefaut) {
        Dialog<Classe> dialog = new Dialog<>();
        dialog.setTitle("Ajouter une classe");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        TextField nom = new TextField();
        TextField capacite = new TextField();
        capacite.setText("20");
        ComboBox<String> niveauCombo = new ComboBox<>();
        niveauCombo.getItems().addAll(SchoolService.getAllNiveaux());
        niveauCombo.setValue(niveauDefaut);

        grid.add(new Label("Nom de la classe:"), 0, 0); grid.add(nom, 1, 0);
        grid.add(new Label("Niveau:"), 0, 1); grid.add(niveauCombo, 1, 1);
        grid.add(new Label("Capacité (max 20):"), 0, 2); grid.add(capacite, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                Classe c = new Classe();
                c.setNom(nom.getText());
                c.setNiveau(niveauCombo.getValue());
                try {
                    c.setCapacite(Integer.parseInt(capacite.getText()));
                } catch (NumberFormatException ex) {
                    c.setCapacite(20);
                }
                return c;
            }
            return null;
        });

        var result = dialog.showAndWait();
        result.ifPresent(classe -> {
            if (SchoolService.saveClasse(classe)) {
                AlertUtils.showInfo("Succès", "Classe ajoutée avec succès");
                navigateToClasses();
            } else {
                AlertUtils.showError("Erreur", "Impossible d'ajouter la classe");
            }
        });
    }

    private void showAddNiveauDialog() {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un niveau");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        TextField libelle = new TextField();
        TextField libelleCourt = new TextField();

        grid.add(new Label("Libellé (ex: 1ère Année):"), 0, 0); grid.add(libelle, 1, 0);
        grid.add(new Label("Abrégé (ex: 1A):"), 0, 1); grid.add(libelleCourt, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                return new String[]{libelle.getText(), libelleCourt.getText()};
            }
            return null;
        });

        var result = dialog.showAndWait();
        result.ifPresent(data -> {
            if (SchoolService.saveNiveau(data[0], data[1])) {
                AlertUtils.showInfo("Succès", "Niveau ajouté avec succès");
                navigateToClasses();
            } else {
                AlertUtils.showError("Erreur", "Impossible d'ajouter le niveau");
            }
        });
    }

    private VBox createClasseCard(Classe classe) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(15));
        card.setPrefWidth(200);

        Label nomLabel = new Label(classe.getNomComplet());
        nomLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1a237e;");

        ProgressBar bar = new ProgressBar(classe.getRemplissage());
        bar.setPrefWidth(180);
        String barColor = classe.getRemplissage() > 0.9 ? "#f44336" : classe.getRemplissage() > 0.7 ? "#ff9800" : "#4caf50";
        bar.setStyle("-fx-accent: " + barColor + ";");

        Label capacityLabel = new Label(classe.getEffectifActuel() + "/" + classe.getCapacite() + " élèves");
        capacityLabel.getStyleClass().add("capacity-indicator");

        card.getChildren().addAll(nomLabel, bar, capacityLabel);
        return card;
    }

    private void showMatieresView() {
        VBox box = new VBox(15);
        HBox topBar = new HBox(10);
        Button addBtn = new Button("Ajouter une matière");
        addBtn.getStyleClass().add("btn");
        addBtn.setOnAction(e -> showAddMatiereDialog());
        topBar.getChildren().addAll(addBtn);

        TableView<Matiere> table = new TableView<>();
        table.getColumns().addAll(
            createColumn("Code", "code", 80),
            createColumn("Libellé", "libelle", 250),
            createColumn("Coefficient", "coefficient", 100)
        );
        table.setItems(javafx.collections.FXCollections.observableArrayList(SchoolService.getAllMatieres()));
        box.getChildren().addAll(topBar, table);
        contentArea.getChildren().setAll(box);
    }

    private void showAddMatiereDialog() {
        Dialog<Matiere> dialog = new Dialog<>();
        dialog.setTitle("Ajouter une matière");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        TextField code = new TextField();
        TextField libelle = new TextField();
        TextField coefficient = new TextField();
        coefficient.setText("1.0");

        grid.add(new Label("Code:"), 0, 0); grid.add(code, 1, 0);
        grid.add(new Label("Libellé:"), 0, 1); grid.add(libelle, 1, 1);
        grid.add(new Label("Coefficient:"), 0, 2); grid.add(coefficient, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                Matiere m = new Matiere();
                m.setCode(code.getText().toUpperCase());
                m.setLibelle(libelle.getText());
                try {
                    m.setCoefficient(Double.parseDouble(coefficient.getText()));
                } catch (NumberFormatException ex) {
                    m.setCoefficient(1.0);
                }
                return m;
            }
            return null;
        });

        var result = dialog.showAndWait();
        result.ifPresent(matiere -> {
            if (SchoolService.saveMatiere(matiere)) {
                AlertUtils.showInfo("Succès", "Matière ajoutée avec succès");
                navigateToMatieres();
            } else {
                AlertUtils.showError("Erreur", "Impossible d'ajouter la matière");
            }
        });
    }

    private void showInscriptionsView() {
        VBox box = new VBox(15);
        HBox topBar = new HBox(15);

        ComboBox<String> niveauCombo = new ComboBox<>();
        niveauCombo.getItems().addAll(SchoolService.getAllNiveaux());
        niveauCombo.setPromptText("Niveau");

        ComboBox<Classe> classeCombo = new ComboBox<>();
        classeCombo.setPromptText("Classe");

        Label selectedClasseId = new Label("");

        niveauCombo.setOnAction(e -> {
            if (niveauCombo.getValue() != null) {
                classeCombo.getItems().clear();
                classeCombo.getItems().addAll(SchoolService.getClassesByNiveau(niveauCombo.getValue()));
            }
        });

        topBar.getChildren().addAll(new Label("Niveau:"), niveauCombo, new Label("Classe:"), classeCombo);

        HBox splitPane = new HBox(20);

        VBox leftPanel = new VBox(10);
        Label leftTitle = new Label("Élèves non inscrits");
        TableView<Eleve> leftTable = new TableView<>();
        leftTable.getColumns().add(createColumn("Nom", "nom", 150));
        leftTable.getColumns().add(createColumn("Prénom", "prenom", 150));
        Button assignBtn = new Button("Affecter à la classe");
        assignBtn.getStyleClass().add("btn");
        leftPanel.getChildren().addAll(leftTitle, leftTable, assignBtn);

        VBox rightPanel = new VBox(10);
        Label rightTitle = new Label("Classes disponibles");
        GridPane classGrid = new GridPane();
        classGrid.setHgap(10);
        classGrid.setVgap(10);
        rightPanel.getChildren().addAll(rightTitle, classGrid);

        classeCombo.setOnAction(e -> {
            if (classeCombo.getValue() != null) {
                selectedClasseId.setText(String.valueOf(classeCombo.getValue().getIdClasse()));
                String niveau = niveauCombo.getValue();
                int idAnnee = SchoolService.getActiveAnneeScolaire().getIdAnnee();
                leftTable.setItems(javafx.collections.FXCollections.observableArrayList(
                    SchoolService.getElevesNonInscrits(idAnnee, niveau)));

                classGrid.getChildren().clear();
                for (Classe c : SchoolService.getClassesByNiveau(niveau)) {
                    VBox card = createClasseCard(c);
                    card.setOnMouseClicked(ev -> {
                        assignBtn.setOnAction(ev2 -> {
                            Eleve selectedEleve = leftTable.getSelectionModel().getSelectedItem();
                            if (selectedEleve != null) {
                                int classeId = c.getIdClasse();
                                if (SchoolService.inscription(selectedEleve.getMatricule(), idAnnee, classeId)) {
                                    AlertUtils.showInfo("Succès", "Élève inscrit avec succès");
                                    leftTable.getItems().remove(selectedEleve);
                                } else {
                                    AlertUtils.showError("Erreur", "Capacité maximale dépassée (20 élèves)");
                                }
                            }
                        });
                    });
                    classGrid.getChildren().add(card);
                }
            }
        });

        splitPane.getChildren().addAll(leftPanel, rightPanel);
        box.getChildren().addAll(topBar, splitPane);
        contentArea.getChildren().setAll(box);
    }

    private void showNotesView() {
        showNotesViewForAdmin();
    }

    private void showNotesViewForAdmin() {
        VBox box = new VBox(15);
        HBox topBar = new HBox(15);

        ComboBox<String> niveauCombo = new ComboBox<>();
        niveauCombo.getItems().addAll(SchoolService.getAllNiveaux());
        niveauCombo.setPromptText("Niveau");

        ComboBox<Classe> classeCombo = new ComboBox<>();
        classeCombo.setPromptText("Classe");

        ComboBox<Matiere> matiereCombo = new ComboBox<>();
        matiereCombo.getItems().addAll(SchoolService.getAllMatieres());
        matiereCombo.setPromptText("Matière");

        ComboBox<Integer> trimestreCombo = new ComboBox<>();
        trimestreCombo.getItems().addAll(1, 2, 3);
        trimestreCombo.setPromptText("Trimestre");

        TableView<NoteEntry> table = new TableView<>();
        TableColumn<NoteEntry, String> elevCol = new TableColumn<>("Élève");
        elevCol.setCellValueFactory(data -> data.getValue().nomProperty());
        TableColumn<NoteEntry, Double> devoirCol = new TableColumn<>("Note Devoir");
        deberCol.setCellValueFactory(data -> data.getValue().devoirProperty().asObject());
        TableColumn<NoteEntry, Double> examCol = new TableColumn<>("Note Examen");
        examCol.setCellValueFactory(data -> data.getValue().examProperty().asObject());
        TableColumn<NoteEntry, Double> compCol = new TableColumn<>("Note Composition");
        compCol.setCellValueFactory(data -> data.getValue().compProperty().asObject());
        table.getColumns().addAll(elevCol, devoirCol, examCol, compCol);

        Button saveBtn = new Button("Enregistrer les notes");
        saveBtn.getStyleClass().add("btn");

        niveauCombo.setOnAction(e -> {
            if (niveauCombo.getValue() != null) {
                classeCombo.getItems().clear();
                classeCombo.getItems().addAll(SchoolService.getClassesByNiveau(niveauCombo.getValue()));
            }
        });

        classeCombo.setOnAction(e -> loadStudentsForNotes(table, classeCombo, matiereCombo, trimestreCombo));
        matiereCombo.setOnAction(e -> loadStudentsForNotes(table, classeCombo, matiereCombo, trimestreCombo));
        trimestreCombo.setOnAction(e -> loadStudentsForNotes(table, classeCombo, matiereCombo, trimestreCombo));

        saveBtn.setOnAction(e -> saveGradesAdmin(table, classeCombo, matiereCombo, trimestreCombo));

        topBar.getChildren().addAll(
            new Label("Niveau:"), niveauCombo,
            new Label("Classe:"), classeCombo,
            new Label("Matière:"), matiereCombo,
            new Label("Trimestre:"), trimestreCombo
        );

        box.getChildren().addAll(topBar, table, saveBtn);
        contentArea.getChildren().setAll(box);
    }

    private void loadStudentsForNotes(TableView<NoteEntry> table, ComboBox<Classe> classeCombo, ComboBox<Matiere> matiereCombo, ComboBox<Integer> trimestreCombo) {
        if (classeCombo.getValue() == null || matiereCombo.getValue() == null || trimestreCombo.getValue() == null) {
            return;
        }
        
        AnneeScolaire annee = SchoolService.getActiveAnneeScolaire();
        if (annee == null) return;

        List<NoteEntry> entries = new ArrayList<>();
        String query = "SELECT e.matricule, e.nom, e.prenom, n.noteDevoir, n.noteExamens, n.noteComposition " +
                      "FROM ELEVE e " +
                      "JOIN INSCRIPTION i ON e.matricule = i.matricule " +
                      "LEFT JOIN NOTE n ON e.matricule = n.matricule AND n.idClasse = i.idClasse AND n.codeMatiere = ? AND n.trimestre = ? " +
                      "WHERE i.idClasse = ? AND i.idAnnee = ? AND i.statut = 'ACTIF'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, matiereCombo.getValue().getCode());
            stmt.setInt(2, trimestreCombo.getValue());
            stmt.setInt(3, classeCombo.getValue().getIdClasse());
            stmt.setInt(4, annee.getIdAnnee());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                NoteEntry entry = new NoteEntry(rs.getString("prenom") + " " + rs.getString("nom"));
                if (rs.getObject("noteDevoir") != null) entry.setDevoir(rs.getDouble("noteDevoir"));
                if (rs.getObject("noteExamens") != null) entry.setExam(rs.getDouble("noteExamens"));
                if (rs.getObject("noteComposition") != null) entry.setComp(rs.getDouble("noteComposition"));
                entry.setMatricule(rs.getString("matricule"));
                entries.add(entry);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        table.setItems(FXCollections.observableArrayList(entries));
    }

    private void saveGradesAdmin(TableView<NoteEntry> table, ComboBox<Classe> classeCombo, ComboBox<Matiere> matiereCombo, ComboBox<Integer> trimestreCombo) {
        if (classeCombo.getValue() == null || matiereCombo.getValue() == null || trimestreCombo.getValue() == null) {
            AlertUtils.showError("Erreur", "Veuillez sélectionner tous les champs");
            return;
        }

        AnneeScolaire annee = SchoolService.getActiveAnneeScolaire();
        if (annee == null) {
            AlertUtils.showError("Erreur", "Aucune année scolaire active");
            return;
        }

        int saved = 0;
        for (NoteEntry entry : table.getItems()) {
            Note note = new Note(entry.getMatricule(), annee.getIdAnnee(), classeCombo.getValue().getIdClasse(),
                                matiereCombo.getValue().getCode(), trimestreCombo.getValue());
            note.setMatriculeEnseignant(SessionManager.getCurrentUser().getUsername());
            note.setNoteDevoir(entry.getDevoir());
            note.setNoteExamens(entry.getExam());
            note.setNoteComposition(entry.getComp());
            if (SchoolService.saveNote(note)) saved++;
        }
        AlertUtils.showInfo("Succès", saved + " note(s) enregistrée(s)");
    }

    private void showBulletinsView() {
        VBox box = new VBox(20);
        HBox topBar = new HBox(15);

        ComboBox<String> niveauCombo = new ComboBox<>();
        niveauCombo.getItems().addAll(SchoolService.getAllNiveaux());
        niveauCombo.setPromptText("Niveau");

        ComboBox<Classe> classeCombo = new ComboBox<>();
        classeCombo.setPromptText("Classe");

        ComboBox<Integer> trimestreCombo = new ComboBox<>();
        trimestreCombo.getItems().addAll(1, 2, 3);
        trimestreCombo.setPromptText("Trimestre");

        Button generateBtn = new Button("Générer les bulletins");
        generateBtn.getStyleClass().add("btn");

        topBar.getChildren().addAll(
            new Label("Niveau:"), niveauCombo,
            new Label("Classe:"), classeCombo,
            new Label("Trimestre:"), trimestreCombo,
            generateBtn
        );

        box.getChildren().addAll(topBar);
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

    private void showAddEleveDialog() {
        Dialog<Eleve> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un élève");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        TextField matricule = new TextField(), nom = new TextField(), prenom = new TextField(), telephone = new TextField();
        DatePicker dob = new DatePicker();
        ComboBox<String> sexeCombo = new ComboBox<>();
        sexeCombo.getItems().addAll("M", "F");
        ComboBox<String> niveauCombo = new ComboBox<>();
        niveauCombo.getItems().addAll(SchoolService.getAllNiveaux());

        grid.add(new Label("Matricule:"), 0, 0); grid.add(matricule, 1, 0);
        grid.add(new Label("Nom:"), 0, 1); grid.add(nom, 1, 1);
        grid.add(new Label("Prénom:"), 0, 2); grid.add(prenom, 1, 2);
        grid.add(new Label("Date naissance:"), 0, 3); grid.add(dob, 1, 3);
        grid.add(new Label("Sexe:"), 0, 4); grid.add(sexeCombo, 1, 4);
        grid.add(new Label("Téléphone:"), 0, 5); grid.add(telephone, 1, 5);
        grid.add(new Label("Niveau:"), 0, 6); grid.add(niveauCombo, 1, 6);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                Eleve e = new Eleve();
                e.setMatricule(matricule.getText());
                e.setNom(nom.getText());
                e.setPrenom(prenom.getText());
                e.setDateNaissance(dob.getValue());
                e.setSexe(sexeCombo.getValue());
                e.setTelephone(telephone.getText());
                e.setNiveau(niveauCombo.getValue());
                return e;
            }
            return null;
        });

        var result = dialog.showAndWait();
        result.ifPresent(eleve -> {
            if (SchoolService.saveEleve(eleve)) {
                AlertUtils.showInfo("Succès", "Élève ajouté avec succès");
                navigateToEleves();
            } else {
                AlertUtils.showError("Erreur", "Impossible d'ajouter l'élève");
            }
        });
    }

    private void showAddEnseignantDialog() {
        Dialog<Enseignant> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un enseignant");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        TextField matricule = new TextField(), nom = new TextField(), prenom = new TextField(), telephone = new TextField(), grade = new TextField();
        DatePicker dob = new DatePicker();
        ComboBox<String> sexeCombo = new ComboBox<>();
        sexeCombo.getItems().addAll("M", "F");

        grid.add(new Label("Matricule:"), 0, 0); grid.add(matricule, 1, 0);
        grid.add(new Label("Nom:"), 0, 1); grid.add(nom, 1, 1);
        grid.add(new Label("Prénom:"), 0, 2); grid.add(prenom, 1, 2);
        grid.add(new Label("Date naissance:"), 0, 3); grid.add(dob, 1, 3);
        grid.add(new Label("Sexe:"), 0, 4); grid.add(sexeCombo, 1, 4);
        grid.add(new Label("Téléphone:"), 0, 5); grid.add(telephone, 1, 5);
        grid.add(new Label("Grade:"), 0, 6); grid.add(grade, 1, 6);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                Enseignant en = new Enseignant();
                en.setMatricule(matricule.getText());
                en.setNom(nom.getText());
                en.setPrenom(prenom.getText());
                en.setDateNaissance(dob.getValue());
                en.setSexe(sexeCombo.getValue());
                en.setTelephone(telephone.getText());
                en.setGrade(grade.getText());
                return en;
            }
            return null;
        });

        var result = dialog.showAndWait();
        result.ifPresent(enseignant -> {
            if (SchoolService.saveEnseignant(enseignant)) {
                AlertUtils.showInfo("Succès", "Enseignant ajouté avec succès");
                navigateToEnseignants();
            } else {
                AlertUtils.showError("Erreur", "Impossible d'ajouter l'enseignant");
            }
        });
    }

    public static class NoteEntry {
        private String nom;
        private String matricule;
        private final javafx.beans.property.DoubleProperty devoir = new javafx.beans.property.SimpleDoubleProperty();
        private final javafx.beans.property.DoubleProperty exam = new javafx.beans.property.SimpleDoubleProperty();
        private final javafx.beans.property.DoubleProperty comp = new javafx.beans.property.SimpleDoubleProperty();

        public NoteEntry(String nom) { this.nom = nom; }
        public String getNom() { return nom; }
        public void setNom(String nom) { this.nom = nom; }
        public String getMatricule() { return matricule; }
        public void setMatricule(String matricule) { this.matricule = matricule; }
        public javafx.beans.property.StringProperty nomProperty() { return new javafx.beans.property.SimpleStringProperty(nom); }
        public double getDevoir() { return devoir.get(); }
        public void setDevoir(double v) { devoir.set(v); }
        public javafx.beans.property.DoubleProperty devoirProperty() { return devoir; }
        public double getExam() { return exam.get(); }
        public void setExam(double v) { exam.set(v); }
        public javafx.beans.property.DoubleProperty examProperty() { return exam; }
        public double getComp() { return comp.get(); }
        public void setComp(double v) { comp.set(v); }
        public javafx.beans.property.DoubleProperty compProperty() { return comp; }
    }
}
