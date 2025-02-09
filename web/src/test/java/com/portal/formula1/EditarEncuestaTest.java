package com.portal.formula1;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.portal.formula1.controller.EncuestaController;
import com.portal.formula1.model.Encuesta;
import com.portal.formula1.model.Piloto;
import com.portal.formula1.model.Rol;
import com.portal.formula1.model.UsuarioRegistrado;
import com.portal.formula1.repository.PilotoDAO;
import com.portal.formula1.service.EncuestaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import jakarta.servlet.http.HttpSession;

class EditarEncuestaTest {

    @InjectMocks
    private EncuestaController encuestaController;

    @Mock
    private EncuestaService encuestaService;

    @Mock
    private PilotoDAO pilotoDAO;

    @Mock
    private HttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMostrarFormularioEdicion_AdminAccess_Success() {
        UsuarioRegistrado adminUser = new UsuarioRegistrado();
        adminUser.setRol(Rol.ADMIN);
        when(session.getAttribute("usuario")).thenReturn(adminUser);

        String permalink = "test-permalink";
        Encuesta encuesta = new Encuesta();
        List<Piloto> pilotos = Arrays.asList(new Piloto(), new Piloto());

        when(encuestaService.obtenerEncuestaPorPermalink(permalink)).thenReturn(encuesta);
        when(pilotoDAO.findAll()).thenReturn(pilotos);

        ModelAndView result = encuestaController.mostrarFormularioEdicion(permalink, session);

        assertEquals("encuestas/editarEncuesta", result.getViewName());
        assertEquals(encuesta, result.getModel().get("encuesta"));
        assertEquals(pilotos, result.getModel().get("pilotos"));
    }

    @Test
    void testMostrarFormularioEdicion_EncuestaNoEncontrada() {
        UsuarioRegistrado adminUser = new UsuarioRegistrado();
        adminUser.setRol(Rol.ADMIN);
        when(session.getAttribute("usuario")).thenReturn(adminUser);

        String permalink = "invalid-permalink";
        when(encuestaService.obtenerEncuestaPorPermalink(permalink)).thenThrow(NoSuchElementException.class);

        ModelAndView result = encuestaController.mostrarFormularioEdicion(permalink, session);

        assertEquals("error", result.getViewName());
        assertEquals("Encuesta no encontrada.", result.getModel().get("mensajeError"));
    }

    @Test
    void testMostrarFormularioEdicion_AccessDenied() {
        when(session.getAttribute("usuario")).thenReturn(null);

        ModelAndView result = encuestaController.mostrarFormularioEdicion("any-permalink", session);

        assertEquals("error", result.getViewName());
        assertEquals("Acceso denegado.", result.getModel().get("mensajeError"));
    }

    @Test
    void testEditarEncuesta_AdminAccess_Success() {
        UsuarioRegistrado adminUser = new UsuarioRegistrado();
        adminUser.setRol(Rol.ADMIN);
        when(session.getAttribute("usuario")).thenReturn(adminUser);

        Encuesta encuesta = new Encuesta();
        encuesta.setPermalink("test-permalink");
        Set<Integer> pilotosSeleccionados = new HashSet<>(Arrays.asList(1, 2, 3));
        Encuesta nuevaEncuesta = new Encuesta();
        nuevaEncuesta.setPermalink("updated-permalink");

        when(encuestaService.editarEncuesta(encuesta, pilotosSeleccionados)).thenReturn(nuevaEncuesta);

        ModelAndView result = encuestaController.editarEncuesta(encuesta, pilotosSeleccionados, session);

        assertEquals("redirect:/encuestas/updated-permalink", result.getViewName());
    }

    @Test
    void testEditarEncuesta_ErrorHandling() {
        UsuarioRegistrado adminUser = new UsuarioRegistrado();
        adminUser.setRol(Rol.ADMIN);
        when(session.getAttribute("usuario")).thenReturn(adminUser);

        Encuesta encuesta = new Encuesta();
        Set<Integer> pilotosSeleccionados = new HashSet<>(Arrays.asList(1, 2));

        when(encuestaService.editarEncuesta(any(), any())).thenThrow(RuntimeException.class);

        ModelAndView result = encuestaController.editarEncuesta(encuesta, pilotosSeleccionados, session);

        assertEquals("error", result.getViewName());
        assertEquals("Error al crear la encuesta.", result.getModel().get("mensajeError"));
    }

    @Test
    void testEditarEncuesta_AccessDenied() {
        when(session.getAttribute("usuario")).thenReturn(null);

        ModelAndView result = encuestaController.editarEncuesta(new Encuesta(), new HashSet<>(), session);

        assertEquals("error", result.getViewName());
        assertEquals("Acceso denegado.", result.getModel().get("mensajeError"));
    }
}
