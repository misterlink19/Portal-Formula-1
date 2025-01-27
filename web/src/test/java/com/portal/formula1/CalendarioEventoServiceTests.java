package com.portal.formula1;

import com.portal.formula1.model.CalendarioEvento;
import com.portal.formula1.model.Circuito;
import com.portal.formula1.repository.CalendarioEventoDAO;
import com.portal.formula1.service.CalendarioEventoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CalendarioEventoServiceTests {

    @Mock
    private CalendarioEventoDAO calendarioEventoDAO;

    @InjectMocks
    private CalendarioEventoService calendarioEventoService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test para listar todos los eventos.
     */
    @Test
    public void testListarEventos() {
        List<CalendarioEvento> eventos = Arrays.asList(
                new CalendarioEvento(1L, "Evento 1", LocalDate.now(), new Circuito()),
                new CalendarioEvento(2L, "Evento 2", LocalDate.now().plusDays(1), new Circuito())
        );

        when(calendarioEventoDAO.findAll()).thenReturn(eventos);

        List<CalendarioEvento> resultado = calendarioEventoService.listarEventos();

        assertEquals(2, resultado.size());
        verify(calendarioEventoDAO, times(1)).findAll();
    }

    /**
     * Test para guardar un evento.
     */
    @Test
    public void testGuardarEvento() {
        CalendarioEvento evento = new CalendarioEvento(1L, "Nuevo Evento", LocalDate.now(), new Circuito());

        when(calendarioEventoDAO.save(evento)).thenReturn(evento);

        CalendarioEvento resultado = calendarioEventoService.guardarEvento(evento);

        assertEquals("Nuevo Evento", resultado.getNombreEvento());
        verify(calendarioEventoDAO, times(1)).save(evento);
    }

    /**
     * Test para eliminar un evento existente.
     */
    @Test
    public void testEliminarEvento() {
        when(calendarioEventoDAO.existsById(1L)).thenReturn(true);

        calendarioEventoService.eliminarEvento(1L);

        verify(calendarioEventoDAO, times(1)).deleteById(1L);
    }

    /**
     * Test para intentar eliminar un evento inexistente.
     */
    @Test
    public void testEliminarEventoNoExistente() {
        when(calendarioEventoDAO.existsById(1L)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> calendarioEventoService.eliminarEvento(1L));

        verify(calendarioEventoDAO, never()).deleteById(1L);
    }

    /**
     * Test para editar un evento existente.
     */
    @Test
    public void testEditarEvento() {
        Circuito circuito = new Circuito();
        circuito.setId(1L);
        circuito.setNombre("Circuito Ejemplo");
        circuito.setCiudad("Ciudad");
        circuito.setPais("País");
        circuito.setNumeroVueltas(10);
        circuito.setLongitud(1000.0);
        circuito.setNumeroCurvasLentas(2);
        circuito.setNumeroCurvasMedia(3);
        circuito.setNumeroCurvasRapidas(5);
        circuito.setTrazado("trazado.png");
        CalendarioEvento eventoExistente = new CalendarioEvento(1L, "Evento Original", LocalDate.now(), circuito);
        CalendarioEvento nuevoEvento = new CalendarioEvento(1L, "Evento Editado", LocalDate.now().plusDays(1), circuito);

        when(calendarioEventoDAO.findById(1L)).thenReturn(Optional.of(eventoExistente));
        when(calendarioEventoDAO.save(eventoExistente)).thenReturn(eventoExistente);

        CalendarioEvento resultado = calendarioEventoService.editarEvento(1L, nuevoEvento);

        assertEquals("Evento Editado", resultado.getNombreEvento());
        verify(calendarioEventoDAO, times(1)).findById(1L);
        verify(calendarioEventoDAO, times(1)).save(eventoExistente);
    }

    /**
     * Test para buscar eventos por fecha.
     */
    @Test
    public void testBuscarPorFecha() {
        LocalDate fecha = LocalDate.now();
        List<CalendarioEvento> eventos = Arrays.asList(
                new CalendarioEvento(1L, "Evento 1", fecha, new Circuito())
        );

        when(calendarioEventoDAO.findByFecha(fecha)).thenReturn(eventos);

        List<CalendarioEvento> resultado = calendarioEventoService.buscarPorFecha(fecha);

        assertEquals(1, resultado.size());
        verify(calendarioEventoDAO, times(1)).findByFecha(fecha);
    }

    /**
     * Test para obtener un evento por ID.
     */
    @Test
    public void testObtenerEventoPorId() {
        CalendarioEvento evento = new CalendarioEvento(1L, "Evento 1", LocalDate.now(), new Circuito());

        when(calendarioEventoDAO.findById(1L)).thenReturn(Optional.of(evento));

        CalendarioEvento resultado = calendarioEventoService.obtenerEventoPorId(1L);

        assertEquals("Evento 1", resultado.getNombreEvento());
        verify(calendarioEventoDAO, times(1)).findById(1L);
    }

    /**
     * Test para intentar obtener un evento inexistente.
     */
    @Test
    public void testObtenerEventoPorIdNoExistente() {
        when(calendarioEventoDAO.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> calendarioEventoService.obtenerEventoPorId(1L));

        verify(calendarioEventoDAO, times(1)).findById(1L);
    }
}
