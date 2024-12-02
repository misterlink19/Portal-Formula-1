package com.portal.formula1.service;

import com.portal.formula1.model.UsuarioRegistrado;
import com.portal.formula1.repository.UsuarioRegistradoDAO;
import com.portal.formula1.repository.UsuarioRegistradoDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRegistradoDAO usuarioRegistradoDAO;

    @Transactional
    public void registrarUsuario(UsuarioRegistrado usuario) {
        usuarioRegistradoDAO.save(usuario);
    }

    @Transactional
    public void actualizarUsuario(UsuarioRegistrado usuario) {
        usuarioRegistradoDAO.save(usuario);
    }

}
