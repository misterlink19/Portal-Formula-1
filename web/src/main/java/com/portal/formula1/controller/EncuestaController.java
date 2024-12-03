/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.portal.formula1.controller;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import com.portal.formula1.model.Encuesta;
import com.portal.formula1.model.Rol;
import com.portal.formula1.model.UsuarioRegistrado;
import com.portal.formula1.repository.VotoDAO;
import com.portal.formula1.service.EncuestaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Misterlink
 */
@Controller
@RequestMapping("/encuestas")
public class EncuestaController {

    private static final Logger logger = LoggerFactory.getLogger(EncuestaController.class);

    @Autowired
    private EncuestaService encuestaService;

    @Autowired
    private VotoDAO votoDAO;

    @GetMapping
    public ModelAndView mostrarMenuEncuestas(HttpSession session) {
        logger.debug("Entrando a mostrarMenuEncuestas");
        UsuarioRegistrado usuario = (UsuarioRegistrado) session.getAttribute("usuario");
        // Verificar si el usuario es ADMIN
        if (usuario == null || usuario.getRol() != Rol.ADMIN) {
            return new ModelAndView("error").addObject("mensajeError", "Acceso denegado.");
        }
        return new ModelAndView("encuesta");
    }

    @GetMapping({"/crearEncuestas", "/crearEncuesta"})
    public ModelAndView mostrarFormularioCreacion(HttpSession session) {
        logger.debug("Entrando a mostrarFormularioCreacion");
        UsuarioRegistrado usuario = (UsuarioRegistrado) session.getAttribute("usuario");

        // Verificar si el usuario es ADMIN
        if (usuario == null || usuario.getRol() != Rol.ADMIN) {
            return new ModelAndView("error").addObject("mensajeError", "Acceso denegado.");
        }

        ModelAndView mv = new ModelAndView("encuestas/crearEncuesta");
        try {
            mv.addObject("encuesta", new Encuesta());
            List<Object[]> pilotos = encuestaService.getTodosLosPilotos();
            mv.addObject("pilotos", pilotos);
        } catch (Exception e) {
            logger.error("Error al cargar el formulario de creación: ", e);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Error al cargar el formulario de creación.");
        }
        return mv;
    }

    @PostMapping
    public ModelAndView crearEncuesta(@ModelAttribute Encuesta encuesta, @RequestParam Set<String> pilotosSeleccionados, HttpSession session) {
        logger.debug("Entrando a crearEncuesta");
        UsuarioRegistrado usuario = (UsuarioRegistrado) session.getAttribute("usuario");

        // Verificar si el usuario es ADMIN
        if (usuario == null || usuario.getRol() != Rol.ADMIN) {
            return new ModelAndView("error").addObject("mensajeError", "Acceso denegado.");
        }

        ModelAndView mv = new ModelAndView();
        try {
            Set<String> pilotoSet = new HashSet<>(pilotosSeleccionados);
            Encuesta nuevaEncuesta = encuestaService.crearEncuesta(encuesta, pilotoSet);
            mv.setViewName("redirect:/encuestas/" + nuevaEncuesta.getPermalink());
        } catch (Exception e) {
            logger.error("Error al crear la encuesta: ", e);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Error al crear la encuesta.");
        }
        return mv;
    }

    @GetMapping("/{permalink}")
    public ModelAndView mostrarEncuesta(@PathVariable String permalink, HttpSession session) {
        logger.debug("Entrando a mostrarEncuesta con permalink: {}", permalink);
        UsuarioRegistrado usuario = (UsuarioRegistrado) session.getAttribute("usuario");

        // Verificar si el usuario es ADMIN
        if (usuario == null || usuario.getRol() != Rol.ADMIN) {
            return new ModelAndView("error").addObject("mensajeError", "Acceso denegado.");
        }

        ModelAndView mv = new ModelAndView("encuestas/verEncuesta");
        try {
            Encuesta encuesta = encuestaService.obtenerEncuestaPorPermalink(permalink);
            List<Object[]> pilotos = encuestaService.getTodosLosPilotos();
            mv.addObject("encuesta", encuesta);
            mv.addObject("pilotos", pilotos);
        }catch (NoSuchElementException e) {
            logger.error("Encuesta no encontrada con permalink: {}", permalink);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Encuesta no encontrada.");
        }
        return mv;
    }

    @GetMapping("/listar")
    public ModelAndView mostrarListaEncuestas(HttpSession session) {
        logger.debug("Entrando a mostrarListaEncuestas");
        UsuarioRegistrado usuario = (UsuarioRegistrado) session.getAttribute("usuario");
        ModelAndView mv = new ModelAndView();
        if (usuario == null || usuario.getRol() != Rol.ADMIN) {
            logger.error("El listado de Encuesta no esta disponible para estos roles");
            mv.setViewName("error");
            mv.addObject("mensajeError", "Lista de Encuestas no Disponible.");
        } else {
            List<Encuesta> encuestas = encuestaService.getAllEncuestas();
            mv.setViewName("encuestas/listadoEncuestas");
            mv.addObject("encuestas", encuestas);
        }
        return mv;
    }


}
