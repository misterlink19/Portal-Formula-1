/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.portal.formula1.service;

import com.portal.formula1.model.Noticia;
import com.portal.formula1.repository.NoticiaDAO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

/**
 *
 * @author ACOES
 */


@Service
public class NoticiaService {

    @Autowired
    private NoticiaDAO noticiaDAO;

    @Autowired
    private ImagenService imagenService;

    @Transactional
    public Noticia crearNoticia(String titulo, MultipartFile imagen, String texto) {
        Noticia noticia = new Noticia(titulo, null, texto);
        try {
            if (imagen != null && !imagen.isEmpty()) {
                String nombreImagen = imagenService.guardarImagen(imagen);
                noticia.setImagen(nombreImagen); // Guarda el nombre de la imagen en el objeto noticia
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la imagen: " + e.getMessage());
        }

        String permalink = generateUniquePermalink(titulo);
        noticia.setPermalink(permalink);
        return noticiaDAO.save(noticia);
    }

    private String generateUniquePermalink(String titulo) {
        String basePermalink = generatePermalink(titulo);
        String permalink = basePermalink;
        int suffix = 1;

        // Verificar si el permalink existe, y agregar un sufijo si es necesario
        while (noticiaDAO.existsByPermalink(permalink)) {
            permalink = basePermalink + "-" + suffix;
            suffix++;
        }

        return permalink;
    }

    private String generatePermalink(String titulo) {
        // Convierte el título a minúsculas, elimina caracteres especiales y reemplaza espacios por guiones
        String normalizedTitle = Normalizer.normalize(titulo, Normalizer.Form.NFD);
        return normalizedTitle.replaceAll("[^\\p{ASCII}]", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-");
    }


    public List<Noticia> obtenerNoticias() {
        return noticiaDAO.findAllByOrderByFechaPublicacionDesc();
    }
    
    public Noticia obtenerNoticiaPorId(Integer id) {
        return noticiaDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Noticia no encontrada"));
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

    public Noticia obtenerNoticiaParaEdicion(Integer id) {
        Noticia noticia = noticiaDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Noticia no encontrada"));

        // Validar si tiene más de 24 horas
        if (noticia.getFechaPublicacion().isBefore(LocalDateTime.now().minusDays(1))) {
            throw new IllegalStateException("No se puede editar una noticia con más de 24 horas de publicación");
        }

        return noticia;
    }

    @Transactional
    public void actualizarNoticia(Integer id, String titulo, MultipartFile imagen, String texto) {
        Noticia noticia = obtenerNoticiaParaEdicion(id); // Valida antigüedad

        // Actualizar campos editables
        noticia.setTitulo(titulo);
        noticia.setTexto(texto);

        // Manejo de imagen (similar al metodo de creación)
        try {
            if (imagen != null && !imagen.isEmpty()) {
                String nombreImagen = imagenService.guardarImagen(imagen);
                noticia.setImagen(nombreImagen);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al actualizar la imagen: " + e.getMessage());
        }

        noticiaDAO.save(noticia);
    }


    public class EntityNotFoundException extends RuntimeException {
        public EntityNotFoundException(String message) {
            super(message);
        }
    }

    public class NoticiaNoEditableException extends RuntimeException {
        public NoticiaNoEditableException(String message) {
            super(message);
        }
    }



}
