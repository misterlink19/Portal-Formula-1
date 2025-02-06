package com.portal.formula1;

import com.portal.formula1.model.Coches;
import com.portal.formula1.repository.CocheDAO;
import com.portal.formula1.service.CocheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class CocheServiceTest {

    @Mock
    private CocheDAO cocheDAO;

    @InjectMocks
    private CocheService cocheService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testObtenerCochePorCodigo() {
        Coches coche = new Coches();
        coche.setCodigo("A524");
        when(cocheDAO.findById(anyString())).thenReturn(Optional.of(coche));

        Optional<Coches> result = cocheService.obtnerCochePorCodigo("A524");

        assertEquals(coche, result.get());
        verify(cocheDAO, times(1)).findById("A524");
    }

    @Test
    public void testActualizarCoche() {
        Coches coche = new Coches();
        coche.setCodigo("A524");

        cocheService.actualizarCoche(coche);

        verify(cocheDAO, times(1)).save(coche);
    }
}