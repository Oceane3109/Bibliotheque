-- Suppression des données
DELETE FROM notifications;
DELETE FROM users_penalises;
DELETE FROM penalites;
DELETE FROM retours_livre;
DELETE FROM prolongements_pret;
DELETE FROM reservations;
DELETE FROM prets_livre;
DELETE FROM exemplaires;
DELETE FROM types_pret;
DELETE FROM adherents;
DELETE FROM users;
DELETE FROM type_adherent;
DELETE FROM livres;
DELETE FROM jours_feries;
DELETE FROM notes_livres;
DELETE FROM abonnement;

-- Réinitialisation des séquences
ALTER SEQUENCE notifications_id_notification_seq RESTART WITH 1;
ALTER SEQUENCE users_penalises_id_user_penalise_seq RESTART WITH 1;
ALTER SEQUENCE penalites_id_penalite_seq RESTART WITH 1;
ALTER SEQUENCE retours_livre_id_retour_seq RESTART WITH 1;
ALTER SEQUENCE prolongements_pret_id_prolongation_seq RESTART WITH 1;
ALTER SEQUENCE reservations_id_reservation_seq RESTART WITH 1;
ALTER SEQUENCE prets_livre_id_pret_seq RESTART WITH 1;
ALTER SEQUENCE exemplaires_id_exemplaire_seq RESTART WITH 1;
ALTER SEQUENCE types_pret_id_type_pret_seq RESTART WITH 1;
ALTER SEQUENCE adherents_id_adherent_seq RESTART WITH 1;
ALTER SEQUENCE users_id_user_seq RESTART WITH 1;
ALTER SEQUENCE type_adherent_id_type_adherent_seq RESTART WITH 1;
ALTER SEQUENCE livres_id_livre_seq RESTART WITH 1;
ALTER SEQUENCE jours_feries_id_jour_ferie_seq RESTART WITH 1;
ALTER SEQUENCE notes_livres_id_seq RESTART WITH 1;
ALTER SEQUENCE abonnement_id_abonnement_seq RESTART WITH 1;