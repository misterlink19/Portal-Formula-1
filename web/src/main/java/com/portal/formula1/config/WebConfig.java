/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.portal.formula1.config;


import com.portal.formula1.interceptors.SessionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.HiddenHttpMethodFilter;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private SessionInterceptor sessionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/")
                .excludePathPatterns("/login")
                .excludePathPatterns("/calendario/evento/**")
                .excludePathPatterns("/encuestas/**")
                .excludePathPatterns("/noticias/**")
                .excludePathPatterns("/styles/**")
                .excludePathPatterns("/javascript/**")
                .excludePathPatterns("/images/**")
                .excludePathPatterns("/favicon.ico")
                .excludePathPatterns("/votos/**")
                .excludePathPatterns("/registro/**")
                .excludePathPatterns("/uploads/**");
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/")
                .addResourceLocations("file:src/main/resources/static/uploads/"); // Con esto carga las imagenes que estan en uploads dentro de static;
    }
    // Agrega este Bean para manejar métodos HTTP ocultos
    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }
}
