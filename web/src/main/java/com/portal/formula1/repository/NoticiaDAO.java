/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.portal.formula1.repository;

import com.portal.formula1.model.Noticia;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 *
 * @author ACOES
 */
@Repository
public interface NoticiaDAO extends JpaRepository<Noticia, Integer> {
    
    boolean existsByPermalink(String permalink);
    
    List<Noticia> findAllByOrderByFechaPublicacionDesc();
    // Método para buscar noticias por título o texto, ignorando mayúsculas y minúsculas
    List<Noticia> findByTituloContainingIgnoreCaseOrTextoContainingIgnoreCase(String titulo, String texto);
}
