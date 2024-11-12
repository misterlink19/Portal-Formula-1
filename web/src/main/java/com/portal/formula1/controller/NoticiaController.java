
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.portal.formula1.controller;


import com.portal.formula1.model.Noticia;
import com.portal.formula1.service.ImagenService;
import com.portal.formula1.service.NoticiaService;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/")
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
    @GetMapping
    public ModelAndView listarNoticias() {
        ModelAndView mv = new ModelAndView("listadoNoticias");
        List<Noticia> noticias = noticiaService.obtenerNoticias();
        mv.addObject("noticias", noticias);
        return mv;
    }

    // Muestra el formulario para crear una nueva noticia
    @GetMapping("/crear")
    public ModelAndView mostrarFormularioCreacion() {
        ModelAndView mv = new ModelAndView("crearNoticia");
        mv.addObject("noticia", new Noticia());
        return mv;
    }

    @PostMapping("/crear")
    public ModelAndView crearNoticia(@Valid @ModelAttribute("noticia") Noticia noticia,
                                     BindingResult result,
                                     @RequestParam("imagenArchivo") MultipartFile imagenArchivo,
                                     RedirectAttributes redirectAttributes) {

    ModelAndView mv = new ModelAndView("crearNoticia");
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
        }

        // Guardar la noticia con permalink único
        noticiaService.crearNoticia(noticia.getTitulo(), imagenArchivo, noticia.getTexto());
        redirectAttributes.addFlashAttribute("mensaje", "La noticia ha sido creada exitosamente.");
        mv.setViewName("redirect:/");




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
            mv.setViewName("redirect:/");
            return mv;
        }

        mv.setViewName("/detalle");
        mv.addObject("noticia", noticia);
        return mv;
    }

    // Proceso de búsqueda de noticias por título o descripción
    @GetMapping("/buscar")
    public ModelAndView buscarNoticias(@RequestParam("query") String query) {
        ModelAndView mv = new ModelAndView("listadoNoticias");
        List<Noticia> resultados = noticiaService.buscarNoticias(query);
        mv.addObject("noticias", resultados);
        mv.addObject("query", query);
        return mv;
    }


    // Elimina una noticia específica
    @PostMapping("/{id}/eliminar")
    public ModelAndView eliminarNoticia(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        noticiaService.eliminarNoticia(id);
        redirectAttributes.addFlashAttribute("mensaje", "La noticia ha sido eliminada correctamente.");
        return new ModelAndView("redirect:/");
    }
        




}
