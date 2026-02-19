package com.bibliotheque.service.impl;

import com.bibliotheque.model.Penalite;
import com.bibliotheque.model.PretLivre;
import com.bibliotheque.model.Adherent;
import com.bibliotheque.repository.PenaliteRepository;
import com.bibliotheque.service.PenaliteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PenaliteServiceImpl implements PenaliteService {
    private final PenaliteRepository penaliteRepository;

    @Autowired
    public PenaliteServiceImpl(PenaliteRepository penaliteRepository) {
        this.penaliteRepository = penaliteRepository;
    }

    @Override
    public Penalite savePenalite(Penalite penalite) {
        return penaliteRepository.save(penalite);
    }

    @Override
    public Optional<Penalite> getPenaliteById(Long id) {
        return penaliteRepository.findById(id);
    }

    @Override
    public List<Penalite> getAllPenalites() {
        return penaliteRepository.findAll();
    }

    @Override
    public List<Penalite> getPenalitesByAdherent(Adherent adherent) {
        return penaliteRepository.findByPretLivre_Adherent(adherent);
    }

    @Override
    public List<Penalite> getPenalitesByPret(PretLivre pretLivre) {
        return penaliteRepository.findByPretLivre(pretLivre);
    }

    @Override
    public Optional<Penalite> getDernierePenalitePourPret(PretLivre pretLivre) {
        return penaliteRepository.findFirstByPretLivreOrderByDateEmissionDesc(pretLivre);
    }

    @Override
    public void deletePenalite(Long id) {
        penaliteRepository.deleteById(id);
    }
} 