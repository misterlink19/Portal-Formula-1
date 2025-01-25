package com.portal.formula1.controller;

import com.portal.formula1.model.CalendarioEvento;
import com.portal.formula1.service.CalendarioEventoService;
import com.portal.formula1.service.CircuitoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/calendario")
public class CalendarioEventoController {

    @Autowired
    private CalendarioEventoService calendarioEventoService;
    @Autowired
    private CircuitoService circuitoService;

    // Mostrar eventos en el calendario
    @GetMapping
    public String listarEventos(Model model) {
        List<CalendarioEvento> eventos = calendarioEventoService.listarEventos();
        model.addAttribute("eventos", eventos);
        return "home"; // Renderizar la vista home.html
    }

    @GetMapping("/gestion")
    public String gestionEventos(Model model) {
        List<CalendarioEvento> eventos = calendarioEventoService.listarEventos();
        model.addAttribute("eventos", eventos);
        return "calendario/gestionCalendario";
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrearEvento(Model model) {
        model.addAttribute("evento", new CalendarioEvento());
        model.addAttribute("circuitos", circuitoService.listarCircuitos());
        return "calendario/crearEvento";
    }

    @PostMapping("/crear")
    public String crearEvento(@ModelAttribute CalendarioEvento evento) {
        calendarioEventoService.guardarEvento(evento);
        return "redirect:/calendario/gestion";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarEvento(@PathVariable Long id) {
        calendarioEventoService.eliminarEvento(id);
        return "redirect:/calendario/gestion";
    }
}