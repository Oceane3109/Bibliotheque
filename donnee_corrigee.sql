-- Script de données corrigé pour la bibliothèque
-- Basé sur les entités Java

-- Insertion des types d'adhérents
INSERT INTO type_adherent (nom_type, description) VALUES
('étudiant', 'Étudiant avec tarif réduit'),
('adulte', 'Adulte standard'),
('senior', 'Personne de plus de 60 ans');

-- Insertion des utilisateurs
INSERT INTO users (nom_utilisateur, mot_de_passe, email, date_creation, est_admin) VALUES
('admin1', '$2a$10$P.M/0GzcmU4ZWEBGAMnghuqBtT4sLS7Z7E4XaJI9hHgR3EpQN7MTq', 'admin@biblio.com', '2025-07-01', TRUE),
('jean.dupont', '$2a$10$HX0PxnntLeXhe32gueOiA.4cfmcSGiPYPyH9elS2g3d0a2ayB8SBa', 'jean.dupont@email.com', '2025-07-01', FALSE),
('marie.curie', '$2a$10$DQNXmBFGoYLCAUHfZQhgfO7qTdDlQ8JJ1eRC64mEcJL793os83nVG', 'marie.curie@email.com', '2025-07-01', FALSE);

-- Insertion des adhérents
INSERT INTO adherents (id_user, id_type_adherent, nom, prenom, adresse, email, telephone, date_inscription, date_naissance, max_livres_domicile, max_livres_surplace, livres_empruntes_domicile, livres_empruntes_surplace, duree_pret) VALUES
((SELECT id_user FROM users WHERE nom_utilisateur = 'jean.dupont'), (SELECT id_type_adherent FROM type_adherent WHERE nom_type = 'étudiant'), 'Dupont', 'Jean', '123 Rue Exemple, Paris', 'jean.dupont@email.com', '0123456789', '2025-07-01', '2000-05-15', 3, 3, 1, 0, 14),
((SELECT id_user FROM users WHERE nom_utilisateur = 'marie.curie'), (SELECT id_type_adherent FROM type_adherent WHERE nom_type = 'adulte'), 'Curie', 'Marie', '456 Avenue Test, Paris', 'marie.curie@email.com', '0987654321', '2025-07-01', '1970-11-07', 5, 5, 0, 0, 21);

-- Insertion des livres
INSERT INTO livres (titre, auteur, editeur, annee_publication, isbn, age_minimum) VALUES
('1984', 'George Orwell', 'Gallimard', 1949, '9782070368228', 16),
('Les Misérables', 'Victor Hugo', 'Hachette', 1862, '9782011691163', NULL);

-- Insertion des exemplaires (sans date_acquisition qui n'existe pas)
INSERT INTO exemplaires (id_livre, code_exemplaire, etat) VALUES
((SELECT id_livre FROM livres WHERE titre = '1984'), '1984-001', 'disponible'),
((SELECT id_livre FROM livres WHERE titre = '1984'), '1984-002', 'disponible'),
((SELECT id_livre FROM livres WHERE titre = 'Les Misérables'), 'LM-001', 'disponible');

-- Insertion des types de prêt
INSERT INTO types_pret (nom_type_pret, description) VALUES
('domicile', 'Prêt pour emporter à domicile'),
('surplace', 'Consultation sur place');

-- Insertion des jours fériés (avec nom obligatoire)
INSERT INTO jours_feries (nom, date_feriee, description) VALUES
('Fête Nationale', '2025-07-14', 'Fête Nationale'),
('Noël', '2025-12-25', 'Noël');

-- Insertion d'un prêt (avec date_debut au lieu de date_pret)
INSERT INTO prets_livre (id_adherent, id_exemplaire, id_type_pret, date_debut, date_fin, etat_pret) VALUES
((SELECT id_adherent FROM adherents WHERE nom = 'Dupont'), (SELECT id_exemplaire FROM exemplaires WHERE code_exemplaire = '1984-001'), (SELECT id_type_pret FROM types_pret WHERE nom_type_pret = 'domicile'), '2025-07-01', '2025-07-15', 'en_cours');

-- Mise à jour de l'état de l'exemplaire
UPDATE exemplaires SET etat = 'en_pret' WHERE code_exemplaire = '1984-001';

-- Insertion d'une réservation (avec date_pret et date_fin_pret)
INSERT INTO reservations (id_adherent, id_livre, date_pret, date_fin_pret, etat_reservation) VALUES
((SELECT id_adherent FROM adherents WHERE nom = 'Curie'), (SELECT id_livre FROM livres WHERE titre = '1984'), '2025-07-16', '2025-07-30', 'en_attente');

-- Insertion d'une pénalité (avec id_pret_livre au lieu de id_pret)
INSERT INTO penalites (id_pret_livre, motif, jours_penalite, date_emission) VALUES
((SELECT id_pret FROM prets_livre WHERE id_adherent = (SELECT id_adherent FROM adherents WHERE nom = 'Dupont')), 'retard', 2, '2025-07-17');

-- Insertion d'un utilisateur pénalisé
INSERT INTO users_penalises (id_adherent, date_debut, date_fin, motif, actif) VALUES
((SELECT id_adherent FROM adherents WHERE nom = 'Dupont'), '2025-07-17', '2025-07-19', 'Retard de 2 jours', TRUE);

-- Insertion d'une notification
INSERT INTO notifications (id_adherent, titre, message, date_envoi, lu) VALUES
((SELECT id_adherent FROM adherents WHERE nom = 'Dupont'), 'Prêt en retard', 'Vous avez un prêt en retard.', '2025-07-17 10:00:00', FALSE),
((SELECT id_adherent FROM adherents WHERE nom = 'Curie'), 'Réservation disponible', 'Votre réservation est disponible.', '2025-07-18 09:00:00', FALSE); 