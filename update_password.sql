-- Script pour mettre à jour les mots de passe des utilisateurs
-- Mise à jour du mot de passe de jean123
UPDATE users 
SET mot_de_passe = '$2a$10$w/43wX.MYtoYNvv9su0vP.t2DftAIyiPYDbQWW6ViLdY1zcfHaAF.'
WHERE nom_utilisateur = 'jean123';

-- Mise à jour du mot de passe de marie123
UPDATE users 
SET mot_de_passe = '$2a$10$4kq1twVN.dBNbUsoxTUb4.7xkTmRofGfBFnux3MRJ39omOH3Ftd1m'
WHERE nom_utilisateur = 'marie123';

-- Vérifier que les mises à jour ont été effectuées
SELECT id_user, nom_utilisateur, mot_de_passe, email, est_admin 
FROM users 
WHERE nom_utilisateur IN ('jean123', 'marie123'); 