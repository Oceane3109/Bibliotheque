-- Script pour désactiver les pénalités expirées
UPDATE users_penalises 
SET actif = FALSE 
WHERE date_fin < CURRENT_DATE AND actif = TRUE;

-- Vérifier les pénalités actives
SELECT * FROM users_penalises WHERE actif = TRUE; 