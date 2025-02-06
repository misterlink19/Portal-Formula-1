package com.portal.formula1.repository;

import com.portal.formula1.model.EncuestaArchivada;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EncuestaArchivadaDAO extends JpaRepository<EncuestaArchivada, Long> {
    Optional<EncuestaArchivada> findByPermalink(String permalink);

    boolean existsByPermalink(String permalink);
}
