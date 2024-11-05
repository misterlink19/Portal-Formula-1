/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.portal.formula1.service;

import com.portal.formula1.model.Noticia;
import com.portal.formula1.repository.NoticiaDAO;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author ACOES
 */


@Service
public class NoticiaService {
    
    @Autowired
    private final NoticiaDAO noticiaDAO;
    
    public NoticiaService(NoticiaDAO noticiaDAO) {
        this.noticiaDAO = noticiaDAO;
    }
    
    public Noticia guardarNoticia(Noticia noticia) {
        noticia.setFechaPublicacion(LocalDateTime.now());
        return noticiaDAO.save(noticia);
    }
    
    public List<Noticia> obtenerNoticias() {
        return noticiaDAO.findAllByOrderByFechaPublicacionDesc();
    }
}
