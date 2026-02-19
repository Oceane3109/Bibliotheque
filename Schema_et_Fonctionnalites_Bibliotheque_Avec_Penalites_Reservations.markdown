# Schéma et Fonctionnalités de l'Application de Gestion de Bibliothèque

Ce document décrit les tables avec leurs attributs (adaptés pour PostgreSQL) et les fonctionnalités de l'application de gestion de bibliothèque. Les exigences incluent :
- Chaque adhérent a un login unique (lié à un compte utilisateur).
- Les administrateurs peuvent voir la fiche complète de chaque adhérent ; les adhérents ne voient que les livres, leurs emprunts, réservations, prolongements, et leur propre profil.
- Les utilisateurs s'inscrivent pour devenir adhérents, avec différents types d'adhérents (ex. étudiant, adulte, senior).
- Les prêts dépendent de la disponibilité des exemplaires et de règles (ex. restriction d'âge).
- Les durées de prêt et le nombre de livres empruntables sont paramétrables par adhérent.
- Les prolongements de prêt sont possibles.
- Les jours fériés et dimanches sont pris en compte pour éviter les pénalités.
- Les pénalités sont basées sur le nombre de jours de retard (jours de retard = jours d'interdiction d'emprunt, sans montant monétaire).
- Les réservations sont pour un jour précis (début du prêt), avec une date de fin précise.
- Chaque cause d'erreur (ex. âge non valide) est affichée dans l'interface.
- Après un emprunt validé, la fiche de l'adhérent montre une diminution du nombre de livres empruntables restants (sur place ou à domicile) et une augmentation du nombre de livres empruntés.
- Base de données : PostgreSQL.

## Tables et Attributs

### 1. Livre
Représente les informations générales sur un livre, avec restrictions.
- **id_livre**: SERIAL PRIMARY KEY (identifiant unique, auto-incrémenté)
- **titre**: VARCHAR(255) NOT NULL (titre du livre)
- **auteur**: VARCHAR(100) NOT NULL (nom de l'auteur)
- **editeur**: VARCHAR(100) (nom de l'éditeur, facultatif)
- **annee_publication**: INTEGER (année de publication, facultatif)
- **isbn**: VARCHAR(13) UNIQUE (code ISBN, facultatif)
- **age_minimum**: INTEGER (âge minimum requis pour emprunter, facultatif)

### 2. Adherent
Représente un adhérent avec son login, son type, ses paramètres personnalisés et ses compteurs.
- **id_adherent**: SERIAL PRIMARY KEY (identifiant unique, auto-incrémenté)
- **id_user**: INTEGER UNIQUE NOT NULL REFERENCES User(id_user) (lien vers le compte utilisateur)
- **id_type_adherent**: INTEGER NOT NULL REFERENCES Type_adherent(id_type_adherent) (type d'adhérent)
- **nom**: VARCHAR(50) NOT NULL (nom de l'adhérent)
- **prenom**: VARCHAR(50) NOT NULL (prénom)
- **adresse**: VARCHAR(255) (adresse complète, facultatif)
- **email**: VARCHAR(100) NOT NULL UNIQUE (email unique)
- **telephone**: VARCHAR(20) (numéro de téléphone, facultatif)
- **date_inscription**: DATE NOT NULL (date d'inscription)
- **date_naissance**: DATE NOT NULL (date de naissance, pour restrictions d'âge)
- **max_livres_domicile**: INTEGER NOT NULL DEFAULT 3 (nombre max de livres à domicile)
- **max_livres_surplace**: INTEGER NOT NULL DEFAULT 3 (nombre max de livres sur place)
- **livres_empruntes_domicile**: INTEGER NOT NULL DEFAULT 0 (livres actuellement empruntés à domicile)
- **livres_empruntes_surplace**: INTEGER NOT NULL DEFAULT 0 (livres actuellement empruntés sur place)
- **duree_pret**: INTEGER NOT NULL DEFAULT 14 (durée du prêt en jours)

### 3. Type_adherent
Définit les catégories d'adhérents (ex. étudiant, adulte, senior).
- **id_type_adherent**: SERIAL PRIMARY KEY (identifiant unique, auto-incrémenté)
- **nom_type**: VARCHAR(50) NOT NULL (nom du type, ex. "étudiant", "adulte", "senior")
- **description**: VARCHAR(255) (description du type, facultative)

### 4. Pret_livre
Enregistre les prêts de livres.
- **id_pret**: SERIAL PRIMARY KEY (identifiant unique, auto-incrémenté)
- **id_adherent**: INTEGER NOT NULL REFERENCES Adherent(id_adherent)
- **id_exemplaire**: INTEGER NOT NULL REFERENCES Exemplaire(id_exemplaire)
- **date_pret**: DATE NOT NULL (date du prêt)
- **date_fin_pret**: DATE NOT NULL (date limite de retour)
- **type_pret**: INTEGER NOT NULL REFERENCES Type_pret(id_type_pret)
- **etat_pret**: VARCHAR(20) NOT NULL CHECK (etat_pret IN ('actif', 'termine', 'retard')) (statut du prêt)

### 5. Exemplaire
Représente une copie physique d'un livre.
- **id_exemplaire**: SERIAL PRIMARY KEY (identifiant unique, auto-incrémenté)
- **id_livre**: INTEGER NOT NULL REFERENCES Livre(id_livre)
- **code_exemplaire**: VARCHAR(20) NOT NULL UNIQUE (code unique)
- **etat**: VARCHAR(20) NOT NULL CHECK (etat IN ('disponible', 'emprunte', 'perdu', 'endommage')) (état de l'exemplaire)
- **date_acquisition**: DATE (date d'acquisition, facultative)

### 6. Retour_livre
Enregistre les retours de livres.
- **id_retour**: SERIAL PRIMARY KEY (identifiant unique, auto-incrémenté)
- **id_pret**: INTEGER NOT NULL REFERENCES Pret_livre(id_pret)
- **date_retour**: DATE NOT NULL (date effective du retour)
- **etat_retour_livre**: VARCHAR(20) NOT NULL CHECK (etat_retour_livre IN ('bon', 'endommage', 'perdu')) (état du livre au retour)

### 7. Type_pret
Définit les types de prêt (sur place ou à domicile).
- **id_type_pret**: SERIAL PRIMARY KEY (identifiant unique, auto-incrémenté)
- **nom_type_pret**: VARCHAR(20) NOT NULL CHECK (nom_type_pret IN ('domicile', 'surplace')) (nom du type)
- **description**: VARCHAR(255) (description, facultative)

### 8. Penalite
Enregistre les pénalités basées sur les jours de retard.
- **id_penalite**: SERIAL PRIMARY KEY (identifiant unique, auto-incrémenté)
- **id_pret**: INTEGER NOT NULL REFERENCES Pret_livre(id_pret)
- **id_adherent**: INTEGER NOT NULL REFERENCES Adherent(id_adherent)
- **jours_penalite**: INTEGER NOT NULL (nombre de jours de pénalité, égal aux jours de retard)
- **motif**: VARCHAR(100) NOT NULL (motif : retard, dommage, perte)
- **date_emission**: DATE NOT NULL (date d'émission)
- **etat_penalite**: VARCHAR(20) NOT NULL CHECK (etat_penalite IN ('en_cours', 'terminee', 'annulee')) (statut)

### 9. Inscription
Gère les inscriptions des utilisateurs pour devenir adhérents.
- **id_inscription**: SERIAL PRIMARY KEY (identifiant unique, auto-incrémenté)
- **id_user**: INTEGER NOT NULL REFERENCES User(id_user)
- **id_type_adherent**: INTEGER REFERENCES Type_adherent(id_type_adherent) (facultatif)
- **date_inscription**: DATE NOT NULL (date de la demande)
- **etat_inscription**: VARCHAR(20) NOT NULL CHECK (etat_inscription IN ('en_attente', 'validee', 'refusee')) (statut)

### 10. User
Représente les utilisateurs de l'application (y compris adhérents et administrateurs).
- **id_user**: SERIAL PRIMARY KEY (identifiant unique, auto-incrémenté)
- **nom_utilisateur**: VARCHAR(50) NOT NULL UNIQUE (nom d'utilisateur)
- **mot_de_passe**: VARCHAR(255) NOT NULL (mot de passe haché)
- **email**: VARCHAR(100) NOT NULL UNIQUE (email unique)
- **date_creation**: DATE NOT NULL (date de création)
- **est_admin**: BOOLEAN NOT NULL DEFAULT FALSE (indique si l'utilisateur est administrateur)

### 11. Reservation
Gère les réservations de livres pour un jour précis.
- **id_reservation**: SERIAL PRIMARY KEY (identifiant unique, auto-incrémenté)
- **id_adherent**: INTEGER NOT NULL REFERENCES Adherent(id_adherent)
- **id_livre**: INTEGER NOT NULL REFERENCES Livre(id_livre)
- **date_pret**: DATE NOT NULL (date précise du début du prêt)
- **date_fin_pret**: DATE NOT NULL (date précise de fin du prêt)
- **etat_reservation**: VARCHAR(20) NOT NULL CHECK (etat_reservation IN ('active', 'annulee', 'expiree', 'confirmee')) (statut)

### 12. Prolongement_pret
Enregistre les demandes de prolongement de prêt.
- **id_prolongation**: SERIAL PRIMARY KEY (identifiant unique, auto-incrémenté)
- **id_pret**: INTEGER NOT NULL REFERENCES Pret_livre(id_pret)
- **date_demande**: DATE NOT NULL (date de la demande)
- **nouvelle_date_fin**: DATE NOT NULL (nouvelle date de fin)
- **etat_prolongation**: VARCHAR(20) NOT NULL CHECK (etat_prolongation IN ('en_attente', 'approuvee', 'refusee')) (statut)

### 13. User_penalise
Suit les adhérents pénalisés.
- **id_user_penalise**: SERIAL PRIMARY KEY (identifiant unique, auto-incrémenté)
- **id_adherent**: INTEGER NOT NULL REFERENCES Adherent(id_adherent)
- **id_penalite**: INTEGER NOT NULL REFERENCES Penalite(id_penalite)
- **date_debut_penalite**: DATE NOT NULL (début de la pénalité)
- **date_fin_penalite**: DATE (fin de la pénalité, nullable)
- **motif**: VARCHAR(100) NOT NULL (motif de la sanction)

### 14. Jour_ferie
Liste les jours fériés pour ajuster les dates de prêt/retour.
- **id_jour_ferie**: SERIAL PRIMARY KEY (identifiant unique, auto-incrémenté)
- **date_ferie**: DATE NOT NULL UNIQUE (date du jour férié)
- **nom_ferie**: VARCHAR(50) (nom du jour férié, facultatif)

## Fonctionnalités Principales

### 1. Gestion des Utilisateurs
- Création de comptes utilisateurs avec nom d'utilisateur, mot de passe, email, et rôle (administrateur ou non) (table : User).
- Authentification sécurisée pour accéder à l'application.
- Chaque adhérent est lié à un compte utilisateur unique via **Adherent.id_user**.
- **Affichage des erreurs** : Si l'email ou le nom d'utilisateur est déjà utilisé, afficher "Email déjà enregistré" ou "Nom d'utilisateur déjà pris".

### 2. Inscription des Adhérents
- Soumission d'une demande d'inscription avec choix du type d'adhérent (ex. étudiant, adulte, senior) et date de naissance (tables : Inscription, Type_adherent).
- Validation par un administrateur pour créer un profil d'adhérent lié au compte utilisateur (table : Adherent).
- Configuration des paramètres personnalisés : nombre maximum de livres (sur place et à domicile) et durée de prêt (table : Adherent).
- **Affichage des erreurs** : Si la demande est incomplète (ex. date de naissance manquante), afficher "Veuillez fournir une date de naissance".

### 3. Gestion des Types d'Adhérents
- Définition des catégories d'adhérents (ex. étudiant, adulte, senior) avec descriptions (table : Type_adherent).
- Attribution d'un type d'adhérent lors de l'inscription ou modification du profil (tables : Inscription, Adherent).
- **Affichage des erreurs** : Si un type d'adhérent invalide est sélectionné, afficher "Type d'adhérent non reconnu".

### 4. Gestion des Livres
- Ajout, modification, suppression des livres (titre, auteur, éditeur, ISBN, âge minimum) (table : Livre).
- Gestion des exemplaires physiques (code unique, état : disponible, emprunté, perdu, endommagé) (table : Exemplaire).
- Suivi des exemplaires disponibles pour chaque livre.
- **Affichage des erreurs** : Si un ISBN est déjà utilisé ou si les champs obligatoires (titre, auteur) sont manquants, afficher "ISBN déjà enregistré" ou "Titre requis".

### 5. Gestion des Prêts
- Emprunt de livres (sur place ou à domicile) si un exemplaire est disponible (tables : Pret_livre, Exemplaire, Type_pret).
- Vérification des conditions :
  - Nombre de livres empruntés (**livres_empruntes_domicile/surplace**) inférieur à **max_livres_domicile/surplace** (table : Adherent).
  - Âge de l'adhérent (calculé à partir de **date_naissance**) supérieur ou égal à **age_minimum** du livre (table : Livre).
  - Adhérent non pénalisé (vérification via **User_penalise**, où **date_fin_penalite** est NULL ou postérieure à la date actuelle).
- Mise à jour de la fiche de l'adhérent après un emprunt validé :
  - Augmenter **livres_empruntes_domicile** ou **livres_empruntes_surplace** selon le type de prêt.
  - Afficher le nombre de livres restants (calculé comme **max_livres_domicile/surplace** - **livres_empruntes_domicile/surplace**).
- Calcul de la date de fin de prêt (**date_fin_pret**) en ajoutant **duree_pret** à **date_pret**, en évitant les dimanches et jours fériés (table : Jour_ferie).
- **Affichage des erreurs** :
  - Si aucun exemplaire n'est disponible, afficher "Aucun exemplaire disponible".
  - Si l'adhérent a atteint sa limite d'emprunts, afficher "Limite d'emprunts atteinte pour [type de prêt]".
  - Si l'âge de l'adhérent est inférieur à l'âge minimum du livre, afficher "Âge minimum requis : [âge]".
  - Si l'adhérent est pénalisé, afficher "Emprunt bloqué en raison d'une pénalité active".

### 6. Gestion des Retours
- Enregistrement des retours avec l'état du livre (bon, endommagé, perdu) (table : Retour_livre).
- Mise à jour de la fiche de l'adhérent :
  - Diminuer **livres_empruntes_domicile** ou **livres_empruntes_surplace** selon le type de prêt.
  - Recalculer le nombre de livres restants (**max_livres_domicile/surplace** - **livres_empruntes_domicile/surplace**).
- Vérification des retards (si **date_retour** > **date_fin_pret**) :
  - Calculer les jours de retard et générer une pénalité avec **jours_penalite** = nombre de jours de retard (table : Penalite).
  - Enregistrer la pénalité dans **User_penalise** avec **date_fin_penalite** = **date_debut_penalite** + **jours_penalite**.
- Exemption de pénalité si **date_fin_pret** tombe un dimanche ou jour férié (table : Jour_ferie).
- **Affichage des erreurs** : Si l'état du livre est invalide, afficher "Veuillez indiquer l'état du livre".

### 7. Prolongement des Prêts
- Soumission d'une demande de prolongement par un adhérent (table : Prolongement_pret).
- Validation ou refus par un administrateur, avec ajustement de **nouvelle_date_fin** en évitant les dimanches et jours fériés (tables : Prolongement_pret, Jour_ferie).
- **Affichage des erreurs** :
  - Si le prêt a déjà été prolongé, afficher "Prolongement déjà effectué".
  - Si le livre est réservé par un autre adhérent, afficher "Prolongement refusé : livre réservé".

### 8. Gestion des Réservations
- Réservation d’un livre pour un **jour précis** (**date_pret**) si tous les exemplaires sont empruntés (table : Reservation).
- Calcul de **date_fin_pret** en ajoutant **duree_pret** (de **Adherent**) à **date_pret**, en évitant les dimanches et jours fériés (table : Jour_ferie).
- Conversion de la réservation en prêt (**Pret_livre**) si confirmée à **date_pret**.
- Notification à l'adhérent lorsque le livre devient disponible.
- Gestion de l’expiration des réservations si non confirmées à **date_pret**.
- **Affichage des erreurs** :
  - Si le livre est déjà réservé par l'adhérent, afficher "Livre déjà réservé".
  - Si le livre est disponible, afficher "Livre disponible, pas besoin de réserver".
  - Si la date de prêt choisie est un dimanche ou jour férié, afficher "Date de prêt invalide : jour férié ou dimanche".

### 9. Gestion des Pénalités
- Génération de pénalités pour retards, dommages ou pertes (tables : Penalite, Retour_livre).
- Les pénalités sont basées sur le nombre de jours de retard (**jours_penalite** = jours de retard) ou sur l'état du livre (endommagé, perdu).
- Enregistrement dans **User_penalise** avec **date_fin_penalite** = **date_debut_penalite** + **jours_penalite**, bloquant les emprunts pendant cette période.
- Suivi des pénalités (motif, statut : en_cours, terminee, annulee) (table : Penalite).
- **Affichage des erreurs** : Si les jours de pénalité ne peuvent être calculés, afficher "Erreur dans le calcul des jours de pénalité".

### 10. Gestion des Jours Fériés
- Enregistrement des jours fériés (table : Jour_ferie).
- Ajustement des dates de fin de prêt (**Pret_livre.date_fin_pret**, **Reservation.date_fin_pret**, **Prolongement_pret.nouvelle_date_fin**) pour éviter les dimanches et jours fériés.
- Autorisation des retours avant ou après ces jours sans pénalité.
- **Affichage des erreurs** : Si une date fériée est déjà enregistrée, afficher "Date fériée déjà enregistrée".

### 11. Gestion des Accès et Affichage des Fiches
- **Administrateur connecté** (**User.est_admin = TRUE**) :
  - Accès à la fiche complète de chaque adhérent (**Adherent** : nom, prénom, email, téléphone, adresse, date de naissance, type d'adhérent, max_livres_domicile/surplace, livres_empruntes_domicile/surplace, duree_pret).
  - Consultation des emprunts, réservations, prolongements, et pénalités de chaque adhérent (tables : Pret_livre, Reservation, Prolongement_pret, Penalite, User_penalise).
- **Adhérent connecté** (**User.id_user** lié à **Adherent.id_user**) :
  - Accès limité à :
    - Liste des livres disponibles (table : Livre, avec filtrage sur **age_minimum**).
    - Ses propres emprunts (table : Pret_livre où **id_adherent** correspond).
    - Ses réservations (table : Reservation où **id_adherent** correspond).
    - Ses demandes de prolongement (table : Prolongement_pret où **id_pret** correspond).
    - Ses pénalités (table : Penalite où **id_adherent** correspond).
    - Son propre profil (**Adherent** : nom, prénom, email, téléphone, adresse, type d'adhérent, compteurs de livres, durée de prêt).
  - Aucune visibilité sur les données des autres adhérents.
- **Affichage des erreurs** : Si un adhérent tente d'accéder à des données non autorisées, afficher "Accès non autorisé".

## Notes
- La relation **Adherent.id_user** garantit que chaque adhérent a un login unique via **User**.
- L'attribut **User.est_admin** différencie les accès administrateur/adhérent.
- Les pénalités sont basées sur **jours_penalite** (jours de retard) au lieu d'un montant, avec blocage des emprunts via **User_penalise**.
- Les réservations spécifient **date_pret** et **date_fin_pret**, calculées avec **duree_pret** et ajustées pour éviter les dimanches/jours fériés.
- Les types de données sont adaptés à PostgreSQL (SERIAL, VARCHAR, CHECK pour ENUM).
- Les erreurs sont affichées dans l'interface pour guider l'utilisateur (ex. "Âge minimum requis : 18").
- Les compteurs (**livres_empruntes_domicile/surplace**) sont mis à jour après chaque emprunt/retour.