package com.school.gestion.service;

import com.school.gestion.database.DatabaseConnection;
import com.school.gestion.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SchoolService {

    public static List<Eleve> getAllEleves() {
        List<Eleve> eleves = new ArrayList<>();
        String query = "SELECT * FROM ELEVE ORDER BY nom, prenom";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                eleves.add(extractEleve(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eleves;
    }

    public static List<Eleve> getElevesByNiveau(String niveau) {
        List<Eleve> eleves = new ArrayList<>();
        String query = "SELECT * FROM ELEVE WHERE niveau = ? ORDER BY nom, prenom";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, niveau);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                eleves.add(extractEleve(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eleves;
    }

    public static List<Eleve> getElevesNonInscrits(int idAnnee, String niveau) {
        List<Eleve> eleves = new ArrayList<>();
        String query = "SELECT * FROM ELEVE e WHERE e.niveau = ? AND e.matricule NOT IN " +
                      "(SELECT i.matricule FROM INSCRIPTION i WHERE i.idAnnee = ?) ORDER BY e.nom, e.prenom";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, niveau);
            stmt.setInt(2, idAnnee);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                eleves.add(extractEleve(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eleves;
    }

    public static boolean saveEleve(Eleve eleve) {
        String query = "INSERT INTO ELEVE (matricule, nom, prenom, dateNaissance, sexe, adresse, telephone, niveau) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, eleve.getMatricule());
            stmt.setString(2, eleve.getNom());
            stmt.setString(3, eleve.getPrenom());
            stmt.setDate(4, Date.valueOf(eleve.getDateNaissance()));
            stmt.setString(5, eleve.getSexe());
            stmt.setString(6, eleve.getAdresse());
            stmt.setString(7, eleve.getTelephone());
            stmt.setString(8, eleve.getNiveau());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Enseignant> getAllEnseignants() {
        List<Enseignant> enseignants = new ArrayList<>();
        String query = "SELECT * FROM ENSEIGNANT ORDER BY nom, prenom";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                enseignants.add(extractEnseignant(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return enseignants;
    }

    public static boolean saveEnseignant(Enseignant enseignant) {
        String query = "INSERT INTO ENSEIGNANT (matricule, nom, prenom, dateNaissance, sexe, adresse, telephone, grade) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, enseignant.getMatricule());
            stmt.setString(2, enseignant.getNom());
            stmt.setString(3, enseignant.getPrenom());
            stmt.setDate(4, Date.valueOf(enseignant.getDateNaissance()));
            stmt.setString(5, enseignant.getSexe());
            stmt.setString(6, enseignant.getAdresse());
            stmt.setString(7, enseignant.getTelephone());
            stmt.setString(8, enseignant.getGrade());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Classe> getClassesByNiveau(String niveau) {
        List<Classe> classes = new ArrayList<>();
        String query = "SELECT c.*, (SELECT COUNT(*) FROM INSCRIPTION i WHERE i.idClasse = c.idClasse AND i.statut = 'ACTIF') as effectif " +
                      "FROM CLASSE c WHERE c.niveau = ? ORDER BY c.nom";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, niveau);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Classe classe = extractClasse(rs);
                classe.setEffectifActuel(rs.getInt("effectif"));
                classes.add(classe);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classes;
    }

    public static List<Classe> getAllClasses() {
        List<Classe> classes = new ArrayList<>();
        String query = "SELECT c.*, (SELECT COUNT(*) FROM INSCRIPTION i WHERE i.idClasse = c.idClasse AND i.statut = 'ACTIF') as effectif " +
                      "FROM CLASSE c ORDER BY c.niveau, c.nom";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Classe classe = extractClasse(rs);
                classe.setEffectifActuel(rs.getInt("effectif"));
                classes.add(classe);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classes;
    }

    public static boolean saveClasse(Classe classe) {
        String query = "INSERT INTO CLASSE (nom, niveau, capacite, nomComplet) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, classe.getNom());
            stmt.setString(2, classe.getNiveau());
            stmt.setInt(3, classe.getCapacite());
            stmt.setString(4, classe.getNomComplet());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean inscription(String matricule, int idAnnee, int idClasse) {
        String checkQuery = "SELECT COUNT(*) as count FROM INSCRIPTION WHERE idClasse = ? AND statut = 'ACTIF'";
        String insertQuery = "INSERT INTO INSCRIPTION (matricule, idAnnee, idClasse) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, idClasse);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt("count") >= 20) {
                return false;
            }
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setString(1, matricule);
            insertStmt.setInt(2, idAnnee);
            insertStmt.setInt(3, idClasse);
            return insertStmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Matiere> getAllMatieres() {
        List<Matiere> matieres = new ArrayList<>();
        String query = "SELECT * FROM MATIERE ORDER BY libelle";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Matiere m = new Matiere();
                m.setCode(rs.getString("code"));
                m.setLibelle(rs.getString("libelle"));
                m.setCoefficient(rs.getDouble("coefficient"));
                matieres.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matieres;
    }

    public static List<String> getAllNiveaux() {
        List<String> niveaux = new ArrayList<>();
        String query = "SELECT libelle FROM NIVEAU ORDER BY libelle";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                niveaux.add(rs.getString("libelle"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return niveaux;
    }

    public static AnneeScolaire getActiveAnneeScolaire() {
        String query = "SELECT * FROM ANNEE_SCOLAIRE WHERE estActive = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                AnneeScolaire a = new AnneeScolaire();
                a.setIdAnnee(rs.getInt("idAnnee"));
                a.setAnnee(rs.getString("annee"));
                a.setDateDebut(rs.getDate("dateDebut").toLocalDate());
                a.setDateFin(rs.getDate("dateFin").toLocalDate());
                a.setEstActive(rs.getBoolean("estActive"));
                return a;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Note> getNotesByClasseMatiereTrimestre(int idClasse, String codeMatiere, int trimestre, int idAnnee) {
        List<Note> notes = new ArrayList<>();
        String query = "SELECT * FROM NOTE WHERE idClasse = ? AND codeMatiere = ? AND trimestre = ? AND idAnnee = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idClasse);
            stmt.setString(2, codeMatiere);
            stmt.setInt(3, trimestre);
            stmt.setInt(4, idAnnee);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                notes.add(extractNote(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notes;
    }

    public static boolean saveNote(Note note) {
        String checkQuery = "SELECT idNote FROM NOTE WHERE matricule = ? AND idAnnee = ? AND idClasse = ? AND codeMatiere = ? AND trimestre = ?";
        String updateQuery = "UPDATE NOTE SET noteDevoir = ?, noteExamens = ?, noteComposition = ?, matriculeEnseignant = ?, dateSaisie = CURRENT_DATE WHERE matricule = ? AND idAnnee = ? AND idClasse = ? AND codeMatiere = ? AND trimestre = ?";
        String insertQuery = "INSERT INTO NOTE (matricule, idAnnee, idClasse, codeMatiere, trimestre, noteDevoir, noteExamens, noteComposition, matriculeEnseignant) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            boolean exists = false;
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, note.getMatricule());
                checkStmt.setInt(2, note.getIdAnnee());
                checkStmt.setInt(3, note.getIdClasse());
                checkStmt.setString(4, note.getCodeMatiere());
                checkStmt.setInt(5, note.getTrimestre());
                ResultSet rs = checkStmt.executeQuery();
                exists = rs.next();
            }

            if (exists) {
                try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                    stmt.setDouble(1, note.getNoteDevoir() != null ? note.getNoteDevoir() : 0);
                    stmt.setDouble(2, note.getNoteExamens() != null ? note.getNoteExamens() : 0);
                    stmt.setDouble(3, note.getNoteComposition() != null ? note.getNoteComposition() : 0);
                    stmt.setString(4, note.getMatriculeEnseignant());
                    stmt.setString(5, note.getMatricule());
                    stmt.setInt(6, note.getIdAnnee());
                    stmt.setInt(7, note.getIdClasse());
                    stmt.setString(8, note.getCodeMatiere());
                    stmt.setInt(9, note.getTrimestre());
                    return stmt.executeUpdate() > 0;
                }
            } else {
                try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                    stmt.setString(1, note.getMatricule());
                    stmt.setInt(2, note.getIdAnnee());
                    stmt.setInt(3, note.getIdClasse());
                    stmt.setString(4, note.getCodeMatiere());
                    stmt.setInt(5, note.getTrimestre());
                    stmt.setDouble(6, note.getNoteDevoir() != null ? note.getNoteDevoir() : 0);
                    stmt.setDouble(7, note.getNoteExamens() != null ? note.getNoteExamens() : 0);
                    stmt.setDouble(8, note.getNoteComposition() != null ? note.getNoteComposition() : 0);
                    stmt.setString(9, note.getMatriculeEnseignant());
                    return stmt.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static Eleve extractEleve(ResultSet rs) throws SQLException {
        Eleve e = new Eleve();
        e.setMatricule(rs.getString("matricule"));
        e.setNom(rs.getString("nom"));
        e.setPrenom(rs.getString("prenom"));
        Date dob = rs.getDate("dateNaissance");
        e.setDateNaissance(dob != null ? dob.toLocalDate() : null);
        e.setSexe(rs.getString("sexe"));
        e.setAdresse(rs.getString("adresse"));
        e.setTelephone(rs.getString("telephone"));
        e.setNiveau(rs.getString("niveau"));
        return e;
    }

    private static Enseignant extractEnseignant(ResultSet rs) throws SQLException {
        Enseignant en = new Enseignant();
        en.setMatricule(rs.getString("matricule"));
        en.setNom(rs.getString("nom"));
        en.setPrenom(rs.getString("prenom"));
        Date dob = rs.getDate("dateNaissance");
        en.setDateNaissance(dob != null ? dob.toLocalDate() : null);
        en.setSexe(rs.getString("sexe"));
        en.setAdresse(rs.getString("adresse"));
        en.setTelephone(rs.getString("telephone"));
        en.setGrade(rs.getString("grade"));
        return en;
    }

    private static Classe extractClasse(ResultSet rs) throws SQLException {
        Classe c = new Classe();
        c.setIdClasse(rs.getInt("idClasse"));
        c.setNom(rs.getString("nom"));
        c.setNiveau(rs.getString("niveau"));
        c.setCapacite(rs.getInt("capacite"));
        c.setNomComplet(rs.getString("nomComplet"));
        c.setMatriculeEnseignant(rs.getString("idEnseignant"));
        return c;
    }

    private static Note extractNote(ResultSet rs) throws SQLException {
        Note n = new Note();
        n.setIdNote(rs.getInt("idNote"));
        n.setMatricule(rs.getString("matricule"));
        n.setIdAnnee(rs.getInt("idAnnee"));
        n.setIdClasse(rs.getInt("idClasse"));
        n.setCodeMatiere(rs.getString("codeMatiere"));
        n.setTrimestre(rs.getInt("trimestre"));
        double devoir = rs.getDouble("noteDevoir");
        n.setNoteDevoir(rs.wasNull() ? null : devoir);
        double exam = rs.getDouble("noteExamens");
        n.setNoteExamens(rs.wasNull() ? null : exam);
        double comp = rs.getDouble("noteComposition");
        n.setNoteComposition(rs.wasNull() ? null : comp);
        n.setMatriculeEnseignant(rs.getString("matriculeEnseignant"));
        return n;
    }
}
