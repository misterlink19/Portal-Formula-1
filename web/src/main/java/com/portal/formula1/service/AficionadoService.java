/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.portal.formula1.service;

import java.util.List;
import com.portal.formula1.model.Aficionado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.portal.formula1.repository.AficionadoDAO;

/**
 *
 * @author fjavi
 */
@Service
public class AficionadoService {
    
    @Autowired
    private AficionadoDAO aficionadoDAO;
    
    public List<Aficionado> getAllAficionados(){
        return aficionadoDAO.findAll();
    }
    
    
}
