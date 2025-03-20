package com.portal.formula1.controller;


import com.portal.formula1.model.*;
import com.portal.formula1.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/simulacion")
public class SimulacionController {
    private static final Logger logger = LoggerFactory.getLogger(CocheController.class);

    @Autowired
    private CocheService cocheService;
    @Autowired
    private ConsultaERSService consultaERSService;
    @Autowired
    private AutentificacionService autentificacionService;
    @Autowired
    private CircuitoService circuitoService;
    @Autowired
    private ConsultaCombustibleService consultaCombustibleService;


    @GetMapping
    public ModelAndView mostrarMenuHerramientas(HttpServletRequest request) {
        logger.debug("Entrando al menú de herramientas de simulacion.");
        ModelAndView mv = new ModelAndView("simulacion/simulacion");
        UsuarioRegistrado user = (UsuarioRegistrado) request.getSession().getAttribute("usuario");
        try {
            user = autentificacionService.checkUser(user.getUsuario());
            if (user.getRol() != Rol.JEFE_DE_EQUIPO) {
                mv.setViewName("error");
                mv.addObject("mensajeError", "No tienes permiso para acceder a esta sección.");
                return mv;
            }
            mv.addObject("usuario", user);
        } catch (Exception e) {
            logger.error("Error al cargar el menú de las herramientas de simulacion: ", e);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Error al cargar el menú de gestión de coches.");
        }
        return mv;
    }

    @GetMapping("combustible")
    public ModelAndView calculoCombustible(HttpSession session) {
        logger.debug("Entrando a la herramienta de Calculo de Combustible");
        UsuarioRegistrado usuario = (UsuarioRegistrado) session.getAttribute("usuario");

        // Verificar si el usuario es JEFE DE EQUIPO
        if (usuario == null || (usuario.getRol() != Rol.JEFE_DE_EQUIPO )) {
            return new ModelAndView("error").addObject("mensajeError", "Acceso denegado.");
        }
        ModelAndView mv = new ModelAndView("simulacion/combustible");

        List<Coches> listaCoches = cocheService.listarCochesPorEquipos(usuario.getEquipo().getId());
        List<Circuito> listaCircuito = circuitoService.listarCircuitos();
        mv.addObject("equipo", usuario.getEquipo());
        if(!listaCoches.isEmpty()) {
            mv.addObject("listaCoches", listaCoches);

        }else{
            mv.addObject("listaCoches", null);
        }
        if(!listaCircuito.isEmpty()) {
            mv.addObject("listaCircuito", listaCircuito);

        }else{
            mv.addObject("listaCircuito", null);
        }
        mv.addObject("consultaCombustible", new ConsultaCombustible());
        mv.addObject("usuario", usuario);
        return mv;
    }

    @PostMapping("/combustible")
    public ModelAndView guardarConsulta(@RequestParam("idCircuito") Long idCircuito,
                                        @RequestParam("idCoche") String idCoche,
                                        @Valid @ModelAttribute("consultaCombustible") ConsultaCombustible consultaCombustible,
                                        BindingResult result,
                                        RedirectAttributes redirectAttributes,
                                        HttpServletRequest request) {
        logger.debug("Entrando a guardar consulta");
        ModelAndView mv = new ModelAndView("simulacion/combustible");

        // Obtener usuario de la sesión
        UsuarioRegistrado user = (UsuarioRegistrado) request.getSession().getAttribute("usuario");
        if (user == null) {
            logger.error("No hay usuario en sesión.");
            mv.setViewName("error");
            mv.addObject("mensajeError", "No hay usuario en sesión.");
            return mv;
        }

        logger.debug("Usuario en sesión: {}", user);

        // Verificar permisos del usuario
        try {
            user = autentificacionService.checkUser(user.getUsuario());
            if (user.getRol() != Rol.JEFE_DE_EQUIPO) {
                logger.debug("El usuario no tiene permisos para esta acción.");
                mv.setViewName("error");
                mv.addObject("mensajeError", "No tienes permiso para realizar esta acción.");
                return mv;
            }
        } catch (Exception e) {
            logger.error("Error al autenticar al usuario: ", e);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Error al autenticar al usuario.");
            return mv;
        }

        // Convertir IDs en objetos reales de la base de datos
        Circuito circuito = circuitoService.obtenerCircuitoPorId(idCircuito);
        Coches coche = cocheService.obtenerCocheByCodigo(idCoche);

        if (circuito == null) {
            result.rejectValue("circuito", "error.circuito", "Debe seleccionar un circuito válido.");
        }
        if (coche == null) {
            result.rejectValue("coche", "error.coche", "Debe seleccionar un coche válido.");
        }

        // Verificar si hay errores en el formulario
        if (result.hasErrors()) {
            logger.error("Errores en el formulario: {}", result.getAllErrors());
            mv.addObject("consultaCombustible", consultaCombustible);
            return mv;
        }

        // Asignar los objetos a la entidad antes de guardarla
        consultaCombustible.setIdCircuito(circuito.getId());
        consultaCombustible.setNombreCircuito(circuito.getNombre());
        consultaCombustible.setCodigoCoche(coche.getCodigo());
        consultaCombustible.setNombreCoche(coche.getNombre());
        consultaCombustible.setEquipo(user.getEquipo());

        try {
            // Guardar la consulta en la base de datos
            consultaCombustibleService.guardarConsulta(consultaCombustible);
            redirectAttributes.addFlashAttribute("mensaje", "Consulta guardada con éxito");
            mv.setViewName("redirect:/simulacion");
        } catch (Exception e) {
            logger.error("Error al guardar la consulta: ", e);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Hubo un problema al guardar la consulta. Intente nuevamente más tarde.");
        }

        return mv;
    }

    @GetMapping("ERS")
    public ModelAndView calculoERS(HttpSession session) {
        logger.debug("Entrando a la herramienta de Calculo de Recuperacion de Energia");
        UsuarioRegistrado usuario = (UsuarioRegistrado) session.getAttribute("usuario");

        // Verificar si el usuario es JEFE DE EQUIPO
        if (usuario == null || (usuario.getRol() != Rol.JEFE_DE_EQUIPO )) {
            return new ModelAndView("error").addObject("mensajeError", "Acceso denegado.");
        }
        ModelAndView mv = new ModelAndView("simulacion/ERS");

        List<Coches> listaCoches = cocheService.listarCochesPorEquipos(usuario.getEquipo().getId());
        List<Circuito> listaCircuito = circuitoService.listarCircuitos();
        mv.addObject("equipo", usuario.getEquipo());
        if(!listaCoches.isEmpty()) {
            mv.addObject("listaCoches", listaCoches);

        }else{
            mv.addObject("listaCoches", null);
        }
        if(!listaCircuito.isEmpty()) {
            mv.addObject("listaCircuito", listaCircuito);

        }else{
            mv.addObject("listaCircuito", null);
        }
        mv.addObject("consultaERS", new ConsultaERS());
        mv.addObject("usuario", usuario);
        return mv;
    }

    @PostMapping("/ERS")
    public ModelAndView guardarSimulacionERS(@RequestParam("idCircuito") Long idCircuito,
                                        @RequestParam("idCoche") String idCoche,
                                        @Valid @ModelAttribute("consultaERS") ConsultaERS consultaERS,
                                        BindingResult result,
                                        RedirectAttributes redirectAttributes,
                                        HttpServletRequest request) {
        logger.debug("Entrando a guardar consulta");
        ModelAndView mv = new ModelAndView("simulacion/ERS");

        // Obtener usuario de la sesión
        UsuarioRegistrado user = (UsuarioRegistrado) request.getSession().getAttribute("usuario");
        if (user == null) {
            logger.error("No hay usuario en sesión.");
            mv.setViewName("error");
            mv.addObject("mensajeError", "No hay usuario en sesión.");
            return mv;
        }

        logger.debug("Usuario en sesión: {}", user);

        // Verificar permisos del usuario
        try {
            user = autentificacionService.checkUser(user.getUsuario());
            if (user.getRol() != Rol.JEFE_DE_EQUIPO) {
                logger.debug("El usuario no tiene permisos para esta acción.");
                mv.setViewName("error");
                mv.addObject("mensajeError", "No tienes permiso para realizar esta acción.");
                return mv;
            }
        } catch (Exception e) {
            logger.error("Error al autenticar al usuario: ", e);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Error al autenticar al usuario.");
            return mv;
        }

        // Convertir IDs en objetos reales de la base de datos
        Circuito circuito = circuitoService.obtenerCircuitoPorId(idCircuito);
        Coches coche = cocheService.obtenerCocheByCodigo(idCoche);

        if (circuito == null) {
            result.rejectValue("circuito", "error.circuito", "Debe seleccionar un circuito válido.");
        }
        if (coche == null) {
            result.rejectValue("coche", "error.coche", "Debe seleccionar un coche válido.");
        }

        // Verificar si hay errores en el formulario
        if (result.hasErrors()) {
            logger.error("Errores en el formulario: {}", result.getAllErrors());
            mv.addObject("consultaERS", consultaERS);
            return mv;
        }

        // Asignar los objetos a la entidad antes de guardarla
        consultaERS.setIdCircuito(circuito.getId());
        consultaERS.setNombreCircuito(circuito.getNombre());
        consultaERS.setCodigoCoche(coche.getCodigo());
        consultaERS.setNombreCoche(coche.getNombre());
        consultaERS.setEquipo(user.getEquipo());

        try {
            // Guardar la consulta en la base de datos
            consultaERSService.guardarConsultaERS(consultaERS);
            redirectAttributes.addFlashAttribute("mensaje", "Consulta guardada con éxito");
            mv.setViewName("redirect:/simulacion");
        } catch (Exception e) {
            logger.error("Error al guardar la consulta: ", e);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Hubo un problema al guardar la consulta. Intente nuevamente más tarde.");
        }

        return mv;
    }

    @GetMapping("historico")
    public ModelAndView verHistorico(HttpSession session) {
        logger.debug("Entrando a la lista de consultas");
        UsuarioRegistrado usuario = (UsuarioRegistrado) session.getAttribute("usuario");

        // Verificar si el usuario es JEFE DE EQUIPO
        if (usuario == null || (usuario.getRol() != Rol.JEFE_DE_EQUIPO )) {
            return new ModelAndView("error").addObject("mensajeError", "Acceso denegado.");
        }
        ModelAndView mv = new ModelAndView("simulacion/verHistorico");

        List<ConsultaERS> listaErs = consultaERSService.consultaPorEquipo(usuario.getEquipo().getId());
        List<ConsultaCombustible> listaCombustible = consultaCombustibleService.consultaPorEquipo(usuario.getEquipo().getId());
        mv.addObject("equipo", usuario.getEquipo());
        if(!listaErs.isEmpty()) {
            mv.addObject("listaErs", listaErs);

        }else{
            mv.addObject("listaErs", null);
        }
        if(!listaCombustible.isEmpty()) {
            mv.addObject("listaCombustible", listaCombustible);

        }else{
            mv.addObject("listaCombustible", null);
        }
        mv.addObject("usuario", usuario);
        return mv;
    }
}
