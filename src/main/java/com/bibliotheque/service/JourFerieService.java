package com.bibliotheque.service;

import com.bibliotheque.model.JourFerie;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface JourFerieService {
    JourFerie saveJourFerie(JourFerie jourFerie);
    Optional<JourFerie> getJourFerieById(Long id);
    List<JourFerie> getAllJoursFeries();
    void deleteJourFerie(Long id);
    boolean isJourFerie(LocalDate date);
} 