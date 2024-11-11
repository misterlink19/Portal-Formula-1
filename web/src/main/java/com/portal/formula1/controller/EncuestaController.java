/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.portal.formula1.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.portal.formula1.model.Encuesta;
import com.portal.formula1.model.Voto;
import com.portal.formula1.repository.VotoDAO;
import com.portal.formula1.service.EncuestaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
/**
 *
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
    public ModelAndView mostrarMenuEncuestas() {
        logger.debug("Entrando a mostrarMenuEncuestas");
        return new ModelAndView("encuesta");
    }

    @GetMapping({"/crearEncuestas", "/crearEncuesta"})
    public ModelAndView mostrarFormularioCreacion() {
        logger.debug("Entrando a mostrarFormularioCreacion");
        ModelAndView mv = new ModelAndView("crearEncuesta");
        mv.addObject("encuesta", new Encuesta());
        List<Object[]> pilotos = encuestaService.getTodosLosPilotos();
        mv.addObject("pilotos", pilotos);
        return mv;
    }

    @PostMapping
    public ModelAndView crearEncuesta(@ModelAttribute Encuesta encuesta, @RequestParam Set<String> pilotosSeleccionados) {
        logger.debug("Entrando a crearEncuesta");
        Set<String> pilotoSet = new HashSet<>(pilotosSeleccionados);
        Encuesta nuevaEncuesta = encuestaService.crearEncuesta(encuesta,pilotoSet);
        return new ModelAndView("redirect:/encuestas/" + nuevaEncuesta.getPermalink());
    }

    @GetMapping("/{permalink}")
    public ModelAndView mostrarEncuesta(@PathVariable String permalink) {
        logger.debug("Entrando a mostrarEncuesta con permalink: {}", permalink);
        ModelAndView mv = new ModelAndView("verEncuesta");
        Encuesta encuesta = encuestaService.obtenerEncuestaPorPermalink(permalink);
        mv.addObject("encuesta", encuesta);
        return mv;
    }
    //Este lo habilitare con la historia de votarEncuesta
//    @GetMapping("/{permalink}")
//    public ModelAndView mostrarEncuesta(@PathVariable String permalink) {
//        logger.debug("Entrando a mostrarEncuesta con permalink: {}", permalink);
//        ModelAndView mv = new ModelAndView("votarEncuesta");
//        Encuesta encuesta = encuestaService.obtenerEncuestaPorPermalink(permalink);
//        List<Object[]> pilotos = encuestaService.getTodosLosPilotos();
//        mv.addObject("encuesta", encuesta);
//        mv.addObject("pilotos", pilotos);
//        return mv;
//    }

//    @PostMapping("/{permalink}/votos")
//    public ModelAndView crearVoto(@PathVariable String permalink, @ModelAttribute Voto voto) {
//        logger.debug("Entrando a crearVoto con permalink: {}", permalink);
//        ModelAndView mv = new ModelAndView("votoConfirmado");
//        Encuesta encuesta = encuestaService.obtenerEncuestaPorPermalink(permalink);
//        voto.setEncuesta(encuesta);
//        Voto nuevoVoto = votoDAO.save(voto);
//        mv.addObject("voto", nuevoVoto);
//        return mv;
//    }
}
