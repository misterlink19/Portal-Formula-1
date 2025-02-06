package com.portal.formula1.service;

import com.portal.formula1.model.Encuesta;
import com.portal.formula1.model.EncuestaArchivada;
import com.portal.formula1.model.Voto;
import com.portal.formula1.repository.EncuestaArchivadaDAO;
import com.portal.formula1.repository.EncuestaDAO;
import com.portal.formula1.repository.PilotoDAO;
import com.portal.formula1.repository.VotoDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;

/**
 *
 * @author Misterlink
 */
@Service
public class VotoService {

    @Autowired
    private VotoDAO votoDAO;

    @Autowired
    private PilotoDAO pilotoDAO;

    @Autowired
    private EncuestaDAO encuestaDAO;

    @Autowired
    private EncuestaArchivadaDAO encuestaArchivadaDAO;

    public Voto crearVoto(Voto voto) {
        return votoDAO.save(voto);
    }


    public List<Voto> obtenerVotosPorEncuesta(Encuesta encuesta) {
        return votoDAO.findByEncuesta(encuesta);
    }

    public boolean haVotadoAntes(String correoVotante, Encuesta encuesta) {
        return votoDAO.existsByCorreoVotanteAndEncuesta(correoVotante, encuesta);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getRankingVotacion(String permalink) {
        Encuesta encuesta = encuestaDAO.findById(permalink).orElse(null);
        EncuestaArchivada encuestaArchivada = encuestaArchivadaDAO.findByPermalink(permalink).orElse(null);

        if (encuesta == null && encuestaArchivada == null) {
            throw new NoSuchElementException("Encuesta no encontrada");
        }

        // Obtener los votos de la encuesta original (aunque esté archivada, sigue existiendo)
        List<Voto> votos = votoDAO.findByEncuesta(encuesta);

        // Mapear votos por piloto usando su dorsal como clave
        Map<String, Long> votosPorPiloto = votos.stream()
                .collect(Collectors.groupingBy(Voto::getOpcionSeleccionada, Collectors.counting()));

        // Determinar la lista de pilotos desde EncuestaArchivada
        List<Object[]> ranking;

        if (encuestaArchivada != null) {
            // Usar los pilotos archivados para reconstruir el ranking
            ranking = encuestaArchivada.getPilotosArchivados().stream()
                    .map(piloto -> new Object[]{
                            piloto.getNombre(),
                            piloto.getApellidos(),
                            piloto.getSiglas(),
                            piloto.getDorsal(),
                            piloto.getRutaImagen(),
                            piloto.getPais(),
                            piloto.getTwitter(),
                            votosPorPiloto.getOrDefault(piloto.getDorsal().toString(), 0L), // Asignar votos por dorsal
                            piloto.getEquipo()
                    })
                    .sorted((a, b) -> Long.compare((Long) b[7], (Long) a[7])) // Ordenar por votos
                    .collect(Collectors.toList());
        } else {
            ranking = Collections.emptyList();
        }
        return ranking;
    }
}
