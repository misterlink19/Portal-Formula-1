/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.portal.formula1.service;
import com.portal.formula1.model.UsuarioRegistrado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.portal.formula1.repository.UsuarioRegistradoDAO;

/**
 *
 * @author fjavi
 */
@Service
public class AutentificacionService {
    
    @Autowired
    private UsuarioRegistradoDAO usuarioRegistradoDAO;
    
    public UsuarioRegistrado checkUser(String usuario){
        return usuarioRegistradoDAO.findById(usuario)
                .orElse(null);
    }
    
    
}
