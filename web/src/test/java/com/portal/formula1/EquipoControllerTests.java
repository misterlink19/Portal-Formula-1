package com.portal.formula1;

import com.portal.formula1.controller.EquipoController;
import com.portal.formula1.model.Equipo;
import com.portal.formula1.service.EquipoService;
import com.portal.formula1.service.ImagenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

import static org.mockito.Mockito.when;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EquipoControllerTests {
    @Mock
    private EquipoService equipoService;

    @InjectMocks
    private EquipoController equipoController;

    @Mock
    private ImagenService imagenService;

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
        equipo.setNombre("Nuevo Equipo");
        equipo.setTwitter("twitterHandle");

        when(equipoService.crearEquipo(any(Equipo.class))).thenReturn(equipo);
        when(imagenService.isFormatoValido(any(MultipartFile.class))).thenReturn(true);
        when(imagenService.isTamanoValido(any(MultipartFile.class))).thenReturn(true);

        MockMultipartFile mockFile = new MockMultipartFile("logoArchivo", "logo.png", "image/png", "test content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/equipos/crear")
                        .file(mockFile)
                        .param("nombre", "Nuevo Equipo")
                        .param("twitter", "twitterHandle")
                        .flashAttr("equipo", equipo))
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
                .andExpect(view().name("equipos/verEquipo"))
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
