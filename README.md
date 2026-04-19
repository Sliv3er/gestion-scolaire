# Gestion Scolaire - Système de Gestion Scolaire

Application JavaFX de gestion scolaire pour établissements d'enseignement.

## Fonctionnalités

### Espace Administrateur
- **Tableau de bord** - Statistiques globales (élèves, classes, enseignants)
- **Gestion des élèves** - Ajout, modification, recherche d'élèves
- **Gestion des enseignants** - Ajout et gestion des enseignants
- **Niveaux et Classes** - Création de classes avec capacité max 20 élèves
- **Inscriptions** - Affectation des élèves aux classes (contrôle de capacité automatique)
- **Saisie des notes** - Attribution des notes par trimestre
- **Génération des bulletins** - Création de bulletins de notes

### Espace Enseignant
- **Tableau de bord** - Vue d'ensemble de ses classes et matières
- **Saisie des notes** - Grille de saisie intuitive (Devoir, Examen, Composition)
- **Mes élèves** - Consultation des élèves par classe

## Architecture

```
src/
├── main/
│   ├── java/com/school/gestion/
│   │   ├── model/          # Objets métier (Eleve, Enseignant, Classe, Note, etc.)
│   │   ├── view/           # Vues FXML
│   │   ├── controller/     # Logique métier (Login, Admin, Teacher)
│   │   ├── service/       # Services JDBC (SchoolService)
│   │   ├── database/      # Connexion BD (DatabaseConnection)
│   │   └── util/          # Utilitaires (SessionManager, AlertUtils)
│   └── resources/
│       ├── com/school/gestion/
│       │   ├── css/       # Styles CSS
│       │   └── fxml/      # Fichiers FXML
│       └── images/
└── sql/
    └── schema.sql         # Script DDL (SQL Server)
```

## Prérequis

- **Java 17+**
- **Maven 3.8+**
- **SQL Server 2019+** (ou Azure SQL Database)
- **JavaFX 21**

## Installation

### 1. Configuration de la Base de Données

Exécutez le script SQL dans SQL Server Management Studio ou Azure Data Studio:

```sql
-- Créez d'abord la base de données
CREATE DATABASE GestionScolaire;
GO

-- Exécutez le contenu de src/sql/schema.sql
```

### 2. Configuration des Variables d'Environnement

```bash
# Windows
set GESTION_DB_URL=jdbc:sqlserver://localhost:1433;databaseName=GestionScolaire;encrypt=true;trustServerCertificate=true
set GESTION_DB_USER=sa
set GESTION_DB_PASSWORD=votre_mot_de_passe

# Linux/Mac
export GESTION_DB_URL=jdbc:sqlserver://localhost:1433;databaseName=GestionScolaire;encrypt=true;trustServerCertificate=true
export GESTION_DB_USER=sa
export GESTION_DB_PASSWORD=votre_mot_de_passe
```

### 3. Compilation et Exécution

```bash
# Compiler le projet
mvn clean compile

# Exécuter l'application
mvn javafx:run
```

### 4. Construction du JAR

```bash
mvn clean package
java -jar target/gestion-scolaire-1.0.0.jar
```

## Connexion par Défaut

| Rôle | Nom d'utilisateur | Mot de passe |
|------|------------------|--------------|
| Administrateur | `admin` | `admin123` |

## Règles Métier

1. **Capacité des classes** - Maximum 20 élèves par classe
2. **Rôles** - Administrateur et Enseignant (accès différencié)
3. **Années scolaires** - Données liées à l'année active
4. **Trimestres** - 3 trimestres par année (1, 2, 3)
5. **Notes** - Comprises entre 0 et 20

## Technologies

- **Frontend**: JavaFX 21, FXML, CSS3
- **Backend**: Java 17
- **Base de données**: SQL Server, JDBC
- **Sécurité**: BCrypt (hashing mots de passe)
- **Build**: Maven

## Structure de la Base de Données

| Table | Description |
|-------|-------------|
| `UTILISATEUR` | Comptes utilisateurs (admin/enseignant) |
| `ELEVE` | Informations élèves |
| `ENSEIGNANT` | Informations enseignants |
| `NIVEAU` | Niveaux scolaires (1ère, 2ème, 3ème année) |
| `CLASSE` | Classes avec capacité |
| `MATIERE` | Matières enseignées |
| `INSCRIPTION` | Inscriptions année/classe |
| `CLASSE_ENSEIGNANT` | Relation enseignant-matière-classe |
| `NOTE` | Notes par trimestre |
| `ANNEE_SCOLAIRE` | Années scolaires |

## Capture d'Écran

L'application dispose d'une interface moderne avec:
- Barre latérale de navigation
- Tableaux de données triables
- Indicateurs visuels de capacité
- Grilles de saisie ergonomiques

## Licence

Projet académique - Educational Use
