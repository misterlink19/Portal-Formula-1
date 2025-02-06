package com.portal.formula1.model;

import jakarta.persistence.*;

@Entity
public class ConsultaERS{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consulta_ers", nullable = false, unique = true)
    private Long idConsultaERS;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_coche", nullable = false)
    private Coches coche;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_circuito", nullable = false)
    private Circuito circuito;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_equipo", nullable = false)
    private Equipo equipo;

    @Column(name = "ahorrador_unitario", nullable = false)
    private Double ahorradorUnitario;

    @Column(name = "ahorrador_total", nullable = false)
    private Double ahorradorTotal;

    @Column(name = "normal_unitario", nullable = false)
    private Double normalUnitario;

    @Column(name = "normal_total", nullable = false)
    private Double normalTotal;

    @Column(name = "deportivo_unitario", nullable = false)
    private Double deportivoUnitario;

    @Column(name = "deportivo_total", nullable = false)
    private Double deportivoTotal;

    // Constructores
    public ConsultaERS() {}

    public ConsultaERS(Coches coche, Circuito circuito, Equipo equipo, Double ahorradorUnitario, Double ahorradorTotal,
                       Double normalUnitario, Double normalTotal, Double deportivoUnitario, Double deportivoTotal) {
        this.coche = coche;
        this.circuito = circuito;
        this.equipo = equipo;
        this.ahorradorUnitario = ahorradorUnitario;
        this.ahorradorTotal = ahorradorTotal;
        this.normalUnitario = normalUnitario;
        this.normalTotal = normalTotal;
        this.deportivoUnitario = deportivoUnitario;
        this.deportivoTotal = deportivoTotal;
    }

    // Getters y Setters
    public Long getIdConsultaERS() {
        return idConsultaERS;
    }

    public void setIdConsultaERS(Long idConsultaERS) {
        this.idConsultaERS = idConsultaERS;
    }

    public Coches getCoche() {
        return coche;
    }

    public void setCoche(Coches coche) {
        this.coche = coche;
    }

    public Circuito getCircuito() {
        return circuito;
    }

    public void setCircuito(Circuito circuito) {
        this.circuito = circuito;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    public Double getAhorradorUnitario() {
        return ahorradorUnitario;
    }

    public void setAhorradorUnitario(Double ahorradorUnitario) {
        this.ahorradorUnitario = ahorradorUnitario;
    }

    public Double getAhorradorTotal() {
        return ahorradorTotal;
    }

    public void setAhorradorTotal(Double ahorradorTotal) {
        this.ahorradorTotal = ahorradorTotal;
    }

    public Double getNormalUnitario() {
        return normalUnitario;
    }

    public void setNormalUnitario(Double normalUnitario) {
        this.normalUnitario = normalUnitario;
    }

    public Double getNormalTotal() {
        return normalTotal;
    }

    public void setNormalTotal(Double normalTotal) {
        this.normalTotal = normalTotal;
    }

    public Double getDeportivoUnitario() {
        return deportivoUnitario;
    }

    public void setDeportivoUnitario(Double deportivoUnitario) {
        this.deportivoUnitario = deportivoUnitario;
    }

    public Double getDeportivoTotal() {
        return deportivoTotal;
    }

    public void setDeportivoTotal(Double deportivoTotal) {
        this.deportivoTotal = deportivoTotal;
    }
}
