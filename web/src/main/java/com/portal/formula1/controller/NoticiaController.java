
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.portal.formula1.controller;


import com.portal.formula1.model.Noticia;
import com.portal.formula1.service.ImagenService;
import com.portal.formula1.service.NoticiaService;
import java.util.List;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    private final NoticiaService noticiaService;
    private final ImagenService imagenService;
    private static final Logger logger = LoggerFactory.getLogger(NoticiaController.class);

    public NoticiaController(NoticiaService noticiaService, ImagenService imagenService) {
        this.noticiaService = noticiaService;
        this.imagenService = imagenService;
    }

    @GetMapping("/crear")
    public ModelAndView mostrarFormularioCreacion() {
        ModelAndView mv = new ModelAndView("crearNoticia");
        mv.addObject("noticia", new Noticia());
        return mv;
    }

    @PostMapping("/crear")
    public ModelAndView crearNoticia(@Valid Noticia noticia,
                                 BindingResult result,
                                 @RequestParam("imagen") MultipartFile imagen,
                                 RedirectAttributes redirectAttributes) {
    if (!imagen.isEmpty() && !imagenService.isValidImage(imagen)) {
        result.rejectValue("imagen", "error.noticia", "La imagen debe ser JPG o PNG y no exceder 2MB");
    }

    if (result.hasErrors()) {
            logger.error("Errores en la validación de la noticia: " + result.getAllErrors());
            ModelAndView mv = new ModelAndView("crearNoticia");
            mv.addObject("noticia", noticia);
            return mv;
        }

    if (!imagen.isEmpty()) {
        String imagenUrl = imagenService.guardarImagen(imagen);
        noticia.setImagen(imagenUrl);
    }

    noticiaService.guardarNoticia(noticia);
    redirectAttributes.addFlashAttribute("mensaje", "Noticia creada exitosamente.");
    return new ModelAndView("redirect:/listado");
    }


        @GetMapping("/listado")
            public ModelAndView listarNoticias() {
            ModelAndView mv = new ModelAndView("listadoNoticias");
            List<Noticia> noticias = noticiaService.obtenerNoticias();
            mv.addObject("noticias", noticias);
            return mv;
        }
            
    @PostMapping("/eliminar")
        public ModelAndView eliminarNoticia(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        noticiaService.eliminarNoticia(id);
        redirectAttributes.addFlashAttribute("mensaje", "Noticia eliminada exitosamente.");
        return new ModelAndView("redirect:/listado");
    }
        
    // Opción para búsqueda de noticias
    @GetMapping("/buscar")
    public ModelAndView buscarNoticias(@RequestParam("query") String query) {
        ModelAndView mv = new ModelAndView("listadoNoticias");
        List<Noticia> noticias = noticiaService.buscarNoticias(query);
        mv.addObject("noticias", noticias);
        return mv;
    }
    public ModelAndView verNoticia(@RequestParam("id") Integer id) {
    Noticia noticia = noticiaService.obtenerNoticiaPorId(id);
    if (noticia == null) {
        // Manejar la situación donde no se encuentra la noticia, tal vez redirigir a una página de error
        return new ModelAndView("error/noticiaNoEncontrada");
    }
    ModelAndView mv = new ModelAndView("detalleNoticia");
    mv.addObject("noticia", noticia);
    return mv;
}


}
