-- Désactiver les pénalités expirées
UPDATE users_penalises SET actif = FALSE WHERE date_fin < CURRENT_DATE; 