package com.bibliotheque.repository;

import com.bibliotheque.model.JourFerie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface JourFerieRepository extends JpaRepository<JourFerie, Long> {
    Optional<JourFerie> findByDateFeriee(LocalDate dateFeriee);
    boolean existsByDateFeriee(LocalDate dateFeriee);
} 