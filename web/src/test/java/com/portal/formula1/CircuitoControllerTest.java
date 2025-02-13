package com.portal.formula1;

import com.portal.formula1.controller.CircuitoController;
import com.portal.formula1.model.Circuito;
import com.portal.formula1.service.CircuitoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CircuitoControllerTest {

    @Mock
    private CircuitoService circuitoService;

    @Mock
    private MultipartFile mockFile;

    @Mock
    private Model model;

    @InjectMocks
    private CircuitoController circuitoController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa los mocks
    }

    @Test
    public void testMostrarFormularioEditar_Success() {
        Long circuitoId = 1L;
        Circuito circuito = new Circuito();
        when(circuitoService.obtenerCircuitoPorId(circuitoId)).thenReturn(circuito);

        String result = circuitoController.mostrarFormularioEditar(circuitoId, model);

        assertEquals("circuitos/editarCircuito", result);
        verify(model).addAttribute("circuito", circuito);
    }

    @Test
    public void testEditarCircuito_Success() throws IOException {
        Circuito circuito = new Circuito();
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("example.png");
        InputStream inputStream = mock(InputStream.class);
        when(mockFile.getInputStream()).thenReturn(inputStream);

        String result = circuitoController.editarCircuito(circuito, mockFile, model);

        verify(circuitoService, times(1)).crearOActualizarCircuito(circuito);
        verify(model, times(1)).addAttribute(eq("mensaje"), anyString());

        assertEquals("circuitos/editarCircuito", result);
    }

    @Test
    public void testEditarCircuito_EmptyFile() {
        Circuito circuito = new Circuito();
        when(mockFile.isEmpty()).thenReturn(true);

        String result = circuitoController.editarCircuito(circuito, mockFile, model);

        verify(circuitoService, times(1)).crearOActualizarCircuito(circuito);
        assertEquals(null, circuito.getTrazado(), "El trazado debe ser null si no hay archivo");

        verify(model, times(1)).addAttribute(eq("mensaje"), eq("Circuito editado exitosamente!"));

        assertEquals("circuitos/editarCircuito", result);
    }

    @Test
    public void testEditarCircuito_FileCopyError() throws IOException {
        Circuito circuito = new Circuito();
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("example.png");
        doThrow(IOException.class).when(mockFile).getInputStream();

        String result = circuitoController.editarCircuito(circuito, mockFile, model);

        assertEquals("circuitos/editarCircuito", result);
        verify(model).addAttribute("error", "Hubo un error al guardar el trazado.");
    }

    @Test
    public void testRegistrarCircuitoExitoso() throws IOException {
        Circuito circuito = new Circuito();
        circuito.setNombre("Circuito de Prueba");

        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("trazado.png");
        InputStream inputStream = mock(InputStream.class);
        when(mockFile.getInputStream()).thenReturn(inputStream);

        String viewName = circuitoController.registrarCircuito(circuito, mockFile, model);

        verify(circuitoService, times(1)).crearOActualizarCircuito(circuito);
        verify(model, times(1)).addAttribute(eq("mensaje"), anyString());

        assertEquals("circuitos/crearCircuito", viewName, "La vista no coincide");
    }

    @Test
    public void testRegistrarCircuitoSinImagen() throws IOException {
        Circuito circuito = new Circuito();
        circuito.setNombre("Circuito de Prueba");

        when(mockFile.isEmpty()).thenReturn(true);

        String viewName = circuitoController.registrarCircuito(circuito, mockFile, model);

        verify(circuitoService, times(1)).crearOActualizarCircuito(circuito);
        assertEquals(null, circuito.getTrazado(), "El trazado debe ser null si no hay archivo");

        verify(model, times(1)).addAttribute(eq("mensaje"), eq("Circuito registrado exitosamente!"));

        assertEquals("circuitos/crearCircuito", viewName, "La vista no coincide");
    }

    @Test
    public void testMostrarCircuito() {
        Circuito circuito = new Circuito();
        circuito.setId(1L);
        circuito.setNombre("Circuito de Monza");

        when(circuitoService.obtenerCircuitoPorId(1L)).thenReturn(circuito);
        String viewName = circuitoController.mostrarCircuito(1L, model);

        assertEquals("circuitos/verCircuito", viewName, "La vista no coincide");
        verify(model).addAttribute("circuito", circuito);
    }
}
