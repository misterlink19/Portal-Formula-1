package com.portal.formula1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
public class Equipo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "logo")
    private String logo;

    @Column(name = "twitter")
    private String twitter;

    @OneToMany(mappedBy = "equipo", cascade = CascadeType.ALL)
    private List<UsuarioRegistrado> responsables;
}
