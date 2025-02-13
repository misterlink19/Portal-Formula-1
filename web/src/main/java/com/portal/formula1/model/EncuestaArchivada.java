package com.portal.formula1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class EncuestaArchivada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String descripcion;
    private String permalink;
    private LocalDateTime fechaArchivado;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "encuesta_archivada_id")
    private List<PilotoArchivado> pilotosArchivados = new ArrayList<>();


    public EncuestaArchivada() {}

    public EncuestaArchivada(Encuesta encuesta) {
        this.titulo = encuesta.getTitulo();
        this.descripcion = encuesta.getDescripcion();
        this.permalink = encuesta.getPermalink();
        this.fechaArchivado = LocalDateTime.now();

        for (Piloto piloto : encuesta.getPilotos()) {
            this.pilotosArchivados.add(new PilotoArchivado(piloto));
        }
    }
}
