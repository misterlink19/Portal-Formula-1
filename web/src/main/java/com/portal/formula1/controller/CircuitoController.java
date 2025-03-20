package com.portal.formula1.controller;

import com.portal.formula1.model.Circuito;
import com.portal.formula1.service.CalendarioEventoService;
import com.portal.formula1.service.CircuitoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
@RequestMapping("/admin/circuitos")
public class CircuitoController {
    private static final String UPLOAD_DIR = "uploads/circuitos/";

    @Autowired
    private CircuitoService circuitoService;

    @Autowired
    private CalendarioEventoService calendarioEventoService;


    @GetMapping()
    public String mostrarFormulario() {
        return "circuitos/crearCircuito";
    }

    @GetMapping("/listar") // o la ruta que prefieras
    public String listarCircuitos(Model model) {
        List<Circuito> circuitos = circuitoService.listarCircuitos();
        model.addAttribute("circuitos", circuitos);
        return "circuitos/circuito";
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
            return "circuitos/circuito";
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
            return "circuitos/circuito";
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

    @GetMapping("/eliminar/{id}")
    public String eliminarCircuito(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            // 1. Verificar si el circuito está en el calendario
            if (calendarioEventoService.existsByCircuitoId(id)) {
                redirectAttributes.addFlashAttribute("error", "No se puede eliminar el circuito porque está en el calendario.");
                return "redirect:/admin/circuitos/listar";
            }

            circuitoService.eliminarCircuitoPorId(id);
            redirectAttributes.addFlashAttribute("mensaje", "Circuito eliminado exitosamente!");
            return "redirect:/admin/circuitos/listar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar el circuito.");
            return "redirect:/admin/circuitos/listar";
        }
    }
}