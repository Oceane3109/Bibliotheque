package com.bibliotheque.repository;

import com.bibliotheque.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByNomUtilisateur(String nomUtilisateur);
    Optional<User> findByEmail(String email);
    boolean existsByNomUtilisateur(String nomUtilisateur);
    boolean existsByEmail(String email);
} 