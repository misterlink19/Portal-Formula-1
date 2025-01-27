package com.portal.formula1.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Circuito {

    // Getters y setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String ciudad;
    private String pais;
    private String trazado;
    private int numeroVueltas;
    private double longitud; //metros
    private int numeroCurvasLentas;
    private int numeroCurvasMedia;
    private int numeroCurvasRapidas;

    public Circuito() {
    }

    // Constructor con todos los atributos
    public Circuito(String nombre, String ciudad, String pais, String trazado, int numeroVueltas, double longitud, int numeroCurvasLentas, int numeroCurvasMedia, int numeroCurvasRapidas) {
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.pais = pais;
        this.trazado = trazado;
        this.numeroVueltas = numeroVueltas;
        this.longitud = longitud;
        this.numeroCurvasLentas = numeroCurvasLentas;
        this.numeroCurvasMedia = numeroCurvasMedia;
        this.numeroCurvasRapidas = numeroCurvasRapidas;
    }
}
