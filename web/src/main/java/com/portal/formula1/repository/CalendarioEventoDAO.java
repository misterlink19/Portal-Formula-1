package com.portal.formula1.repository;

import com.portal.formula1.model.CalendarioEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CalendarioEventoDAO extends JpaRepository<CalendarioEvento, Long> {
    List<CalendarioEvento> findByFecha(LocalDate fecha);
    Optional<CalendarioEvento> findByNombreEventoAndFecha(String nombreEvento, LocalDate fecha);
}
