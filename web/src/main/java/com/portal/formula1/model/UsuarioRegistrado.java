/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.portal.formula1.model;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 *
 * @author fjavi
 */
@Entity
@Table(name = "usuarios_registrados")
public class UsuarioRegistrado implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "usuario", unique = true, nullable = false)
    private String usuario;

    @Basic(optional = false)
    @Column(name = "nombre")
    @NotBlank(message = "El nombre no puede estar vacío.")
    private String nombre;

    @Basic(optional = false)
    @Column(name = "email")
    @NotBlank(message = "El email no puede estar vacío.")
    private String email;

    @Basic(optional = false)
    @Column(name = "contrasena")
    @NotBlank(message = "La contraseña no puede estar vacía.")
    private String contrasena;

    @Basic(optional = false)
    @Column(name = "rol")
    @NotBlank(message = "El campo rol no puede estar vacío.")
    private String rol;

    public String getUsuario() {
        return usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public String getRol() {
        return rol;
    }
}
