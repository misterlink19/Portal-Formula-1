package com.portal.formula1;

import com.portal.formula1.model.Circuito;
import com.portal.formula1.repository.CircuitoDAO;
import com.portal.formula1.service.CircuitoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CircuitoServiceTests {

    @Mock
    private CircuitoDAO circuitoDAO;

    @InjectMocks
    private CircuitoService circuitoService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test para crear un nuevo circuito.
     */
    @Test
    public void testCrearCircuito() {
        Circuito circuito = new Circuito("Nombre", "Ciudad", "Pais", "Trazado", 10, 1000, 2, 3, 5);
        when(circuitoDAO.save(circuito)).thenReturn(circuito);

        circuitoService.crearOActualizarCircuito(circuito);

        verify(circuitoDAO, times(1)).save(circuito);
    }

    /**
     * Test para actualizar un circuito existente.
     */
    @Test
    public void testActualizarCircuito() {
        Circuito circuito = new Circuito("Nombre", "Ciudad", "Pais", "Trazado", 10, 1000, 2, 3, 5);
        circuito.setId(1L); // Simula que ya tiene un ID
        when(circuitoDAO.save(circuito)).thenReturn(circuito);

        circuitoService.crearOActualizarCircuito(circuito);

        verify(circuitoDAO, times(1)).save(circuito);
    }

    /**
     * Test para listar todos los circuitos.
     */
    @Test
    public void testListarCircuitos() {
        List<Circuito> circuitos = Arrays.asList(
                new Circuito("Nombre1", "Ciudad1", "Pais1", "Trazado1", 10, 1000, 2, 3, 5),
                new Circuito("Nombre2", "Ciudad2", "Pais2", "Trazado2", 12, 1200, 3, 4, 6)
        );
        when(circuitoDAO.findAll()).thenReturn(circuitos);

        List<Circuito> resultado = circuitoService.listarCircuitos();

        assertEquals(2, resultado.size());
        verify(circuitoDAO, times(1)).findAll();
    }

    /**
     * Test para obtener un circuito por su ID.
     */
    @Test
    public void testObtenerCircuitoPorId() {
        Circuito circuito = new Circuito("Nombre", "Ciudad", "Pais", "Trazado", 10, 1000, 2, 3, 5);
        circuito.setId(1L);
        when(circuitoDAO.findById(1L)).thenReturn(Optional.of(circuito));

        Circuito resultado = circuitoService.obtenerCircuitoPorId(1L);

        assertEquals("Nombre", resultado.getNombre());
        verify(circuitoDAO, times(1)).findById(1L);
    }

    /**
     * Test para intentar obtener un circuito inexistente por su ID.
     */
    @Test
    public void testObtenerCircuitoPorIdNoExistente() {
        when(circuitoDAO.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> circuitoService.obtenerCircuitoPorId(1L));

        verify(circuitoDAO, times(1)).findById(1L);
    }

    /**
     * Test para eliminar un circuito existente.
     */
    @Test
    public void testEliminarCircuito() {
        when(circuitoDAO.existsById(1L)).thenReturn(true);

        circuitoService.eliminarCircuitoPorId(1L);

        verify(circuitoDAO, times(1)).deleteById(1L);
    }

    /**
     * Test para intentar eliminar un circuito inexistente.
     */
    @Test
    public void testEliminarCircuitoNoExistente() {
        when(circuitoDAO.existsById(1L)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> circuitoService.eliminarCircuitoPorId(1L));

        verify(circuitoDAO, never()).deleteById(1L);
    }

    /**
     * Test para eliminar un circuito con referencias a otras entidades.
     */
    @Test
    public void testEliminarCircuitoConReferencias() {
        Long id = 1L;
        when(circuitoDAO.existsById(id)).thenReturn(true);
        doThrow(new DataIntegrityViolationException("Error de integridad referencial")).when(circuitoDAO).deleteById(id);

        assertThrows(RuntimeException.class, () -> circuitoService.eliminarCircuitoPorId(id)); // Espera RuntimeException

        verify(circuitoDAO, times(1)).deleteById(id);
    }
}