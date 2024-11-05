/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.portal.formula1.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Misterlink
 */

@Entity
@Table(name = "Encuesta")
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

//    @Transient //Para que no se almacene en la base de datos por ahora
//    private final List<String> pilotos = Arrays.asList(
//            "Carlos Sainz",
//            "Charles Leclerc",
//            "Lando Norris",
//            "George Russell",
//            "Lewis Hamilton",
//            "Max Verstappen",
//            "Sergio Perez");

    @OneToMany(mappedBy = "encuesta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Voto> votos;

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
}