package com.portal.formula1;
import com.portal.formula1.model.Piloto;
import com.portal.formula1.repository.PilotoDAO;
import com.portal.formula1.service.PilotoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PilotoServiceTests {

    @Mock
    private PilotoDAO pilotoDAO;

    @InjectMocks
    private PilotoService pilotoService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Verifica que se puede guardar un piloto y
     * que se llama al método save del PilotoDAO
     **/
    @Test
    public void testGuardarPiloto() {
        Piloto piloto = new Piloto();
        when(pilotoDAO.save(any(Piloto.class))).thenReturn(piloto);

        Piloto result = pilotoService.guardarPiloto(piloto);

        assertNotNull(result);
        verify(pilotoDAO, times(1)).save(piloto);
    }

    /**
     * Verifica que se puede eliminar un piloto por su dorsal y
     * que se llama al método deleteById del PilotoDAO
     **/
    @Test
    public void testEliminarPiloto() {
        doNothing().when(pilotoDAO).deleteById(anyInt());
        pilotoService.eliminarPiloto(1);

        verify(pilotoDAO, times(1)).deleteById(1);
    }

    /**
     * Verifica que se puede obtener un piloto por su dorsal y
     * que se llama al método findById del PilotoDAO
     **/
    @Test
    public void testObtenerPilotoPorDorsal() {
        Piloto piloto = new Piloto();
        piloto.setDorsal(1);
        when(pilotoDAO.findById(1)).thenReturn(Optional.of(piloto));

        Optional<Piloto> result = pilotoService.obtenerPilotoPorDorsal(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getDorsal());
        verify(pilotoDAO, times(1)).findById(1);
    }

    /**
     * Verifica que se puede listar pilotos por equipo y
     * que se llama al método findByEquipo_Id del PilotoDAO
     **/
    @Test
    public void testListarPilotosPorEquipo() {
        List<Piloto> pilotos = Arrays.asList(new Piloto(), new Piloto());
        when(pilotoDAO.findByEquipo_Id(1L)).thenReturn(pilotos);

        List<Piloto> result = pilotoService.listarPilotosPorEquipo(1L);

        assertEquals(2, result.size());
        verify(pilotoDAO, times(1)).findByEquipo_Id(1L);
    }

    /**
     * Verifica que se puede comprobar si un dorsal existe y
     * que se llama al método existsByDorsal del PilotoDAO
     **/
    @Test
    public void testExisteDorsal() {
        when(pilotoDAO.existsByDorsal(1)).thenReturn(true);

        boolean exists = pilotoService.existeDorsal(1);

        assertTrue(exists);
        verify(pilotoDAO, times(1)).existsByDorsal(1);
    }

    /**
     * Verifica que se puede comprobar si unas siglas existen y
     * que se llama al método existsBySiglas del PilotoDAO
     **/
    @Test
    public void testExisteSiglas() {
        when(pilotoDAO.existsBySiglas("JD")).thenReturn(true);

        boolean exists = pilotoService.existeSiglas("JD");

        assertTrue(exists);
        verify(pilotoDAO, times(1)).existsBySiglas("JD");
    }
}
