
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.portal.formula1.controller;


import com.portal.formula1.model.Noticia;
import com.portal.formula1.model.Rol;
import com.portal.formula1.model.UsuarioRegistrado;
import com.portal.formula1.service.ImagenService;
import com.portal.formula1.service.NoticiaService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.validation.Valid;
@Controller
@RequestMapping("/noticias")
public class NoticiaController {

    @Autowired
    private NoticiaService noticiaService;

    @Autowired
    private ImagenService imagenService;

    private static final Logger logger = LoggerFactory.getLogger(NoticiaController.class);

    @GetMapping("/favicon.ico")
    @ResponseBody
    void returnNoFavicon() {
        // No hacer nada
    }

    // Muestra el listado de noticias en la página principal
    @GetMapping("/listar")
    public ModelAndView listarNoticias() {
        ModelAndView mv = new ModelAndView("noticias/listadoNoticias");
        List<Noticia> noticias = noticiaService.obtenerNoticias();
        mv.addObject("noticias", noticias);
        return mv;
    }

    // Muestra el formulario para crear una nueva noticia
    @GetMapping("/crear")
    public ModelAndView mostrarFormularioCreacion() {
        ModelAndView mv = new ModelAndView("noticias/crearNoticia");
        mv.addObject("noticia", new Noticia());
        return mv;
    }

    @PostMapping("/crear")
    public ModelAndView crearNoticia(@Valid @ModelAttribute("noticia") Noticia noticia,
                                     BindingResult result,
                                     @RequestParam("imagenArchivo") MultipartFile imagenArchivo,
                                     RedirectAttributes redirectAttributes) {

    ModelAndView mv = new ModelAndView("noticias/crear");
    if (result.hasErrors()) {
        return mv;
    }
    try {
        // Validación y guardado de la imagen
        if (imagenArchivo != null && !imagenArchivo.isEmpty()) {
            if (!imagenService.isFormatoValido(imagenArchivo)) {
                result.rejectValue("imagen", "error.noticia", "Formato de imagen no permitido. Solo JPG y PNG.");
                return mv;
            }
            if (!imagenService.isTamanoValido(imagenArchivo)) {
                result.rejectValue("imagen", "error.noticia", "La imagen supera el tamaño máximo permitido de 2 MB.");
                return mv;
            }
            // Guardar la imagen en resources/static/uploads
            String nombreArchivo = System.currentTimeMillis() + "_" + imagenArchivo.getOriginalFilename();
            Path rutaArchivo = Paths.get("src/main/resources/static/uploads").resolve(nombreArchivo).toAbsolutePath();
            Files.copy(imagenArchivo.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

            // Establecer el nombre del archivo en el objeto noticia
            noticia.setImagen(nombreArchivo);
        }

        // Guardar la noticia con permalink único
        noticiaService.crearNoticia(noticia.getTitulo(), imagenArchivo, noticia.getTexto());
        redirectAttributes.addFlashAttribute("mensaje", "La noticia ha sido creada exitosamente.");
        mv.setViewName("redirect:/noticias/listar");




    } catch (Exception e) {
        logger.error("Error al guardar la noticia: ", e);
        result.rejectValue("imagen", "error.noticia", "Error al guardar la imagen: " + e.getMessage());
    }


    return mv;
    }

    // Muestra una noticia específica
    @GetMapping("/{id}")
    public ModelAndView mostrarNoticia(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        ModelAndView mv = new ModelAndView();
        Noticia noticia = noticiaService.obtenerNoticiaPorId(id);

        if (noticia == null) {
            redirectAttributes.addFlashAttribute("error", "La noticia no existe.");
            mv.setViewName("redirect:/noticias/listar");
            return mv;
        }

        mv.setViewName("noticias/detalle");
        mv.addObject("noticia", noticia);
        return mv;
    }

    // Proceso de búsqueda de noticias por título o descripción
    @GetMapping("/buscar")
    public ModelAndView buscarNoticias(@RequestParam("query") String query) {
        ModelAndView mv = new ModelAndView("noticias/listadoNoticias");
        List<Noticia> resultados = noticiaService.buscarNoticias(query);
        mv.addObject("noticias", resultados);
        mv.addObject("query", query);
        return mv;
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Integer id,
                                           Model model,
                                           RedirectAttributes redirectAttributes) {
        try {
            // Valida si la noticia existe y es editable (menos de 24 horas)
            Noticia noticia = noticiaService.obtenerNoticiaParaEdicion(id);

            // Si pasa las validaciones, muestra el formulario
            model.addAttribute("noticia", noticia);
            return "noticias/editar";

        } catch (NoticiaService.EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Noticia no encontrada");
            return "redirect:/noticias/listar";

        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/noticias/listar";
        }
    }

    @PostMapping("/editar/{id}")
    public String editarNoticia(
            @PathVariable Integer id,
            @Valid @ModelAttribute("noticia") Noticia noticiaActualizada,
            BindingResult result,
            @RequestParam("imagenArchivo") MultipartFile imagenArchivo,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            return "noticias/editar";
        }

        try {
            noticiaService.actualizarNoticia(id, noticiaActualizada.getTitulo(), imagenArchivo, noticiaActualizada.getTexto());
            redirectAttributes.addFlashAttribute("mensaje", "Noticia actualizada correctamente");
            return "redirect:/noticias/listar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/noticias/editar/" + id;
        }
    }


    @PostMapping("/{id}/eliminar")
    public ModelAndView eliminarNoticia(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        noticiaService.eliminarNoticia(id);
        redirectAttributes.addFlashAttribute("mensaje", "La noticia ha sido eliminada correctamente.");
        return new ModelAndView("redirect:/noticias/listar");
    }

    @ControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(IllegalStateException.class)
        public String handleEdicionNoPermitida(IllegalStateException ex, RedirectAttributes redirectAttributes) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/noticias/listar";
        }

        @ExceptionHandler(NoticiaService.EntityNotFoundException.class)
        public String handleNoticiaNoEncontrada(NoticiaService.EntityNotFoundException ex, RedirectAttributes redirectAttributes) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/noticias/listar";
        }
    }

}
