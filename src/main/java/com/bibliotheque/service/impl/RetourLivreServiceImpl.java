package com.bibliotheque.service.impl;

import com.bibliotheque.model.RetourLivre;
import com.bibliotheque.model.PretLivre;
import com.bibliotheque.repository.RetourLivreRepository;
import com.bibliotheque.service.RetourLivreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RetourLivreServiceImpl implements RetourLivreService {
    private final RetourLivreRepository retourLivreRepository;

    @Autowired
    public RetourLivreServiceImpl(RetourLivreRepository retourLivreRepository) {
        this.retourLivreRepository = retourLivreRepository;
    }

    @Override
    public RetourLivre saveRetour(RetourLivre retourLivre) {
        return retourLivreRepository.save(retourLivre);
    }

    @Override
    public Optional<RetourLivre> getRetourById(Long id) {
        return retourLivreRepository.findById(id);
    }

    @Override
    public List<RetourLivre> getAllRetours() {
        return retourLivreRepository.findAll();
    }

    @Override
    public List<RetourLivre> getRetoursByPret(PretLivre pretLivre) {
        return retourLivreRepository.findByPretLivre(pretLivre);
    }

    @Override
    public Optional<RetourLivre> getDernierRetourPourPret(PretLivre pretLivre) {
        return retourLivreRepository.findFirstByPretLivreOrderByDateRetourDesc(pretLivre);
    }

    @Override
    public void deleteRetour(Long id) {
        retourLivreRepository.deleteById(id);
    }
} 