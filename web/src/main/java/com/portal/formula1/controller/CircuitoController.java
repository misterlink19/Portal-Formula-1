package com.portal.formula1.controller;

import com.portal.formula1.model.Circuito;
import com.portal.formula1.service.CircuitoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("/admin/circuitos")
public class CircuitoController {
    private static final String UPLOAD_DIR = "uploads/circuitos/";

    @Autowired
    private CircuitoService circuitoService;


    @GetMapping()
    public String mostrarFormulario() {
        return "circuitos/crearCircuito";
    }

    @PostMapping
    public String registrarCircuito(Circuito circuito, @RequestParam("trazadoImg") MultipartFile trazado, Model model) {
        try {
            if (!trazado.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + trazado.getOriginalFilename();
                Path rutaArchivo = Paths.get("src/main/resources/static/uploads/circuitos").resolve(fileName).toAbsolutePath();
                Files.copy(trazado.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);
                circuito.setTrazado(fileName);
            }

            circuitoService.crearOActualizarCircuito(circuito);

            model.addAttribute("mensaje", "Circuito registrado exitosamente!");
            return "circuitos/crearCircuito";
        } catch (IOException e) {
            model.addAttribute("error", "Hubo un error al guardar el trazado.");
            return "circuitos/crearCircuito";
        }
    }
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Circuito circuito = circuitoService.obtenerCircuitoPorId(id);
        model.addAttribute("circuito", circuito);
        return "circuitos/editarCircuito";
    }
    @PostMapping("/editar")
    public String editarCircuito(Circuito circuito, @RequestParam("trazadoImg") MultipartFile trazado, Model model) {
        try {
            if (!trazado.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + trazado.getOriginalFilename();
                Path rutaArchivo = Paths.get("src/main/resources/static/uploads/circuitos").resolve(fileName).toAbsolutePath();
                Files.copy(trazado.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);
                circuito.setTrazado(fileName);
            }

            circuitoService.crearOActualizarCircuito(circuito);

            model.addAttribute("mensaje", "Circuito editado exitosamente!");
            return "circuitos/editarCircuito";
        } catch (IOException e) {
            model.addAttribute("error", "Hubo un error al guardar el trazado.");
            return "circuitos/editarCircuito";
        }
    }

    @GetMapping("/{id}")
    public String mostrarCircuito(@PathVariable Long id, Model model) {
        Circuito circuito = circuitoService.obtenerCircuitoPorId(id);
        model.addAttribute("circuito", circuito);
        return "circuitos/verCircuito";
    }

}