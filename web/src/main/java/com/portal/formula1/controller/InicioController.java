/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.portal.formula1.controller;

import com.portal.formula1.model.UsuarioRegistrado;
import com.portal.formula1.service.AutentificacionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    @Autowired
    AutentificacionService autentificacionService;
    private static final String SESSION_USUARIO = "usuario";
    @GetMapping(value = "/")
    public ModelAndView home(HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        UsuarioRegistrado usuario = (UsuarioRegistrado) session.getAttribute("usuario");
        if (usuario != null) {
            if(usuario.getRol().equals("ADMIN")){
                 mv.setViewName("admin");
            }else{
                mv.setViewName("home");
            }      
            mv.getModel().put(SESSION_USUARIO, usuario);
            return mv;
        }
        mv.setViewName("inicioSesion");

        return mv;
    }    
    @PostMapping("/")
    public ModelAndView procesarFormulario(
            @RequestParam("usuario") String usuario,
            @RequestParam("contrasena") String contrasena,
            HttpSession session) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        ModelAndView mv = new ModelAndView();
        UsuarioRegistrado user =  autentificacionService.checkUser(usuario);
        if (user!=null && passwordEncoder.matches(contrasena, user.getContrasena())) {
            if(user.getRol().equals("ADMIN")){
                 mv.setViewName("admin");
            }else{
                mv.setViewName("home");
            }  
            mv.addObject(SESSION_USUARIO, user);
            session.setAttribute(SESSION_USUARIO, user);
        } else {
            mv.setViewName("inicioSesion");
            mv.addObject("error", "Credenciales inválidas, intente de nuevo.");
        }
        return mv;
    }
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Finaliza la sesión
        return "redirect:/";
    }
}
