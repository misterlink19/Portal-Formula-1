package com.portal.formula1;

import com.portal.formula1.model.Encuesta;
import com.portal.formula1.model.EncuestaArchivada;
import com.portal.formula1.model.PilotoArchivado;
import com.portal.formula1.model.Voto;
import com.portal.formula1.repository.EncuestaArchivadaDAO;
import com.portal.formula1.repository.EncuestaDAO;
import com.portal.formula1.repository.PilotoDAO;
import com.portal.formula1.repository.VotoDAO;
import com.portal.formula1.service.VotoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class VotoServiceTests {

    @Mock
    private VotoDAO votoDAO;

    @Mock
    private EncuestaDAO encuestaDAO;

    @Mock
    private EncuestaArchivadaDAO encuestaArchivadaDAO;

    @InjectMocks
    private VotoService votoService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Verifica que se crea un voto correctamente.
     */
    @Test
    public void testCrearVoto() {
        Voto voto = new Voto();
        when(votoDAO.save(any(Voto.class))).thenReturn(voto);

        Voto resultado = votoService.crearVoto(voto);
        assertNotNull(resultado);
    }

    /**
     * Verifica que se obtienen los votos por encuesta correctamente.
     */
    @Test
    public void testObtenerVotosPorEncuesta() {
        Encuesta encuesta = new Encuesta();
        List<Voto> votos = Arrays.asList(new Voto(), new Voto());
        when(votoDAO.findByEncuesta(any(Encuesta.class))).thenReturn(votos);

        List<Voto> resultado = votoService.obtenerVotosPorEncuesta(encuesta);
        assertEquals(2, resultado.size());
    }

    /**
     * Verifica que se comprueba correctamente si un usuario ha votado antes.
     */
    @Test
    public void testHaVotadoAntes() {
        Encuesta encuesta = new Encuesta();
        when(votoDAO.existsByCorreoVotanteAndEncuesta(anyString(), any(Encuesta.class))).thenReturn(true);

        boolean resultado = votoService.haVotadoAntes("test@example.com", encuesta);
        assertTrue(resultado);
    }

    /**
     * Verifica que se obtiene el ranking de votación correctamente.
     */
    @Test
    public void testGetRankingVotacion() {
        Encuesta encuesta = new Encuesta();
        encuesta.setPermalink("test-permalink");
        EncuestaArchivada encuestaArchivada = new EncuestaArchivada();
        encuestaArchivada.setPermalink("test-permalink");

        Voto voto = new Voto();
        voto.setOpcionSeleccionada("44");
        List<Voto> votos = Collections.singletonList(voto);

        when(encuestaDAO.findById(anyString())).thenReturn(Optional.of(encuesta));
        when(encuestaArchivadaDAO.findByPermalink(anyString())).thenReturn(Optional.of(encuestaArchivada));
        when(votoDAO.findByEncuesta(any(Encuesta.class))).thenReturn(votos);

        List<Object[]> ranking = votoService.getRankingVotacion("test-permalink");
        assertNotNull(ranking);
    }

    /**
     * Verifica que se obtienen los votos por usuario correctamente.
     */
    @Test
    public void testObtenerVotosPorUsuario() {
        List<Voto> votos = Arrays.asList(new Voto(), new Voto());
        when(votoDAO.findVotoByCorreoVotante(anyString())).thenReturn(votos);

        List<Voto> resultado = votoService.obtenerVotosPorUsuario("test@example.com");
        assertEquals(2, resultado.size());
    }

    /**
     * Verifica que se obtiene un voto por correo y encuesta correctamente.
     */
    @Test
    public void testObtenerVotoPorCorreoYEncuesta() {
        Voto voto = new Voto();
        when(votoDAO.findVotoByCorreoVotanteAndEncuesta_Permalink(anyString(), anyString())).thenReturn(voto);

        Voto resultado = votoService.obtenerVotoPorCorreoYEncuesta("test@example.com", "test-permalink");
        assertNotNull(resultado);
    }

    /**
     * Verifica que se lanza una excepción cuando no se encuentra la encuesta al obtener el ranking de votación.
     */
    @Test
    public void testGetRankingVotacion_EncuestaNoEncontrada() {
        when(encuestaDAO.findById(anyString())).thenReturn(Optional.empty());
        when(encuestaArchivadaDAO.findByPermalink(anyString())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            votoService.getRankingVotacion("test-permalink");
        });
    }
}