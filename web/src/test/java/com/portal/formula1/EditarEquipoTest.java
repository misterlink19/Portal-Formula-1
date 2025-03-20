package com.portal.formula1;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.portal.formula1.controller.EquipoController;
import com.portal.formula1.model.Equipo;
import com.portal.formula1.model.UsuarioRegistrado;
import com.portal.formula1.model.Rol;
import com.portal.formula1.service.EquipoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;

class EditarEquipoTest {

    @Mock
    private EquipoService equipoService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @InjectMocks
    private EquipoController equipoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void editarEquipo_usuarioAutenticadoYResponsable_devuelveVistaEditar() {
        // Arrange
        Long equipoId = 1L;
        UsuarioRegistrado usuario = new UsuarioRegistrado("user1", Rol.USUARIO_BASICO);
        Equipo equipo = new Equipo();
        equipo.setId(equipoId);
        equipo.getResponsables().add(usuario);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(usuario);
        when(equipoService.obtenerEquipoPorId(equipoId)).thenReturn(Optional.of(equipo));

        // Act
        ModelAndView mv = equipoController.editarEquipo(equipoId, request);

        // Assert
        assertEquals("equipos/editarEquipo", mv.getViewName());
        assertEquals(equipo, mv.getModel().get("equipo"));
    }

    @Test
    void editarEquipo_usuarioNoAutenticado_lanzaAccessDeniedException() {
        // Arrange
        Long equipoId = 1L;
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(null);

        // Act
        ModelAndView mv = equipoController.editarEquipo(equipoId, request);

        // Assert
        assertEquals("error", mv.getViewName());
        assertEquals("Usuario no autenticado.", mv.getModel().get("mensajeError"));
    }

    @Test
    void editarEquipo_usuarioNoEsResponsableYNoEsAdmin_lanzaAccessDeniedException() {
        // Arrange
        Long equipoId = 1L;
        UsuarioRegistrado usuario = new UsuarioRegistrado("user1", Rol.USUARIO_BASICO);
        Equipo equipo = new Equipo();
        equipo.setId(equipoId);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(usuario);
        when(equipoService.obtenerEquipoPorId(equipoId)).thenReturn(Optional.of(equipo));

        // Act
        ModelAndView mv = equipoController.editarEquipo(equipoId, request);

        // Assert
        assertEquals("error", mv.getViewName());
        assertEquals("No tienes permisos para editar este equipo.", mv.getModel().get("mensajeError"));
    }

    @Test
    void editarEquipo_equipoNoEncontrado_devuelveVistaError() {
        // Arrange
        Long equipoId = 1L;
        UsuarioRegistrado usuario = new UsuarioRegistrado("user1", Rol.USUARIO_BASICO);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(usuario);
        when(equipoService.obtenerEquipoPorId(equipoId)).thenReturn(Optional.empty());

        // Act
        ModelAndView mv = equipoController.editarEquipo(equipoId, request);

        // Assert
        assertEquals("error", mv.getViewName());
        assertEquals("Equipo no encontrado.", mv.getModel().get("mensajeError"));
    }

    @Test
    void editarEquipo_errorGeneral_devuelveVistaError() {
        // Arrange
        Long equipoId = 1L;
        UsuarioRegistrado usuario = new UsuarioRegistrado("user1", Rol.USUARIO_BASICO);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(usuario);
        when(equipoService.obtenerEquipoPorId(equipoId)).thenThrow(new RuntimeException("Error inesperado"));

        // Act
        ModelAndView mv = equipoController.editarEquipo(equipoId, request);

        // Assert
        assertEquals("error", mv.getViewName());
        assertEquals("Error al cargar la vista de editar el equipo.", mv.getModel().get("mensajeError"));
    }
}

