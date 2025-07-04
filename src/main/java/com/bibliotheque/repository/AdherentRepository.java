package com.bibliotheque.repository;

import com.bibliotheque.model.Adherent;
import com.bibliotheque.model.TypeAdherent;
import com.bibliotheque.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdherentRepository extends JpaRepository<Adherent, Long> {
    Optional<Adherent> findByUser(User user);
    Optional<Adherent> findByEmail(String email);
    List<Adherent> findByTypeAdherent(TypeAdherent typeAdherent);
    boolean existsByEmail(String email);
    boolean existsByUser(User user);
    
    @Query("SELECT a FROM Adherent a WHERE a.livresEmpruntesDomicile > 0 OR a.livresEmpruntesSurplace > 0")
    List<Adherent> findAllWithActivePrets();
} 