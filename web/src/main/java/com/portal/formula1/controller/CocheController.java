package com.portal.formula1.controller;


import com.portal.formula1.model.*;
import com.portal.formula1.service.AutentificacionService;
import com.portal.formula1.service.CocheService;
import com.portal.formula1.service.PilotoService;
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
import java.util.Optional;

@Controller
@RequestMapping("/coches")
public class CocheController {
    private static final Logger logger = LoggerFactory.getLogger(CocheController.class);

    @Autowired
    private CocheService cocheService;

    @Autowired
    private AutentificacionService autentificacionService;
    @Autowired
    private PilotoService pilotoService;

    @GetMapping
    public ModelAndView mostrarMenuCoches(HttpServletRequest request) {
        logger.debug("Entrando al menú de gestión de coches.");
        ModelAndView mv = new ModelAndView("coches/coches");
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
            logger.error("Error al cargar el menú de gestión de coches: ", e);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Error al cargar el menú de gestión de coches.");
        }
        return mv;
    }

    @GetMapping("/crear")
    public ModelAndView mostrarFormularioDeCreacionCoche(HttpServletRequest request) {
        logger.debug("Entrando a crear un coche");
        ModelAndView mv = new ModelAndView("coches/crearCoche");
        UsuarioRegistrado user = (UsuarioRegistrado) request.getSession().getAttribute("usuario");
        try {
            user = autentificacionService.checkUser(user.getUsuario());
            if (user.getRol() != Rol.JEFE_DE_EQUIPO || user.getEquipo() == null) {
                mv.setViewName("error");
                mv.addObject("mensajeError", "No tienes permiso para acceder a esta sección.");
                return mv;
            }
            mv.addObject("coche", new Coches());
        } catch (Exception e) {
            logger.error("Error al cargar el formulario de creación de coches: ", e);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Error al cargar el formulario de creación de coche.");
        }
        return mv;
    }

    @PostMapping("/crear")
    public ModelAndView crearCoche(@Valid @ModelAttribute("coche") Coches coche,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes,
                                    HttpServletRequest request) {
        logger.debug("Entrando a crear un coche");
        ModelAndView mv = new ModelAndView("coches/crearCoche");

        // Obtener usuario de la sesión
        UsuarioRegistrado user = (UsuarioRegistrado) request.getSession().getAttribute("usuario");
        logger.debug("Usuario en sesión: {}", user);

        // Verificar permisos del usuario
        try {
            user = autentificacionService.checkUser(user.getUsuario());
            if (user.getRol() != Rol.JEFE_DE_EQUIPO) {
                logger.debug("El usuario no tiene permisos para crear un coche.");
                mv.setViewName("error");
                mv.addObject("mensajeError", "No tienes permiso para crear un coche.");
                return mv;
            }
        } catch (Exception e) {
            logger.error("Error al autenticar al usuario: ", e);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Error al autenticar al usuario.");
            return mv;
        }

        // Validar formulario y otros errores
        if (result.hasErrors()) {
            mv.addObject("coche", coche);
            return mv;
        }

        if (cocheService.existeCocheByCodigo(coche.getCodigo())) {
            result.rejectValue("codigo", "error.coche", "Ya existe un coche con este codigo.");
            mv.addObject("coche", coche);
            return mv;
        }

        try {
            if (coche.getConsumo() <= 0) {
                result.rejectValue("consumo", "error.coche", "El consumo esta mal ingresado.");
                mv.addObject("coche", coche);
                return mv;
            }
            if (coche.getErsCurvaLenta() < 0.01 || coche.getErsCurvaLenta() > 0.06) {
                result.rejectValue("ErsCurvaLenta", "error.coche", "El valor ingresado esta fuera del margen.");
                mv.addObject("coche", coche);
                return mv;
            }
            if (coche.getErsCurvaMedia() < 0.01 || coche.getErsCurvaMedia() > 0.06) {
                result.rejectValue("ErsCurvaMedia", "error.coche", "El valor ingresado esta fuera del margen.");
                mv.addObject("coche", coche);
                return mv;
            }
            if (coche.getErsCurvaRapida() < 0.01 || coche.getErsCurvaRapida() > 0.06) {
                result.rejectValue("ErsCurvaRapida", "error.coche", "El valor ingresado esta fuera del margen.");
                mv.addObject("coche", coche);
                return mv;
            }


            coche.setEquipo(user.getEquipo());
            cocheService.guardarCoche(coche);
            redirectAttributes.addFlashAttribute("mensaje", "El coche ha sido creado exitosamente.");
            mv.setViewName("redirect:/coches");
        } catch (Exception e) {
            logger.error("Error al crear el coche: ", e);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Error al crear el coche.");
        }

        return mv;
    }

    @GetMapping("listar")
    public ModelAndView listarCoches(HttpSession session) {
        logger.debug("Entrando a Listado de Coches");
        UsuarioRegistrado usuario = (UsuarioRegistrado) session.getAttribute("usuario");

        // Verificar si el usuario es JEFE DE EQUIPO
        if (usuario == null || (usuario.getRol() != Rol.JEFE_DE_EQUIPO )) {
            return new ModelAndView("error").addObject("mensajeError", "Acceso denegado.");
        }
        ModelAndView mv = new ModelAndView("coches/verCoches");

        List<Coches> listaCoches = cocheService.listarCochesPorEquipos(usuario.getEquipo().getId());
        if(!listaCoches.isEmpty()) {
            mv.addObject("listaCoches", listaCoches);
        }else{
            mv.addObject("listaCoches", null);
        }
        mv.addObject("usuario", usuario);
        return mv;
    }

    @GetMapping("asignar")
    public ModelAndView asignarCoche(HttpSession session) {
        logger.debug("Entrando a la asignacion de Coches");
        UsuarioRegistrado usuario = (UsuarioRegistrado) session.getAttribute("usuario");

        // Verificar si el usuario es JEFE DE EQUIPO
        if (usuario == null || (usuario.getRol() != Rol.JEFE_DE_EQUIPO )) {
            return new ModelAndView("error").addObject("mensajeError", "Acceso denegado.");
        }
        ModelAndView mv = new ModelAndView("coches/asignarCoches");

        List<Coches> listaCoches = cocheService.listarCochesPorEquipoSinPiloto(usuario.getEquipo().getId());
        List<Piloto> listaPilotos = pilotoService.listarPilotosPorEquipoSinCoche(usuario.getEquipo().getId());

        if(!listaCoches.isEmpty()) {
            mv.addObject("listaCoches", listaCoches);

        }else{
            mv.addObject("listaCoches", null);
        }
        if(!listaPilotos.isEmpty()) {
            mv.addObject("listaPilotos", listaPilotos);

        }else{
            mv.addObject("listaPilotos", null);
        }
        mv.addObject("usuario", usuario);
        return mv;
    }

    @PostMapping("/asignar")
    public ModelAndView asignarCoche(@RequestParam("idPiloto") Integer idPiloto,
                                     @RequestParam("idCoche") String idCoche,
                                   RedirectAttributes redirectAttributes,
                                   HttpServletRequest request) {
        logger.debug("Entrando a asignacion de un coche");
        ModelAndView mv = new ModelAndView("coches/asignarCoches");

        // Obtener usuario de la sesión
        UsuarioRegistrado user = (UsuarioRegistrado) request.getSession().getAttribute("usuario");
        logger.debug("Usuario en sesión: {}", user);

        // Verificar permisos del usuario
        try {
            user = autentificacionService.checkUser(user.getUsuario());
            if (user.getRol() != Rol.JEFE_DE_EQUIPO) {
                logger.debug("El usuario no tiene permisos para asignar un coche.");
                mv.setViewName("error");
                mv.addObject("mensajeError", "No tienes permiso para asignar un coche.");
                return mv;
            }
        } catch (Exception e) {
            logger.error("Error al autenticar al usuario: ", e);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Error al autenticar al usuario.");
            return mv;
        }

        if(idPiloto == null || idCoche == null) {
            mv.setViewName("error");
            mv.addObject("mensajeError", "Error al Asignar un coche.");
            return mv;
        }

        if(!pilotoService.existeDorsal(idPiloto) && !cocheService.existeCocheByCodigo(idCoche)) {
            mv.setViewName("error");
            mv.addObject("mensajeError", "No existe el piloto o coche seleccionado.");
            return mv;
        }

        pilotoService.asignarCoche(idPiloto, idCoche);
        cocheService.asignarPiloto(idPiloto, idCoche);
        redirectAttributes.addFlashAttribute("mensaje", "El coche ha sido asignado exitosamente.");
        mv.setViewName("redirect:/coches");
        return mv;
    }
}
