package com.portal.formula1.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class ConsultaERS {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consulta_ers", nullable = false, unique = true)
    private Long idConsultaERS;

    @Column(name = "id_circuito", nullable = false)
    private Long idCircuito;

    @Column(name = "codigo_coche", nullable = false, length = 50)
    private String codigoCoche;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaConsulta;

    @PrePersist
    protected void onCreate() {
        this.fechaConsulta = LocalDate.now();
    }

    @Column(name = "nombre_circuito", nullable = false, length = 100)
    private String nombreCircuito;

    @Column(name = "nombre_coche", nullable = false, length = 100)
    private String nombreCoche;

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

    public ConsultaERS(LocalDate fechaConsulta, Long idCircuito, String codigoCoche, String nombreCircuito, String nombreCoche, Equipo equipo,
                       Double ahorradorUnitario, Double ahorradorTotal, Double normalUnitario, Double normalTotal,
                       Double deportivoUnitario, Double deportivoTotal) {
        this.fechaConsulta = fechaConsulta;
        this.idCircuito = idCircuito;
        this.codigoCoche = codigoCoche;
        this.nombreCircuito = nombreCircuito;
        this.nombreCoche = nombreCoche;
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

    public Long getIdCircuito() {
        return idCircuito;
    }

    public void setIdCircuito(Long idCircuito) {
        this.idCircuito = idCircuito;
    }

    public String getCodigoCoche() {
        return codigoCoche;
    }

    public void setCodigoCoche(String codigoCoche) {
        this.codigoCoche = codigoCoche;
    }

    public String getNombreCircuito() {
        return nombreCircuito;
    }

    public void setNombreCircuito(String nombreCircuito) {
        this.nombreCircuito = nombreCircuito;
    }

    public String getNombreCoche() {
        return nombreCoche;
    }

    public void setNombreCoche(String nombreCoche) {
        this.nombreCoche = nombreCoche;
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

    public LocalDate getFechaConsulta() {
        return fechaConsulta;
    }

    public void setFechaConsulta(LocalDate fechaConsulta) {
        this.fechaConsulta = fechaConsulta;
    }
}
