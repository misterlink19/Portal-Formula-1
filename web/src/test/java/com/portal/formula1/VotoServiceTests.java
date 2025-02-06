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
    private PilotoDAO pilotoDAO;

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

    @Test
    public void testCrearVoto() {
        Voto voto = new Voto();
        voto.setCorreoVotante("test@example.com");
        voto.setOpcionSeleccionada("44");
        Encuesta encuesta = new Encuesta();
        voto.setEncuesta(encuesta);

        when(votoDAO.save(any(Voto.class))).thenReturn(voto);

        Voto result = votoService.crearVoto(voto);

        assertEquals("test@example.com", result.getCorreoVotante());
        assertEquals("44", result.getOpcionSeleccionada());
        verify(votoDAO, times(1)).save(voto);
    }

    @Test
    public void testObtenerVotosPorEncuesta() {
        Encuesta encuesta = new Encuesta();
        Voto voto1 = new Voto();
        voto1.setEncuesta(encuesta);
        Voto voto2 = new Voto();
        voto2.setEncuesta(encuesta);
        List<Voto> votos = Arrays.asList(voto1, voto2);

        when(votoDAO.findByEncuesta(encuesta)).thenReturn(votos);

        List<Voto> result = votoService.obtenerVotosPorEncuesta(encuesta);

        assertEquals(2, result.size());
        assertTrue(result.contains(voto1));
        assertTrue(result.contains(voto2));
    }

    @Test
    public void testHaVotadoAntes() {
        Encuesta encuesta = new Encuesta();
        String correoVotante = "test@example.com";

        when(votoDAO.existsByCorreoVotanteAndEncuesta(correoVotante, encuesta)).thenReturn(true);

        boolean result = votoService.haVotadoAntes(correoVotante, encuesta);

        assertTrue(result);
    }

    @Test
    public void testGetRankingVotacion() {
        Encuesta encuesta = new Encuesta();
        encuesta.setPermalink("test-permalink");
        PilotoArchivado pilotoArchivado = new PilotoArchivado();
        pilotoArchivado.setDorsal(44);
        pilotoArchivado.setNombre("Piloto 1");
        EncuestaArchivada encuestaArchivada = new EncuestaArchivada();
        encuestaArchivada.setPermalink("test-permalink");
        encuestaArchivada.setPilotosArchivados(Collections.singletonList(pilotoArchivado));
        Voto voto = new Voto();
        voto.setOpcionSeleccionada("44");
        voto.setEncuesta(encuesta);

        when(encuestaDAO.findById("test-permalink")).thenReturn(Optional.of(encuesta));
        when(encuestaArchivadaDAO.findByPermalink("test-permalink")).thenReturn(Optional.of(encuestaArchivada));
        when(votoDAO.findByEncuesta(encuesta)).thenReturn(Collections.singletonList(voto));

        List<Object[]> result = votoService.getRankingVotacion("test-permalink");

        assertEquals(1, result.size());
        assertEquals("Piloto 1", result.get(0)[0]);
        assertEquals(44, result.get(0)[3]);
        assertEquals(1L, result.get(0)[7]);
    }
}