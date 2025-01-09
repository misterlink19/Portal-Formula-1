package com.portal.formula1.service;

import com.portal.formula1.model.Encuesta;
import com.portal.formula1.model.Voto;
import com.portal.formula1.repository.EncuestaDAO;
import com.portal.formula1.repository.PilotoDAO;
import com.portal.formula1.repository.VotoDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;
import java.util.NoSuchElementException;
import java.util.Map;
import java.util.HashMap;

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
        Encuesta encuesta = encuestaDAO.findById(permalink).orElseThrow(() -> new NoSuchElementException("Encuesta no encontrada"));

        // Mapear votos por piloto utilizando String como clave porque el dorsal es un String
        Map<String, Long> votosPorPiloto = votoDAO.findByEncuesta(encuesta).stream()
                .collect(Collectors.groupingBy(Voto::getOpcionSeleccionada, Collectors.counting()));

        // Crear ranking basado en los pilotos y sus votos
        List<Object[]> ranking = encuesta.getPilotos().stream()
                .map(piloto -> new Object[]{
                        piloto.getNombre(),
                        piloto.getApellidos(),
                        piloto.getSiglas(),
                        piloto.getDorsal(),
                        piloto.getRutaImagen(),
                        piloto.getPais(),
                        piloto.getTwitter(),
                        votosPorPiloto.getOrDefault(piloto.getDorsal().toString(), 0L),
                        piloto.getEquipo().getNombre()
                })
                .sorted((a, b) -> Long.compare((Long) b[7], (Long) a[7]))
                .collect(Collectors.toList());

        return ranking;
    }
}
