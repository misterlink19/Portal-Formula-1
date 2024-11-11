/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.portal.formula1.interceptors;

import com.portal.formula1.model.UsuarioRegistrado;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author fjavi
 */
@Component
public class SessionInterceptor  implements HandlerInterceptor {
    
    public SessionInterceptor(){
        routeRoles.put("/admin","ADMIN" );
    }
    private final Map<String, String> routeRoles = new HashMap<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        Quitar de comentario si quieren probrar las funcionalidades del apartado de encuestas
        String requestURI = request.getRequestURI();
        // Permitir acceso a las rutas de encuestas sin autenticación
        if (requestURI.startsWith("/encuestas")) {
            return true;
        }


        UsuarioRegistrado user = (UsuarioRegistrado) request.getSession().getAttribute("usuario");
        if (user == null) {
            response.sendRedirect("/");
            return false;
        }
        String userRole = user.getRol();
        for (Map.Entry<String, String> entry : routeRoles.entrySet()) {
            String path = entry.getKey();
            String requiredRole = entry.getValue();
        
            if (request.getRequestURI().startsWith(path) && !requiredRole.equals(userRole)) {
                response.sendRedirect("/accesoDenegado.jsp");
                return false;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // Aquí puedes agregar lógica después de que la vista haya sido renderizada
    }
}
