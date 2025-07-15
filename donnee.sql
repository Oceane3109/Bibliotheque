-- Script de données corrigé pour la bibliothèque
-- Basé sur les entités Java

-- Insertion des types d'adhérents
INSERT INTO type_adherent (nom_type, description) VALUES
('étudiant', 'Étudiant avec tarif réduit'),
('adulte', 'Adulte standard'),
('senior', 'Personne de plus de 60 ans');

-- Insertion des utilisateurs
INSERT INTO users (nom_utilisateur, mot_de_passe, email, date_creation, est_admin) VALUES
('admin1', 'admin123', 'admin@biblio.com', '2025-07-01', TRUE),
('jean.dupont', 'hashed_password_jean', 'jean.dupont@email.com', '2025-07-01', FALSE),
('marie.curie', 'hashed_password_marie', 'marie.curie@email.com', '2025-07-01', FALSE);

-- Insertion des adhérents
INSERT INTO adherents (id_user, id_type_adherent, nom, prenom, adresse, email, telephone, date_inscription, date_naissance, max_livres_domicile, max_livres_surplace, livres_empruntes_domicile, livres_empruntes_surplace, duree_pret) VALUES
((SELECT id_user FROM users WHERE nom_utilisateur = 'jean.dupont'), (SELECT id_type_adherent FROM type_adherent WHERE nom_type = 'étudiant'), 'Dupont', 'Jean', '123 Rue Exemple, Paris', 'jean.dupont@email.com', '0123456789', '2025-07-01', '2000-05-15', 3, 3, 1, 0, 14),
((SELECT id_user FROM users WHERE nom_utilisateur = 'marie.curie'), (SELECT id_type_adherent FROM type_adherent WHERE nom_type = 'adulte'), 'Curie', 'Marie', '456 Avenue Test, Paris', 'marie.curie@email.com', '0987654321', '2025-07-01', '1970-11-07', 5, 5, 0, 0, 21);

-- Insertion de livres avec images distantes

-- Insertion des types de prêt
INSERT INTO types_pret (nom_type_pret, description) VALUES
('domicile', 'Prêt pour emporter à domicile'),
('surplace', 'Consultation sur place');

-- Insertion des jours fériés (avec nom obligatoire)
INSERT INTO jours_feries (nom, date_feriee, description) VALUES
('Fête Nationale', '2025-07-14', 'Fête Nationale'),
('Noël', '2025-12-25', 'Noël');

-- Insertion d'un prêt (avec date_debut au lieu de date_pret)


-- Script pour ajouter la colonne quota_prolongements à la table adherents
-- Exécuter ces commandes dans PostgreSQL

-- 1. Ajouter la colonne avec une valeur par défaut
ALTER TABLE adherents ADD COLUMN quota_prolongements INTEGER DEFAULT 2;

-- 2. Mettre à jour les valeurs NULL avec la valeur par défaut
UPDATE adherents SET quota_prolongements = 2 WHERE quota_prolongements IS NULL;

-- 3. Rendre la colonne NOT NULL après avoir mis à jour toutes les valeurs
ALTER TABLE adherents ALTER COLUMN quota_prolongements SET NOT NULL;

-- 4. Vérifier que la colonne a été ajoutée correctement
SELECT id_adherent, nom, prenom, quota_prolongements FROM adherents LIMIT 5;


-- Ajoute la colonne si ce n'est pas déjà fait
ALTER TABLE livres ADD COLUMN IF NOT EXISTS image_url VARCHAR(500);

-- Insertion de livres avec des images distantes fiables
INSERT INTO livres (titre, auteur, editeur, annee_publication, isbn, age_minimum, image_url) VALUES
('Le Petit Prince', 'Antoine de Saint-Exupéry', 'Gallimard', 1943, '9782070612758', 7, 'https://upload.wikimedia.org/wikipedia/commons/0/05/Littleprince.JPG'),
('Harry Potter à l''école des sorciers', 'J.K. Rowling', 'Gallimard Jeunesse', 1998, '9782070643028', 9, 'https://covers.openlibrary.org/b/isbn/9782070643028-L.jpg'),
('L''Étranger', 'Albert Camus', 'Gallimard', 1942, '9782070360024', 15, 'https://covers.openlibrary.org/b/isbn/9782070360024-L.jpg'),
('1984', 'George Orwell', 'Gallimard', 1949, '9782070368228', 16, 'https://covers.openlibrary.org/b/isbn/9782070368228-L.jpg'),
('Les Misérables', 'Victor Hugo', 'Hachette', 1862, '9782011691163', 12, 'https://covers.openlibrary.org/b/id/11156361-L.jpg');

-- Insertion d'exemplaires pour chaque livre

-- Le Petit Prince
INSERT INTO exemplaires (id_livre, code_exemplaire, etat)
VALUES
((SELECT id_livre FROM livres WHERE titre = 'Le Petit Prince'), 'LPP-001', 'disponible'),
((SELECT id_livre FROM livres WHERE titre = 'Le Petit Prince'), 'LPP-002', 'disponible');

-- Harry Potter à l'école des sorciers
INSERT INTO exemplaires (id_livre, code_exemplaire, etat)
VALUES
((SELECT id_livre FROM livres WHERE titre = 'Harry Potter à l''école des sorciers'), 'HP1-001', 'disponible'),
((SELECT id_livre FROM livres WHERE titre = 'Harry Potter à l''école des sorciers'), 'HP1-002', 'disponible'),
((SELECT id_livre FROM livres WHERE titre = 'Harry Potter à l''école des sorciers'), 'HP1-003', 'disponible');

-- L'Étranger
INSERT INTO exemplaires (id_livre, code_exemplaire, etat)
VALUES
((SELECT id_livre FROM livres WHERE titre = 'L''Étranger'), 'ETR-001', 'disponible'),
((SELECT id_livre FROM livres WHERE titre = 'L''Étranger'), 'ETR-002', 'disponible');

-- 1984
INSERT INTO exemplaires (id_livre, code_exemplaire, etat)
VALUES
((SELECT id_livre FROM livres WHERE titre = '1984'), 'NIN-001', 'disponible'),
((SELECT id_livre FROM livres WHERE titre = '1984'), 'NIN-002', 'disponible');

-- Les Misérables
INSERT INTO exemplaires (id_livre, code_exemplaire, etat)
VALUES
((SELECT id_livre FROM livres WHERE titre = 'Les Misérables'), 'LM-001', 'disponible'),
((SELECT id_livre FROM livres WHERE titre = 'Les Misérables'), 'LM-002', 'disponible');