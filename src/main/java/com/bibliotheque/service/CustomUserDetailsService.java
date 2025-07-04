package com.bibliotheque.service;

import com.bibliotheque.model.User;
import com.bibliotheque.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByNomUtilisateur(username)
            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
        String role = user.isAdmin() ? "ADMIN" : "USER";
        System.out.println("[AUTH] Utilisateur: " + username + " | Rôle attribué: " + role);
        return org.springframework.security.core.userdetails.User
            .withUsername(user.getNomUtilisateur())
            .password(user.getMotDePasse())
            .roles(role)
            .build();
    }
} 