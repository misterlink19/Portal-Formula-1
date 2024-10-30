/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.portal.formula1.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author fjavi
 */
@Controller
@RequestMapping("/")
public class InicioController {
    private static final String SESSION_USUARIO = "usuario";
    @GetMapping(value = "/")
    public ModelAndView home(HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        String usuario = (String) session.getAttribute("usuario");
        if (usuario != null) {
            mv.setViewName("home");
            mv.getModel().put(SESSION_USUARIO, usuario);
            return mv;
        }
        mv.setViewName("inicioSesion");

        return mv;
    }    
    @PostMapping("/")
    public ModelAndView procesarFormulario(
            @RequestParam("usuario") String username,
            @RequestParam("contrasena") String password,
            HttpSession session) {
        
        ModelAndView mv = new ModelAndView();

        if ("prueba@uah.es".equals(username) && "12345".equals(password)) {
            session.setAttribute(SESSION_USUARIO, username);
            mv.setViewName("home");
            mv.addObject(SESSION_USUARIO, username);
        } else {
            mv.setViewName("inicioSesion");
            mv.addObject("error", "Credenciales inválidas, intente de nuevo.");
        }

        return mv;
    }
}
