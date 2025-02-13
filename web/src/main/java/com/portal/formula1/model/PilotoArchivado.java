package com.portal.formula1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class PilotoArchivado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer dorsal;
    private String nombre;
    private String apellidos;
    private String siglas;
    private String rutaImagen;
    private String pais;
    private String twitter;
    private String equipo;

    public PilotoArchivado() {}

    public PilotoArchivado(Piloto piloto) {
        this.dorsal = piloto.getDorsal();
        this.nombre = piloto.getNombre();
        this.apellidos = piloto.getApellidos();
        this.siglas = piloto.getSiglas();
        this.rutaImagen = piloto.getRutaImagen();
        this.pais = piloto.getPais();
        this.twitter = piloto.getTwitter();
        this.equipo = piloto.getEquipo().getNombre();
    }
}
