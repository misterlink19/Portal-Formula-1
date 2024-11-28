package com.portal.formula1.controller;

import com.portal.formula1.model.Equipo;
import com.portal.formula1.service.EquipoService;
import com.portal.formula1.service.ImagenService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping
    public ModelAndView mostrarMenuEquipos() {
        logger.debug("Entrando a mostrarMenuEquipos");
        ModelAndView mv = new ModelAndView("equipos/equipo");
        try {
            List<Equipo> equipos = equipoService.obtenerTodosLosEquipos();
            mv.addObject("equipos", equipos);
        } catch (Exception e) {
            logger.error("Error al cargar el menú de equipos: ", e);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Error al cargar el menú de equipos.");
        }
        return mv;
    }


    @GetMapping("/crear")
    public ModelAndView mostrarFormularioCreacion() {
        logger.debug("Entrando a mostrarFormularioCreacion");
        ModelAndView mv = new ModelAndView("equipos/crearEquipo");
        try {
            mv.addObject("equipo", new Equipo());
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
                                    RedirectAttributes redirectAttributes) {
        logger.debug("Entrando a crearEquipo");
        ModelAndView mv = new ModelAndView("equipos/crearEquipo");

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

            // Guardar la imagen en resources/static/uploads
            String nombreArchivo = System.currentTimeMillis() + "_" + logoArchivo.getOriginalFilename();
            Path rutaArchivo = Paths.get("src/main/resources/static/uploads").resolve(nombreArchivo).toAbsolutePath();
            Files.copy(logoArchivo.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

            // Establecer el nombre del archivo en el objeto equipo
            equipo.setLogo(nombreArchivo);

            // Guardar el equipo
            Equipo nuevoEquipo = equipoService.crearEquipo(equipo);
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
    public ModelAndView mostrarEquipo(@PathVariable Long id) {
        logger.debug("Entrando a mostrarEquipo con id: {}", id);
        ModelAndView mv = new ModelAndView("equipos/verEquipo");
        try {
            Equipo equipo = equipoService.obtenerEquipoPorId(id)
                    .orElseThrow(() -> new NoSuchElementException("Equipo no encontrado"));
            mv.addObject("equipo", equipo);
        } catch (NoSuchElementException e) {
            logger.error("Equipo no encontrado con id: {}", id);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Equipo no encontrado.");
        } catch (Exception e) {
            logger.error("Error al mostrar el equipo con id: {}", id, e);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Error al mostrar el equipo.");
        }
        return mv;
    }




}
