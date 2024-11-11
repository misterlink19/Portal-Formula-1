package com.portal.formula1;

import com.portal.formula1.controller.InicioController;
import com.portal.formula1.model.UsuarioRegistrado;
import com.portal.formula1.service.AutentificacionService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(InicioController.class)
public class AutentificacionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AutentificacionService autentificacionService;
    @Mock
    private HttpSession session;
    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    private UsuarioRegistrado usuario;

    @BeforeEach
    public void setUp() {
        usuario = new UsuarioRegistrado();
        usuario.setUsuario("adminUser");
        usuario.setContrasena("$2a$10$Mk6Be3FX0HHjODkn69FWIeCh15NoI/CoVHaV1mPeJI2JkNfseihrW");
        usuario.setRol("ADMIN");
        usuario.setNombre("administrador Juan");
        usuario.setEmail("administrador@gmail.com");

        when(autentificacionService.checkUser("adminUser")).thenReturn(usuario);

        when(passwordEncoder.matches("adminPassword", "$2a$10$Mk6Be3FX0HHjODkn69FWIeCh15NoI/CoVHaV1mPeJI2JkNfseihrW")).thenReturn(true);
    }
    @Test
    public void testHome_AdminUserLoggedIn() throws Exception {
        when(session.getAttribute("usuario")).thenReturn(usuario);

        mockMvc.perform(get("/").sessionAttr("usuario", usuario))
                .andExpect(status().isOk())
                .andExpect(view().name("admin"))
                .andExpect(model().attributeExists("usuario"));
    }

    @Test
    public void testHome_UsuarioNoLogueado() throws Exception {
        when(session.getAttribute("usuario")).thenReturn(null);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("inicioSesion"));
    }
    @Test
    public void testProcesarFormulario_UsuarioYContrasenaCorrectos() throws Exception {
        mockMvc.perform(post("/")
                        .param("usuario", "adminUser")
                        .param("contrasena", "adminPassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin"))
                .andExpect(model().attributeExists("usuario"));
    }

    @Test
    public void testProcesarFormulario_UsuarioIncorrectoOContrasenaIncorrecta() throws Exception {
        // Simulamos que la contraseña es incorrecta
        when(passwordEncoder.matches("wrongPassword", "encodedAdminPassword")).thenReturn(false);

        mockMvc.perform(post("/")
                        .param("usuario", "adminUser")
                        .param("contrasena", "wrongPassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("inicioSesion"))
                .andExpect(model().attribute("error", "Correo electrónico o contraseña incorrectos"));
    }
}
