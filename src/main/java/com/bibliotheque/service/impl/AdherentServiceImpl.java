package com.bibliotheque.service.impl;

import com.bibliotheque.model.Adherent;
import com.bibliotheque.model.TypeAdherent;
import com.bibliotheque.model.User;
import com.bibliotheque.repository.AdherentRepository;
import com.bibliotheque.service.AdherentService;
import com.bibliotheque.service.UserPenaliseService;
import com.bibliotheque.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AdherentServiceImpl implements AdherentService {

    private final AdherentRepository adherentRepository;
    private final UserPenaliseService userPenaliseService;
    private final UserService userService;

    @Autowired
    public AdherentServiceImpl(AdherentRepository adherentRepository, UserPenaliseService userPenaliseService, UserService userService) {
        this.adherentRepository = adherentRepository;
        this.userPenaliseService = userPenaliseService;
        this.userService = userService;
    }

    @Override
    public Adherent saveAdherent(Adherent adherent) {
        return adherentRepository.save(adherent);
    }

    @Override
    public Optional<Adherent> getAdherentById(Long id) {
        return adherentRepository.findById(id);
    }

    @Override
    public Optional<Adherent> getAdherentByUser(User user) {
        return adherentRepository.findByUser(user);
    }

    @Override
    public Optional<Adherent> getAdherentByEmail(String email) {
        return adherentRepository.findByEmail(email);
    }

    @Override
    public List<Adherent> getAllAdherents() {
        return adherentRepository.findAll();
    }

    @Override
    public List<Adherent> getAdherentsByType(TypeAdherent typeAdherent) {
        return adherentRepository.findByTypeAdherent(typeAdherent);
    }

    @Override
    public List<Adherent> getAdherentsWithActivePrets() {
        return adherentRepository.findAllWithActivePrets();
    }

    @Override
    public void deleteAdherent(Long id) {
        adherentRepository.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return adherentRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUser(User user) {
        return adherentRepository.existsByUser(user);
    }

    @Override
    public boolean peutEmprunterDomicile(Long adherentId) {
        return getAdherentById(adherentId)
                .map(Adherent::peutEmprunterDomicile)
                .orElse(false);
    }

    @Override
    public boolean peutEmprunterSurPlace(Long adherentId) {
        return getAdherentById(adherentId)
                .map(Adherent::peutEmprunterSurPlace)
                .orElse(false);
    }

    @Override
    @Transactional
    public void incrementerLivresEmpruntesDomicile(Long adherentId) {
        Adherent adherent = getAdherentById(adherentId)
                .orElseThrow(() -> new EntityNotFoundException("Adhérent non trouvé avec l'ID: " + adherentId));
        
        if (adherent.peutEmprunterDomicile()) {
            adherent.setLivresEmpruntesDomicile(adherent.getLivresEmpruntesDomicile() + 1);
            adherentRepository.save(adherent);
        } else {
            throw new IllegalStateException("L'adhérent a atteint sa limite d'emprunts à domicile");
        }
    }

    @Override
    @Transactional
    public void incrementerLivresEmpruntesSurPlace(Long adherentId) {
        Adherent adherent = getAdherentById(adherentId)
                .orElseThrow(() -> new EntityNotFoundException("Adhérent non trouvé avec l'ID: " + adherentId));
        
        if (adherent.peutEmprunterSurPlace()) {
            adherent.setLivresEmpruntesSurplace(adherent.getLivresEmpruntesSurplace() + 1);
            adherentRepository.save(adherent);
        } else {
            throw new IllegalStateException("L'adhérent a atteint sa limite d'emprunts sur place");
        }
    }

    @Override
    @Transactional
    public void decrementerLivresEmpruntesDomicile(Long adherentId) {
        Adherent adherent = getAdherentById(adherentId)
                .orElseThrow(() -> new EntityNotFoundException("Adhérent non trouvé avec l'ID: " + adherentId));
        System.out.println("[DEBUG] Compteur livresEmpruntesDomicile avant décrémentation pour adhérent " + adherentId + " : " + adherent.getLivresEmpruntesDomicile());
        if (adherent.getLivresEmpruntesDomicile() > 0) {
            adherent.setLivresEmpruntesDomicile(adherent.getLivresEmpruntesDomicile() - 1);
            adherentRepository.save(adherent);
        } else {
            throw new IllegalStateException("L'adhérent n'a pas de livres empruntés à domicile");
        }
    }

    @Override
    @Transactional
    public void decrementerLivresEmpruntesSurPlace(Long adherentId) {
        Adherent adherent = getAdherentById(adherentId)
                .orElseThrow(() -> new EntityNotFoundException("Adhérent non trouvé avec l'ID: " + adherentId));
        
        if (adherent.getLivresEmpruntesSurplace() > 0) {
            adherent.setLivresEmpruntesSurplace(adherent.getLivresEmpruntesSurplace() - 1);
            adherentRepository.save(adherent);
        } else {
            throw new IllegalStateException("L'adhérent n'a pas de livres empruntés sur place");
        }
    }

    @Override
    public boolean isPenalise(Long adherentId) {
        return getAdherentById(adherentId)
            .map(userPenaliseService::isAdherentPenalise)
            .orElse(false);
    }

    @Override
    public Optional<Adherent> getAdherentByUserUsername(String username) {
        return userService.getUserByUsername(username).flatMap(this::getAdherentByUser);
    }

    @Override
    public Optional<Adherent> getAdherentByUserUsernameWithPrets(String username) {
        return adherentRepository.findByUserUsernameWithPrets(username);
    }
} 