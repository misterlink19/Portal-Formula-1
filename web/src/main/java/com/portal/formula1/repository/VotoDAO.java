/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.portal.formula1.repository;

import com.portal.formula1.model.Encuesta;
import com.portal.formula1.model.EncuestaArchivada;
import com.portal.formula1.model.Voto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 *
 * @author Misterlink
 */
@Repository
public interface VotoDAO extends JpaRepository<Voto, Long>{

    List<Voto> findByEncuesta(Encuesta encuesta);

    boolean existsByCorreoVotanteAndEncuesta(String correoVotante, Encuesta encuesta);

    List<Voto> findVotoByCorreoVotante(String correoVotante);

    Voto findVotoByCorreoVotanteAndEncuesta_Permalink(String correoVotante, String encuestaPermalink);
}
