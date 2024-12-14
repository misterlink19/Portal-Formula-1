package com.portal.formula1.service;

import com.portal.formula1.model.Rol;
import com.portal.formula1.model.UsuarioRegistrado;
import com.portal.formula1.repository.UsuarioRegistradoDAO;
import com.portal.formula1.repository.UsuarioRegistradoDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRegistradoDAO usuarioRegistradoDAO;

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




}
