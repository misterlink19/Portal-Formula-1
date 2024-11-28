package com.portal.formula1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Setter
@Getter
@Entity
public class Equipo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del equipo es obligatorio")
    @Size(max = 255, message = "El nombre del equipo no puede tener más de 255 caracteres")
    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;

    @Column(name = "logo")
    private String logo;

    @Size(max = 255, message = "El Twitter del equipo no puede tener más de 255 caracteres")
    @Column(name = "twitter")
    private String twitter;

    @OneToMany(mappedBy = "equipo", cascade = CascadeType.ALL)
    private List<UsuarioRegistrado> responsables = new ArrayList<>();
}

