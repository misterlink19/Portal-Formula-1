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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

        mockMvc.perform(get("/noticias/listar")) // Ruta correcta para listar noticias
                .andExpect(status().isOk())
                .andExpect(view().name("noticias/listadoNoticias"))
                .andExpect(model().attribute("noticias", noticias));

        verify(noticiaService, times(1)).obtenerNoticias();
        verifyNoMoreInteractions(noticiaService);
    }


    @Test
    public void testMostrarFormularioCreacion() throws Exception {
        mockMvc.perform(get("/noticias/crear")) // Ruta correcta para el formulario de creación
                .andExpect(status().isOk())
                .andExpect(view().name("noticias/crearNoticia"))
                .andExpect(model().attributeExists("noticia"));
    }


    @Test
    public void testMostrarNoticia() throws Exception {
        Noticia noticia = new Noticia("Título de prueba", "imagen.jpg", "Texto de prueba");
        when(noticiaService.obtenerNoticiaPorId(1)).thenReturn(noticia);

        mockMvc.perform(get("/noticias/1")) // Ruta correcta para mostrar noticia
                .andExpect(status().isOk())
                .andExpect(view().name("noticias/detalle"))
                .andExpect(model().attribute("noticia", noticia));

        verify(noticiaService, times(1)).obtenerNoticiaPorId(1);
        verifyNoMoreInteractions(noticiaService);
    }


    @Test
    public void testBuscarNoticias() throws Exception {
        List<Noticia> resultados = Arrays.asList(new Noticia("Título de prueba", "imagen.jpg", "Texto de prueba"));
        when(noticiaService.buscarNoticias("prueba")).thenReturn(resultados);

        mockMvc.perform(get("/noticias/buscar").param("query", "prueba")) // Ruta correcta para buscar noticias
                .andExpect(status().isOk())
                .andExpect(view().name("noticias/listadoNoticias"))
                .andExpect(model().attribute("noticias", resultados))
                .andExpect(model().attribute("query", "prueba"));

        verify(noticiaService, times(1)).buscarNoticias("prueba");
        verifyNoMoreInteractions(noticiaService);
    }

    @Test
    public void testEliminarNoticia() throws Exception {
        int noticiaId = 1;

        mockMvc.perform(post("/noticias/{id}/eliminar", noticiaId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/noticias/listar"))
                .andExpect(flash().attribute("mensaje", "La noticia ha sido eliminada correctamente."));

        verify(noticiaService, times(1)).eliminarNoticia(noticiaId);
        verifyNoMoreInteractions(noticiaService);
    }
}