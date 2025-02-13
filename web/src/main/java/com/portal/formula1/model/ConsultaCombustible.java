package com.portal.formula1.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class ConsultaCombustible {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consulta", nullable = false, unique = true)
    private Long idConsulta;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaConsulta;

    @PrePersist
    protected void onCreate() {
        this.fechaConsulta = LocalDate.now();
    }

    @Column(name = "codigo_coche", nullable = false, length = 50)
    private String codigoCoche;

    @Column(name = "id_circuito", nullable = false)
    private Long idCircuito;

    @Column(name = "nombre_coche", nullable = false, length = 100)
    private String nombreCoche;

    @Column(name = "nombre_circuito", nullable = false, length = 100)
    private String nombreCircuito;

    @Column(name = "consumo_vuelta", nullable = false)
    private Double consumoVuelta;

    @Column(name = "consumo_total", nullable = false)
    private Double consumoTotal;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_equipo", nullable = false)
    private Equipo equipo;

    // Constructores
    public ConsultaCombustible() {}

    public ConsultaCombustible(LocalDate fechaConsulta, String codigoCoche, Long idCircuito, String nombreCoche, String nombreCircuito, Double consumoVuelta, Double consumoTotal, Equipo equipo) {
        this.fechaConsulta = fechaConsulta;
        this.codigoCoche = codigoCoche;
        this.idCircuito = idCircuito;
        this.nombreCoche = nombreCoche;
        this.nombreCircuito = nombreCircuito;
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

    public LocalDate getFechaConsulta() {
        return fechaConsulta;
    }

    public void setFechaConsulta(LocalDate fechaConsulta) {
        this.fechaConsulta = fechaConsulta;
    }

    public String getCodigoCoche() {
        return codigoCoche;
    }

    public void setCodigoCoche(String codigoCoche) {
        this.codigoCoche = codigoCoche;
    }

    public Long getIdCircuito() {
        return idCircuito;
    }

    public void setIdCircuito(Long idCircuito) {
        this.idCircuito = idCircuito;
    }

    public String getNombreCoche() {
        return nombreCoche;
    }

    public void setNombreCoche(String nombreCoche) {
        this.nombreCoche = nombreCoche;
    }

    public String getNombreCircuito() {
        return nombreCircuito;
    }

    public void setNombreCircuito(String nombreCircuito) {
        this.nombreCircuito = nombreCircuito;
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
