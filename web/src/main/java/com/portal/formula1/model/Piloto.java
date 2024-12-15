package com.portal.formula1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Entity
@Table(name = "piloto")
public class Piloto implements Serializable {

    @Id
    @Column(name = "dorsal", nullable = false)
    private Integer dorsal;

    @Column(name = "Nombre", nullable = false, length = 125)
    private String nombre;

    @Column(name = "apellidos", nullable = false, length = 255)
    private String apellidos;

    @Column(name = "siglas", nullable = false, length = 3)
    private String siglas;

    @Column(name = "ruta_imagen", nullable = false, length = 255)
    private String rutaImagen;

    @Column(name = "pais", nullable = false, length = 255)
    private String pais;

    @Column(name = "twitter", nullable = false, length = 255)
    private String twitter;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipo")
    private Equipo equipo;

    // Getters and Setters

    public void setDorsal(Integer dorsal) {
        this.dorsal = dorsal;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public void setSiglas(String siglas) {
        this.siglas = siglas;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }
}
