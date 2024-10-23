/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.portal.formula1.controller;

import com.portal.formula1.model.Aficionado;
import com.portal.formula1.service.AficionadoService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author fjavi
 */
@Controller
@RequestMapping(value = "/")
public class AficionadoController {
    
    @Autowired
    AficionadoService service;
    @GetMapping(value = "/aficionado")
    public ModelAndView getUser() {
        
        List<Aficionado> lista = service.getAllAficionados();
        ModelAndView mv = new ModelAndView();
        mv.setViewName("aficionados");
        mv.getModel().put("aficionados", lista);
        
        return mv;
    }
}

  
