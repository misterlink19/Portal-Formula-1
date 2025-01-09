/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.portal.formula1.repository;

import com.portal.formula1.model.UsuarioRegistrado;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    List<UsuarioRegistrado> findByEquipo_Id(Long equipo_id);
    @Transactional
    @Modifying
    @Query("DELETE FROM UsuarioRegistrado u WHERE u.usuario = :usuario")
    void deleteUsuarioRegistradoByUsuario(@Param("usuario") String usuario);

    @Transactional
    @Modifying
    @Query("UPDATE UsuarioRegistrado u SET u.contrasena = :nuevaContrasena WHERE u.usuario = :usuario")
    void actualizarContrasena(@Param("usuario") String usuario, @Param("nuevaContrasena") String nuevaContrasena);

    // Nuevo metodo para contar jefes de equipo por equipo
    @Query("SELECT COUNT(u) FROM UsuarioRegistrado u WHERE u.equipo.id = :equipoId AND u.rol = 'JEFE_DE_EQUIPO'")
    long countJefesDeEquipoPorEquipo(@Param("equipoId") Long equipoId);

    // Nuevo metodo para verificar si existe otro admin
    @Query("SELECT COUNT(u) FROM UsuarioRegistrado u WHERE u.rol = 'ADMIN' AND u.usuario != :usuarioActual")
    long countOtrosAdministradores(@Param("usuarioActual") String usuarioActual);

    @Query("SELECT u FROM UsuarioRegistrado u LEFT JOIN FETCH u.equipo WHERE u.usuario = :usuario")
    UsuarioRegistrado findByUsuarioWithEquipo(@Param("usuario") String usuario);


}
