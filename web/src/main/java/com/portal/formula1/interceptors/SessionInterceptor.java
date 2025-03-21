/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.portal.formula1.interceptors;

import com.portal.formula1.model.Rol;
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
        routeRoles.put("/admin",Rol.ADMIN);
        routeRoles.put("/noticias/crear",Rol.ADMIN);
        routeRoles.put("/noticias/eliminar",Rol.ADMIN);
    }

    private final Map<String, Rol> routeRoles = new HashMap<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        UsuarioRegistrado user = (UsuarioRegistrado) request.getSession().getAttribute("usuario");
        if (user == null) {
            response.sendRedirect("/");
            return false;
        }
        Rol userRole = user.getRol();
        for (Map.Entry<String, Rol> entry : routeRoles.entrySet()) {
            String path = entry.getKey();
            Rol requiredRole = entry.getValue();
        
            if (request.getRequestURI().startsWith(path) && !requiredRole.equals(userRole)) {
                response.sendRedirect("/accesoDenegado.html");
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
