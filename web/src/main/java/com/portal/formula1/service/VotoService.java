package com.portal.formula1.service;

import com.portal.formula1.model.Encuesta;
import com.portal.formula1.model.Voto;
import com.portal.formula1.repository.VotoDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 *
 * @author Misterlink
 */
@Service
public class VotoService {

    @Autowired
    private VotoDAO votoDAO;

    @PersistenceContext //Para manejar pilotos directamente de la base de datos
    private EntityManager entityManager;

    public Voto crearVoto(Voto voto) {
        return votoDAO.save(voto);
    }


    public List<Voto> obtenerVotosPorEncuesta(Encuesta encuesta) {
        return votoDAO.findByEncuesta(encuesta);
    }

    public boolean haVotadoAntes(String correoVotante, Encuesta encuesta) {
        return votoDAO.existsByCorreoVotanteAndEncuesta(correoVotante, encuesta);
    }

    public List<Object> getRankingVotacion(String permalink) {
        if (entityManager == null) {
            throw new IllegalStateException("EntityManager is null");
        }

        String sql = """
                SELECT 
                    p.nombre, 
                    p.apellidos, 
                    p.siglas, 
                    p.dorsal, 
                    p.rutaImagen, 
                    p.pais, 
                    p.twitter, 
                    COUNT(v.id) AS votos
                FROM 
                    piloto p
                LEFT JOIN 
                    votos v 
                ON 
                    p.dorsal = v.opcion_seleccionada
                WHERE 
                    v.encuesta_permalink = ?
                GROUP BY 
                    p.nombre, p.apellidos, p.siglas, p.dorsal, p.rutaImagen, p.pais, p.twitter
                ORDER BY 
                    votos DESC
            """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, permalink);

        if (query == null) {
            throw new IllegalStateException("Query is null");
        }

        return query.getResultList();
    }
}
