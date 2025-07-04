package com.bibliotheque.service;

import com.bibliotheque.model.TypePret;
import java.util.List;
import java.util.Optional;

public interface TypePretService {
    TypePret saveTypePret(TypePret typePret);
    Optional<TypePret> getTypePretById(Long id);
    Optional<TypePret> getTypePretByNom(String nomTypePret);
    List<TypePret> getAllTypePrets();
    void deleteTypePret(Long id);
    boolean existsByNomTypePret(String nomTypePret);
} 