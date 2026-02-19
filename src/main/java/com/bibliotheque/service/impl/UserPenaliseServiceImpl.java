package com.bibliotheque.service.impl;

import com.bibliotheque.model.UserPenalise;
import com.bibliotheque.model.Adherent;
import com.bibliotheque.repository.UserPenaliseRepository;
import com.bibliotheque.service.UserPenaliseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserPenaliseServiceImpl implements UserPenaliseService {
    private final UserPenaliseRepository userPenaliseRepository;

    @Autowired
    public UserPenaliseServiceImpl(UserPenaliseRepository userPenaliseRepository) {
        this.userPenaliseRepository = userPenaliseRepository;
    }

    @Override
    public UserPenalise saveUserPenalise(UserPenalise userPenalise) {
        return userPenaliseRepository.save(userPenalise);
    }

    @Override
    public Optional<UserPenalise> getUserPenaliseById(Long id) {
        return userPenaliseRepository.findById(id);
    }

    @Override
    public List<UserPenalise> getAllUserPenalises() {
        return userPenaliseRepository.findAll();
    }

    @Override
    public void deleteUserPenalise(Long id) {
        userPenaliseRepository.deleteById(id);
    }

    @Override
    public List<UserPenalise> getUserPenalisesByAdherent(Adherent adherent) {
        return userPenaliseRepository.findByAdherent(adherent);
    }

    @Override
    public boolean isAdherentPenalise(Adherent adherent) {
        return userPenaliseRepository.existsByAdherentAndActifTrue(adherent);
    }

    @Override
    public boolean isAdherentPenaliseAtDate(Adherent adherent, LocalDate date) {
        return userPenaliseRepository.findByAdherentAndActifTrue(adherent).stream()
                .anyMatch(p -> (date.isEqual(p.getDateDebut()) || date.isAfter(p.getDateDebut())) && date.isBefore(p.getDateFin().plusDays(1)));
    }

    @Override
    public void desactiverSuspensionsExpirees() {
        List<UserPenalise> actives = userPenaliseRepository.findAll().stream()
            .filter(UserPenalise::isActif)
            .toList();
        LocalDate today = LocalDate.now();
        for (UserPenalise up : actives) {
            if (up.getDateFin().isBefore(today)) {
                up.setActif(false);
                userPenaliseRepository.save(up);
            }
        }
    }

    @PostConstruct
    public void init() {
        desactiverSuspensionsExpirees();
    }
} 