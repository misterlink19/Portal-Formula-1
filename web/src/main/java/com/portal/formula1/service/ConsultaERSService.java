package com.portal.formula1.service;

import com.portal.formula1.model.ConsultaERS;
import com.portal.formula1.repository.ConsultaERSDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultaERSService {
    @Autowired
    private ConsultaERSDAO consultaERSDAO;

    public void guardarConsultaERS(ConsultaERS consultaERS) {
        consultaERSDAO.save(consultaERS);
    }

    public List<ConsultaERS> listarConsultaERS() {
        return consultaERSDAO.findAll();
    }

    public ConsultaERS consultaPorId(Long id) {
        return consultaERSDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Consulta no encontrada"));
    }
}
