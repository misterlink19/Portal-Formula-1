package com.portal.formula1.service;

import com.portal.formula1.model.Circuito;
import com.portal.formula1.repository.CircuitoDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CircuitoService {

    @Autowired
    CircuitoDAO circuitoDAO;

    public void crearCircuito(Circuito circuito) {
        circuitoDAO.save(circuito);
    }
}
