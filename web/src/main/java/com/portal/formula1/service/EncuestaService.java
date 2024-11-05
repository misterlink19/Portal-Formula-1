/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.portal.formula1.service;

import com.portal.formula1.model.Encuesta;
import com.portal.formula1.repository.EncuestaDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.time.LocalDateTime;
import java.util.List;
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

    @PersistenceContext //Para manejar pilotos directamente de la base de datos
    private EntityManager entityManager;

    public List<Encuesta> getAllEncuestas() {
        return encuestaDAO.findAll();
    }

    public Encuesta crearEncuesta(Encuesta encuesta) {
        if (encuesta.getFechaInicio() == null) {
            encuesta.setFechaInicio(LocalDateTime.now());
        }
        return encuestaDAO.save(encuesta);
    }

    public Encuesta obtenerEncuestaPorPermalink(String permalink) {
        return encuestaDAO.findById(permalink).orElseThrow();
    }
    
    public List<Object[]> getTodosLosPilotos(){
        String sql ="SELECT Nombre, Apellidos, Siglas, Dorsal, RutaImagen, Pais, Twitter FROM Piloto ";
        Query query = entityManager.createNativeQuery(sql);
        return query.getResultList();
    }
}
