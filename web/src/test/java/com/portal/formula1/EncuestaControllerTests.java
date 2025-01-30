/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.portal.formula1;

import com.portal.formula1.controller.EncuestaController;
import com.portal.formula1.model.Encuesta;
import com.portal.formula1.model.Piloto;
import com.portal.formula1.model.Rol;
import com.portal.formula1.model.UsuarioRegistrado;
import com.portal.formula1.repository.PilotoDAO;
import com.portal.formula1.repository.VotoDAO;
import com.portal.formula1.service.EncuestaService;
import java.time.LocalDateTime;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 *
 * @author Misterlink
 */
public class EncuestaControllerTests {

    private MockMvc mockMvc;

    @Mock
    private EncuestaService encuestaService;

    @Mock
    private VotoDAO votoDAO;

    @Mock
    private PilotoDAO  pilotoDAO;

    @InjectMocks
    private EncuestaController encuestaController;

    private MockHttpSession session;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(encuestaController).build();

        // Configurar usuario administrador en la sesión
        UsuarioRegistrado adminUser = new UsuarioRegistrado();
        adminUser.setUsuario("admin");
        adminUser.setRol(Rol.ADMIN);
        session = new MockHttpSession();
        session.setAttribute("usuario", adminUser);
    }

    /**
     * Verifica que el formulario para crear encuestas se muestra correctamente
     * y que el modelo contiene los datos necesarios.
     */
    @Test
    public void testMostrarFormularioCreacion() throws Exception {
        List<Piloto> pilotos = new ArrayList<>();
        Piloto piloto1 = new Piloto();
        piloto1.setDorsal(44);
        piloto1.setNombre("Lewis");
        piloto1.setApellidos("Hamilton");
        piloto1.setSiglas("HAM");
        piloto1.setRutaImagen("/img/hamilton.jpg");
        piloto1.setPais("Reino Unido");
        piloto1.setTwitter("@LewisHamilton");

        pilotos.add(piloto1);
        when(pilotoDAO.findAll()).thenReturn(pilotos);

        mockMvc.perform(get("/encuestas/crearEncuestas")
                        .session(session)) // Incluir la sesión con el usuario autenticado
                .andExpect(status().isOk())
                .andExpect(view().name("encuestas/crearEncuesta"))
                .andExpect(model().attributeExists("pilotos"));
    }




    /**
     * Asegura que el controlador puede procesar correctamente una solicitud
     * POST para crear una encuesta y redirige a la URL correcta.
     */
    @Test
    public void testCrearEncuesta() throws Exception {
        Encuesta encuesta = new Encuesta();
        encuesta.setTitulo("Título de prueba");
        encuesta.setDescripcion("Descripción de prueba");
        encuesta.setFechaLimite(LocalDateTime.parse("2024-12-31T23:59:59"));
        encuesta.setPermalink("titulo-de-prueba");

        Piloto piloto1 = new Piloto();
        piloto1.setDorsal(44);
        when(pilotoDAO.findById(44)).thenReturn(Optional.of(piloto1));
        when(encuestaService.crearEncuesta(any(Encuesta.class), any(Set.class))).thenReturn(encuesta);

        mockMvc.perform(post("/encuestas")
                        .session(session) // Incluir la sesión con el usuario autenticado
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("titulo", "Título de prueba")
                        .param("descripcion", "Descripción de prueba")
                        .param("fechaLimite", "2024-12-31T23:59:59")
                        .param("pilotosSeleccionados", "44"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/encuestas/" + encuesta.getPermalink()));
    }




    /**
     * Comprueba que se puede mostrar una encuesta específica y que el modelo
     * contiene los datos adecuados.
     */
    @Test
    public void testMostrarEncuesta() throws Exception {
        Encuesta encuesta = new Encuesta("Título de prueba", "Descripción de prueba", LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        encuesta.setPermalink("test-permalink");

        Piloto piloto1 = new Piloto();
        piloto1.setDorsal(44);
        piloto1.setNombre("Lewis");
        piloto1.setApellidos("Hamilton");
        piloto1.setSiglas("HAM");
        piloto1.setRutaImagen("/img/hamilton.jpg");
        piloto1.setPais("Reino Unido");
        piloto1.setTwitter("@LewisHamilton");

        encuesta.getPilotos().add(piloto1);

        when(encuestaService.obtenerEncuestaPorPermalink("test-permalink")).thenReturn(encuesta);

        mockMvc.perform(get("/encuestas/test-permalink")
                        .session(session)) // Incluir la sesión con el usuario autenticado
                .andExpect(status().isOk())
                .andExpect(view().name("encuestas/verEncuesta"))
                .andExpect(model().attributeExists("encuesta"))
                .andExpect(model().attributeExists("pilotos"))
                .andExpect(model().attribute("pilotos", new ArrayList<>(encuesta.getPilotos())));
    }



    /**
     * Comprueba que se puede mostrar la lista de encuestas cuando se es admin
     */
    @Test
    public void testMostrarListaEncuestas_UsuarioAdmin() throws Exception {
        UsuarioRegistrado usuarioAdmin = new UsuarioRegistrado();
        usuarioAdmin.setRol(Rol.ADMIN);
        List<Encuesta> encuestas = new ArrayList<>();
        Encuesta encuesta1 = new Encuesta("Encuesta 1", "Descripción 1", LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        Encuesta encuesta2 = new Encuesta("Encuesta 2", "Descripción 2", LocalDateTime.now(), LocalDateTime.now().plusDays(2));
        encuestas.add(encuesta1);
        encuestas.add(encuesta2);
        when(encuestaService.getAllEncuestas()).thenReturn(encuestas);
        mockMvc.perform(get("/encuestas/listar")
                        .sessionAttr("usuario", usuarioAdmin))
                .andExpect(status().isOk())
                .andExpect(view().name("encuestas/listadoEncuestas"))
                .andExpect(model().attributeExists("encuestas"))
                .andExpect(model().attribute("encuestas", encuestas));
    }

    /**
     * Verifica que un administrador puede eliminar una encuesta exitosamente.
     */
    @Test
    public void testEliminarEncuesta_AdminSuccess() throws Exception {
        mockMvc.perform(delete("/encuestas/eliminar/test-permalink")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/encuestas/listar"))
                .andExpect(flash().attributeExists("mensaje"))
                .andExpect(flash().attribute("mensaje", "La encuesta ha sido eliminada exitosamente."));
    }

    /**
     * Verifica que se muestra el mensaje de éxito tras la eliminación.
     */
    @Test
    public void testEliminarEncuesta_MensajeExito() throws Exception {
        mockMvc.perform(delete("/encuestas/eliminar/test-permalink")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/encuestas/listar"))
                .andExpect(flash().attributeExists("mensaje"))
                .andExpect(flash().attribute("mensaje", "La encuesta ha sido eliminada exitosamente."));
    }

    /**
     * Verifica que un usuario no autorizado no puede eliminar una encuesta.
     */
    @Test
    public void testEliminarEncuesta_UsuarioNoAutorizado() throws Exception {
        UsuarioRegistrado usuarioNoAutorizado = new UsuarioRegistrado();
        usuarioNoAutorizado.setRol(Rol.USUARIO_BASICO);
        MockHttpSession sessionNoAutorizada = new MockHttpSession();
        sessionNoAutorizada.setAttribute("usuario", usuarioNoAutorizado);

        mockMvc.perform(delete("/encuestas/eliminar/test-permalink")
                        .session(sessionNoAutorizada))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/encuestas/listar"))
                .andExpect(flash().attributeExists("mensajeError"))
                .andExpect(flash().attribute("mensajeError", "Acceso denegado."));
    }


    /**
     * Verifica que se maneja el caso de intentar eliminar una encuesta que no existe.
     */
    @Test
    public void testEliminarEncuesta_NoEncontrada() throws Exception {
        doThrow(new NoSuchElementException("Encuesta no encontrada")).when(encuestaService).eliminarEncuesta(anyString());

        mockMvc.perform(delete("/encuestas/eliminar/test-permalink")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/encuestas/listar"))
                .andExpect(flash().attributeExists("mensajeError"))
                .andExpect(flash().attribute("mensajeError", "Encuesta no encontrada."));
    }
}
