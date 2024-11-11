/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.portal.formula1;

import com.portal.formula1.controller.EncuestaController;
import com.portal.formula1.model.Encuesta;
import com.portal.formula1.repository.VotoDAO;
import com.portal.formula1.service.EncuestaService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 *
 * @author Misterlink
 */
public class EncuestaControllerTests {

    private MockMvc mockMvc;

    @Mock
    private EncuestaService encuestaService;

    @Mock
    private VotoDAO votoDAO;

    @InjectMocks
    private EncuestaController encuestaController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(encuestaController).build();
    }

    /**
     * Verifica que el formulario para crear encuestas se muestra correctamente
     * y que el modelo contiene los datos necesarios.
     */
    @Test
    public void testMostrarFormularioCreacion() throws Exception {
        List<Object[]> pilotos = new ArrayList<>();
        pilotos.add(new Object[]{"Lewis", "Hamilton", "HAM", 44, "/img/hamilton.jpg", "Reino Unido", "@LewisHamilton"});
        when(encuestaService.getTodosLosPilotos()).thenReturn(pilotos);

        mockMvc.perform(get("/encuestas/crearEncuestas"))
                .andExpect(status().isOk())
                .andExpect(view().name("crearEncuesta"))
                .andExpect(model().attributeExists("pilotos"));
    }


    /**
     * Asegura que el controlador puede procesar correctamente una solicitud
     * POST para crear una encuesta y redirige a la URL correcta.
     */
    @Test
    public void testCrearEncuesta() throws Exception {
        Encuesta encuesta = new Encuesta();
        encuesta.setTitulo("Título de prueba");
        encuesta.setDescripcion("Descripción de prueba");
        encuesta.setFechaLimite(LocalDateTime.parse("2024-12-31T23:59:59"));
        when(encuestaService.crearEncuesta(any(Encuesta.class), any(Set.class))).thenReturn(encuesta);

        mockMvc.perform(post("/encuestas")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("titulo", "Título de prueba")
                        .param("descripcion", "Descripción de prueba")
                        .param("fechaLimite", "2024-12-31T23:59:59")
                        .param("pilotosSeleccionados", "HAM"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/encuestas/" + encuesta.getPermalink()));
    }


    /**
     * Comprueba que se puede mostrar una encuesta específica y que el modelo
     * contiene los datos adecuados.
     */
    @Test
    public void testMostrarEncuesta() throws Exception {
        Encuesta encuesta = new Encuesta("Título de prueba", "Descripción de prueba", LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        List<Object[]> pilotos = new ArrayList<>();
        pilotos.add(new Object[]{"Lewis", "Hamilton", "HAM", 44, "/img/hamilton.jpg", "Reino Unido", "@LewisHamilton"});
        when(encuestaService.obtenerEncuestaPorPermalink("test-permalink")).thenReturn(encuesta);
        when(encuestaService.getTodosLosPilotos()).thenReturn(pilotos);

        mockMvc.perform(get("/encuestas/test-permalink"))
                .andExpect(status().isOk())
                .andExpect(view().name("votarEncuesta"))
                .andExpect(model().attributeExists("encuesta"))
                .andExpect(model().attributeExists("pilotos"));
    }
}
