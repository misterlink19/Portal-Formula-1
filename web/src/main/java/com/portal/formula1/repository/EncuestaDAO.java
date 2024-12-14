/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.portal.formula1.repository;

import com.portal.formula1.model.Encuesta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 *
 * @author Misterlink
 */
@Repository
public interface EncuestaDAO extends JpaRepository<Encuesta, String>{

    /**
     * Encuentra la próxima encuesta basada en la fecha límite futura más cercana a now.
     * */
    Optional<Encuesta> findFirstByFechaLimiteAfterOrderByFechaLimiteAsc(LocalDateTime now);

    /**
     *
     * Encuentra la encuesta más recientemente creada en base a la fecha de inicio.
     */
    Optional<Encuesta> findFirstByOrderByFechaInicioDesc();


}
