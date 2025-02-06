package com.portal.formula1.model;

import jakarta.persistence.*;

@Entity
public class ConsultaCombustible {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consulta", nullable = false, unique = true)
    private Long idConsulta;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_coche", nullable = false)
    private Coches coche;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_circuito", nullable = false)
    private Circuito circuito;

    @Column(name = "consumo_vuelta", nullable = false)
    private Double consumoVuelta;

    @Column(name = "consumo_total", nullable = false)
    private Double consumoTotal;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_equipo", nullable = false)
    private Equipo equipo;

    // Constructores
    public ConsultaCombustible() {}

    public ConsultaCombustible(Coches coche, Circuito circuito, Double consumoVuelta, Double consumoTotal, Equipo equipo) {
        this.coche = coche;
        this.circuito = circuito;
        this.consumoVuelta = consumoVuelta;
        this.consumoTotal = consumoTotal;
        this.equipo = equipo;
    }

    // Getters y Setters
    public Long getIdConsulta() {
        return idConsulta;
    }

    public void setIdConsulta(Long idConsulta) {
        this.idConsulta = idConsulta;
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

    public Double getConsumoVuelta() {
        return consumoVuelta;
    }

    public void setConsumoVuelta(Double consumoVuelta) {
        this.consumoVuelta = consumoVuelta;
    }

    public Double getConsumoTotal() {
        return consumoTotal;
    }

    public void setConsumoTotal(Double consumoTotal) {
        this.consumoTotal = consumoTotal;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }
}
