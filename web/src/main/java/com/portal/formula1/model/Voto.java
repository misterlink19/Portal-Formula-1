/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.portal.formula1.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 *
 * @author Misterlink
 */

@Entity
@Table(name = "Votos")
public class Voto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombreVotante;
    private String correoVotante;
    private String opcionSeleccionada;
    
    @ManyToOne
    @JoinColumn(name = "encuesta_permalink",nullable = false)
    private Encuesta encuesta;

    public Voto() {
    }

    public Voto(String nombreVotante, String correoVotante, String opcionSeleccionada, Encuesta encuesta) {
        this.nombreVotante = nombreVotante;
        this.correoVotante = correoVotante;
        this.opcionSeleccionada = opcionSeleccionada;
        this.encuesta = encuesta;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreVotante() {
        return nombreVotante;
    }

    public void setNombreVotante(String nombreVotante) {
        this.nombreVotante = nombreVotante;
    }

    public String getCorreoVotante() {
        return correoVotante;
    }

    public void setCorreoVotante(String correoVotante) {
        this.correoVotante = correoVotante;
    }

    public String getOpcionSeleccionada() {
        return opcionSeleccionada;
    }

    public void setOpcionSeleccionada(String opcionSeleccionada) {
        this.opcionSeleccionada = opcionSeleccionada;
    }

    public Encuesta getEncuesta() {
        return encuesta;
    }

    public void setEncuesta(Encuesta encuesta) {
        this.encuesta = encuesta;
    }
    
    
}
