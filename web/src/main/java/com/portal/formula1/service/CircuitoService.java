package com.portal.formula1.service;

import com.portal.formula1.model.Circuito;
import com.portal.formula1.repository.CircuitoDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CircuitoService {

    @Autowired
    CircuitoDAO circuitoDAO;

    public void crearOActualizarCircuito(Circuito circuito) {
        circuitoDAO.save(circuito);
    }

    public List<Circuito> listarCircuitos() {
        return circuitoDAO.findAll();
    }

    public Circuito obtenerCircuitoPorId(Long id) {
        return circuitoDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Circuito no encontrado"));
    }

    public void eliminarCircuitoPorId(Long id) {
        if (!circuitoDAO.existsById(id)) {
            throw new NoSuchElementException("Circuito no encontrado");
        }
        try {
            circuitoDAO.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("No se puede eliminar el circuito. Podría tener carreras evento.", e);
        }
    }
}
