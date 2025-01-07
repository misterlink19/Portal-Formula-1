/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.portal.formula1.service;

import com.portal.formula1.model.Encuesta;
import com.portal.formula1.model.Piloto;
import com.portal.formula1.repository.EncuestaDAO;
import com.portal.formula1.repository.PilotoDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Misterlink
 */
@Service
public class EncuestaService {

    @Autowired
    private EncuestaDAO encuestaDAO;

    @Autowired
    private PilotoDAO pilotoDAO;

    public List<Encuesta> getAllEncuestas() {
        return encuestaDAO.findAll();
    }

    public Encuesta crearEncuesta(Encuesta encuesta, Set<Integer> pilotoIds) {
        if (encuesta.getFechaInicio() == null) {
            encuesta.setFechaInicio(LocalDateTime.now());
        }
        Set<Piloto> pilotos = new HashSet<>();
        for (Integer pilotoId : pilotoIds) {
            Piloto piloto = pilotoDAO.findById(pilotoId).orElseThrow(() -> new NoSuchElementException("Piloto no encontrado con id: " + pilotoId));
            pilotos.add(piloto);
        }
        encuesta.getPilotos().addAll(pilotos);
        return encuestaDAO.save(encuesta);
    }

    public Encuesta obtenerEncuestaPorPermalink(String permalink) {
        Optional<Encuesta> encuestaOptional = encuestaDAO.findById(permalink);
        return encuestaOptional.orElseThrow(() -> new NoSuchElementException("Encuesta no encontrada con permalink: " + permalink));
    }

    public Encuesta obtenerUltimaEncuestaDisponible() {
        return encuestaDAO.findFirstByOrderByFechaInicioDesc()
                .orElseThrow(() -> new NoSuchElementException("No hay encuestas disponibles en este momento"));
    }
    public void eliminarEncuesta(String permalink) {
        Encuesta encuesta = encuestaDAO.findById(permalink).orElseThrow(() -> new NoSuchElementException("Encuesta no encontrada con permalink: " + permalink));
        encuestaDAO.delete(encuesta);
    }

}
