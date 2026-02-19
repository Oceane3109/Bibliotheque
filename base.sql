-- Suppression et création de la base
DROP DATABASE IF EXISTS bibliotheque;
CREATE DATABASE bibliotheque;
\c bibliotheque;

-- Table des utilisateurs
CREATE TABLE users (
    id_user SERIAL PRIMARY KEY,
    nom_utilisateur VARCHAR(50) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    date_creation DATE NOT NULL,
    est_admin BOOLEAN NOT NULL DEFAULT FALSE
);

-- Table des types d'adhérents
CREATE TABLE type_adherent (
    id_type_adherent SERIAL PRIMARY KEY,
    nom_type VARCHAR(50) NOT NULL,
    description VARCHAR(255)
);

-- Table des adhérents
CREATE TABLE adherents (
    id_adherent SERIAL PRIMARY KEY,
    id_user INTEGER NOT NULL UNIQUE REFERENCES users(id_user),
    id_type_adherent INTEGER NOT NULL REFERENCES type_adherent(id_type_adherent),
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    adresse VARCHAR(255),
    email VARCHAR(100) NOT NULL UNIQUE,
    telephone VARCHAR(20),
    date_inscription DATE NOT NULL,
    date_naissance DATE NOT NULL,
    max_livres_domicile INTEGER NOT NULL DEFAULT 3,
    max_livres_surplace INTEGER NOT NULL DEFAULT 3,
    livres_empruntes_domicile INTEGER NOT NULL DEFAULT 0,
    livres_empruntes_surplace INTEGER NOT NULL DEFAULT 0,
    duree_pret INTEGER NOT NULL DEFAULT 14
);

-- Table des livres
CREATE TABLE livres (
    id_livre SERIAL PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    auteur VARCHAR(100) NOT NULL,
    editeur VARCHAR(100),
    annee_publication INTEGER,
    isbn VARCHAR(13) UNIQUE,
    age_minimum INTEGER,
    image_nom VARCHAR(255),
    image_type VARCHAR(100),
    image_donnees BYTEA,
    image_url VARCHAR(500)
);

-- Table des exemplaires
CREATE TABLE exemplaires (
    id_exemplaire SERIAL PRIMARY KEY,
    id_livre INTEGER NOT NULL REFERENCES livres(id_livre),
    code_exemplaire VARCHAR(20) NOT NULL UNIQUE,
    etat VARCHAR(20) NOT NULL,
    date_acquisition DATE
);

-- Table des types de prêt
CREATE TABLE types_pret (
    id_type_pret SERIAL PRIMARY KEY,
    nom_type_pret VARCHAR(20) NOT NULL,
    description VARCHAR(255)
);

-- Table des prêts
CREATE TABLE prets_livre (
    id_pret SERIAL PRIMARY KEY,
    id_adherent INTEGER NOT NULL REFERENCES adherents(id_adherent),
    id_exemplaire INTEGER NOT NULL REFERENCES exemplaires(id_exemplaire),
    date_pret DATE NOT NULL,
    date_fin_pret DATE NOT NULL,
    id_type_pret INTEGER NOT NULL REFERENCES types_pret(id_type_pret),
    etat_pret VARCHAR(20) NOT NULL
);

-- Table des retours
CREATE TABLE retours_livre (
    id_retour SERIAL PRIMARY KEY,
    id_pret INTEGER NOT NULL REFERENCES prets_livre(id_pret),
    date_retour DATE NOT NULL,
    etat_retour_livre VARCHAR(20) NOT NULL
);

-- Table des pénalités
CREATE TABLE penalites (
    id_penalite SERIAL PRIMARY KEY,
    id_pret INTEGER NOT NULL REFERENCES prets_livre(id_pret),
    id_adherent INTEGER NOT NULL REFERENCES adherents(id_adherent),
    jours_penalite INTEGER NOT NULL,
    motif VARCHAR(100) NOT NULL,
    date_emission DATE NOT NULL,
    etat_penalite VARCHAR(20) NOT NULL
);

-- Table des utilisateurs pénalisés
CREATE TABLE users_penalises (
    id_user_penalise SERIAL PRIMARY KEY,
    id_adherent INTEGER NOT NULL REFERENCES adherents(id_adherent),
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    motif VARCHAR(100),
    actif BOOLEAN NOT NULL
);

-- Table des réservations
CREATE TABLE reservations (
    id_reservation SERIAL PRIMARY KEY,
    id_adherent INTEGER NOT NULL REFERENCES adherents(id_adherent),
    id_livre INTEGER NOT NULL REFERENCES livres(id_livre),
    date_pret DATE NOT NULL,
    date_fin_pret DATE NOT NULL,
    etat_reservation VARCHAR(20) NOT NULL
);

-- Table des prolongements de prêt
CREATE TABLE prolongements_pret (
    id_prolongation SERIAL PRIMARY KEY,
    id_pret INTEGER NOT NULL REFERENCES prets_livre(id_pret),
    date_demande DATE NOT NULL,
    nouvelle_date_fin DATE NOT NULL,
    etat_prolongation VARCHAR(20) NOT NULL
);

-- Table des jours fériés
CREATE TABLE jours_feries (
    id_jour_ferie SERIAL PRIMARY KEY,
    date_feriee DATE NOT NULL UNIQUE,
    description VARCHAR(100)
);

-- Table des notifications
CREATE TABLE notifications (
    id_notification SERIAL PRIMARY KEY,
    id_adherent INTEGER NOT NULL REFERENCES adherents(id_adherent),
    titre VARCHAR(100) NOT NULL,
    message VARCHAR(500) NOT NULL,
    date_envoi TIMESTAMP NOT NULL,
    lu BOOLEAN NOT NULL DEFAULT FALSE
);

-- Table des notes des livres
CREATE TABLE notes_livres (
    id SERIAL PRIMARY KEY,
    livre_id BIGINT NOT NULL REFERENCES livres(id_livre) ON DELETE CASCADE,
    adherent_id BIGINT NOT NULL REFERENCES adherents(id_adherent) ON DELETE CASCADE,
    note INTEGER NOT NULL CHECK (note >= 1 AND note <= 5),
    commentaire VARCHAR(500),
    date TIMESTAMP NOT NULL,
    CONSTRAINT unique_livre_adherent UNIQUE (livre_id, adherent_id)
);




javac -cp "$(cat cp.txt)" HashGenerator.java
java -cp ".:$(cat cp.txt)" HashGenerator 