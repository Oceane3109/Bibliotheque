package com.bibliotheque.repository;

import com.bibliotheque.model.TypeAdherent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypeAdherentRepository extends JpaRepository<TypeAdherent, Long> {
    Optional<TypeAdherent> findByNomType(String nomType);
    boolean existsByNomType(String nomType);
} 