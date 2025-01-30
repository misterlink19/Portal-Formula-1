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

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @GetMapping(value = "/")
    public ModelAndView home(HttpSession session) {
        ModelAndView mv = new ModelAndView("home");
        configurarModelo(mv, session);
        return mv;
    }



    // Mostrar el formulario de inicio de sesión
    @GetMapping("/login")
    public ModelAndView login(HttpSession session) {
        UsuarioRegistrado usuario = (UsuarioRegistrado) session.getAttribute("usuario");

        // Si ya hay un usuario autenticado, redirigir a home
        if (usuario != null) {
            ModelAndView mv = new ModelAndView("redirect:/");
            configurarModelo(mv, session);
            return mv;
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
            configurarModelo(mv, session);
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

    private void configurarModelo(ModelAndView mv, HttpSession session) {
        // Obtener noticias y eventos
        List<Noticia> noticias = noticiaService.obtenerNoticias();
        List<CalendarioEvento> eventos = calendarioEventoService.listarEventos();

        // Ordenar eventos por fecha
        eventos = eventos.stream()
                .sorted(Comparator.comparing(CalendarioEvento::getFecha))
                .toList();

        // Filtrar próximos eventos de esta semana
        LocalDate hoy = LocalDate.now();
        LocalDate finDeSemana = hoy.plusDays(7); // Siete días desde hoy
        List<CalendarioEvento> proximosEventos = eventos.stream()
                .filter(evento -> evento.getFecha().isAfter(hoy.minusDays(1)) && evento.getFecha().isBefore(finDeSemana))
                .toList();

        // Convertir eventos a JSON para FullCalendar
        List<Map<String, Object>> eventosCalendario = eventos.stream().map(evento -> {
            Map<String, Object> eventoJson = new HashMap<>();
            eventoJson.put("id", evento.getId());
            eventoJson.put("title", evento.getNombreEvento());
            eventoJson.put("start", evento.getFecha().toString()); // Formato ISO
            eventoJson.put("url", "/calendario/evento/" + evento.getId());
            if (evento.getFecha().isBefore(LocalDate.now())) {
                eventoJson.put("color", "gray"); // Eventos pasados
            } else if (evento.getFecha().isEqual(LocalDate.now())) {
                eventoJson.put("color", "red"); // Evento de hoy
            } else {
                eventoJson.put("color", "blue"); // Eventos futuros
            }
            return eventoJson;
        }).toList();

        // Pasar noticias y eventos al modelo
        mv.addObject("noticias", noticias);
        mv.addObject("eventosCalendario", eventosCalendario); // Esto se procesará en el HTML
        mv.addObject("proximosEventos", proximosEventos); // Próximos eventos

        // Verificar si hay usuario autenticado y añadirlo al modelo
        UsuarioRegistrado usuario = (UsuarioRegistrado) session.getAttribute("usuario");
        if (usuario != null) {
            mv.addObject(SESSION_USUARIO, usuario);
        }
    }


}
