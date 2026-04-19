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
- **IDE**: Eclipse (recommandé) ou IntelliJ IDEA
- **Maven 3.8+**
- **Base de données**: XAMPP (MySQL / MariaDB) ou SQL Server
- **JavaFX 21**

## Installation (Eclipse & XAMPP)

### 1. Importer le projet dans Eclipse
1. Ouvrez Eclipse et allez dans **File > Import...**
2. Sélectionnez **Maven > Existing Maven Projects** et cliquez sur Next.
3. Parcourez votre système pour sélectionner le dossier `projet federee` (où se trouve le fichier `pom.xml`).
4. Cliquez sur **Finish**. Eclipse va télécharger toutes les dépendances automatiquement.

### 2. Configuration de la Base de Données (XAMPP)

1. Ouvrez **XAMPP Control Panel** et démarrez le module **MySQL** (et Apache si vous utilisez phpMyAdmin).
2. Ouvrez phpMyAdmin (http://localhost/phpmyadmin) ou votre client SQL préféré.
3. Créez une nouvelle base de données nommée `gestion_scolaire`.
4. Importez le fichier **`src/sql/schema_mysql.sql`** dans cette nouvelle base de données.
   - *Note: Ce script contient toutes les tables, triggers de capacité et les données de test.*

### 3. Exécution du Projet dans Eclipse

1. Faites un clic droit sur le fichier **`MainApp.java`** (`src/main/java/com/school/gestion/MainApp.java`).
2. Sélectionnez **Run As > Java Application**.
   - *Le projet est préconfiguré pour se connecter à `jdbc:mysql://localhost:3306/gestion_scolaire` avec l'utilisateur `root` et sans mot de passe (configuration XAMPP par défaut).*

---

## Configuration Alternative (SQL Server)

Si vous préférez utiliser SQL Server au lieu de XAMPP :
1. Importez le fichier `src/sql/schema.sql` dans SQL Server.
2. Modifiez les variables d'environnement ou le fichier `DatabaseConnection.java` pour utiliser le driver MSSQL.

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
