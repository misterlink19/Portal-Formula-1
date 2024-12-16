package com.portal.formula1;

import com.portal.formula1.controller.VotoController;
import com.portal.formula1.model.Encuesta;
import com.portal.formula1.model.Piloto;
import com.portal.formula1.model.Voto;
import com.portal.formula1.service.EncuestaService;
import com.portal.formula1.service.VotoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


/**
 *
 * @author Misterlink
 */
public class VotoControllerTests {

    private MockMvc mockMvc;

    @Mock
    private VotoService votoService;

    @Mock
    private EncuestaService encuestaService;

    @InjectMocks
    private VotoController votoController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(votoController).build();
    }

    /**
    *
    * Prueba que el formulario de votación se muestra correctamente.
     */
    @Test
    public void testMostrarFormularioVotacion() throws Exception {
        Encuesta encuesta = new Encuesta();
        encuesta.setTitulo("Test Encuesta");
        encuesta.setDescripcion("Descripción de prueba");

        Piloto piloto1 = new Piloto();
        piloto1.setDorsal(44);
        piloto1.setNombre("Lewis");
        piloto1.setApellidos("Hamilton");
        piloto1.setSiglas("HAM");
        piloto1.setRutaImagen("/img/hamilton.jpg");
        piloto1.setPais("Reino Unido");
        piloto1.setTwitter("@LewisHamilton");

        encuesta.getPilotos().add(piloto1);

        when(encuestaService.obtenerEncuestaPorPermalink(anyString())).thenReturn(encuesta);

        mockMvc.perform(get("/votos/test-permalink/votar"))
                .andExpect(status().isOk())
                .andExpect(view().name("votos/votarEncuesta"))
                .andExpect(model().attributeExists("encuesta"))
                .andExpect(model().attributeExists("pilotos"))
                .andExpect(model().attribute("pilotos", new ArrayList<>(encuesta.getPilotos())))
                .andExpect(model().attributeExists("voto"));
    }


    /**
     *
     * Prueba que se muestra un mensaje de error si el correo ya ha votado.
     */
    @Test
    public void testCrearVoto_existeVotoPorCorreo() throws Exception {
        Encuesta encuesta = new Encuesta();
        when(encuestaService.obtenerEncuestaPorPermalink(anyString())).thenReturn(encuesta);
        when(votoService.haVotadoAntes(anyString(), eq(encuesta))).thenReturn(true);

        mockMvc.perform(post("/votos/test-permalink/votar")
                        .param("nombreVotante", "Test")
                        .param("correoVotante", "test@example.com")
                        .param("opcionSeleccionada", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("votos/votarEncuesta"))
                .andExpect(model().attributeExists("mensajeError"));
    }

    /**
     *
     * Prueba que se crea un nuevo voto correctamente.
     */
    @Test
    public void testCrearVoto_nuevoVoto() throws Exception {
        Encuesta encuesta = new Encuesta();
        when(encuestaService.obtenerEncuestaPorPermalink(anyString())).thenReturn(encuesta);
        when(votoService.haVotadoAntes(anyString(), eq(encuesta))).thenReturn(false);
        when(votoService.crearVoto(any(Voto.class))).thenReturn(new Voto());

        mockMvc.perform(post("/votos/test-permalink/votar")
                        .param("nombreVotante", "Test")
                        .param("correoVotante", "test@example.com")
                        .param("opcionSeleccionada", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("votos/votoConfirmado"))
                .andExpect(model().attributeExists("voto"));
    }


    /**
     *
     * Prueba que se muestra correctamente los resultados de una encuesta.
     */
    @Test
    public void testMostrarResultados_EncuestaEncontrada() throws Exception {
        // Mock para una encuesta encontrada
        Encuesta encuesta = new Encuesta();
        encuesta.setTitulo("Test Encuesta");
        encuesta.setDescripcion("Descripción de prueba");

        // Mock para el ranking de votación
        List<Object[]> ranking = new ArrayList<>();
        ranking.add(new Object[]{"Lewis", "Hamilton", "HAM", 44, "/img/hamilton.jpg", "Reino Unido", "@LewisHamilton", 10L, "Mercedes"});
        ranking.add(new Object[]{"Max", "Verstappen", "VER", 33, "/img/verstappen.jpg", "Países Bajos", "@Max33Verstappen", 8L, "Red Bull Racing"});

        // Configuración de los mocks
        when(encuestaService.obtenerEncuestaPorPermalink("test-permalink")).thenReturn(encuesta);
        when(votoService.getRankingVotacion("test-permalink")).thenReturn(ranking);

        // Ejecución del test
        mockMvc.perform(get("/votos/test-permalink/resultados"))
                .andExpect(status().isOk())
                .andExpect(view().name("votos/resultadosEncuesta"))
                .andExpect(model().attributeExists("encuesta"))
                .andExpect(model().attribute("encuesta", encuesta))
                .andExpect(model().attributeExists("ranking"))
                .andExpect(model().attribute("ranking", ranking));
    }




    /**
     *
     * Prueba como se maneja cuando no se encuentra la encuesta de la que se busca el resultado.
     */
    @Test
    public void testMostrarResultados_EncuestaNoEncontrada() throws Exception {
        // Mock para una encuesta no encontrada
        when(encuestaService.obtenerEncuestaPorPermalink("test-permalink")).thenThrow(new NoSuchElementException());

        // Ejecución del test
        mockMvc.perform(get("/votos/test-permalink/resultados"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("mensajeError"))
                .andExpect(model().attribute("mensajeError", "Encuesta no encontrada."));
    }
}
