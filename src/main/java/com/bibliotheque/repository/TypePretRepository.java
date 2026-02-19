package com.bibliotheque.repository;

import com.bibliotheque.model.TypePret;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypePretRepository extends JpaRepository<TypePret, Long> {
    Optional<TypePret> findByNomTypePret(String nomTypePret);
    boolean existsByNomTypePret(String nomTypePret);
} 