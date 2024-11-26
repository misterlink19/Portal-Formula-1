package com.portal.formula1;

import com.portal.formula1.model.Equipo;
import com.portal.formula1.model.UsuarioRegistrado;
import com.portal.formula1.repository.EquipoDAO;
import com.portal.formula1.repository.UsuarioRegistradoDAO;
import com.portal.formula1.service.EquipoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class EquipoServiceTests {

    @Mock
    private EquipoDAO equipoDAO;

    @Mock
    private UsuarioRegistradoDAO usuarioRegistradoDAO;

    @InjectMocks
    private EquipoService equipoService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Verifica que se puede crear un equipo y
     * que se llama al método save del EquipoDAO
     **/
    @Test
    public void testCrearEquipo() {
        Equipo equipo = new Equipo();
        when(equipoDAO.save(any(Equipo.class))).thenReturn(equipo);

        Equipo result = equipoService.crearEquipo(equipo);

        assertNotNull(result);
        verify(equipoDAO, times(1)).save(equipo);
    }

    /**
     * Verifica que se puede obtener un equipo por su ID y
     * que se llama al método findById del EquipoDAO
     **/
    @Test
    public void testObtenerEquipoPorId() {
        Equipo equipo = new Equipo();
        equipo.setId(1L);
        when(equipoDAO.findById(1L)).thenReturn(Optional.of(equipo));

        Optional<Equipo> result = equipoService.obtenerEquipoPorId(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(equipoDAO, times(1)).findById(1L);
    }

    /**
     * Verifica que se pueden obtener todos los equipos y
     * que se llama al método findAll del EquipoDAO.
     **/
    @Test
    public void testObtenerTodosLosEquipos() {
        List<Equipo> equipos = Arrays.asList(new Equipo(), new Equipo());
        when(equipoDAO.findAll()).thenReturn(equipos);

        List<Equipo> result = equipoService.obtenerTodosLosEquipos();

        assertEquals(2, result.size());
        verify(equipoDAO, times(1)).findAll();
    }

    /**
     * Verifica que se puede agregar un responsable a un equipo y
     * que se llaman a los métodos findById y save del EquipoDAO
     * y UsuarioRegistradoDAO.
     **/
    @Test
    public void testAddResponsableToEquipo() {
        Equipo equipo = new Equipo();
        equipo.setId(1L);
        equipo.setResponsables(new ArrayList<>());  // Inicializando la lista aquí
        UsuarioRegistrado responsable = new UsuarioRegistrado();
        responsable.setUsuario("usuario1");

        when(equipoDAO.findById(1L)).thenReturn(Optional.of(equipo));
        when(usuarioRegistradoDAO.findById("usuario1")).thenReturn(Optional.of(responsable));
        when(equipoDAO.save(any(Equipo.class))).thenReturn(equipo);
        when(usuarioRegistradoDAO.save(any(UsuarioRegistrado.class))).thenReturn(responsable);

        Equipo result = equipoService.addResponsableToEquipo(1L, "usuario1");

        assertNotNull(result);
        assertEquals(equipo, result);
        verify(equipoDAO, times(1)).findById(1L);
        verify(usuarioRegistradoDAO, times(1)).findById("usuario1");
        verify(usuarioRegistradoDAO, times(1)).save(responsable);
        verify(equipoDAO, times(1)).save(equipo);
    }
}
