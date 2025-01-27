package com.portal.formula1.repository;

import com.portal.formula1.model.CalendarioEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CalendarioEventoDAO extends JpaRepository<CalendarioEvento, Long> {
    List<CalendarioEvento> findByFecha(LocalDate fecha);
}
