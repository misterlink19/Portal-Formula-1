package com.portal.formula1;

import com.portal.formula1.controller.VotoController;
import com.portal.formula1.model.*;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


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
        // Mock para una encuesta archivada encontrada
        EncuestaArchivada encuestaArchivada = new EncuestaArchivada();
        encuestaArchivada.setPermalink("test-permalink");
        PilotoArchivado pilotoArchivado = new PilotoArchivado();
        pilotoArchivado.setDorsal(44);
        pilotoArchivado.setNombre("Lewis");
        encuestaArchivada.setPilotosArchivados(Collections.singletonList(pilotoArchivado));

        // Mock para el ranking de votación
        List<Object[]> ranking = new ArrayList<>();
        ranking.add(new Object[]{"Lewis", "Hamilton", "HAM", 44, "/img/hamilton.jpg", "Reino Unido", "@LewisHamilton", 10L, "Mercedes"});

        // Configuración de los mocks
        when(encuestaService.obtenerEncuestaArchivadaPorPermalink("test-permalink")).thenReturn(encuestaArchivada);
        when(votoService.getRankingVotacion("test-permalink")).thenReturn(ranking);

        // Ejecución del test
        mockMvc.perform(get("/votos/test-permalink/resultados"))
                .andExpect(status().isOk())
                .andExpect(view().name("votos/resultadosEncuesta"))
                .andExpect(model().attributeExists("encuestaArchivada"))
                .andExpect(model().attribute("encuestaArchivada", encuestaArchivada))
                .andExpect(model().attributeExists("pilotosArchivados"))
                .andExpect(model().attribute("pilotosArchivados", encuestaArchivada.getPilotosArchivados()))
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
        when(encuestaService.obtenerEncuestaArchivadaPorPermalink("test-permalink")).thenThrow(new NoSuchElementException());

        // Ejecución del test
        mockMvc.perform(get("/votos/test-permalink/resultados"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("mensajeError"))
                .andExpect(model().attribute("mensajeError", "Encuesta no encontrada."));
    }

    /**
     * Verifica que se redirige correctamente a la página de votación de la última encuesta disponible.
     */
    @Test
    public void testRedirigirAVotar_UltimaEncuestaDisponible() throws Exception {
        Encuesta ultimaEncuesta = new Encuesta();
        ultimaEncuesta.setPermalink("ultima-encuesta");
        when(encuestaService.obtenerUltimaEncuestaDisponible()).thenReturn(ultimaEncuesta);

        mockMvc.perform(get("/votos/votar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/votos/ultima-encuesta/votar"));
    }

    /**
     * Verifica que se muestra un mensaje de error si no hay encuestas disponibles.
     */
    @Test
    public void testRedirigirAVotar_NoEncuestasDisponibles() throws Exception {
        when(encuestaService.obtenerUltimaEncuestaDisponible()).thenThrow(new NoSuchElementException());

        mockMvc.perform(get("/votos/votar"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("mensajeError"))
                .andExpect(model().attribute("mensajeError", "No hay encuestas disponibles en este momento."));
    }

    /**
     * Verifica que se maneja correctamente el caso en que la encuesta no se encuentra al mostrar el formulario de votación.
     */
    @Test
    public void testMostrarFormularioVotacion_EncuestaNoEncontrada() throws Exception {
        when(encuestaService.obtenerEncuestaPorPermalink(anyString())).thenThrow(new NoSuchElementException());

        mockMvc.perform(get("/votos/test-permalink/votar"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("mensajeError"))
                .andExpect(model().attribute("mensajeError", "Encuesta no encontrada."));
    }

    /**
     * Verifica que se maneja correctamente el caso en que la encuesta no se encuentra al crear un voto.
     */
    @Test
    public void testCrearVoto_EncuestaNoEncontrada() throws Exception {
        when(encuestaService.obtenerEncuestaPorPermalink(anyString())).thenThrow(new NoSuchElementException());

        mockMvc.perform(post("/votos/test-permalink/votar")
                        .param("nombreVotante", "Test")
                        .param("correoVotante", "test@example.com")
                        .param("opcionSeleccionada", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("mensajeError"))
                .andExpect(model().attribute("mensajeError", "Encuesta no encontrada."));
    }
}
