package com.bibliotheque.service;

import com.bibliotheque.model.UserPenalise;
import com.bibliotheque.model.Adherent;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserPenaliseService {
    UserPenalise saveUserPenalise(UserPenalise userPenalise);
    Optional<UserPenalise> getUserPenaliseById(Long id);
    List<UserPenalise> getAllUserPenalises();
    void deleteUserPenalise(Long id);
    List<UserPenalise> getUserPenalisesByAdherent(Adherent adherent);
    boolean isAdherentPenalise(Adherent adherent);
    boolean isAdherentPenaliseAtDate(Adherent adherent, LocalDate date);
    void desactiverSuspensionsExpirees();
} 