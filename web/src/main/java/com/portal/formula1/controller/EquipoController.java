package com.portal.formula1.controller;

import com.portal.formula1.model.Equipo;
import com.portal.formula1.model.Rol;
import com.portal.formula1.model.UsuarioRegistrado;
import com.portal.formula1.service.AutentificacionService;
import com.portal.formula1.service.EquipoService;
import com.portal.formula1.service.ImagenService;
import com.portal.formula1.service.UsuarioService;
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
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/equipos")
public class EquipoController {

    private static final Logger logger = LoggerFactory.getLogger(EquipoController.class);

    @Autowired
    private EquipoService equipoService;

    @Autowired
    private ImagenService imagenService;
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AutentificacionService autentificacionService;

    @GetMapping
    public ModelAndView mostrarMenuEquipos(HttpServletRequest request) {
        logger.debug("Entrando a mostrarMenuEquipos");
        ModelAndView mv = new ModelAndView("equipos/equipo");
        UsuarioRegistrado user = (UsuarioRegistrado) request.getSession().getAttribute("usuario");
        try {
            user = autentificacionService.checkUser(user.getUsuario());
            // Con esto me aseguro que solo los admins y los responsables de equipo pueden entrar
            // a la gestion de equipos.
            if (user.getRol() != Rol.ADMIN && user.getRol() != Rol.JEFE_DE_EQUIPO) {
                mv.setViewName("error");
                mv.addObject("mensajeError", "No tienes permiso para acceder a esta sección.");
                return mv;
            }

            logger.debug("Usuario recuperado con equipo: {}", user.getEquipo()); // Agregar log de depuración
            mv.addObject("usuario", user);

        } catch (Exception e) {
            logger.error("Error al cargar el menú de equipos: ", e);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Error al cargar el menú de equipos.");
        }
        return mv;
    }


    @GetMapping("/crear")
    public ModelAndView mostrarFormularioCreacion(HttpServletRequest request) {
        logger.debug("Entrando a mostrarFormularioCreacion");
        ModelAndView mv = new ModelAndView("equipos/crearEquipo");
        UsuarioRegistrado user = (UsuarioRegistrado) request.getSession().getAttribute("usuario");

        try {
            // Con esto revisamos si el responsable de equipo ya tiene un equipo, antes de que le permita crear uno.
            user = autentificacionService.checkUser(user.getUsuario());

            if (user.getRol() == Rol.JEFE_DE_EQUIPO && user.getEquipo() != null) {
                mv.setViewName("error");
                mv.addObject("mensajeError", "Lo sentimos, ya tienes un equipo asignado.");
            } else {
                mv.addObject("equipo", new Equipo());
            }
        } catch (Exception e) {
            logger.error("Error al cargar el formulario de creación: ", e);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Error al cargar el formulario de creación.");
        }
        return mv;
    }


    @PostMapping("/crear")
    public ModelAndView crearEquipo(@Valid @ModelAttribute("equipo") Equipo equipo,
                                    BindingResult result,
                                    @RequestParam("logoArchivo") MultipartFile logoArchivo,
                                    RedirectAttributes redirectAttributes,
                                    HttpServletRequest request) { //Con httpservletRequest es que se obtiene la session para verificar que usario es.
        logger.debug("Entrando a crearEquipo");
        ModelAndView mv = new ModelAndView("equipos/crearEquipo");
        UsuarioRegistrado user = (UsuarioRegistrado) request.getSession().getAttribute("usuario");

        // Validación del lado del servidor
        if (result.hasErrors()) {
            mv.addObject("equipo", equipo);
            return mv;
        }
        // Verificar si el nombre del equipo ya existe
        if (equipoService.existePorNombre(equipo.getNombre())) {
            result.rejectValue("nombre", "error.equipo", "Ya existe un equipo con este nombre.");
            mv.addObject("equipo", equipo);
            return mv;
        }

        try {
            // Validación y guardado de la imagen
            if (logoArchivo == null || logoArchivo.isEmpty()) {
                result.rejectValue("logo", "error.equipo", "El logo del equipo es obligatorio.");
                mv.addObject("equipo", equipo);
                return mv;
            }
            if (!imagenService.isFormatoValido(logoArchivo)) {
                result.rejectValue("logo", "error.equipo", "Formato de imagen no permitido. Solo JPG y PNG.");
                mv.addObject("equipo", equipo);
                return mv;
            }
            if (!imagenService.isTamanoValido(logoArchivo)) {
                result.rejectValue("logo", "error.equipo", "La imagen supera el tamaño máximo permitido de 2 MB.");
                mv.addObject("equipo", equipo);
                return mv;
            }
            // Revisa por si acaso de alguna manera entro responsable de equipo con equipo asignado
            user = autentificacionService.checkUser(user.getUsuario());
            if (user.getRol() == Rol.JEFE_DE_EQUIPO && user.getEquipo() != null) {
                mv.addObject("mensajeError", "Lo sentimos, ya tienes un equipo asignado.");
                mv.addObject("equipo", equipo);
                return mv;
            }
            // Guardar la imagen en resources/static/uploads
            String nombreArchivo = System.currentTimeMillis() + "_" + logoArchivo.getOriginalFilename();
            Path rutaArchivo = Paths.get("src/main/resources/static/uploads").resolve(nombreArchivo).toAbsolutePath();
            Files.copy(logoArchivo.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

            // Establecer el nombre del archivo en el objeto equipo
            equipo.setLogo(nombreArchivo);

            // Guardar el equipo
            Equipo nuevoEquipo = equipoService.crearEquipo(equipo);

            // Asigna el equipo al responsable de equipo que lo crea
            if (user.getRol() == Rol.JEFE_DE_EQUIPO) {
                user.setEquipo(nuevoEquipo);
                usuarioService.actualizarUsuario(user);
            }

            redirectAttributes.addFlashAttribute("mensaje", "El equipo ha sido creado exitosamente.");
            mv.setViewName("redirect:/equipos/" + nuevoEquipo.getId());
        } catch (Exception e) {
            logger.error("Error al crear el equipo: ", e);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Error al crear el equipo.");
        }
        return mv;
    }


    @GetMapping("/{id}")
    public ModelAndView mostrarEquipo(@PathVariable Long id, HttpServletRequest request) {
        logger.debug("Entrando a mostrarEquipo con id: {}", id);
        ModelAndView mv = new ModelAndView("equipos/verEquipo");
        try {
            UsuarioRegistrado user = (UsuarioRegistrado) request.getSession().getAttribute("usuario");
            if (user == null) {
                throw new AccessDeniedException("Usuario no autenticado.");
            }

            Equipo equipo = equipoService.obtenerEquipoPorId(id)
                    .orElseThrow(() -> new NoSuchElementException("Equipo no encontrado"));

            boolean esResponsable = equipo.getResponsables().stream()
                    .anyMatch(responsable -> responsable.getUsuario().equals(user.getUsuario()));
            if (!esResponsable && user.getRol() != Rol.ADMIN) {
                throw new AccessDeniedException("No tienes permisos para ver este equipo.");
            }

            mv.addObject("equipo", equipo);
        } catch (NoSuchElementException e) {
            logger.error("Equipo no encontrado con id: {}", id);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Equipo no encontrado.");
        } catch (AccessDeniedException e) {
            logger.error("Acceso denegado para el usuario: {}", request.getSession().getAttribute("usuario"));
            mv.setViewName("error");
            mv.addObject("mensajeError", "No tienes permisos para ver este equipo.");
        } catch (Exception e) {
            logger.error("Error al mostrar el equipo con id: {}", id, e);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Error al mostrar el equipo.");
        }
        return mv;
    }


}
