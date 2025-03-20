package com.portal.formula1.service;

import com.portal.formula1.model.ConsultaCombustible;
import com.portal.formula1.repository.ConsultaCombustibleDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ConsultaCombustibleService {
    @Autowired
    ConsultaCombustibleDAO consultaCombustibleDAO;

    public void guardarConsulta(ConsultaCombustible consulta) {consultaCombustibleDAO.save(consulta);}

    public List<ConsultaCombustible> listarConsultas() {
        return consultaCombustibleDAO.findAll();
    }

    public ConsultaCombustible consultaPorId(Long id) {
        return consultaCombustibleDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Consulta no encontrada"));
    }

    public List<ConsultaCombustible> consultaPorEquipo(Long idEquipo) {
        return consultaCombustibleDAO.findAllByEquipo_Id(idEquipo);
    }
}
