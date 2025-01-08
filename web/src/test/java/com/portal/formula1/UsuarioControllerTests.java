package com.portal.formula1;

import com.portal.formula1.model.Rol;
import com.portal.formula1.model.UsuarioRegistrado;
import com.portal.formula1.model.Equipo;
import com.portal.formula1.service.AutentificacionService;
import com.portal.formula1.service.UsuarioService;
import com.portal.formula1.controller.UsuarioController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioControllerTests {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private AutentificacionService autentificacionService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Model model;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private UsuarioController usuarioController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController).build();
    }

    @Test
    void mostrarFormularioRegistro_DebeRetornarVistaCorrecta() {
        ModelAndView result = usuarioController.mostrarFormularioRegistro(model);

        assertEquals("registro", result.getViewName());
        assertNotNull(result.getModel().get("usuarioRegistrado"));
    }

    @Test
    void listarUsuarios_SinFiltros_DebeRetornarTodosLosUsuarios() {
        // Given
        List<UsuarioRegistrado> usuarios = Arrays.asList(
                crearUsuarioRegistrado("user1", "Nombre1", Rol.USUARIO_BASICO),
                crearUsuarioRegistrado("user2", "Nombre2", Rol.USUARIO_BASICO)
        );
        when(usuarioService.obtenerTodosLosUsuarios()).thenReturn(usuarios);

        // When
        String viewName = usuarioController.listarUsuarios(null, null, null, null, model);

        // Then
        assertEquals("admin/listaUsuarios", viewName);
        verify(model).addAttribute("usuarios", usuarios);
    }

    @Test
    void listarUsuarios_ConFiltroRol_DebeRetornarUsuariosFiltrados() {
        // Given
        List<UsuarioRegistrado> usuarios = Arrays.asList(
                crearUsuarioRegistrado("admin1", "Admin", Rol.ADMIN)
        );
        when(usuarioService.filtrarPorRol("ADMINISTRADOR")).thenReturn(usuarios);

        // When
        String viewName = usuarioController.listarUsuarios("ADMINISTRADOR", null, null, null, model);

        // Then
        assertEquals("admin/listaUsuarios", viewName);
        verify(model).addAttribute("usuarios", usuarios);
    }

    @Test
    void verDetallesUsuario_UsuarioExistente_DebeRetornarDetalles() {
        // Given
        UsuarioRegistrado usuario = crearUsuarioRegistrado("user1", "Nombre1", Rol.USUARIO_BASICO);
        when(usuarioService.obtenerUsuarioPorUsuario("user1")).thenReturn(usuario);

        // When
        String viewName = usuarioController.verDetallesUsuario("user1", model);

        // Then
        assertEquals("admin/detalleUsuario", viewName);
        verify(model).addAttribute("usuario", usuario);
    }

    @Test
    void validarUsuarios_DebeRedirigirConExito() {
        // Given
        List<String> usuariosIds = Arrays.asList("user1", "user2");

        // When
        String result = usuarioController.validarUsuarios(usuariosIds);

        // Then
        assertEquals("redirect:/admin/usuarios?validacion=success", result);
        verify(usuarioService).validarUsuarios(usuariosIds);
    }

    @Test
    void validarUsuariosRol_DebeRedirigirConExito() {
        // Given
        List<String> usuariosIds = Arrays.asList("user1", "user2");

        // When
        String result = usuarioController.validarUsuariosRol(usuariosIds);

        // Then
        assertEquals("redirect:/admin/usuarios?validacion=success", result);
        verify(usuarioService).validarUsuariosRol(usuariosIds);
    }

    @Test
    void mostrarFormularioNuevoResponsable_UsuarioAutorizado_DebeRetornarFormulario() {
        // Given
        UsuarioRegistrado jefeEquipo = crearUsuarioJefeEquipo();
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(jefeEquipo);
        when(autentificacionService.checkUser(jefeEquipo.getUsuario())).thenReturn(jefeEquipo);

        // When
        ModelAndView result = usuarioController.mostrarFormularioNuevoResponsable(request);

        // Then
        assertEquals("equipos/crearResponsable", result.getViewName());
        assertNotNull(result.getModel().get("nuevoResponsable"));
    }

    @Test
    void crearNuevoResponsable_DatosValidos_DebeCrearResponsable() {
        // Given
        UsuarioRegistrado jefeEquipo = crearUsuarioJefeEquipo();
        UsuarioRegistrado nuevoResponsable = crearUsuarioRegistrado("resp1", "Responsable", Rol.JEFE_DE_EQUIPO);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(jefeEquipo);
        when(autentificacionService.checkUser(jefeEquipo.getUsuario())).thenReturn(jefeEquipo);
        when(usuarioService.crearResponsableEquipo(any(), any())).thenReturn(nuevoResponsable);
        when(bindingResult.hasErrors()).thenReturn(false);

        // When
        ModelAndView result = usuarioController.crearNuevoResponsable(
                nuevoResponsable, bindingResult, request, redirectAttributes);

        // Then
        assertTrue(result.getViewName().startsWith("redirect:/equipos/"));
        verify(usuarioService).crearResponsableEquipo(any(), any());
    }

    // Tests para cambio de contraseña
    @Test
    void mostrarFormularioCambioContrasena_UsuarioAutenticado_DebeRetornarVista() {
        // Given
        UsuarioRegistrado usuario = crearUsuarioRegistrado("user1", "Usuario Test", Rol.USUARIO_BASICO);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(usuario);

        // When
        String viewName = usuarioController.mostrarFormularioCambioContrasena(model, request);

        // Then
        assertEquals("cambiarContrasena", viewName);
        verify(model).addAttribute("usuario", usuario);
    }

    @Test
    void mostrarFormularioCambioContrasena_UsuarioNoAutenticado_DebeRedirigirLogin() {
        // Given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(null);

        // When
        String viewName = usuarioController.mostrarFormularioCambioContrasena(model, request);

        // Then
        assertEquals("redirect:/login", viewName);
    }

    @Test
    void cambiarContrasena_CasoExitoso_DebeRedirigirLogin() {
        // Given
        UsuarioRegistrado usuario = crearUsuarioRegistrado("user1", "Usuario Test", Rol.USUARIO_BASICO);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(usuario);

        String contrasenaActual = "oldPass";
        String nuevaContrasena = "newPass";
        String confirmarContrasena = "newPass";

        doNothing().when(usuarioService).cambiarContrasena(
                usuario.getUsuario(),
                contrasenaActual,
                nuevaContrasena,
                confirmarContrasena
        );

        // When
        String viewName = usuarioController.cambiarContrasena(
                contrasenaActual,
                nuevaContrasena,
                confirmarContrasena,
                request,
                redirectAttributes
        );

        // Then
        assertEquals("redirect:/login", viewName);
        verify(redirectAttributes).addFlashAttribute("mensaje", "Contraseña actualizada correctamente.");
        verify(usuarioService).cambiarContrasena(
                usuario.getUsuario(),
                contrasenaActual,
                nuevaContrasena,
                confirmarContrasena
        );
    }

    @Test
    void cambiarContrasena_UsuarioNoAutenticado_DebeRedirigirLogin() {
        // Given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(null);

        // When
        String viewName = usuarioController.cambiarContrasena(
                "oldPass",
                "newPass",
                "newPass",
                request,
                redirectAttributes
        );

        // Then
        assertEquals("redirect:/login", viewName);
        verify(usuarioService, never()).cambiarContrasena(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void cambiarContrasena_ErrorValidacion_DebeRedirigirConError() {
        // Given
        UsuarioRegistrado usuario = crearUsuarioRegistrado("user1", "Usuario Test", Rol.USUARIO_BASICO);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(usuario);

        String mensajeError = "La contraseña actual es incorrecta";
        doThrow(new IllegalArgumentException(mensajeError))
                .when(usuarioService)
                .cambiarContrasena(anyString(), anyString(), anyString(), anyString());

        // When
        String viewName = usuarioController.cambiarContrasena(
                "oldPass",
                "newPass",
                "newPass",
                request,
                redirectAttributes
        );

        // Then
        assertEquals("redirect:/cambiarContrasena?error", viewName);
        verify(redirectAttributes).addFlashAttribute("error", mensajeError);
    }

    @Test
    void cambiarContrasena_ErrorInesperado_DebeRedirigirConError() {
        // Given
        UsuarioRegistrado usuario = crearUsuarioRegistrado("user1", "Usuario Test", Rol.USUARIO_BASICO);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(usuario);

        doThrow(new RuntimeException("Error inesperado"))
                .when(usuarioService)
                .cambiarContrasena(anyString(), anyString(), anyString(), anyString());

        // When
        String viewName = usuarioController.cambiarContrasena(
                "oldPass",
                "newPass",
                "newPass",
                request,
                redirectAttributes
        );

        // Then
        assertEquals("redirect:/cambiarContrasena?error", viewName);
        verify(redirectAttributes).addFlashAttribute("error", "Error inesperado al cambiar la contraseña.");
    }





    // Métodos auxiliares
    private UsuarioRegistrado crearUsuarioRegistrado(String usuario, String nombre, Rol rol) {
        UsuarioRegistrado usuarioRegistrado = new UsuarioRegistrado();
        usuarioRegistrado.setUsuario(usuario);
        usuarioRegistrado.setNombre(nombre);
        usuarioRegistrado.setRol(rol);
        return usuarioRegistrado;
    }

    private UsuarioRegistrado crearUsuarioJefeEquipo() {
        UsuarioRegistrado jefeEquipo = crearUsuarioRegistrado("jefe1", "Jefe Equipo", Rol.JEFE_DE_EQUIPO);
        Equipo equipo = new Equipo();
        equipo.setId(1L);
        jefeEquipo.setEquipo(equipo);
        return jefeEquipo;
    }
}