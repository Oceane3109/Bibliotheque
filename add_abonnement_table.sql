-- Script de création de la table abonnement
CREATE TABLE abonnement (
    id_abonnement BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_adherent BIGINT NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    statut VARCHAR(32) NOT NULL,
    CONSTRAINT fk_abonnement_adherent FOREIGN KEY (id_adherent) REFERENCES adherents(id_adherent)
);
-- Index pour accélérer les recherches
CREATE INDEX idx_abonnement_adherent ON abonnement(id_adherent);
CREATE INDEX idx_abonnement_statut ON abonnement(statut); 