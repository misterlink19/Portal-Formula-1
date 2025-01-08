package com.portal.formula1;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.portal.formula1.controller.EquipoController;
import com.portal.formula1.model.Equipo;
import com.portal.formula1.model.Piloto;
import com.portal.formula1.model.Rol;
import com.portal.formula1.model.UsuarioRegistrado;
import com.portal.formula1.service.EquipoService;
import com.portal.formula1.service.PilotoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.*;

class EliminarEquipoTest {

    @InjectMocks
    private EquipoController equipoController;

    @Mock
    private EquipoService equipoService;

    @Mock
    private PilotoService pilotoService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(request.getSession()).thenReturn(session);
    }

    @Test
    void eliminarEquipo_UsuarioConPermisos_Exito() {
        Long equipoId = 1L;
        UsuarioRegistrado user = new UsuarioRegistrado();
        user.setUsuario("admin");
        user.setRol(Rol.ADMIN);
        Equipo equipo = new Equipo();
        equipo.setResponsables(Collections.emptyList());

        when(session.getAttribute("usuario")).thenReturn(user);
        when(equipoService.obtenerEquipoPorId(equipoId)).thenReturn(Optional.of(equipo));

        ModelAndView mv = equipoController.eliminarEquipo(equipoId, "", request);

        assertEquals("redirect:/equipos/listar", mv.getViewName());
        verify(equipoService).eliminarEquipo(equipoId);
    }

    @Test
    void eliminarEquipo_UsuarioSinPermisos_AccesoDenegado() {
        Long equipoId = 1L;
        UsuarioRegistrado user = new UsuarioRegistrado( );
        user.setUsuario("user");
        user.setRol(Rol.USUARIO_BASICO);
        Equipo equipo = new Equipo();
        UsuarioRegistrado responsable = new UsuarioRegistrado();
        responsable.setUsuario("otroUser");
        responsable.setRol(Rol.USUARIO_BASICO);
        equipo.setResponsables(List.of(responsable));

        when(session.getAttribute("usuario")).thenReturn(user);
        when(equipoService.obtenerEquipoPorId(equipoId)).thenReturn(Optional.of(equipo));

        ModelAndView mv = equipoController.eliminarEquipo(equipoId, "", request);

        assertEquals("error", mv.getViewName());
        assertEquals("No tienes permisos para eliminar este equipo.", mv.getModel().get("mensajeError"));
    }

    @Test
    void eliminarEquipo_EquipoNoEncontrado() {
        Long equipoId = 1L;
        UsuarioRegistrado user = new UsuarioRegistrado();
        user.setUsuario("admin");
        user.setRol(Rol.ADMIN);
        when(session.getAttribute("usuario")).thenReturn(user);
        when(equipoService.obtenerEquipoPorId(equipoId)).thenReturn(Optional.empty());

        ModelAndView mv = equipoController.eliminarEquipo(equipoId, "", request);

        assertEquals("error", mv.getViewName());
        assertEquals("Equipo no encontrado.", mv.getModel().get("mensajeError"));
    }

    @Test
    void eliminarEquipo_UsuarioNoAutenticado() {
        Long equipoId = 1L;

        when(session.getAttribute("usuario")).thenReturn(null);

        ModelAndView mv = equipoController.eliminarEquipo(equipoId, "", request);

        assertEquals("error", mv.getViewName());
        assertEquals("No tienes permisos para eliminar este equipo.", mv.getModel().get("mensajeError"));
    }

    @Test
    void eliminarEquipo_ErrorAlEliminarPiloto() {
        Long equipoId = 1L;
        UsuarioRegistrado user = new UsuarioRegistrado();
        user.setUsuario("admin");
        user.setRol(Rol.ADMIN);
        Equipo equipo = new Equipo();
        Piloto piloto = new Piloto();
        piloto.setDorsal(10);
        equipo.setPilotos(List.of(piloto));

        when(session.getAttribute("usuario")).thenReturn(user);
        when(equipoService.obtenerEquipoPorId(equipoId)).thenReturn(Optional.of(equipo));
        doThrow(new RuntimeException("Error al eliminar piloto")).when(pilotoService).eliminarPiloto(piloto.getDorsal());

        ModelAndView mv = equipoController.eliminarEquipo(equipoId, "", request);

        assertEquals("error", mv.getViewName());
        assertEquals("Error al eliminar un piloto del equipo.", mv.getModel().get("mensajeError"));
    }
}
