package com.portal.formula1.service;


import com.portal.formula1.model.Rol;
import com.portal.formula1.model.UsuarioRegistrado;
import com.portal.formula1.repository.UsuarioRegistradoDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRegistradoDAO usuarioRegistradoDAO;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder; // Declaración del passwordEncoder



    @Transactional
    public void registrarUsuario(UsuarioRegistrado usuario) {
        if(!usuario.getRol().equals(Rol.USUARIO_BASICO)){
            usuario.setRolSolicitado(usuario.getRol());
            usuario.setRol(Rol.USUARIO_BASICO);
        }
        usuarioRegistradoDAO.save(usuario);
    }

    @Transactional
    public void actualizarUsuario(UsuarioRegistrado usuario) {
        usuarioRegistradoDAO.save(usuario);
    }


    @Transactional
    public void validarUsuarios(List<String> usuariosIds) {
        usuariosIds.forEach(id -> {
            UsuarioRegistrado usuario = usuarioRegistradoDAO.findById(id).orElse(null);
            if (usuario != null) {
                usuario.setValidacion(true);
                usuarioRegistradoDAO.save(usuario);
            }
        });
    }

    @Transactional
    public void validarUsuariosRol(List<String> usuariosIds) {
        usuariosIds.forEach(id -> {
            UsuarioRegistrado usuario = usuarioRegistradoDAO.findById(id).orElse(null);
            if (usuario != null && usuario.getRolSolicitado() != null) {
                usuario.setRol(usuario.getRolSolicitado());
                usuario.setRolSolicitado(null);
                usuarioRegistradoDAO.save(usuario);
            }
        });
    }

    public List<UsuarioRegistrado> obtenerTodosLosUsuarios() {
        return usuarioRegistradoDAO.findAll();
    }

    public UsuarioRegistrado obtenerUsuarioPorUsuario(String usuario) {
        return usuarioRegistradoDAO.findById(usuario).orElse(null);
    }

    public List<UsuarioRegistrado> filtrarPorRol(String rol) {
        return usuarioRegistradoDAO.findByRol(rol);
    }

    public List<UsuarioRegistrado> buscarPorNombre(String nombre) {
        return usuarioRegistradoDAO.findByNombreContainingIgnoreCase(nombre);
    }

    public List<UsuarioRegistrado> obtenerUsuariosNoValidados() {
        return usuarioRegistradoDAO.findByValidacionFalse();
    }

    public List<UsuarioRegistrado> ordenarPorFechaRegistro() {
        return usuarioRegistradoDAO.findAllByOrderByFechaRegistroDesc();
    }

    public List<UsuarioRegistrado> filtrarPorRolYNombre(String rol, String nombre) {
        return usuarioRegistradoDAO.findByRolAndNombreContainingIgnoreCase(rol, nombre);
    }

    public List<UsuarioRegistrado> obtenerUsuarioPorIdEquipo(Long idEquipo) {
        return usuarioRegistradoDAO.findByEquipo_Id(idEquipo);
    }

    public void eliminarUsuarioRespondable(String usuario){
        usuarioRegistradoDAO.deleteUsuarioRegistradoByUsuario(usuario);
    }

    // metodo generar contraseña
    private String generarContrasenaTemporal() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder contrasenaTemporal = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 8; i++) {
            contrasenaTemporal.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }

        return contrasenaTemporal.toString();
    }

    //Servicio para que un JEFE_DE_EQUIPO pueda crear un nuevo usuario JEFE_DE_EQUIPO
    @Transactional
    public UsuarioRegistrado crearResponsableEquipo(UsuarioRegistrado creador, UsuarioRegistrado nuevoResponsable) {
        // Validar que el creador sea JEFE_DE_EQUIPO y tenga un equipo asignado
        if (creador.getRol() != Rol.JEFE_DE_EQUIPO || creador.getEquipo() == null) {
            throw new IllegalStateException("Solo los jefes de equipo pueden crear otros responsables");
        }

        // Verificar si el usuario ya existe
        if (usuarioRegistradoDAO.findById(nuevoResponsable.getUsuario()).isPresent()) {
            throw new IllegalStateException("El nombre de usuario ya existe");
        }

        // Validar campos obligatorios del nuevo responsable
        if (nuevoResponsable.getUsuario() == null || nuevoResponsable.getUsuario().trim().isEmpty() ||
                nuevoResponsable.getNombre() == null || nuevoResponsable.getNombre().trim().isEmpty() ||
                nuevoResponsable.getEmail() == null || nuevoResponsable.getEmail().trim().isEmpty()) {
            throw new IllegalStateException("Todos los campos son obligatorios");
        }

        try {
            // Generar contraseña temporal
            String contrasenaTemporal = generarContrasenaTemporal();

            // Configurar el nuevo responsable
            nuevoResponsable.setRol(Rol.JEFE_DE_EQUIPO);
            nuevoResponsable.setEquipo(creador.getEquipo());
            nuevoResponsable.setContrasena(passwordEncoder.encode(contrasenaTemporal));
            nuevoResponsable.setValidacion(true);
            nuevoResponsable.setFechaRegistro(LocalDateTime.now());
            nuevoResponsable.setRolSolicitado(null);

            // Guardar el nuevo responsable
            UsuarioRegistrado responsableGuardado = usuarioRegistradoDAO.save(nuevoResponsable);

            // Establecer la contraseña temporal sin encriptar para mostrarla una única vez
            UsuarioRegistrado usuarioTemp = new UsuarioRegistrado();
            usuarioTemp.setContrasena(contrasenaTemporal);
            return usuarioTemp;
        } catch (Exception e) {
            throw new IllegalStateException("Error al crear el nuevo responsable: " + e.getMessage());
        }
    }


}
