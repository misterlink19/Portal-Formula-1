package com.portal.formula1.controller;

import com.portal.formula1.model.Equipo;
import com.portal.formula1.service.EquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/equipos")
public class EquipoController {

    private static final Logger logger = LoggerFactory.getLogger(EquipoController.class);

    @Autowired
    private EquipoService equipoService;

    @GetMapping
    public ModelAndView mostrarMenuEquipos() {
        logger.debug("Entrando a mostrarMenuEquipos");
        ModelAndView mv = new ModelAndView("equipos/equipo");
        try {
            List<Equipo> equipos = equipoService.obtenerTodosLosEquipos();
            mv.addObject("equipos", equipos);
        } catch (Exception e) {
            logger.error("Error al cargar el menú de equipos: ", e);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Error al cargar el menú de equipos.");
        }
        return mv;
    }


    @GetMapping("/crear")
    public ModelAndView mostrarFormularioCreacion() {
        logger.debug("Entrando a mostrarFormularioCreacion");
        ModelAndView mv = new ModelAndView("equipos/crearEquipo");
        try {
            mv.addObject("equipo", new Equipo());
        } catch (Exception e) {
            logger.error("Error al cargar el formulario de creación: ", e);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Error al cargar el formulario de creación.");
        }
        return mv;
    }


    @PostMapping
    public ModelAndView crearEquipo(@ModelAttribute Equipo equipo) {
        logger.debug("Entrando a crearEquipo");
        ModelAndView mv = new ModelAndView();
        try {
            Equipo nuevoEquipo = equipoService.crearEquipo(equipo);
            mv.setViewName("redirect:/equipos/" + nuevoEquipo.getId());
        } catch (Exception e) {
            logger.error("Error al crear el equipo: ", e);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Error al crear el equipo.");
        }
        return mv;
    }

    @GetMapping("/{id}")
    public ModelAndView mostrarEquipo(@PathVariable Long id) {
        logger.debug("Entrando a mostrarEquipo con id: {}", id);
        ModelAndView mv = new ModelAndView("equipos/verEquipo");  // Asegúrate de que la ruta sea correcta
        try {
            Equipo equipo = equipoService.obtenerEquipoPorId(id)
                    .orElseThrow(() -> new NoSuchElementException("Equipo no encontrado"));
            mv.addObject("equipo", equipo);
        } catch (NoSuchElementException e) {
            logger.error("Equipo no encontrado con id: {}", id);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Equipo no encontrado.");
        } catch (Exception e) {
            logger.error("Error al mostrar el equipo con id: {}", id, e);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Error al mostrar el equipo.");
        }
        return mv;
    }

}
