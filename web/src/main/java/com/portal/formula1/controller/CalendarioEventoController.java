package com.portal.formula1.controller;

import com.portal.formula1.model.CalendarioEvento;
import com.portal.formula1.service.CalendarioEventoService;
import com.portal.formula1.service.CircuitoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
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
    public ModelAndView listarEventos() {
        ModelAndView mv = new ModelAndView("home"); // Especifica la vista que deseas renderizar
        try {
            List<CalendarioEvento> eventos = calendarioEventoService.listarEventos();
            mv.addObject("eventos", eventos);
        } catch (Exception e) {
            logger.error("Error al cargar los eventos: ", e);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Error al cargar los eventos.");
        }
        return mv;
    }

    @GetMapping("/gestion")
    public ModelAndView menuEventos() {
        ModelAndView mv = new ModelAndView("calendario/calendario");
        try {
            List<CalendarioEvento> eventos = calendarioEventoService.listarEventos();
            mv.addObject("eventos", eventos);
            mv.addObject("now", LocalDate.now()); // Añadimos la fecha actual al modelo
        } catch (Exception e) {
            logger.error("Error al cargar los eventos: ", e);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Error al cargar los eventos.");
        }
        return mv;
    }



    @GetMapping("/crear")
    public ModelAndView mostrarFormularioCrearEvento() {
        ModelAndView mv = new ModelAndView("calendario/crearEvento");
        try {
            mv.addObject("evento", new CalendarioEvento());
            mv.addObject("circuitos", circuitoService.listarCircuitos());
            mv.addObject("title","Crear Nuevo Evento");
        } catch (Exception e) {
            logger.error("Error al cargar el formulario de creación: ", e);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Error al cargar el formulario de creación.");
        }
        return mv;
    }

    @PostMapping("/crear")
    public ModelAndView crearEvento(@Valid @ModelAttribute("evento") CalendarioEvento evento,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes) {
        ModelAndView mv = new ModelAndView();
        // Validar que la fecha sea posterior al día actual
        if (evento.getFecha() == null || evento.getFecha().isBefore(LocalDate.now())) {
            result.rejectValue("fecha", "error.evento", "La fecha debe ser posterior al día actual.");
        }
        if (result.hasErrors()) {
            // Si hay errores de validación, volver a la vista del formulario
            mv.setViewName("calendario/crearEvento");
            mv.addObject("evento", evento); // Agregar el objeto al modelo
            mv.addObject("circuitos", circuitoService.listarCircuitos());
            return mv;
        }

        try {
            calendarioEventoService.guardarEvento(evento);
            redirectAttributes.addFlashAttribute("mensaje", "El evento ha sido creado exitosamente.");
            mv.setViewName("redirect:/calendario/gestion");
            return mv;
        } catch (IllegalArgumentException e) {
            // Manejar la excepción cuando el evento ya existe
            result.rejectValue("nombreEvento", "error.evento", e.getMessage());
        } catch (Exception e) {
            logger.error("Error al crear el evento: ", e);
            result.reject("error.evento", "Ha ocurrido un error al crear el evento.");
        }
        // Si hay errores, retornar la vista del formulario
        mv.setViewName("calendario/crearEvento");
        mv.addObject("evento", evento); // Agregar el objeto al modelo
        mv.addObject("circuitos", circuitoService.listarCircuitos());
        return mv;
    }


    @PostMapping("/{id}/eliminar")
    public ModelAndView eliminarEvento(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        ModelAndView mv = new ModelAndView();
        try {
            calendarioEventoService.eliminarEvento(id);
            redirectAttributes.addFlashAttribute("mensaje", "El evento ha sido eliminado exitosamente.");
        } catch (NoSuchElementException e) {
            logger.error("No se puede eliminar el evento con id:{}", id);
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        } catch (Exception e) {
            logger.error("Error al eliminar el evento con id:{}", id, e);
            redirectAttributes.addFlashAttribute("mensajeError", "Error al eliminar el evento.");
        }
        mv.setViewName("redirect:/calendario/gestion");
        return mv;
    }


    @GetMapping("/evento/{id}")
    public ModelAndView verEvento(@PathVariable Long id) {
        ModelAndView mv = new ModelAndView("calendario/verEvento");
        try {
            CalendarioEvento evento = calendarioEventoService.obtenerEventoPorId(id);
            // Verificar si el circuito tiene un trazado
            if (evento.getCircuito().getTrazado() == null || evento.getCircuito().getTrazado().isEmpty()) {
                evento.getCircuito().setTrazado("default-trazado.png"); // Imagen por defecto
            }
            mv.addObject("evento", evento);
        } catch (NoSuchElementException e) {
            logger.error("Evento no encontrado con id:{}", id);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Evento no encontrado.");
        } catch (Exception e) {
            logger.error("Error al mostrar el evento con id:{}", id, e);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Error al mostrar el evento.");
        }
        return mv;
    }

    @GetMapping("/editar/{id}")
    public ModelAndView mostrarFormularioEditarEvento(@PathVariable Long id) {
        ModelAndView mv = new ModelAndView("calendario/crearEvento"); // Usamos la misma vista de creación
        try {
            CalendarioEvento evento = calendarioEventoService.obtenerEventoPorId(id);
            if (evento.getFecha().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("No se puede editar eventos pasados.");
            }
            mv.addObject("evento", evento);
            mv.addObject("circuitos", circuitoService.listarCircuitos());
            mv.addObject("title","Editar Evento");
        } catch (NoSuchElementException e) {
            logger.error("Evento no encontrado con id:{}", id);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Evento no encontrado.");
        } catch (IllegalArgumentException e) {
            logger.error("Error de validación: ", e);
            mv.setViewName("error");
            mv.addObject("mensajeError", e.getMessage());
        } catch (Exception e) {
            logger.error("Error al mostrar el formulario de edición: ", e);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Error al mostrar el formulario de edición.");
        }
        return mv;
    }

    @PostMapping("/editar")
    public ModelAndView editarEvento(@Valid @ModelAttribute("evento") CalendarioEvento evento,
                                     BindingResult result,
                                     RedirectAttributes redirectAttributes) {
        ModelAndView mv = new ModelAndView();
        if (evento.getFecha() == null || evento.getFecha().isBefore(LocalDate.now())) {
            result.rejectValue("fecha", "error.evento", "La fecha debe ser hoy o posterior.");
        }
        if (result.hasErrors()) {
            mv.setViewName("calendario/crearEvento"); // Usamos la misma vista de creación
            mv.addObject("evento", evento);
            mv.addObject("circuitos", circuitoService.listarCircuitos());
            return mv;
        }
        try {
            calendarioEventoService.editarEvento(evento.getId(), evento); // Usamos el método editarEvento del servicio
            redirectAttributes.addFlashAttribute("mensaje", "El evento ha sido actualizado exitosamente.");
            mv.setViewName("redirect:/calendario/gestion");
            return mv;
        } catch (NoSuchElementException e) {
            logger.error("Evento no encontrado con id:{}", evento.getId());
            mv.setViewName("calendario/crearEvento");
            mv.addObject("mensajeError", "Evento no encontrado.");
        } catch (Exception e) {
            logger.error("Error al actualizar el evento: ", e);
            result.reject("error.evento", "Ha ocurrido un error al actualizar el evento.");
        }
        mv.setViewName("calendario/crearEvento"); // Usamos la misma vista de creación
        mv.addObject("evento", evento);
        mv.addObject("circuitos", circuitoService.listarCircuitos());
        return mv;
    }



}