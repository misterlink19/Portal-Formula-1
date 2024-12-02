/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.portal.formula1.repository;

import com.portal.formula1.model.UsuarioRegistrado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author fjavi
 */
@Repository
public interface UsuarioRegistradoDAO extends JpaRepository<UsuarioRegistrado,String> {
    List<UsuarioRegistrado> findByRol(String rol);
    List<UsuarioRegistrado> findByNombreContainingIgnoreCase(String nombre);
    List<UsuarioRegistrado> findAllByOrderByFechaRegistroDesc();
    List<UsuarioRegistrado> findByValidacionFalse();
    UsuarioRegistrado findByEmail(String email);
    List<UsuarioRegistrado> findByRolAndNombreContainingIgnoreCase(String rol, String nombre);

}
