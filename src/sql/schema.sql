-- =====================================================
-- GESTION SCOLAIRE - Database Schema
-- SQL Server / Oracle Compatible DDL
-- =====================================================

-- Drop tables if exist (for development reset)
DROP TABLE IF EXISTS NOTE;
DROP TABLE IF EXISTS INSCRIPTION;
DROP TABLE IF EXISTS CLASSE_ENSEIGNANT;
DROP TABLE IF EXISTS MATIERE_CLASSE;
DROP TABLE IF EXISTS CLASSE;
DROP TABLE IF EXISTS NIVEAU;
DROP TABLE IF EXISTS MATIERE;
DROP TABLE IF EXISTS ENSEIGNANT;
DROP TABLE IF EXISTS ELEVE;
DROP TABLE IF EXISTS ANNEE_SCOLAIRE;
DROP TABLE IF EXISTS UTILISATEUR;

-- =====================================================
-- Table: ANNEE_SCOLAIRE
-- =====================================================
CREATE TABLE ANNEE_SCOLAIRE (
    idAnnee INT PRIMARY KEY IDENTITY(1,1),
    annee VARCHAR(9) NOT NULL UNIQUE,
    dateDebut DATE NOT NULL,
    dateFin DATE NOT NULL,
    estActive BIT DEFAULT 0
);

-- =====================================================
-- Table: NIVEAU
-- =====================================================
CREATE TABLE NIVEAU (
    idNiveau INT PRIMARY KEY IDENTITY(1,1),
    libelle VARCHAR(50) NOT NULL UNIQUE,
    libelleCourt VARCHAR(10) NOT NULL
);

-- =====================================================
-- Table: MATIERE
-- =====================================================
CREATE TABLE MATIERE (
    code VARCHAR(10) PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE,
    coefficient DECIMAL(4,2) NOT NULL DEFAULT 1.0
);

-- =====================================================
-- Table: UTILISATEUR (Login credentials)
-- =====================================================
CREATE TABLE UTILISATEUR (
    id INT PRIMARY KEY IDENTITY(1,1),
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'ENSEIGNANT')),
    idPersonne INT NOT NULL,
    estActif BIT DEFAULT 1
);

-- =====================================================
-- Table: ELEVE
-- =====================================================
CREATE TABLE ELEVE (
    matricule VARCHAR(20) PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    dateNaissance DATE NOT NULL,
    sexe VARCHAR(1) NOT NULL CHECK (sexe IN ('M', 'F')),
    adresse VARCHAR(255),
    telephone VARCHAR(20),
    photo VARBINARY(MAX),
    niveau VARCHAR(50) NOT NULL,
    idUtilisateur INT,
    dateInscription DATE DEFAULT GETDATE()
);

-- =====================================================
-- Table: ENSEIGNANT
-- =====================================================
CREATE TABLE ENSEIGNANT (
    matricule VARCHAR(20) PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    dateNaissance DATE NOT NULL,
    sexe VARCHAR(1) NOT NULL CHECK (sexe IN ('M', 'F')),
    adresse VARCHAR(255),
    telephone VARCHAR(20),
    photo VARBINARY(MAX),
    grade VARCHAR(50) NOT NULL,
    idUtilisateur INT
);

-- =====================================================
-- Table: CLASSE
-- =====================================================
CREATE TABLE CLASSE (
    idClasse INT PRIMARY KEY IDENTITY(1,1),
    nom VARCHAR(20) NOT NULL,
    niveau VARCHAR(50) NOT NULL,
    capacite INT NOT NULL DEFAULT 20 CHECK (capacite >= 1 AND capacite <= 40),
    nomComplet VARCHAR(100),
    idEnseignant INT,
    FOREIGN KEY (niveau) REFERENCES NIVEAU(libelle),
    FOREIGN KEY (idEnseignant) REFERENCES ENSEIGNANT(matricule),
    UNIQUE (nom, niveau)
);

-- =====================================================
-- Table: INSCRIPTION
-- =====================================================
CREATE TABLE INSCRIPTION (
    idInscription INT PRIMARY KEY IDENTITY(1,1),
    matricule VARCHAR(20) NOT NULL,
    idAnnee INT NOT NULL,
    idClasse INT NOT NULL,
    dateInscription DATE DEFAULT GETDATE(),
    statut VARCHAR(20) DEFAULT 'ACTIF',
    FOREIGN KEY (matricule) REFERENCES ELEVE(matricule),
    FOREIGN KEY (idAnnee) REFERENCES ANNEE_SCOLAIRE(idAnnee),
    FOREIGN KEY (idClasse) REFERENCES CLASSE(idClasse),
    UNIQUE (matricule, idAnnee)
);

-- =====================================================
-- Table: CLASSE_ENSEIGNANT (Enseignant-Classes relationship)
-- =====================================================
CREATE TABLE CLASSE_ENSEIGNANT (
    id INT PRIMARY KEY IDENTITY(1,1),
    matriculeEnseignant VARCHAR(20) NOT NULL,
    idClasse INT NOT NULL,
    codeMatiere VARCHAR(10) NOT NULL,
    FOREIGN KEY (matriculeEnseignant) REFERENCES ENSEIGNANT(matricule),
    FOREIGN KEY (idClasse) REFERENCES CLASSE(idClasse),
    FOREIGN KEY (codeMatiere) REFERENCES MATIERE(code),
    UNIQUE (matriculeEnseignant, idClasse, codeMatiere)
);

-- =====================================================
-- Table: NOTE
-- =====================================================
CREATE TABLE NOTE (
    idNote INT PRIMARY KEY IDENTITY(1,1),
    matricule VARCHAR(20) NOT NULL,
    idAnnee INT NOT NULL,
    idClasse INT NOT NULL,
    codeMatiere VARCHAR(10) NOT NULL,
    trimestre INT NOT NULL CHECK (trimestre BETWEEN 1 AND 3),
    noteDevoir DECIMAL(4,2) CHECK (noteDevoir IS NULL OR (noteDevoir >= 0 AND noteDevoir <= 20)),
    noteExamens DECIMAL(4,2) CHECK (noteExamens IS NULL OR (noteExamens >= 0 AND noteExamens <= 20)),
    noteComposition DECIMAL(4,2) CHECK (noteComposition IS NULL OR (noteComposition >= 0 AND noteComposition <= 20)),
    dateSaisie DATE DEFAULT GETDATE(),
    matriculeEnseignant VARCHAR(20),
    FOREIGN KEY (matricule) REFERENCES ELEVE(matricule),
    FOREIGN KEY (idAnnee) REFERENCES ANNEE_SCOLAIRE(idAnnee),
    FOREIGN KEY (idClasse) REFERENCES CLASSE(idClasse),
    FOREIGN KEY (codeMatiere) REFERENCES MATIERE(code),
    FOREIGN KEY (matriculeEnseignant) REFERENCES ENSEIGNANT(matricule),
    UNIQUE (matricule, idAnnee, idClasse, codeMatiere, trimestre)
);

-- =====================================================
-- Triggers for class capacity enforcement
-- =====================================================
CREATE TRIGGER trg_CheckClasseCapacity
ON INSCRIPTION
AFTER INSERT, UPDATE
AS
BEGIN
    IF EXISTS (
        SELECT 1 FROM INSCRIPTION i
        INNER JOIN inserted ins ON i.idClasse = ins.idClasse
        GROUP BY i.idClasse
        HAVING COUNT(*) > (SELECT capacite FROM CLASSE WHERE idClasse = ins.idClasse)
    )
    BEGIN
        RAISERROR('La capacité maximale de la classe est dépassée (20 élèves)', 16, 1);
        ROLLBACK TRANSACTION;
    END
END;

-- =====================================================
-- Insert sample data
-- =====================================================
INSERT INTO NIVEAU (libelle, libelleCourt) VALUES
('1ère Année', '1A'),
('2ème Année', '2A'),
('3ème Année', '3A');

INSERT INTO MATIERE (code, libelle, coefficient) VALUES
(' MATH', 'Mathématiques', 4.00),
(' PHY', 'Physique', 3.00),
(' FR', 'Français', 3.50),
(' HG', 'Histoire-Géographie', 2.00),
(' SVT', 'Sciences Naturelles', 2.50),
(' ANG', 'Anglais', 2.00);

INSERT INTO ANNEE_SCOLAIRE (annee, dateDebut, dateFin, estActive) VALUES
('2025-2026', '2025-10-01', '2026-06-30', 1);

-- Default Admin user (password: admin123)
INSERT INTO UTILISATEUR (username, password, role, idPersonne, estActif) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhVu', 'ADMIN', 0, 1);
