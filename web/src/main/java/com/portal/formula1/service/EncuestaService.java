/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.portal.formula1.service;

import com.portal.formula1.model.Encuesta;
import com.portal.formula1.model.EncuestaArchivada;
import com.portal.formula1.model.Piloto;
import com.portal.formula1.model.PilotoArchivado;
import com.portal.formula1.repository.EncuestaArchivadaDAO;
import com.portal.formula1.repository.EncuestaDAO;
import com.portal.formula1.repository.PilotoArchivadoDAO;
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
    private EncuestaArchivadaDAO encuestaArchivadaDAO;

    @Autowired
    private PilotoArchivadoDAO pilotoArchivadoDAO;

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
    public Encuesta editarEncuesta(Encuesta encuesta, Set<Integer> pilotoIds) {
        if (encuesta.getFechaInicio() == null) {
            encuesta.setFechaInicio(LocalDateTime.now());
        }
        Set<Piloto> pilotos = new HashSet<>();
        for (Integer pilotoId : pilotoIds) {
            Piloto piloto = pilotoDAO.findById(pilotoId).orElseThrow(() -> new NoSuchElementException("Piloto no encontrado con id: " + pilotoId));
            pilotos.add(piloto);
        }
        encuesta.setPilotos(pilotos);
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


    public boolean estaPilotoEnEncuestaActiva(Integer pilotoId) {
        LocalDateTime fechaActual = LocalDateTime.now();
        List<Encuesta> encuestasActivas = encuestaDAO.findByFechaLimiteAfter(fechaActual);

        for (Encuesta encuesta : encuestasActivas) {
            if (encuesta.getPilotos().stream().anyMatch(piloto -> piloto.getDorsal().equals(pilotoId))) {
                return true;
            }
        }
        return false;
    }

    public void archivarEncuestasExpiradas() {
        List<Encuesta> encuestasExpiradas = encuestaDAO.findByFechaLimiteBefore(LocalDateTime.now());
        for (Encuesta encuesta : encuestasExpiradas) {
            if (!encuestaArchivadaDAO.existsByPermalink(encuesta.getPermalink())) { // Verificar si ya existe la encuesta archivada
                EncuestaArchivada archivada = new EncuestaArchivada(encuesta);

                // Guardar cada piloto archivado
                for (PilotoArchivado pilotoArchivado : archivada.getPilotosArchivados()) {
                    pilotoArchivadoDAO.save(pilotoArchivado);
                }
                encuestaArchivadaDAO.save(archivada);
                encuesta.setPilotos(new HashSet<>()); // Limpiar la relación con los pilotos
                encuestaDAO.save(encuesta);
            }
        }
    }
    public EncuestaArchivada obtenerEncuestaArchivadaPorPermalink(String permalink) {
        Optional<EncuestaArchivada> encuestaOptional = encuestaArchivadaDAO.findByPermalink(permalink);
        return encuestaOptional.orElseThrow(() -> new NoSuchElementException("Encuesta no encontrada con permalink: " + permalink));
    }

}
