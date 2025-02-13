package com.portal.formula1.service;

import com.portal.formula1.model.CalendarioEvento;
import com.portal.formula1.model.Encuesta;
import com.portal.formula1.repository.CalendarioEventoDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CalendarioEventoService {

    @Autowired
    private CalendarioEventoDAO calendarioEventoDAO;

    // Listar todos los eventos
    public List<CalendarioEvento> listarEventos() {
        return calendarioEventoDAO.findAll();
    }

    // Crea un evento
    public CalendarioEvento guardarEvento(CalendarioEvento evento) {
        Optional<CalendarioEvento> eventoExistente = calendarioEventoDAO.findByNombreEventoAndFecha(
                evento.getNombreEvento(), evento.getFecha());

        if (eventoExistente.isPresent()) {
            throw new IllegalArgumentException("Ya existe un evento con el mismo nombre en esa fecha.");
        }
        return calendarioEventoDAO.save(evento);
    }

    // Eliminar un evento por ID
    public void eliminarEvento(Long id) {
        if (!calendarioEventoDAO.existsById(id)) {
            throw new NoSuchElementException("El evento con ID " + id + " no existe.");
        }
        calendarioEventoDAO.deleteById(id);
    }

    // Editar un evento
    public CalendarioEvento editarEvento(Long id, CalendarioEvento nuevoEvento) {
        CalendarioEvento eventoExistente = calendarioEventoDAO.findById(id)
                .orElseThrow(() -> new NoSuchElementException("El evento con ID " + id + " no existe."));
        eventoExistente.setFecha(nuevoEvento.getFecha());
        eventoExistente.setNombreEvento(nuevoEvento.getNombreEvento());
        eventoExistente.setCircuito(nuevoEvento.getCircuito());
        return calendarioEventoDAO.save(eventoExistente);
    }

    // Buscar eventos por fecha
    public List<CalendarioEvento> buscarPorFecha(LocalDate fecha) {
        return calendarioEventoDAO.findByFecha(fecha);
    }

    public CalendarioEvento obtenerEventoPorId(Long id) {
        Optional<CalendarioEvento> calendarioEventoOptional = calendarioEventoDAO.findById(id);
        return calendarioEventoOptional.orElseThrow(() -> new NoSuchElementException("Evento no encontrado con id: " + id));
    }
    public boolean existsByCircuitoId(Long circuitoId) {
        return calendarioEventoDAO.existsByCircuitoId(circuitoId);
    }

}
