package com.portal.formula1.repository;

import com.portal.formula1.model.Piloto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PilotoDAO extends JpaRepository<Piloto, Long> {
}
