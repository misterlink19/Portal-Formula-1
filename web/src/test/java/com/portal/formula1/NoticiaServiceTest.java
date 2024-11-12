package com.portal.formula1;

import com.portal.formula1.model.Noticia;
import com.portal.formula1.repository.NoticiaDAO;
import com.portal.formula1.service.ImagenService;
import com.portal.formula1.service.NoticiaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NoticiaServiceTest {

    @Mock
    private NoticiaDAO noticiaDAO;

    @Mock
    private ImagenService imagenService;

    @InjectMocks
    private NoticiaService noticiaService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCrearNoticia() throws IOException {
        String titulo = "Título de prueba";
        MultipartFile imagen = mock(MultipartFile.class);
        String texto = "Texto de prueba";
        Noticia noticia = new Noticia(titulo, "imagen.jpg", texto);

        when(imagen.isEmpty()).thenReturn(false);
        when(imagenService.guardarImagen(imagen)).thenReturn("imagen.jpg");
        when(noticiaDAO.save(any(Noticia.class))).thenReturn(noticia);
        when(noticiaDAO.existsByPermalink(anyString())).thenReturn(false);

        Noticia result = noticiaService.crearNoticia(titulo, imagen, texto);

        assertNotNull(result);
        assertEquals(titulo, result.getTitulo());
        assertEquals("imagen.jpg", result.getImagen());
        assertEquals(texto, result.getTexto());
        verify(imagenService, times(1)).guardarImagen(imagen);
        verify(noticiaDAO, times(1)).save(any(Noticia.class));
    }

    @Test
    public void testObtenerNoticias() {
        List<Noticia> noticias = Arrays.asList(new Noticia("Título de prueba", "imagen.jpg", "Texto de prueba"));
        when(noticiaDAO.findAllByOrderByFechaPublicacionDesc()).thenReturn(noticias);

        List<Noticia> result = noticiaService.obtenerNoticias();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(noticiaDAO, times(1)).findAllByOrderByFechaPublicacionDesc();
    }

    @Test
    public void testObtenerNoticiaPorId() {
        Noticia noticia = new Noticia("Título de prueba", "imagen.jpg", "Texto de prueba");
        when(noticiaDAO.findById(1)).thenReturn(Optional.of(noticia));

        Noticia result = noticiaService.obtenerNoticiaPorId(1);

        assertNotNull(result);
        assertEquals("Título de prueba", result.getTitulo());
        verify(noticiaDAO, times(1)).findById(1);
    }

    @Test
    public void testEliminarNoticia() {
        when(noticiaDAO.existsById(1)).thenReturn(true);

        noticiaService.eliminarNoticia(1);

        verify(noticiaDAO, times(1)).deleteById(1);
    }

    @Test
    public void testBuscarNoticias() {
        List<Noticia> noticias = Arrays.asList(new Noticia("Título de prueba", "imagen.jpg", "Texto de prueba"));
        when(noticiaDAO.findByTituloContainingIgnoreCaseOrTextoContainingIgnoreCase("prueba", "prueba")).thenReturn(noticias);

        List<Noticia> result = noticiaService.buscarNoticias("prueba");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(noticiaDAO, times(1)).findByTituloContainingIgnoreCaseOrTextoContainingIgnoreCase("prueba", "prueba");
    }
}
