package com.portal.formula1.controller;
import com.portal.formula1.model.Equipo;
import com.portal.formula1.model.Piloto;
import com.portal.formula1.model.Rol;
import com.portal.formula1.model.UsuarioRegistrado;
import com.portal.formula1.service.AutentificacionService;
import com.portal.formula1.service.ImagenService;
import com.portal.formula1.service.PilotoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/pilotos")
public class PilotoController {

    private static final Logger logger = LoggerFactory.getLogger(PilotoController.class);

    @Autowired
    private PilotoService pilotoService;

    @Autowired
    private ImagenService imagenService;

    @Autowired
    private AutentificacionService autentificacionService;

    @GetMapping
    public ModelAndView mostrarMenuPilotos(HttpServletRequest request) {
        logger.debug("Entrando al menú de gestión de pilotos.");
        ModelAndView mv = new ModelAndView("pilotos/piloto");
        UsuarioRegistrado user = (UsuarioRegistrado) request.getSession().getAttribute("usuario");
        try {
            user = autentificacionService.checkUser(user.getUsuario());
            if (user.getRol() != Rol.ADMIN && user.getRol() != Rol.JEFE_DE_EQUIPO) {
                mv.setViewName("error");
                mv.addObject("mensajeError", "No tienes permiso para acceder a esta sección.");
                return mv;
            }
            mv.addObject("usuario", user);
        } catch (Exception e) {
            logger.error("Error al cargar el menú de gestión de pilotos: ", e);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Error al cargar el menú de gestión de pilotos.");
        }
        return mv;
    }


    @GetMapping("/crear")
    public ModelAndView mostrarFormularioCreacion(HttpServletRequest request) {
        logger.debug("Entrando a mostrarFormularioCreacion");
        ModelAndView mv = new ModelAndView("pilotos/crearPiloto");
        UsuarioRegistrado user = (UsuarioRegistrado) request.getSession().getAttribute("usuario");
        try {
            user = autentificacionService.checkUser(user.getUsuario());
            if (user.getRol() != Rol.JEFE_DE_EQUIPO || user.getEquipo() == null) {
                mv.setViewName("error");
                mv.addObject("mensajeError", "No tienes permiso para acceder a esta sección.");
                return mv;
            }
            mv.addObject("piloto", new Piloto());
        } catch (Exception e) {
            logger.error("Error al cargar el formulario de creación de piloto: ", e);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Error al cargar el formulario de creación de piloto.");
        }
        return mv;
    }


    @PostMapping("/crear")
    public ModelAndView crearPiloto(@Valid @ModelAttribute("piloto") Piloto piloto,
                                    BindingResult result,
                                    @RequestParam("fotoArchivo") MultipartFile fotoArchivo,
                                    RedirectAttributes redirectAttributes,
                                    HttpServletRequest request) {
        logger.debug("Entrando a crearPiloto");
        ModelAndView mv = new ModelAndView("pilotos/crearPiloto");

        // Obtener usuario de la sesión
        UsuarioRegistrado user = (UsuarioRegistrado) request.getSession().getAttribute("usuario");
        logger.debug("Usuario en sesión: {}", user);

        // Verificar permisos del usuario
        try {
            user = autentificacionService.checkUser(user.getUsuario());
            if (user.getRol() != Rol.JEFE_DE_EQUIPO && user.getRol() != Rol.ADMIN) {
                logger.debug("El usuario no tiene permisos para crear un piloto.");
                mv.setViewName("error");
                mv.addObject("mensajeError", "No tienes permiso para crear un piloto.");
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
            mv.addObject("piloto", piloto);
            return mv;
        }

        if (pilotoService.existeDorsal(piloto.getDorsal())) {
            result.rejectValue("dorsal", "error.piloto", "Ya existe un piloto con este dorsal.");
            mv.addObject("piloto", piloto);
            return mv;
        }

        try {
            if (fotoArchivo == null || fotoArchivo.isEmpty()) {
                result.rejectValue("rutaImagen", "error.piloto", "La foto del piloto es obligatoria.");
                mv.addObject("piloto", piloto);
                return mv;
            }
            if (!imagenService.isFormatoValido(fotoArchivo)) {
                result.rejectValue("rutaImagen", "error.piloto", "Formato de imagen no permitido. Solo JPG y PNG.");
                mv.addObject("piloto", piloto);
                return mv;
            }
            if (!imagenService.isTamanoValido(fotoArchivo)) {
                result.rejectValue("rutaImagen", "error.piloto", "La imagen supera el tamaño máximo permitido de 2 MB.");
                mv.addObject("piloto", piloto);
                return mv;
            }

            String nombreArchivo = System.currentTimeMillis() + "_" + fotoArchivo.getOriginalFilename();
            Path rutaArchivo = Paths.get("src/main/resources/static/uploads").resolve(nombreArchivo).toAbsolutePath();
            Files.copy(fotoArchivo.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);
            piloto.setRutaImagen(nombreArchivo);
            piloto.setEquipo(user.getEquipo());

            Piloto nuevoPiloto = pilotoService.guardarPiloto(piloto);
            redirectAttributes.addFlashAttribute("successMessage", "El piloto ha sido creado exitosamente.");
            mv.setViewName("redirect:/pilotos/" + nuevoPiloto.getDorsal());
        } catch (Exception e) {
            logger.error("Error al crear el piloto: ", e);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Error al crear el piloto.");
        }

        return mv;
    }


    @GetMapping("/{dorsal}")
    public ModelAndView mostrarPiloto(@PathVariable Integer dorsal, HttpServletRequest request) {
        logger.debug("Entrando a mostrarPiloto con dorsal:{}", dorsal);
        ModelAndView mv = new ModelAndView("pilotos/verPiloto");
        try {
            UsuarioRegistrado user = (UsuarioRegistrado) request.getSession().getAttribute("usuario");
            if (user == null) {
                throw new AccessDeniedException("Usuario no autenticado.");
            }
            Piloto piloto = pilotoService.obtenerPilotoPorDorsal(dorsal)
                    .orElseThrow(() -> new NoSuchElementException("Piloto no encontrado"));

            Equipo equipo = piloto.getEquipo();
            if (equipo == null || equipo.getResponsables() == null) {
                throw new NoSuchElementException("Equipo no encontrado o sin responsables.");
            }

            boolean esResponsable = equipo.getResponsables()
                    .stream()
                    .anyMatch(responsable -> responsable.getUsuario().equals(user.getUsuario()));

            if (!esResponsable && user.getRol() != Rol.ADMIN) {
                throw new AccessDeniedException("No tienes permisos para ver este piloto.");
            }
            mv.addObject("piloto", piloto);
        } catch (NoSuchElementException e) {
            logger.error("Piloto no encontrado con dorsal:{}", dorsal);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Piloto no encontrado.");
        } catch (AccessDeniedException e) {
            logger.error("Acceso denegado para el usuario:{}", request.getSession().getAttribute("usuario"));
            mv.setViewName("error");
            mv.addObject("mensajeError", "No tienes permisos para ver este piloto.");
        } catch (Exception e) {
            logger.error("Error al mostrar el piloto con dorsal:{}", dorsal, e);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Error al mostrar el piloto.");
        }
        return mv;
    }



}
