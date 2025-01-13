package com.portal.formula1.model;

import jakarta.persistence.*;

@Entity
public class Coches {

    @Id
    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "ers_curva_lenta", nullable = false)
    private Double ersCurvaLenta;

    @Column(name = "ers_curva_media", nullable = false)
    private Double ersCurvaMedia;

    @Column(name = "ers_curva_rapida", nullable = false)
    private Double ersCurvaRapida;

    @Column(name = "consumo", nullable = false)
    private Double consumo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_equipo", nullable = false)
    private Equipo equipo;

    // Constructores
    public Coches() {}

    public Coches(String codigo, String nombre, Double ersCurvaLenta, Double ersCurvaMedia, Double ersCurvaRapida, Double consumo, Equipo equipo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.ersCurvaLenta = ersCurvaLenta;
        this.ersCurvaMedia = ersCurvaMedia;
        this.ersCurvaRapida = ersCurvaRapida;
        this.consumo = consumo;
        this.equipo = equipo;
    }

    // Getters y Setters
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getErsCurvaLenta() {
        return ersCurvaLenta;
    }

    public void setErsCurvaLenta(Double ersCurvaLenta) {
        this.ersCurvaLenta = ersCurvaLenta;
    }

    public Double getErsCurvaMedia() {
        return ersCurvaMedia;
    }

    public void setErsCurvaMedia(Double ersCurvaMedia) {
        this.ersCurvaMedia = ersCurvaMedia;
    }

    public Double getErsCurvaRapida() {
        return ersCurvaRapida;
    }

    public void setErsCurvaRapida(Double ersCurvaRapida) {
        this.ersCurvaRapida = ersCurvaRapida;
    }

    public Double getConsumo() {
        return consumo;
    }

    public void setConsumo(Double consumo) {
        this.consumo = consumo;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    @Override
    public String toString() {
        return "Coche{" +
                "codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", ersCurvaLenta=" + ersCurvaLenta +
                ", ersCurvaMedia=" + ersCurvaMedia +
                ", ersCurvaRapida=" + ersCurvaRapida +
                ", consumo=" + consumo +
                '}';
    }
}
