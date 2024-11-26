package com.portal.formula1;

import com.portal.formula1.controller.EquipoController;
import com.portal.formula1.model.Equipo;
import com.portal.formula1.service.EquipoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.Mockito.when;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EquipoControllerTests {
    @Mock
    private EquipoService equipoService;

    @InjectMocks
    private EquipoController equipoController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(equipoController).build();
    }

    /**
     * Verifica que la vista de la lista de equipos se muestra correctamente.
     */
    @Test
    public void testMostrarMenuEquipos() throws Exception {
        when(equipoService.obtenerTodosLosEquipos()).thenReturn(Arrays.asList(new Equipo(), new Equipo()));
        mockMvc.perform(get("/equipos"))
                .andExpect(status().isOk())
                .andExpect(view().name("equipos/equipo"))
                .andExpect(model().attributeExists("equipos"));
    }

    /**
     * Verifica que el formulario de creación de equipos se muestra correctamente.
     **/
    @Test
    public void testMostrarFormularioCreacion() throws Exception {
        mockMvc.perform(get("/equipos/crear"))
                .andExpect(status().isOk())
                .andExpect(view().name("equipos/crearEquipo"))
                .andExpect(model().attributeExists("equipo"));
    }

    /**
     * Verifica que se puede crear un equipo y que se redirige a la vista del equipo creado.
     **/
    @Test
    public void testCrearEquipo() throws Exception {
        Equipo equipo = new Equipo();
        equipo.setId(1L);
        when(equipoService.crearEquipo(any(Equipo.class))).thenReturn(equipo);

        mockMvc.perform(post("/equipos")
                        .flashAttr("equipo", new Equipo()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/equipos/1"));
    }

    /***
     * Verifica que la vista de un equipo específico se muestra correctamente.
     * **/
    @Test
    public void testMostrarEquipo() throws Exception {
        Equipo equipo = new Equipo();
        equipo.setId(1L);
        when(equipoService.obtenerEquipoPorId(1L)).thenReturn(Optional.of(equipo));

        mockMvc.perform(get("/equipos/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("verEquipo"))
                .andExpect(model().attributeExists("equipo"))
                .andExpect(model().attribute("equipo", equipo));
    }

    /**
     * Verifica que se maneja correctamente el caso en el que no se encuentra un equipo.
     **/
    @Test
    public void testMostrarEquipoNoEncontrado() throws Exception {
        when(equipoService.obtenerEquipoPorId(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/equipos/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("mensajeError"));
    }
}
