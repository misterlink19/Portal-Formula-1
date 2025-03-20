package com.portal.formula1;

import com.portal.formula1.controller.InicioController;
import com.portal.formula1.model.CalendarioEvento;
import com.portal.formula1.model.Noticia;
import com.portal.formula1.model.Rol;
import com.portal.formula1.model.UsuarioRegistrado;
import com.portal.formula1.service.AutentificacionService;
import com.portal.formula1.service.CalendarioEventoService;
import com.portal.formula1.service.NoticiaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class InicioControllerTests {

    @Mock
    private AutentificacionService autentificacionService;

    @Mock
    private CalendarioEventoService calendarioEventoService;

    @Mock
    private NoticiaService noticiaService;

    @InjectMocks
    private InicioController inicioController;

    private MockMvc mockMvc;
    private MockHttpSession session;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(inicioController).build();

        // Configurar una sesión con un usuario admin
        UsuarioRegistrado adminUser = new UsuarioRegistrado();
        adminUser.setUsuario("admin");
        adminUser.setRol(Rol.ADMIN);
        session = new MockHttpSession();
        session.setAttribute("usuario", adminUser);
    }

    /**
     * Verifica que se muestre correctamente la página de inicio con noticias y eventos.
     */
    @Test
    public void testHome() throws Exception {
        // Mock de noticias
        List<Noticia> noticias = new ArrayList<>();
        Noticia noticia = new Noticia();
        noticia.setId(1);
        noticia.setTitulo("Título Noticia");
        noticia.setTexto("Contenido Noticia");
        noticia.setImagen("imagen.png");
        noticias.add(noticia);

        // Mock de eventos
        List<CalendarioEvento> eventos = new ArrayList<>();
        eventos.add(new CalendarioEvento(1L, "Evento 1", LocalDate.now().plusDays(1), null));
        eventos.add(new CalendarioEvento(2L, "Evento 2", LocalDate.now().plusDays(5), null));

        when(noticiaService.obtenerNoticias()).thenReturn(noticias);
        when(calendarioEventoService.listarEventos()).thenReturn(eventos);

        mockMvc.perform(get("/").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("noticias"))
                .andExpect(model().attributeExists("eventosCalendario"))
                .andExpect(model().attributeExists("proximosEventos"));
    }

    /**
     * Verifica que el formulario de inicio de sesión se cargue correctamente.
     */
    @Test
    public void testLogin() throws Exception {
        mockMvc.perform(get("/login").session(new MockHttpSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("inicioSesion"));
    }

    /**
     * Verifica el inicio de sesión exitoso de un usuario administrador.
     */
    @Test
    public void testProcesarFormularioLoginAdmin() throws Exception {
        UsuarioRegistrado adminUser = new UsuarioRegistrado();
        adminUser.setUsuario("admin");
        adminUser.setContrasena(new BCryptPasswordEncoder().encode("adminpass"));
        adminUser.setRol(Rol.ADMIN);

        when(autentificacionService.checkUser("admin")).thenReturn(adminUser);

        mockMvc.perform(post("/")
                        .param("usuario", "admin")
                        .param("contrasena", "adminpass")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("usuario"));
    }

    /**
     * Verifica el inicio de sesión fallido por credenciales incorrectas.
     */
    @Test
    public void testProcesarFormularioLoginFallido() throws Exception {
        when(autentificacionService.checkUser("usuario")).thenReturn(null);

        mockMvc.perform(post("/")
                        .param("usuario", "usuario")
                        .param("contrasena", "incorrecta")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("inicioSesion"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Correo electrónico o contraseña incorrectos"));
    }

    /**
     * Verifica que se redirija correctamente al cerrar sesión.
     */
    @Test
    public void testLogout() throws Exception {
        mockMvc.perform(post("/logout").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}
