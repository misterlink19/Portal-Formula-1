/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.portal.formula1.controller;

import com.portal.formula1.model.Encuesta;
import com.portal.formula1.model.Voto;
import com.portal.formula1.repository.VotoDAO;
import com.portal.formula1.service.EncuestaService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Misterlink
 */
@Controller
@RequestMapping("/encuestas")
public class EncuestaController {

    @Autowired
    private EncuestaService encuestaService;

    @Autowired
    private VotoDAO votoDAO;

    @GetMapping
    public String mostrarMenuEncuestas() {
        return "encuesta";
    }

    @GetMapping("/crearEncuestas")
    public ModelAndView mostrarFormularioCreacion() {
        ModelAndView mv = new ModelAndView("crearEncuesta");
        List<Object[]> pilotos = encuestaService.getTodosLosPilotos();
        mv.addObject("pilotos", pilotos);
        return mv;
    }

    @PostMapping
    public ModelAndView crearEncuesta(@RequestBody Encuesta encuesta) {
        ModelAndView mv = new ModelAndView();
        Encuesta nuevaEncuesta = encuestaService.crearEncuesta(encuesta);
        mv.addObject("encuesta", nuevaEncuesta);
        mv.setViewName("redirect:/encuestas/" + nuevaEncuesta.getPermalink());
        return mv;
    }

    @GetMapping("/{permalink}")
    public ModelAndView mostrarEncuesta(@PathVariable String permalink) {
        ModelAndView mv = new ModelAndView("votarEncuesta");
        Encuesta encuesta = encuestaService.obtenerEncuestaPorPermalink(permalink);
        List<Object[]> pilotos = encuestaService.getTodosLosPilotos();
        mv.addObject("encuesta", encuesta);
        mv.addObject("pilotos", pilotos);
        return mv;
    }

    @PostMapping("/{permalink}/votos")
    public ModelAndView crearVoto(@PathVariable String permalink, @RequestBody Voto voto) {
        ModelAndView mv = new ModelAndView("votoConfirmado");
        Encuesta encuesta = encuestaService.obtenerEncuestaPorPermalink(permalink);
        voto.setEncuesta(encuesta);
        Voto nuevoVoto = votoDAO.save(voto);
        mv.addObject("voto", nuevoVoto);
        return mv;
    }
}
