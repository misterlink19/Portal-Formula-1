package com.portal.formula1;

import com.portal.formula1.controller.NoticiaController;
import com.portal.formula1.model.Noticia;
import com.portal.formula1.service.ImagenService;
import com.portal.formula1.service.NoticiaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class NoticiaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private NoticiaService noticiaService;

    @Mock
    private ImagenService imagenService;

    @InjectMocks
    private NoticiaController noticiaController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(noticiaController).build();
    }

    @Test
    public void testListarNoticias() throws Exception {
        List<Noticia> noticias = Arrays.asList(new Noticia("Título de prueba", "imagen.jpg", "Texto de prueba"));
        when(noticiaService.obtenerNoticias()).thenReturn(noticias);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("listadoNoticias"))
                .andExpect(model().attribute("noticias", noticias));

        verify(noticiaService, times(1)).obtenerNoticias();
        verifyNoMoreInteractions(noticiaService);
    }

    @Test
    public void testMostrarFormularioCreacion() throws Exception {
        mockMvc.perform(get("/crear"))
                .andExpect(status().isOk())
                .andExpect(view().name("crearNoticia"))
                .andExpect(model().attributeExists("noticia"));
    }

    @Test
    public void testMostrarNoticia() throws Exception {
        Noticia noticia = new Noticia("Título de prueba", "imagen.jpg", "Texto de prueba");
        when(noticiaService.obtenerNoticiaPorId(1)).thenReturn(noticia);

        mockMvc.perform(get("/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("/detalle"))
                .andExpect(model().attribute("noticia", noticia));

        verify(noticiaService, times(1)).obtenerNoticiaPorId(1);
        verifyNoMoreInteractions(noticiaService);
    }

    @Test
    public void testBuscarNoticias() throws Exception {
        List<Noticia> resultados = Arrays.asList(new Noticia("Título de prueba", "imagen.jpg", "Texto de prueba"));
        when(noticiaService.buscarNoticias("prueba")).thenReturn(resultados);

        mockMvc.perform(get("/buscar").param("query", "prueba"))
                .andExpect(status().isOk())
                .andExpect(view().name("listadoNoticias"))
                .andExpect(model().attribute("noticias", resultados))
                .andExpect(model().attribute("query", "prueba"));

        verify(noticiaService, times(1)).buscarNoticias("prueba");
        verifyNoMoreInteractions(noticiaService);
    }
}