package com.bibliotheque.service.impl;

import com.bibliotheque.model.TypePret;
import com.bibliotheque.repository.TypePretRepository;
import com.bibliotheque.service.TypePretService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TypePretServiceImpl implements TypePretService {
    private final TypePretRepository typePretRepository;

    @Autowired
    public TypePretServiceImpl(TypePretRepository typePretRepository) {
        this.typePretRepository = typePretRepository;
    }

    @Override
    public TypePret saveTypePret(TypePret typePret) {
        return typePretRepository.save(typePret);
    }

    @Override
    public Optional<TypePret> getTypePretById(Long id) {
        return typePretRepository.findById(id);
    }

    @Override
    public Optional<TypePret> getTypePretByNom(String nomTypePret) {
        return typePretRepository.findByNomTypePret(nomTypePret);
    }

    @Override
    public List<TypePret> getAllTypePrets() {
        return typePretRepository.findAll();
    }

    @Override
    public void deleteTypePret(Long id) {
        typePretRepository.deleteById(id);
    }

    @Override
    public boolean existsByNomTypePret(String nomTypePret) {
        return typePretRepository.existsByNomTypePret(nomTypePret);
    }
} 