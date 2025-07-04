package com.bibliotheque.service.impl;

import com.bibliotheque.model.JourFerie;
import com.bibliotheque.repository.JourFerieRepository;
import com.bibliotheque.service.JourFerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class JourFerieServiceImpl implements JourFerieService {
    private final JourFerieRepository jourFerieRepository;

    @Autowired
    public JourFerieServiceImpl(JourFerieRepository jourFerieRepository) {
        this.jourFerieRepository = jourFerieRepository;
    }

    @Override
    public JourFerie saveJourFerie(JourFerie jourFerie) {
        return jourFerieRepository.save(jourFerie);
    }

    @Override
    public Optional<JourFerie> getJourFerieById(Long id) {
        return jourFerieRepository.findById(id);
    }

    @Override
    public List<JourFerie> getAllJoursFeries() {
        return jourFerieRepository.findAll();
    }

    @Override
    public void deleteJourFerie(Long id) {
        jourFerieRepository.deleteById(id);
    }

    @Override
    public boolean isJourFerie(LocalDate date) {
        return jourFerieRepository.existsByDateFeriee(date);
    }
} 