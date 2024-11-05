package com.portal.formula1.model;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table (name = "noticias")
public class Noticia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;
    
    @Basic(optional = false)
    @Column(name = "permalink", unique = true)
    @NotBlank(message = "El permalink es obligatorio.")
    @Size(max = 100, message = "El permalink no puede superar 100 caracteres")
    private String permalink;
    
    @Basic(optional = false)
    @Column(name = "titulo")
    @NotBlank(message = "Tiene que haber un titulo")
    @Size(max = 100, message = "El titulo no puede superar 100 caracteres")
    private String titulo;
    
    @Basic(optional = false)
    @Column(name = "imagen")
    private String imagen;
    
    @Basic(optional = false)
    @Column(name = "texto")
    @NotBlank(message = "Tiene que haber una descripción")
    @Size(min = 500, max = 2000, message = "La descripción debe tener entre 500 y 2000 caracteres")
    private String texto;
    
    /*
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario creador;
    */

    private LocalDateTime fechaPublicacion;
    
    public Noticia() {
    } 
    
    public Noticia(Integer id, String permalink, String titulo, String imagen, String texto) {
        this.id = id;
        this.permalink = permalink;
        this.titulo = titulo;
        this.imagen = imagen;
        this.texto = texto;
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    @Override
    public String toString() {
        return "Noticia{" + "id=" + id + ", permalink=" + permalink + ", titulo=" + titulo + ", imagen=" + imagen + ", texto=" + texto + '}';
    }
    
    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDateTime fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }
}
