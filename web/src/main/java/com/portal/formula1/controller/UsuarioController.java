package com.portal.formula1.controller;

import com.portal.formula1.model.UsuarioRegistrado;
import com.portal.formula1.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/registro")
    public ModelAndView mostrarFormularioRegistro(Model model) {
        ModelAndView mv = new ModelAndView();
        mv.getModel().put("usuarioRegistrado", new UsuarioRegistrado());
        mv.setViewName("registro");
        return mv;
    }

    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute("usuarioRegistrado") UsuarioRegistrado usuario) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        usuarioService.registrarUsuario(usuario);
        return "redirect:/usuarios/registro?success";
    }
}
