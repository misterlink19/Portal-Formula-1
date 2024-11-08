/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.portal.formula1.service;

import com.portal.formula1.model.Noticia;
import com.portal.formula1.repository.NoticiaDAO;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 *
 * @author ACOES
 */


@Service
public class NoticiaService {
    
    private final NoticiaDAO noticiaDAO;
    private static final int MAX_PERMALINK_LENGTH = 100;
    
    public NoticiaService(NoticiaDAO noticiaDAO) {
        this.noticiaDAO = noticiaDAO;
    }
    
    public Noticia guardarNoticia(Noticia noticia) {
        noticia.setPermalink(generarPermalink(noticia.getTitulo()));
        return noticiaDAO.save(noticia);
    }
    
    public List<Noticia> obtenerNoticias() {
        return noticiaDAO.findAllByOrderByFechaPublicacionDesc();
    }
    
    public Noticia obtenerNoticiaPorId(Integer id) {
        return noticiaDAO.findById(id)
                .orElse(null);
    }
    
    @Transactional
    public void eliminarNoticia(Integer id) {
        if (noticiaDAO.existsById(id)) {
            noticiaDAO.deleteById(id);
        }
    }
    
    public List<Noticia> buscarNoticias(String query) {
        return noticiaDAO.findByTituloContainingIgnoreCaseOrTextoContainingIgnoreCase(query, query);
    }
    
    private String generarPermalink(String titulo) {
        String baseUrl = "http://www.tu-dominio.com/";

        // Genera el permalink a partir del título
        String permalink = titulo.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-")
                .trim();

        // Asegurarse de que no exceda los MAX_PERMALINK_LENGTH caracteres
        if (permalink.length() > MAX_PERMALINK_LENGTH) {
            permalink = permalink.substring(0, MAX_PERMALINK_LENGTH);
        }

        // Asegurar unicidad con sufijo si el permalink ya existe
        String basePermalink = permalink;
        int count = 1;
        while (noticiaDAO.existsByPermalink(baseUrl + permalink)) {
            permalink = basePermalink + "-" + count++;

            // Trunca a MAX_PERMALINK_LENGTH caracteres nuevamente después de añadir el sufijo
            if (permalink.length() > MAX_PERMALINK_LENGTH) {
                permalink = permalink.substring(0, MAX_PERMALINK_LENGTH);
            }
        }

        // Concatenar con la base URL
        return baseUrl + permalink;
    }


}
