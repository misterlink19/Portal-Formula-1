package com.portal.formula1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios_registrados")
public class UsuarioRegistrado implements Serializable {

    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    @Id
    @Column(name = "usuario", unique = true, nullable = false)
    private String usuario;

    @Getter
    @Setter
    @Column(name = "nombre", nullable = false)
    @NotBlank(message = "El nombre no puede estar vacío.")
    private String nombre;

    @Getter
    @Setter
    @Column(name = "email", nullable = false)
    @NotBlank(message = "El email no puede estar vacío.")
    private String email;

    @Getter
    @Setter
    @Column(name = "contrasena", nullable = false)
    @NotBlank(message = "La contraseña no puede estar vacía.")
    private String contrasena;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    @NotNull(message = "El campo rol no puede ser nulo.")
    private Rol rol;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "rol_solicitado", nullable = true)
    private Rol rolSolicitado;

    @Getter
    @Setter
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime fechaRegistro;


    @Getter
    @Setter
    @Column(name = "validacion", nullable = false)
    private Boolean validacion = false;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipo_id")
    private Equipo equipo;

    public UsuarioRegistrado() {
        this.validacion = false;
        this.fechaRegistro = LocalDateTime.now();
    }

    public boolean isValidacion() {
        return this.validacion;
    }
}
