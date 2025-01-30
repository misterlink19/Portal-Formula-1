package com.portal.formula1;

import com.portal.formula1.controller.CalendarioEventoController;
import com.portal.formula1.model.CalendarioEvento;
import com.portal.formula1.model.Circuito;
import com.portal.formula1.model.Rol;
import com.portal.formula1.model.UsuarioRegistrado;
import com.portal.formula1.service.CalendarioEventoService;
import com.portal.formula1.service.CircuitoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CalendarioEventoControllerTests {

    @Mock
    private CalendarioEventoService calendarioEventoService;

    @Mock
    private CircuitoService circuitoService;

    @InjectMocks
    private CalendarioEventoController calendarioEventoController;

    private MockMvc mockMvc;
    private MockHttpSession session;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(calendarioEventoController).build();

        // Configurar usuario administrador en la sesión
        UsuarioRegistrado adminUser = new UsuarioRegistrado();
        adminUser.setUsuario("admin");
        adminUser.setRol(Rol.ADMIN);
        session = new MockHttpSession();
        session.setAttribute("usuario", adminUser);
    }

    /**
     * Verifica que se muestren los eventos en la vista principal (home).
     */
    @Test
    public void testListarEventos() throws Exception {
        List<CalendarioEvento> eventos = new ArrayList<>();
        eventos.add(new CalendarioEvento(1L, "Evento 1", LocalDate.now(), new Circuito()));
        eventos.add(new CalendarioEvento(2L, "Evento 2", LocalDate.now().plusDays(1), new Circuito()));

        when(calendarioEventoService.listarEventos()).thenReturn(eventos);

        mockMvc.perform(get("/calendario").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("eventos"));
    }

    /**
     * Verifica que se cargue correctamente el formulario para crear eventos.
     */
    @Test
    public void testMostrarFormularioCrearEvento() throws Exception {
        List<Circuito> circuitos = new ArrayList<>();
        Circuito circuito = new Circuito();
        circuito.setId(1L);
        circuito.setNombre("Circuito Ejemplo");
        circuito.setCiudad("Ciudad");
        circuito.setPais("País");
        circuito.setNumeroVueltas(10);
        circuito.setLongitud(1000.0);
        circuito.setNumeroCurvasLentas(2);
        circuito.setNumeroCurvasMedia(3);
        circuito.setNumeroCurvasRapidas(5);
        circuito.setTrazado("trazado.png");

        circuitos.add(circuito);
        when(circuitoService.listarCircuitos()).thenReturn(circuitos);

        mockMvc.perform(get("/calendario/crear").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("calendario/crearEvento"))
                .andExpect(model().attributeExists("evento"))
                .andExpect(model().attributeExists("circuitos"));
    }

    /**
     * Verifica que se pueda crear un evento correctamente.
     */
    @Test
    public void testCrearEvento() throws Exception {
        CalendarioEvento nuevoEvento = new CalendarioEvento(1L, "Nuevo Evento", LocalDate.now(), new Circuito());

        when(calendarioEventoService.guardarEvento(any(CalendarioEvento.class))).thenReturn(nuevoEvento);

        mockMvc.perform(post("/calendario/crear")
                        .param("nombreEvento", "Nuevo Evento")
                        .param("fecha", LocalDate.now().toString())
                        .param("circuito.id", "1")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/calendario/gestion"));
    }

    /**
     * Verifica que se maneje correctamente al intentar crear un evento con error.
     */
    @Test
    public void testCrearEventoConError() throws Exception {
        // Simular una excepción al guardar el evento
        doThrow(new RuntimeException("Error al guardar el evento")).when(calendarioEventoService).guardarEvento(any(CalendarioEvento.class));

        // Mockear la lista de circuitos
        List<Circuito> circuitos = new ArrayList<>();
        circuitos.add(new Circuito());
        when(circuitoService.listarCircuitos()).thenReturn(circuitos);

        mockMvc.perform(post("/calendario/crear")
                        .param("nombreEvento", "Evento Erróneo")
                        .param("fecha", LocalDate.now().toString())
                        .param("circuito.id", "1")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("calendario/crearEvento"))
                .andExpect(model().attributeExists("evento"))
                .andExpect(model().attributeExists("circuitos"))
                .andExpect(model().attributeHasErrors("evento"));
    }


    /**
     * Verifica que se pueda eliminar un evento existente.
     */
    @Test
    public void testEliminarEvento() throws Exception {
        doNothing().when(calendarioEventoService).eliminarEvento(1L);

        mockMvc.perform(post("/calendario/1/eliminar").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/calendario/gestion"));
    }

    /**
     * Verifica que al intentar eliminar un evento inexistente se maneje correctamente.
     */
    @Test
    public void testEliminarEventoNoExistente() throws Exception {
        doThrow(new NoSuchElementException("El evento con ID 1 no existe.")).when(calendarioEventoService).eliminarEvento(1L);

        mockMvc.perform(post("/calendario/1/eliminar").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/calendario/gestion"));
    }

    /**
     * Verifica que se muestre correctamente el detalle de un evento.
     */
    @Test
    public void testVerEvento() throws Exception {
        // Crear un circuito completamente inicializado
        Circuito circuito = new Circuito();
        circuito.setId(1L);
        circuito.setNombre("Circuito Ejemplo");
        circuito.setCiudad("Ciudad");
        circuito.setPais("País");
        circuito.setNumeroVueltas(10);
        circuito.setLongitud(1000.0);
        circuito.setNumeroCurvasLentas(2);
        circuito.setNumeroCurvasMedia(3);
        circuito.setNumeroCurvasRapidas(5);
        circuito.setTrazado("trazado.png");


        // Crear un evento con el circuito completamente inicializado
        CalendarioEvento evento = new CalendarioEvento(
                1L,
                "Evento Detalle",
                LocalDate.now(),
                circuito
        );

        // Simular el comportamiento del servicio
        when(calendarioEventoService.obtenerEventoPorId(1L)).thenReturn(evento);

        // Ejecutar la petición y verificar resultados
        mockMvc.perform(get("/calendario/evento/1").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("calendario/verEvento"))
                .andExpect(model().attributeExists("evento"))
                .andExpect(model().attribute("evento", evento));
    }


    /**
     * Verifica que se maneje correctamente cuando el detalle del evento no existe.
     */
    @Test
    public void testVerEventoNoExistente() throws Exception {
        when(calendarioEventoService.obtenerEventoPorId(1L)).thenThrow(new NoSuchElementException("Evento no encontrado"));

        mockMvc.perform(get("/calendario/evento/1").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("mensajeError"));
    }
    /**
     * Verifica que se maneje correctamente cuando el la fecha
     * es valida.
     */
    @Test
    public void testCrearEventoConFechaValida() throws Exception {
        CalendarioEvento evento = new CalendarioEvento("Evento Válido", LocalDate.now().plusDays(1), new Circuito());
        when(calendarioEventoService.guardarEvento(any(CalendarioEvento.class))).thenReturn(evento);

        mockMvc.perform(post("/calendario/crear")
                        .param("nombreEvento", "Evento Válido")
                        .param("fecha", LocalDate.now().plusDays(1).toString())
                        .param("circuito.id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/calendario/gestion"));
    }
    /**
     * Verifica que se maneje correctamente cuando la fecha del evento
     * no es valida existe.
     */
    @Test
    public void testCrearEventoConFechaInvalida() throws Exception {
        // Mockear la lista de circuitos
        List<Circuito> circuitos = new ArrayList<>();
        circuitos.add(new Circuito());
        when(circuitoService.listarCircuitos()).thenReturn(circuitos);

        mockMvc.perform(post("/calendario/crear")
                        .param("nombreEvento", "Evento Inválido")
                        .param("fecha", LocalDate.now().minusDays(1).toString())
                        .param("circuito.id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("calendario/crearEvento"))
                .andExpect(model().attributeHasFieldErrors("evento", "fecha"))
                .andExpect(model().attributeExists("evento"))
                .andExpect(model().attributeExists("circuitos"));
    }


}
