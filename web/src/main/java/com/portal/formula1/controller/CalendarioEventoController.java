package com.portal.formula1.controller;

import com.portal.formula1.model.CalendarioEvento;
import com.portal.formula1.service.CalendarioEventoService;
import com.portal.formula1.service.CircuitoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/calendario")
public class CalendarioEventoController {
    private static final Logger logger = LoggerFactory.getLogger(CalendarioEventoController.class);

    @Autowired
    private CalendarioEventoService calendarioEventoService;
    @Autowired
    private CircuitoService circuitoService;

    // Mostrar eventos en el calendario
    @GetMapping
    public String listarEventos(Model model) {
        try {
            List<CalendarioEvento> eventos = calendarioEventoService.listarEventos();
            model.addAttribute("eventos", eventos);
            return "home"; // Renderizar la vista home.html
        } catch (Exception e) {
            logger.error("Error al cargar los eventos: ", e);
            model.addAttribute("mensajeError", "Error al cargar los eventos.");
            return "error";
        }
    }

    @GetMapping("/gestion")
    public String menuEventos(Model model) {
        try {
            List<CalendarioEvento> eventos = calendarioEventoService.listarEventos();
            model.addAttribute("eventos", eventos);
            return "calendario/calendario";
        } catch (Exception e) {
            logger.error("Error al cargar los eventos: ", e);
            model.addAttribute("mensajeError", "Error al cargar los eventos.");
            return "error";
        }
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrearEvento(Model model) {
        try {
            model.addAttribute("evento", new CalendarioEvento());
            model.addAttribute("circuitos", circuitoService.listarCircuitos());
            return "calendario/crearEvento";
        } catch (Exception e) {
            logger.error("Error al cargar el formulario de creación: ", e);
            model.addAttribute("mensajeError", "Error al cargar el formulario de creación.");
            return "error";
        }
    }

    @PostMapping("/crear")
    public String crearEvento(@ModelAttribute CalendarioEvento evento, RedirectAttributes redirectAttributes) {
        try {
            calendarioEventoService.guardarEvento(evento);
            redirectAttributes.addFlashAttribute("mensaje", "El evento ha sido creado exitosamente.");
            return "redirect:/calendario/gestion";
        } catch (Exception e) {
            logger.error("Error al crear el evento: ", e);
            redirectAttributes.addFlashAttribute("mensajeError", "Error al crear el evento.");
            return "redirect:/calendario/crear";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarEvento(@PathVariable Long id, RedirectAttributes redirectAttributes ) {
        try {
            calendarioEventoService.eliminarEvento(id);
            redirectAttributes.addFlashAttribute("mensaje", "El evento ha sido eliminado exitosamente.");
            return "redirect:/calendario/gestion";
        }  catch (IllegalStateException e) {
            logger.error("No se puede eliminar el evento con id:{}", id);
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
            return "redirect:/calendario/gestion";
        } catch (Exception e) {
            logger.error("Error al eliminar el evento con id:{}", id, e);
            redirectAttributes.addFlashAttribute("mensajeError", "Error al eliminar el evento.");
            return "redirect:/calendario/gestion";
        }
    }

    @GetMapping("/evento/{id}")
    public String verEvento(@PathVariable Long id, Model model) {
        try {
            CalendarioEvento evento = calendarioEventoService.obtenerEventoPorId(id);

            // Verificar si el circuito tiene un trazado
            if (evento.getCircuito().getTrazado() == null || evento.getCircuito().getTrazado().isEmpty()) {
                evento.getCircuito().setTrazado("default-trazado.png"); // Imagen por defecto
            }

            model.addAttribute("evento", evento);
            return "calendario/verEvento";
        } catch (NoSuchElementException e) {
            logger.error("Evento no encontrado con id:{}", id);
            model.addAttribute("mensajeError", "Evento no encontrado.");
            return "error";
        } catch (Exception e) {
            logger.error("Error al mostrar el evento con id:{}", id, e);
            model.addAttribute("mensajeError", "Error al mostrar el evento.");
            return "error";
        }

    }


}