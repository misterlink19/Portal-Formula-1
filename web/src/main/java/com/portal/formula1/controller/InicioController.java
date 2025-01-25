/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.portal.formula1.controller;

import com.portal.formula1.model.CalendarioEvento;
import com.portal.formula1.model.Noticia;
import com.portal.formula1.model.UsuarioRegistrado;
import com.portal.formula1.service.AutentificacionService;
import com.portal.formula1.service.CalendarioEventoService;
import com.portal.formula1.service.NoticiaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import java.util.List;

/**
 *
 * @author fjavi
 */
@Controller
@RequestMapping("/")
public class InicioController {
    @Autowired
    private AutentificacionService autentificacionService;

    @Autowired
    private CalendarioEventoService calendarioEventoService;

    @Autowired
    private NoticiaService noticiaService;


    private static final String SESSION_USUARIO = "usuario";

    // Home para usuarios autenticados y no autenticados
    @GetMapping(value = "/")
    public ModelAndView home(HttpSession session) {
        ModelAndView mv = new ModelAndView("home");

        // Obtener noticias y eventos
        List<Noticia> noticias = noticiaService.obtenerNoticias();
        List<CalendarioEvento> eventos = calendarioEventoService.listarEventos();

        // Pasar las noticias y los eventos al modelo
        mv.addObject("noticias", noticias);
        mv.addObject("eventos", eventos);

        // Verificar si hay usuario autenticado y añadirlo al modelo
        UsuarioRegistrado usuario = (UsuarioRegistrado) session.getAttribute("usuario");
        if (usuario != null) {
            mv.addObject(SESSION_USUARIO, usuario);
        }

        return mv;
    }
    // Mostrar el formulario de inicio de sesión
    @GetMapping("/login")
    public ModelAndView login(HttpSession session) {
        UsuarioRegistrado usuario = (UsuarioRegistrado) session.getAttribute("usuario");

        // Si ya hay un usuario autenticado, redirigir a home
        if (usuario != null) {
            return new ModelAndView("redirect:/");
        }

        // Cargar la vista del formulario de inicio de sesión
        return new ModelAndView("inicioSesion");
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
            mv.addObject("error", "Correo electrónico o contraseña incorrectos");
        }
        return mv;
    }
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/";
    }



}
