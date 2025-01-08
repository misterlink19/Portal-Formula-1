package com.portal.formula1.controller;

import com.portal.formula1.model.Rol;
import com.portal.formula1.model.UsuarioRegistrado;
import com.portal.formula1.service.AutentificacionService;
import com.portal.formula1.service.UsuarioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;


@Controller
@Slf4j
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AutentificacionService autentificacionService; // Si lo usas para verificar usuarios

    @Value("${recaptcha.secret}")
    private String recaptchaSecret;

    @Value("${recaptcha.url}")
    private String recaptchaServerUrl;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Autowired
    @Lazy
    private RestTemplate restTemplate;

    @GetMapping("/registro")
    public ModelAndView mostrarFormularioRegistro(Model model) {
        ModelAndView mv = new ModelAndView();
        mv.getModel().put("usuarioRegistrado", new UsuarioRegistrado());
        mv.setViewName("registro");
        return mv;
    }

    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute("usuarioRegistrado") UsuarioRegistrado usuario, HttpServletRequest request,
                                   HttpServletResponse response, Model model) throws IOException {

        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");

        if (!verifyReCAPTCHA(gRecaptchaResponse)){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        usuarioService.registrarUsuario(usuario);
        return "redirect:/usuarios/registro?success";
    }

    private boolean verifyReCAPTCHA(String gRecaptchaResponse) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("secret", recaptchaSecret);
        map.add("response", gRecaptchaResponse);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        RecaptchaResponse response = restTemplate.postForObject(recaptchaServerUrl, request, RecaptchaResponse.class);

        System.out.println("Success: " + response.isSuccess());
        System.out.println("Hostname: " + response.getHostname());
        System.out.println("Challenge Timestamp: " + response.getChallenge_ts());

        if (response.getErrorCodes() != null) {
            for (String error : response.getErrorCodes()) {
                System.out.println("/t" + error);
            }
        }

        return response.isSuccess();

    }

    @GetMapping("/admin/usuarios")
    public String listarUsuarios(@RequestParam(required = false) String rol,
                                 @RequestParam(required = false) String nombre,
                                 @RequestParam(required = false) String validacion,
                                 @RequestParam(required = false) String orden,
                                 Model model) {
        List<UsuarioRegistrado> usuarios;

        if (rol != null && !rol.isEmpty() && nombre != null && !nombre.isEmpty()) {
            usuarios = usuarioService.filtrarPorRolYNombre(rol, nombre);
        } else if (rol != null && !rol.isEmpty()) {
            usuarios = usuarioService.filtrarPorRol(rol);
        } else if (nombre != null && !nombre.isEmpty()) {
            usuarios = usuarioService.buscarPorNombre(nombre);
        } else {
            usuarios = usuarioService.obtenerTodosLosUsuarios();
        }

        /*if (validacion != null && !validacion.isEmpty()) {
            boolean isValidacion = Boolean.parseBoolean(validacion);
            usuarios = usuarios.stream()
                    .filter(usuario -> usuario.isValidacion() == isValidacion)
                    .collect(Collectors.toList());
        }*/

        if ("fecha".equals(orden)) {
            usuarios.sort(Comparator.comparing(UsuarioRegistrado::getFechaRegistro));
        } else if ("nombre".equals(orden)) {
            usuarios.sort(Comparator.comparing(UsuarioRegistrado::getNombre));
        }

        model.addAttribute("usuarios", usuarios);
        return "admin/listaUsuarios";
    }

    @GetMapping("/admin/usuarios/{usuario}")
    public String verDetallesUsuario(@PathVariable String usuario, Model model) {
        UsuarioRegistrado usuarioRegistrado = usuarioService.obtenerUsuarioPorUsuario(usuario);
        model.addAttribute("usuario", usuarioRegistrado);
        return "admin/detalleUsuario";
    }

    @PostMapping("/admin/usuarios/validar")
    public String validarUsuarios(@RequestParam List<String> usuariosIds) {
        usuarioService.validarUsuarios(usuariosIds);
        return "redirect:/admin/usuarios?validacion=success";
    }


    @PostMapping("/admin/usuarios/validarRol")
    public String validarUsuariosRol(@RequestParam List<String> usuariosIds) {
        usuarioService.validarUsuariosRol(usuariosIds);
        return "redirect:/admin/usuarios?validacion=success";
    }

    @GetMapping("/equipos/responsables/crear")
    public ModelAndView mostrarFormularioNuevoResponsable(HttpServletRequest request) {
        log.debug("Entrando a mostrarFormularioNuevoResponsable");
        ModelAndView mv = new ModelAndView("equipos/crearResponsable");

        try {
            UsuarioRegistrado usuarioActual = (UsuarioRegistrado) request.getSession().getAttribute("usuario");
            // Verificar usuario actual
            usuarioActual = autentificacionService.checkUser(usuarioActual.getUsuario());

            if (usuarioActual == null || usuarioActual.getRol() != Rol.JEFE_DE_EQUIPO || usuarioActual.getEquipo() == null) {
                mv.setViewName("error");
                mv.addObject("mensajeError", "No tienes permisos para crear responsables de equipo.");
                return mv;
            }


            UsuarioRegistrado nuevoResponsable = new UsuarioRegistrado();
            nuevoResponsable.setContrasena("temporal123");
            nuevoResponsable.setRol(Rol.JEFE_DE_EQUIPO); // Asigna un rol predeterminado

            mv.addObject("nuevoResponsable", nuevoResponsable);
            mv.addObject("usuario", usuarioActual); // Para usar en la vista si es necesario

        } catch (Exception e) {
            log.error("Error al mostrar formulario de nuevo responsable: ", e);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Error al cargar el formulario.");
        }

        return mv;
    }

    @PostMapping("/equipos/responsables/crear")
    public ModelAndView crearNuevoResponsable(
            @ModelAttribute("nuevoResponsable") @Valid UsuarioRegistrado nuevoResponsable,
            BindingResult result,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        log.debug("Iniciando creación de nuevo responsable");
        ModelAndView mv = new ModelAndView("equipos/crearResponsable");

        try {
            UsuarioRegistrado creador = (UsuarioRegistrado) request.getSession().getAttribute("usuario");
            creador = autentificacionService.checkUser(creador.getUsuario());

            // Validación del usuario creador
            if (creador == null || creador.getRol() != Rol.JEFE_DE_EQUIPO || creador.getEquipo() == null) {
                mv.setViewName("error");
                mv.addObject("mensajeError", "No tienes permisos para crear responsables de equipo.");
                return mv;
            }

            // Si hay errores de validación
            if (result.hasErrors()) {
                mv.addObject("error", "Por favor, corrija los errores en el formulario.");
                return mv;
            }

            // Crear el nuevo responsable
            UsuarioRegistrado responsableCreado = usuarioService.crearResponsableEquipo(creador, nuevoResponsable);
            log.info("Responsable creado exitosamente: {}", responsableCreado.getUsuario());

            // Redireccionar con mensaje de éxito
            redirectAttributes.addFlashAttribute("mensaje",
                    "Responsable creado exitosamente. Contraseña temporal: " + responsableCreado.getContrasena());
            return new ModelAndView("redirect:/equipos/" + creador.getEquipo().getId());

        } catch (IllegalStateException e) {
            log.error("Error al crear responsable: {}", e.getMessage());
            mv.addObject("error", e.getMessage());
            return mv;
        } catch (Exception e) {
            log.error("Error inesperado al crear responsable: ", e);
            mv.setViewName("error");
            mv.addObject("mensajeError", "Error inesperado al crear el responsable.");
            return mv;
        }
    }

    @GetMapping("/cambiarContrasena")
    public String mostrarFormularioCambioContrasena(Model model, HttpServletRequest request) {
        UsuarioRegistrado usuarioActual = (UsuarioRegistrado) request.getSession().getAttribute("usuario");
        if (usuarioActual == null) {
            return "redirect:/login"; // Redirigir al login si el usuario no está autenticado
        }
        model.addAttribute("usuario", usuarioActual);
        return "cambiarContrasena"; // Vista Thymeleaf para el formulario
    }

    @PostMapping("/cambiarContrasena")
    public String cambiarContrasena(@RequestParam("contrasenaActual") String contrasenaActual,
                                    @RequestParam("nuevaContrasena") String nuevaContrasena,
                                    @RequestParam("confirmarContrasena") String confirmarContrasena,
                                    HttpServletRequest request,
                                    RedirectAttributes redirectAttributes) {
        UsuarioRegistrado usuarioActual = (UsuarioRegistrado) request.getSession().getAttribute("usuario");

        if (usuarioActual == null) {
            return "redirect:/login"; // Redirigir al login si el usuario no está autenticado
        }

        try {
            // Llamar al servicio para cambiar la contraseña
            usuarioService.cambiarContrasena(usuarioActual.getUsuario(), contrasenaActual, nuevaContrasena, confirmarContrasena);

            // Añadir mensaje de éxito
            redirectAttributes.addFlashAttribute("mensaje", "Contraseña actualizada correctamente.");
            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            // Añadir mensaje de error en caso de validaciones fallidas
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cambiarContrasena?error";
        } catch (Exception e) {
            // Manejo de errores generales
            redirectAttributes.addFlashAttribute("error", "Error inesperado al cambiar la contraseña.");
            return "redirect:/cambiarContrasena?error";
        }
    }


}
