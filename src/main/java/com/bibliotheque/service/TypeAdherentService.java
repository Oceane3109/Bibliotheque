package com.bibliotheque.service;

import com.bibliotheque.model.TypeAdherent;
import java.util.List;
import java.util.Optional;

public interface TypeAdherentService {
    TypeAdherent saveTypeAdherent(TypeAdherent typeAdherent);
    Optional<TypeAdherent> getTypeAdherentById(Long id);
    Optional<TypeAdherent> getTypeAdherentByNom(String nomType);
    List<TypeAdherent> getAllTypeAdherents();
    void deleteTypeAdherent(Long id);
    boolean existsByNomType(String nomType);
} 