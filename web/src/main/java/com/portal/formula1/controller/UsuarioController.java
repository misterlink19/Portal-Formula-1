package com.portal.formula1.controller;

import com.portal.formula1.model.UsuarioRegistrado;
import com.portal.formula1.service.UsuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);


    @GetMapping("/registro")
    public ModelAndView mostrarFormularioRegistro(Model model) {
        logger.debug("Entrando a mostrarFormularioRegistro");
        ModelAndView mv = new ModelAndView();
        mv.getModel().put("usuarioRegistrado", new UsuarioRegistrado());
        mv.setViewName("registro");
        return mv;
    }

    @PostMapping("/registro")
    public String registrarUsuario(@Valid @ModelAttribute("usuarioRegistrado") UsuarioRegistrado usuario, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("usuarioRegistrado", usuario);
            return "registro";
        }
        logger.debug("Entrando a registrarUsuario con usuario: {}", usuario);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        usuarioService.registrarUsuario(usuario);
        logger.debug("Usuario registrado exitosamente");
        return "redirect:/usuarios/registro?success";
    }



}
