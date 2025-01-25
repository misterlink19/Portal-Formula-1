package com.portal.formula1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "calendario_evento")
public class CalendarioEvento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha; // Usamos LocalDate para manejar la fecha

    @Column(name = "nombre_evento", nullable = false, length = 255)
    private String nombreEvento;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "circuito_id", nullable = true)
    private CircuitoPlaceholder  circuito; // Relación opcional con el circuito
}