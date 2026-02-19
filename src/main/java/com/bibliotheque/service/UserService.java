package com.bibliotheque.service;

import com.bibliotheque.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User saveUser(User user);
    Optional<User> getUserById(Long id);
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserByEmail(String email);
    List<User> getAllUsers();
    void deleteUser(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean isAdmin(Long userId);
    // Ajouté pour notifications admin
    List<User> getAllAdmins();
} 