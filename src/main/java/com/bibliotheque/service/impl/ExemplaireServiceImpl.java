package com.bibliotheque.service.impl;

import com.bibliotheque.model.Exemplaire;
import com.bibliotheque.model.Livre;
import com.bibliotheque.repository.ExemplaireRepository;
import com.bibliotheque.service.ExemplaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExemplaireServiceImpl implements ExemplaireService {
    private final ExemplaireRepository exemplaireRepository;

    @Autowired
    public ExemplaireServiceImpl(ExemplaireRepository exemplaireRepository) {
        this.exemplaireRepository = exemplaireRepository;
    }

    @Override
    public Exemplaire saveExemplaire(Exemplaire exemplaire) {
        return exemplaireRepository.save(exemplaire);
    }

    @Override
    public Optional<Exemplaire> getExemplaireById(Long id) {
        return exemplaireRepository.findById(id);
    }

    @Override
    public Optional<Exemplaire> getExemplaireByCode(String codeExemplaire) {
        return exemplaireRepository.findByCodeExemplaire(codeExemplaire);
    }

    @Override
    public List<Exemplaire> getAllExemplaires() {
        return exemplaireRepository.findAll();
    }

    @Override
    public List<Exemplaire> getExemplairesByLivre(Livre livre) {
        return exemplaireRepository.findByLivre(livre);
    }

    @Override
    public List<Exemplaire> getExemplairesByEtat(String etat) {
        return exemplaireRepository.findByEtat(etat);
    }

    @Override
    public List<Exemplaire> getExemplairesByLivreAndEtat(Livre livre, String etat) {
        return exemplaireRepository.findByLivreAndEtat(livre, etat);
    }

    @Override
    public List<Exemplaire> getExemplairesDisponiblesByLivre(Livre livre) {
        return exemplaireRepository.findByLivreAndEtat(livre, "disponible");
    }

    @Override
    public void deleteExemplaire(Long id) {
        exemplaireRepository.deleteById(id);
    }

    @Override
    public boolean existsByCodeExemplaire(String codeExemplaire) {
        return exemplaireRepository.existsByCodeExemplaire(codeExemplaire);
    }
} 