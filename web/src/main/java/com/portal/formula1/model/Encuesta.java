/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.portal.formula1.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @author Misterlink
 */

@Entity
@Table(name = "encuesta")
public class Encuesta {

    @Id
    @Column(length = 36, unique = true)
    private String permalink;

    @Column(name = "titulo_encuesta")
    private String titulo;

    @Column(name = "descripcion_encuesta")
    private String descripcion;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_limite")
    private LocalDateTime fechaLimite;

    @OneToMany(mappedBy = "encuesta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Voto> votos;


    @ManyToMany
    @JoinTable(
            name = "encuesta_piloto",
            joinColumns = @JoinColumn(name = "encuesta_id"),
            inverseJoinColumns = @JoinColumn(name = "piloto_id")
    )
    private Set<Piloto> pilotos = new HashSet<>();

    // Para que genere el codigo permalink antes de registrarlo en la base datos
    @PrePersist
    public void prePersist() {
        if (this.permalink == null) {
            this.permalink = UUID.randomUUID().toString();
        }
    }


    public Encuesta() {
    }

    public Encuesta(String titulo, String descripcion, LocalDateTime fechaInicio, LocalDateTime fechaLimite) {
        this.permalink = UUID.randomUUID().toString();
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaLimite = fechaLimite;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDateTime fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public List<Voto> getVotos() {
        return votos;
    }

    public void setVotos(List<Voto> votos) {
        this.votos = votos;
    }

    public Set<Piloto> getPilotos() {return pilotos;}

    public void setPilotos(Set<Piloto> pilotos) { this.pilotos = pilotos;}
}