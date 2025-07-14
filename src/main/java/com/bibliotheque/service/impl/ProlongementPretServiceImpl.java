package com.bibliotheque.service.impl;

import com.bibliotheque.model.ProlongementPret;
import com.bibliotheque.model.PretLivre;
import com.bibliotheque.model.Adherent;
import com.bibliotheque.repository.ProlongementPretRepository;
import com.bibliotheque.service.ProlongementPretService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProlongementPretServiceImpl implements ProlongementPretService {
    private final ProlongementPretRepository prolongementPretRepository;

    @Autowired
    public ProlongementPretServiceImpl(ProlongementPretRepository prolongementPretRepository) {
        this.prolongementPretRepository = prolongementPretRepository;
    }

    @Override
    public ProlongementPret saveProlongement(ProlongementPret prolongementPret) {
        System.out.println("=== DÉBUT saveProlongement ===");
        System.out.println("ProlongementPret à sauvegarder:");
        System.out.println("  - ID: " + prolongementPret.getIdProlongation());
        System.out.println("  - Prêt ID: " + (prolongementPret.getPretLivre() != null ? prolongementPret.getPretLivre().getIdPret() : "NULL"));
        System.out.println("  - Date demande: " + prolongementPret.getDateDemande());
        System.out.println("  - Nouvelle date fin: " + prolongementPret.getNouvelleDateFin());
        System.out.println("  - État: " + prolongementPret.getEtatProlongation());
        
        try {
            ProlongementPret saved = prolongementPretRepository.save(prolongementPret);
            System.out.println("Prolongement sauvegardé avec succès, ID: " + saved.getIdProlongation());
            System.out.println("=== FIN saveProlongement ===");
            return saved;
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde: " + e.getMessage());
            e.printStackTrace();
            System.out.println("=== FIN saveProlongement (ERREUR) ===");
            throw e;
        }
    }

    @Override
    public Optional<ProlongementPret> getProlongementById(Long id) {
        return prolongementPretRepository.findById(id);
    }

    @Override
    public List<ProlongementPret> getAllProlongements() {
        return prolongementPretRepository.findAll();
    }

    @Override
    public List<ProlongementPret> getProlongementsByPret(PretLivre pretLivre) {
        return prolongementPretRepository.findByPretLivre(pretLivre);
    }

    @Override
    public List<ProlongementPret> getProlongementsByEtat(String etatProlongation) {
        return prolongementPretRepository.findByEtatProlongation(etatProlongation);
    }

    @Override
    public List<ProlongementPret> getProlongementsByPretAndEtat(PretLivre pretLivre, String etatProlongation) {
        return prolongementPretRepository.findByPretLivreAndEtatProlongation(pretLivre, etatProlongation);
    }

    @Override
    public Optional<ProlongementPret> getDernierProlongementPourPret(PretLivre pretLivre) {
        return prolongementPretRepository.findFirstByPretLivreOrderByDateDemandeDesc(pretLivre);
    }

    @Override
    public void deleteProlongement(Long id) {
        prolongementPretRepository.deleteById(id);
    }

    @Override
    public List<ProlongementPret> getProlongementsByAdherent(Adherent adherent) {
        return prolongementPretRepository.findByPretLivre_Adherent(adherent);
    }

    @Override
    public int getNombreProlongementsApprouvesByAdherent(Adherent adherent) {
        return prolongementPretRepository.countByPretLivre_AdherentAndEtatProlongation(adherent, "approuvee");
    }

    @Override
    public List<ProlongementPret> getAllProlongementsWithRelations() {
        return prolongementPretRepository.findAllWithRelations();
    }
} 