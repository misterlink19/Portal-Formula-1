package com.portal.formula1.controller;

import com.portal.formula1.model.*;
import com.portal.formula1.service.EncuestaService;
import com.portal.formula1.service.VotoService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

/**
 *
 * @author Misterlink
 */
@Controller
@RequestMapping("/votos")
public class VotoController {

    private static final Logger logger = LoggerFactory.getLogger(VotoController.class);

    @Autowired
    private VotoService votoService;

    @Autowired
    private EncuestaService encuestaService;


    @GetMapping("/{permalink}/votar")
    public ModelAndView mostrarFormularioVotacion(@PathVariable String permalink, HttpSession session) {
        logger.debug("Entrando a mostrarFormularioVotacion con permalink: {}", permalink);
        ModelAndView mv = new ModelAndView("votos/votarEncuesta");
        try {
            Encuesta encuesta = encuestaService.obtenerEncuestaPorPermalink(permalink);
            List<Piloto> pilotos = new ArrayList<>(encuesta.getPilotos());
            UsuarioRegistrado usuario = (UsuarioRegistrado)  session.getAttribute("usuario");

            Voto voto = new Voto();
            if (usuario != null)
            {
                voto.setNombreVotante(usuario.getNombre());
                voto.setCorreoVotante(usuario.getEmail());
            }

            mv.addObject("encuesta", encuesta);
            mv.addObject("pilotos", pilotos);
            mv.addObject("voto", voto);
        } catch (NoSuchElementException e) {
            logger.error("Encuesta no encontrada con permalink: {}", permalink);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Encuesta no encontrada.");
        }
        return mv;
    }

    @PostMapping("/{permalink}/votar")
    public ModelAndView crearVoto(@PathVariable String permalink, @ModelAttribute Voto voto, HttpSession session) {
        logger.debug("Entrando a crearVoto con permalink: {}", permalink);
        ModelAndView mv = new ModelAndView("votos/votarEncuesta");
        try {
            Encuesta encuesta = encuestaService.obtenerEncuestaPorPermalink(permalink);
            voto.setEncuesta(encuesta);

            // Validar si el usuario ya ha votado usando el mismo correo electrónico
            boolean yaHaVotado = votoService.haVotadoAntes(voto.getCorreoVotante(), encuesta);
            if (yaHaVotado) {
                UsuarioRegistrado usuario = (UsuarioRegistrado) session.getAttribute("usuario");
                if (usuario != null) {
                    voto.setNombreVotante(usuario.getNombre());
                    voto.setCorreoVotante(usuario.getEmail());
                }

                mv.addObject("encuesta", encuesta);
                mv.addObject("pilotos", new ArrayList<>(encuesta.getPilotos()));
                mv.addObject("voto", voto);
                mv.addObject("mensajeError", "Ya has votado en esta encuesta con este correo electrónico.");
                return mv;
            }

            // Asegurarse de que los datos del usuario estén presentes antes de guardar el voto
            UsuarioRegistrado usuario = (UsuarioRegistrado) session.getAttribute("usuario");
            if (usuario != null) {
                voto.setNombreVotante(usuario.getNombre());
                voto.setCorreoVotante(usuario.getEmail());
            }

            Voto nuevoVoto = votoService.crearVoto(voto);
            mv.setViewName("votos/votoConfirmado");
            mv.addObject("voto", nuevoVoto);
        } catch (NoSuchElementException e) {
            logger.error("Encuesta no encontrada con permalink: {}", permalink);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Encuesta no encontrada.");
        }
        return mv;
    }

    @GetMapping("/votar")
    public ModelAndView redirigirAVotar() {
        ModelAndView mv = new ModelAndView();
        try {
            Encuesta ultimaEncuesta = encuestaService.obtenerUltimaEncuestaDisponible();
            mv.setViewName("redirect:/votos/" + ultimaEncuesta.getPermalink() + "/votar");
        } catch (NoSuchElementException e) {
            mv.setViewName("error");
            mv.addObject("mensajeError", "No hay encuestas disponibles en este momento.");
        }
        return mv;
    }

    @GetMapping("/{permalink}/resultados")
    public ModelAndView mostrarResultados(@PathVariable String permalink, HttpSession session) {
        logger.debug("Entrando a mostrarResultados con permalink: {}", permalink);
        ModelAndView mv = new ModelAndView("votos/resultadosEncuesta");

        try {
            EncuestaArchivada encuestaArchivada = encuestaService.obtenerEncuestaArchivadaPorPermalink(permalink);
            List<PilotoArchivado> pilotosArchivados = encuestaArchivada.getPilotosArchivados();
            List<Object[]> ranking = votoService.getRankingVotacion(permalink);

            UsuarioRegistrado usuario = (UsuarioRegistrado) session.getAttribute("usuario");
            Voto votoUsuario = null;
            if (usuario != null) {
                votoUsuario = votoService.obtenerVotoPorCorreoYEncuesta(usuario.getEmail(), encuestaArchivada.getPermalink());
            }

            mv.addObject("encuestaArchivada", encuestaArchivada);
            mv.addObject("pilotosArchivados", pilotosArchivados);
            mv.addObject("ranking", ranking);
            mv.addObject("votoUsuario", votoUsuario);
        } catch (NoSuchElementException e) {
            logger.error("Encuesta no encontrada con permalink: {}", permalink);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Encuesta no encontrada.");
        }

        return mv;
    }




}
