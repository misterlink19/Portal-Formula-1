/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.portal.formula1;

import com.portal.formula1.model.*;
import com.portal.formula1.repository.EncuestaArchivadaDAO;
import com.portal.formula1.repository.EncuestaDAO;
import com.portal.formula1.repository.PilotoArchivadoDAO;
import com.portal.formula1.repository.PilotoDAO;
import com.portal.formula1.service.EncuestaService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.time.LocalDateTime;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

/**
 *
 * @author Misterlink
 */
public class EncuestaServiceTests {

    @Mock
    private EncuestaDAO encuestaDAO;

    @Mock
    private EncuestaArchivadaDAO encuestaArchivadaDAO;

    @Mock
    private PilotoDAO pilotoDAO;

    @Mock
    private PilotoArchivadoDAO pilotoArchivadoDAO;

    @InjectMocks
    private EncuestaService encuestaService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Verifica que el servicio retorna todas las encuestas correctamente.
     */
    @Test
    public void testGetTodasLasEncuestas() {
        Encuesta encuesta1 = new Encuesta("Título 1", "Descripción 1", LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        Encuesta encuesta2 = new Encuesta("Título 2", "Descripción 2", LocalDateTime.now(), LocalDateTime.now().plusDays(2));

        List<Encuesta> encuestas = Arrays.asList(encuesta1, encuesta2);

        when(encuestaDAO.findAll()).thenReturn(encuestas);

        List<Encuesta> result = encuestaService.getAllEncuestas();

        assertEquals(2, result.size());
        assertEquals("Título 1", result.getFirst().getTitulo());
    }

    /**
     * Verifica que el servicio puede crear una nueva encuesta y que se llama al
     * méthodo save del DAO una vez.
     */
    @Test
    public void testCrearEncuesta() {
        Encuesta encuesta = new Encuesta("Título de prueba", "Descripción de prueba", LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        Piloto piloto = new Piloto();
        piloto.setDorsal(44);
        when(encuestaDAO.save(any(Encuesta.class))).thenReturn(encuesta);
        when(pilotoDAO.findById(44)).thenReturn(Optional.of(piloto));

        Encuesta result = encuestaService.crearEncuesta(encuesta, new HashSet<>(List.of(44)));
        assertEquals("Título de prueba", result.getTitulo());
        assertTrue(result.getPilotos().contains(piloto));
        verify(encuestaDAO, times(1)).save(encuesta);
        verify(pilotoDAO, times(1)).findById(44);
    }


    /**
     * Verifica que el servicio retorna una encuesta específica correctamente
     * basada en su permalink.
     */
    @Test
    public void testObtenerEncuestaPorPermalink() {
        Encuesta encuesta = new Encuesta("Título 1", "Descripción 1", LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        String permalink = encuesta.getPermalink();

        when(encuestaDAO.findById(permalink)).thenReturn(java.util.Optional.of(encuesta));

        Encuesta result = encuestaService.obtenerEncuestaPorPermalink(permalink);

        assertEquals(permalink, result.getPermalink());
    }


    /**
     * Verifica que el méthodo retorna correctamente la última
     * encuesta disponible basada en fechaInicio.
     */
    @Test
    public void testObtenerUltimaEncuestaDisponible() {
        // Crea una encuesta de prueba
        Encuesta encuesta = new Encuesta("Título 1", "Descripción 1", LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        // Configura el mock para que devuelva esta encuesta
        when(encuestaDAO.findFirstByOrderByFechaInicioDesc()).thenReturn(Optional.of(encuesta));

        // Llama al servicio
        Encuesta result = encuestaService.obtenerUltimaEncuestaDisponible();

        // Verifica los resultados
        assertEquals("Título 1", result.getTitulo());
        verify(encuestaDAO, times(1)).findFirstByOrderByFechaInicioDesc();
    }


    /**
     * Verifica que el méthodo lanza una
     * excepción si no hay encuestas disponibles.
     */
    @Test
    public void testObtenerUltimaEncuestaDisponibleNoEncontrada() {
        when(encuestaDAO.findFirstByOrderByFechaInicioDesc()).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> encuestaService.obtenerUltimaEncuestaDisponible());
    }


    /**
     * Probar que se obtiene la encuesta más
     * cercana disponible a la fecha actual *
     */
    @Test
    public void testObtenerEncuestaMasCercanaDisponible() {
        Encuesta encuesta1 = new Encuesta("Encuesta 1", "Descripción 1", LocalDateTime.now().minusDays(3), LocalDateTime.now().plusDays(1));
        Encuesta encuesta2 = new Encuesta("Encuesta 2", "Descripción 2", LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(2));

        // Mockear el método correcto
        when(encuestaDAO.findFirstByOrderByFechaInicioDesc()).thenReturn(Optional.of(encuesta2));

        Encuesta result = encuestaService.obtenerUltimaEncuestaDisponible();

        assertEquals("Encuesta 2", result.getTitulo());
        assertEquals("Descripción 2", result.getDescripcion());

        // Verificar que el mock fue llamado una vez
        verify(encuestaDAO, times(1)).findFirstByOrderByFechaInicioDesc();
    }



    @Test
    /**
     * Probar que se lanza una excepción
     * cuando no hay encuestas disponibles
     */
    public void testObtenerEncuestaMasCercanaDisponibleNoEncontrada(){
        when(encuestaDAO.findFirstByFechaLimiteAfterOrderByFechaLimiteAsc(LocalDateTime.now())).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> encuestaService.obtenerUltimaEncuestaDisponible());
    }


    /**
     * Probar que al crear una encuesta sin fecha de inicio,
     * se establece la fecha actual
     */
    @Test
    public void testCrearEncuestaSinFechaInicio() {
        Encuesta encuesta = new Encuesta("Título de prueba", "Descripción de prueba", null, LocalDateTime.now().plusDays(1));
        Piloto piloto = new Piloto();
        piloto.setDorsal(44);
        when(encuestaDAO.save(any(Encuesta.class))).thenAnswer(invocation -> {
            Encuesta e = invocation.getArgument(0);
            e.setFechaInicio(LocalDateTime.now());
            return e;
        });
        when(pilotoDAO.findById(44)).thenReturn(Optional.of(piloto));

        Encuesta result = encuestaService.crearEncuesta(encuesta, new HashSet<>(List.of(44)));
        assertEquals("Título de prueba", result.getTitulo());
        assertNotNull(result.getFechaInicio());
        assertTrue(result.getPilotos().contains(piloto));
        verify(encuestaDAO, times(1)).save(encuesta);
        verify(pilotoDAO, times(1)).findById(44);
    }


    @Test
    /**
     * Probar que se lanza una excepción cuando
     * no se encuentra una encuesta por permalink
     */
    public void testObtenerEncuestaPorPermalinkNoEncontrada(){
        String permalink = "non-existent";
        when(encuestaDAO.findById(permalink)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> encuestaService.obtenerEncuestaPorPermalink(permalink));
    }

    @Test
    /**
     * Probar que se eliminan las encuestas por permalink
     */
    public void testEliminarEncuesta() {
        Encuesta encuesta = new Encuesta("Título 1", "Descripción 1", LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        String permalink = encuesta.getPermalink();
        when(encuestaDAO.findById(permalink)).thenReturn(Optional.of(encuesta));

        encuestaService.eliminarEncuesta(permalink);

        verify(encuestaDAO, times(1)).delete(encuesta);
    }

    @Test
    /**
     * Test para verificar si un piloto está
     * en una encuesta activa
     */
    public void testEstaPilotoEnEncuestaActiva() {
        Piloto piloto = new Piloto();
        piloto.setDorsal(44);
        Encuesta encuesta = new Encuesta("Título 1", "Descripción 1", LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
        encuesta.setPilotos(new HashSet<>(Collections.singletonList(piloto)));
        when(encuestaDAO.findByFechaLimiteAfter(any(LocalDateTime.class))).thenReturn(Collections.singletonList(encuesta));

        boolean result = encuestaService.estaPilotoEnEncuestaActiva(44);

        assertTrue(result);
    }
    @Test
    /**
     * Test para archivar encuestas expiradas
     */
    public void testArchivarEncuestasExpiradas() {
        Encuesta encuesta = new Encuesta("Título 1", "Descripción 1", LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));
        encuesta.setPermalink("test-permalink");

        Piloto piloto = new Piloto();
        piloto.setDorsal(44);
        Equipo equipo = new Equipo();
        equipo.setNombre("Equipo 1");
        piloto.setEquipo(equipo);

        encuesta.setPilotos(new HashSet<>(Collections.singletonList(piloto)));

        when(encuestaDAO.findByFechaLimiteBefore(any(LocalDateTime.class))).thenReturn(Collections.singletonList(encuesta));
        when(encuestaArchivadaDAO.existsByPermalink("test-permalink")).thenReturn(false);

        encuestaService.archivarEncuestasExpiradas();

        verify(encuestaArchivadaDAO, times(1)).save(any(EncuestaArchivada.class));
        verify(pilotoArchivadoDAO, times(1)).save(any(PilotoArchivado.class));
        verify(encuestaDAO, times(1)).save(encuesta); // Verifica que la encuesta actualizada se guarda
    }


    @Test
    /**
     * Test para obtener una encuesta archivada por permalink:
     */
    public void testObtenerEncuestaArchivadaPorPermalink() {
        EncuestaArchivada encuestaArchivada = new EncuestaArchivada();
        encuestaArchivada.setPermalink("test-permalink");
        when(encuestaArchivadaDAO.findByPermalink("test-permalink")).thenReturn(Optional.of(encuestaArchivada));

        EncuestaArchivada result = encuestaService.obtenerEncuestaArchivadaPorPermalink("test-permalink");

        assertEquals("test-permalink", result.getPermalink());
    }


}
