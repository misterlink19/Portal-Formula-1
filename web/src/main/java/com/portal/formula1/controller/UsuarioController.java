package com.portal.formula1.controller;

import com.portal.formula1.model.UsuarioRegistrado;
import com.portal.formula1.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

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
}
