package com.portal.formula1;

import com.portal.formula1.controller.CocheController;
import com.portal.formula1.model.Coches;
import com.portal.formula1.model.Equipo;
import com.portal.formula1.model.Rol;
import com.portal.formula1.model.UsuarioRegistrado;
import com.portal.formula1.service.AutentificacionService;
import com.portal.formula1.service.CocheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CocheControllerTest {

    @Mock
    private CocheService cocheService;

    @Mock
    private AutentificacionService autentificacionService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private CocheController cocheController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(cocheController).build();
    }

    @Test
    public void testMostrarFormularioDeEdicionCoche() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        UsuarioRegistrado user = new UsuarioRegistrado();
        user.setUsuario("testUser");
        user.setRol(Rol.JEFE_DE_EQUIPO);
        Equipo equipo = new Equipo();
        equipo.setId(1L);
        user.setEquipo(equipo);
        session.setAttribute("usuario", user);
        request.setSession(session);

        Coches coche = new Coches();
        coche.setCodigo("A524");
        when(autentificacionService.checkUser(anyString())).thenReturn(user);
        when(cocheService.obtnerCochePorCodigo(anyString())).thenReturn(Optional.of(coche));

        ModelAndView mv = cocheController.mostrarFormularioDeEdicionCoche("A524", request);

        assertEquals("coches/editarCoche", mv.getViewName());
        assertEquals(coche, mv.getModel().get("coche"));
    }

    @Test
    public void testEditarCoche() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        UsuarioRegistrado user = new UsuarioRegistrado();
        user.setUsuario("testUser");
        user.setRol(Rol.JEFE_DE_EQUIPO);
        Equipo equipo = new Equipo();
        equipo.setId(1L);
        user.setEquipo(equipo);
        session.setAttribute("usuario", user);
        request.setSession(session);

        Coches coche = new Coches();
        coche.setCodigo("A524");
        coche.setEquipo(equipo);

        when(autentificacionService.checkUser(anyString())).thenReturn(user);
        when(bindingResult.hasErrors()).thenReturn(false);

        ModelAndView mv = cocheController.editarCoche(coche, bindingResult, null, request);

        assertEquals("redirect:/coches", mv.getViewName());
        verify(cocheService, times(1)).actualizarCoche(coche);
    }

    @Test
    public void testEditarCocheConErrores() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        UsuarioRegistrado user = new UsuarioRegistrado();
        user.setUsuario("testUser");
        user.setRol(Rol.JEFE_DE_EQUIPO);
        Equipo equipo = new Equipo();
        equipo.setId(1L);
        user.setEquipo(equipo);
        session.setAttribute("usuario", user);
        request.setSession(session);

        Coches coche = new Coches();
        coche.setCodigo("A524");
        coche.setEquipo(equipo);

        when(autentificacionService.checkUser(anyString())).thenReturn(user);
        when(bindingResult.hasErrors()).thenReturn(true);

        ModelAndView mv = cocheController.editarCoche(coche, bindingResult, null, request);

        assertEquals("coches/editarCoche", mv.getViewName());
        verify(cocheService, never()).actualizarCoche(any(Coches.class));
    }
}
