package com.portal.formula1.repository;

import com.portal.formula1.model.Circuito;
import com.portal.formula1.model.Coches;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CircuitoDAO  extends JpaRepository<Circuito, Long> {
}
