package com.bibliotheque.service.impl;

import com.bibliotheque.model.TypeAdherent;
import com.bibliotheque.repository.TypeAdherentRepository;
import com.bibliotheque.service.TypeAdherentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TypeAdherentServiceImpl implements TypeAdherentService {

    private final TypeAdherentRepository typeAdherentRepository;

    @Autowired
    public TypeAdherentServiceImpl(TypeAdherentRepository typeAdherentRepository) {
        this.typeAdherentRepository = typeAdherentRepository;
    }

    @Override
    public TypeAdherent saveTypeAdherent(TypeAdherent typeAdherent) {
        return typeAdherentRepository.save(typeAdherent);
    }

    @Override
    public Optional<TypeAdherent> getTypeAdherentById(Long id) {
        return typeAdherentRepository.findById(id);
    }

    @Override
    public Optional<TypeAdherent> getTypeAdherentByNom(String nomType) {
        return typeAdherentRepository.findByNomType(nomType);
    }

    @Override
    public List<TypeAdherent> getAllTypeAdherents() {
        return typeAdherentRepository.findAll();
    }

    @Override
    public void deleteTypeAdherent(Long id) {
        typeAdherentRepository.deleteById(id);
    }

    @Override
    public boolean existsByNomType(String nomType) {
        return typeAdherentRepository.existsByNomType(nomType);
    }
} 