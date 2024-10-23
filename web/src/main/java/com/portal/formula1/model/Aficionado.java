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
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "aficionados")
public class Aficionado {
    
    @Id
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Basic(optional = false)
    @Column(name = "nombre")
    @NotBlank(message = "El nombre no puede estar vacío.")
    private String nombre;

    @Basic(optional = false)
    @Column(name = "usuario")
    @NotBlank(message = "El usuario no puede estar vacío.")
    private String usuario;

    @Basic(optional = false)
    @Column(name = "contraseña")
    @NotBlank(message = "La contraseña no puede estar vacía.")
    @Size(min = 5, message = "La contraseña debe tener al menos 5 caracteres.")
    private String contraseña;

    @Basic(optional = false)
    @Column(name = "rol")
    @NotBlank(message = "El rol no puede estar vacío.")
    private String rol;

    public String getEmail() {
        return email;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getContraseña() {
        return contraseña;
    }

    public String getRol() {
        return rol;
    }
    
}
