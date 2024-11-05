
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.portal.formula1.controller;


import com.portal.formula1.model.Noticia;
import com.portal.formula1.service.NoticiaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "/")
public class NoticiaController {

    private final NoticiaService noticiaService;

    public NoticiaController(NoticiaService noticiaService) {
        this.noticiaService = noticiaService;
    }

    @GetMapping("/crearNoticia")
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
        ModelAndView mv = new ModelAndView();

        if (!imagen.isEmpty() && !isValidImage(imagen)) {
            result.rejectValue("imagen", "error.noticia", "La imagen debe ser JPG o PNG y no exceder 2MB");
        }

        if (result.hasErrors()) {
            mv.setViewName("crearNoticia");
            mv.addObject("noticia", noticia);
            return mv;
        }

        if (!imagen.isEmpty()) {
            String imagenUrl = guardarImagen(imagen);
            noticia.setImagen(imagenUrl);
        }

        noticiaService.guardarNoticia(noticia);
        redirectAttributes.addFlashAttribute("mensaje", "Noticia creada exitosamente.");
        mv.setViewName("redirect:/admin/noticias/listado");
        return mv;
    }

    @GetMapping("/listado")
    public ModelAndView listarNoticias() {
        ModelAndView mv = new ModelAndView("listadoNoticias");
        mv.addObject("noticias", noticiaService.obtenerNoticias());
        return mv;
    }

    private boolean isValidImage(MultipartFile file) {
        String contentType = file.getContentType();
        long size = file.getSize();
        return (contentType.equals("image/jpeg") || contentType.equals("image/png")) && size <= 2 * 1024 * 1024;
    }

    private String guardarImagen(MultipartFile file) {
        // Lógica para guardar la imagen y devolver la URL
        return "/path/to/image";  // Placeholder
    }
}
