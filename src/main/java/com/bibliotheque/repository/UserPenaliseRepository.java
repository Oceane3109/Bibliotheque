package com.bibliotheque.repository;

import com.bibliotheque.model.UserPenalise;
import com.bibliotheque.model.Adherent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPenaliseRepository extends JpaRepository<UserPenalise, Long> {
    List<UserPenalise> findByAdherent(Adherent adherent);
    List<UserPenalise> findByAdherentAndActifTrue(Adherent adherent);
    boolean existsByAdherentAndActifTrue(Adherent adherent);
} 