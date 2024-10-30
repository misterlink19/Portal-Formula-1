/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.portal.formula1;

import com.portal.formula1.model.Aficionado;
import com.portal.formula1.service.AficionadoService;
import java.util.List;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AficionadoTests {

    @Autowired
    private AficionadoService aficionadoService;

    @Test
    public void testGetAllAficionados() {
        List<Aficionado> aficionados = aficionadoService.getAllAficionados();
        assertNotNull(aficionados);
        assertFalse(aficionados.isEmpty());
    }
}
