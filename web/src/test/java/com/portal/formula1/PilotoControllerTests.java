package com.portal.formula1;

import com.portal.formula1.controller.PilotoController;
import com.portal.formula1.model.Equipo;
import com.portal.formula1.model.Piloto;
import com.portal.formula1.model.Rol;
import com.portal.formula1.model.UsuarioRegistrado;
import com.portal.formula1.service.AutentificacionService;
import com.portal.formula1.service.ImagenService;
import com.portal.formula1.service.PilotoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PilotoControllerTests {

    @Mock
    private PilotoService pilotoService;

    @Mock
    private ImagenService imagenService;

    @Mock
    private AutentificacionService autentificacionService;

    @InjectMocks
    private PilotoController pilotoController;

    private MockMvc mockMvc;
    private MockHttpSession session;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(pilotoController).build();

        // Configurar usuario administrador en la sesión
        UsuarioRegistrado adminUser = new UsuarioRegistrado();
        adminUser.setUsuario("admin");
        adminUser.setRol(Rol.ADMIN);
        session = new MockHttpSession();
        session.setAttribute("usuario", adminUser);
    }

    /**
     * Verifica que la vista de la lista de pilotos se muestra correctamente.
     */
    @Test
    public void testMostrarMenuPilotos() throws Exception {
        // Simular la verificación del usuario
        when(autentificacionService.checkUser(any(String.class))).thenReturn((UsuarioRegistrado) session.getAttribute("usuario"));

        mockMvc.perform(get("/pilotos")
                        .session(session)) // Incluir la sesión con el usuario autenticado
                .andExpect(status().isOk())
                .andExpect(view().name("pilotos/piloto"))
                .andExpect(model().attributeExists("usuario"));
    }

    /**
     * Verifica que el formulario de creación de pilotos se muestra correctamente.
     **/
    @Test
    public void testMostrarFormularioCreacion() throws Exception {
        UsuarioRegistrado jefeDeEquipoUser = new UsuarioRegistrado();
        jefeDeEquipoUser.setUsuario("jefe");
        jefeDeEquipoUser.setRol(Rol.JEFE_DE_EQUIPO);
        jefeDeEquipoUser.setEquipo(new com.portal.formula1.model.Equipo());
        session.setAttribute("usuario", jefeDeEquipoUser);

        when(autentificacionService.checkUser(any(String.class))).thenReturn(jefeDeEquipoUser);

        mockMvc.perform(get("/pilotos/crear")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("pilotos/crearPiloto"))
                .andExpect(model().attributeExists("piloto"));
    }


    /**
     * Verifica que se puede crear un piloto y que se redirige a la vista del piloto creado.
     **/
    @Test
    public void testCrearPiloto() throws Exception {
        Piloto piloto = new Piloto();
        piloto.setDorsal(1);
        piloto.setNombre("Nuevo Piloto");
        piloto.setSiglas("NP");

        UsuarioRegistrado jefeDeEquipoUser = new UsuarioRegistrado();
        jefeDeEquipoUser.setUsuario("jefe");
        jefeDeEquipoUser.setRol(Rol.JEFE_DE_EQUIPO);
        jefeDeEquipoUser.setEquipo(new com.portal.formula1.model.Equipo());
        session.setAttribute("usuario", jefeDeEquipoUser);

        // Simular la verificación del usuario
        when(autentificacionService.checkUser(any(String.class))).thenReturn(jefeDeEquipoUser);
        when(pilotoService.guardarPiloto(any(Piloto.class))).thenReturn(piloto);
        when(imagenService.isFormatoValido(any(MultipartFile.class))).thenReturn(true);
        when(imagenService.isTamanoValido(any(MultipartFile.class))).thenReturn(true);

        MockMultipartFile mockFile = new MockMultipartFile("fotoArchivo", "foto.png", "image/png", "test content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/pilotos/crear")
                        .file(mockFile)
                        .session(session) // Incluir la sesión con el usuario autenticado
                        .param("dorsal", "1")
                        .param("nombre", "Nuevo Piloto")
                        .param("siglas", "NP")
                        .flashAttr("piloto", piloto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pilotos/1"));
    }

    /**
     * Verifica que la vista de un piloto específico se muestra correctamente.
     * **/
    @Test
    public void testMostrarPiloto() throws Exception {
        Piloto piloto = new Piloto();
        piloto.setDorsal(1);

        // Crear un equipo y asignarlo al piloto
        Equipo equipo = new Equipo();
        equipo.setId(1L);

        piloto.setEquipo(equipo);

        // Crear un usuario responsable
        UsuarioRegistrado responsable = new UsuarioRegistrado();
        responsable.setUsuario("responsable");

        // Añadir el responsable al equipo
        List<UsuarioRegistrado> responsables = new ArrayList<>();
        responsables.add(responsable);
        equipo.setResponsables(responsables);

        // Simular la verificación del usuario
        when(autentificacionService.checkUser(any(String.class))).thenReturn((UsuarioRegistrado) session.getAttribute("usuario"));
        when(pilotoService.obtenerPilotoPorDorsal(1)).thenReturn(Optional.of(piloto));

        mockMvc.perform(get("/pilotos/1")
                        .session(session)) // Incluir la sesión con el usuario autenticado
                .andExpect(status().isOk())
                .andExpect(view().name("pilotos/verPiloto"))
                .andExpect(model().attributeExists("piloto"))
                .andExpect(model().attribute("piloto", piloto));
    }

    /**
     * Verifica que se maneja correctamente el caso en el que no se permite crear un piloto sin permisos adecuados.
     **/
    @Test
    public void testCrearPilotoSinPermisos() throws Exception {
        UsuarioRegistrado usuarioSinPermisos = new UsuarioRegistrado();
        usuarioSinPermisos.setUsuario("USUARIO_BASICO");
        usuarioSinPermisos.setRol(Rol.USUARIO_BASICO);
        session.setAttribute("usuario", usuarioSinPermisos);

        when(autentificacionService.checkUser(any(String.class))).thenReturn(usuarioSinPermisos);

        MockMultipartFile mockFile = new MockMultipartFile("fotoArchivo", "foto.png", "image/png", "test content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/pilotos/crear")
                        .file(mockFile)
                        .session(session)
                        .param("dorsal", "1")
                        .param("nombre", "Nuevo Piloto")
                        .param("siglas", "NP"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("mensajeError"));
    }

    /**
     * Verifica que se maneja correctamente el caso en el que el formato de la imagen del piloto no es válido.
     **/
    @Test
    public void testCrearPilotoFormatoImagenInvalido() throws Exception {
        UsuarioRegistrado jefeDeEquipoUser = new UsuarioRegistrado();
        jefeDeEquipoUser.setUsuario("jefe");
        jefeDeEquipoUser.setRol(Rol.JEFE_DE_EQUIPO);
        jefeDeEquipoUser.setEquipo(new com.portal.formula1.model.Equipo());
        session.setAttribute("usuario", jefeDeEquipoUser);

        // Simular la verificación del usuario
        when(autentificacionService.checkUser(any(String.class))).thenReturn(jefeDeEquipoUser);
        when(imagenService.isFormatoValido(any(MultipartFile.class))).thenReturn(false);

        MockMultipartFile mockFile = new MockMultipartFile("fotoArchivo", "foto.txt", "text/plain", "test content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/pilotos/crear")
                        .file(mockFile)
                        .session(session)
                        .param("dorsal", "1")
                        .param("nombre", "Nuevo Piloto")
                        .param("siglas", "NP"))
                .andExpect(status().isOk())
                .andExpect(view().name("pilotos/crearPiloto"))
                .andExpect(model().attributeExists("piloto"))
                .andExpect(model().attributeHasFieldErrors("piloto", "rutaImagen")); // Cambiado de "foto" a "rutaImagen"
    }


    /**
     * Verifica que se maneja correctamente el caso en el que el tamaño de la imagen del piloto no es válido.
     **/
    @Test
    public void testCrearPilotoTamanoImagenInvalido() throws Exception {
        UsuarioRegistrado jefeDeEquipoUser = new UsuarioRegistrado();
        jefeDeEquipoUser.setUsuario("jefe");
        jefeDeEquipoUser.setRol(Rol.JEFE_DE_EQUIPO);
        jefeDeEquipoUser.setEquipo(new com.portal.formula1.model.Equipo());
        session.setAttribute("usuario", jefeDeEquipoUser);

        // Simular la verificación del usuario
        when(autentificacionService.checkUser(any(String.class))).thenReturn(jefeDeEquipoUser);
        when(imagenService.isFormatoValido(any(MultipartFile.class))).thenReturn(true);
        when(imagenService.isTamanoValido(any(MultipartFile.class))).thenReturn(false);

        MockMultipartFile mockFile = new MockMultipartFile("fotoArchivo", "foto.png", "image/png", "test content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/pilotos/crear")
                        .file(mockFile)
                        .session(session)
                        .param("dorsal", "1")
                        .param("nombre", "Nuevo Piloto")
                        .param("siglas", "NP"))
                .andExpect(status().isOk())
                .andExpect(view().name("pilotos/crearPiloto"))
                .andExpect(model().attributeExists("piloto"))
                .andExpect(model().attributeHasFieldErrors("piloto", "rutaImagen")); // Actualizado aquí
    }


    /**
     * Verifica que se maneja correctamente el caso en el que ya existe un piloto con el mismo dorsal.
     **/
    @Test
    public void testCrearPilotoDorsalExistente() throws Exception {
        UsuarioRegistrado jefeDeEquipoUser = new UsuarioRegistrado();
        jefeDeEquipoUser.setUsuario("jefe");
        jefeDeEquipoUser.setRol(Rol.JEFE_DE_EQUIPO);
        jefeDeEquipoUser.setEquipo(new com.portal.formula1.model.Equipo());
        session.setAttribute("usuario", jefeDeEquipoUser);

        // Simular la verificación del usuario
        when(autentificacionService.checkUser(any(String.class))).thenReturn(jefeDeEquipoUser);
        when(pilotoService.existeDorsal(anyInt())).thenReturn(true);

        MockMultipartFile mockFile = new MockMultipartFile("fotoArchivo", "foto.png", "image/png", "test content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/pilotos/crear")
                        .file(mockFile)
                        .session(session)
                        .param("dorsal", "1")
                        .param("nombre", "Nuevo Piloto")
                        .param("siglas", "NP"))
                .andExpect(status().isOk())
                .andExpect(view().name("pilotos/crearPiloto"))
                .andExpect(model().attributeExists("piloto"))
                .andExpect(model().attributeHasFieldErrors("piloto", "dorsal"));
    }

    /**
     * Verifica que no permite crear pilotos a usuarios sin permisos de responsables de equipo
     * */
    @Test
    public void testMostrarPilotoSinPermisos() throws Exception {
        UsuarioRegistrado usuarioSinPermisos = new UsuarioRegistrado();
        usuarioSinPermisos.setUsuario("USUARIO_BASICO");
        usuarioSinPermisos.setRol(Rol.USUARIO_BASICO);
        session.setAttribute("usuario", usuarioSinPermisos);

        when(autentificacionService.checkUser(any(String.class))).thenReturn(usuarioSinPermisos);

        mockMvc.perform(get("/pilotos/1")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("mensajeError"));
    }
}


