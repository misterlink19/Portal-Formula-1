package com.portal.formula1.service;

import com.portal.formula1.model.Encuesta;
import com.portal.formula1.model.Voto;
import com.portal.formula1.repository.VotoDAO;
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

    public Voto crearVoto(Voto voto) {
        return votoDAO.save(voto);
    }


    public List<Voto> obtenerVotosPorEncuesta(Encuesta encuesta) {
        return votoDAO.findByEncuesta(encuesta);
    }

    public boolean haVotadoAntes(String correoVotante, Encuesta encuesta) {
        return votoDAO.existsByCorreoVotanteAndEncuesta(correoVotante, encuesta);
    }
}
