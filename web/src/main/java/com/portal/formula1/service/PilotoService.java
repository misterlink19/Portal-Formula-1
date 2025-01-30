package com.portal.formula1.service;

import com.portal.formula1.model.Coches;
import com.portal.formula1.model.Piloto;
import com.portal.formula1.repository.CocheDAO;
import com.portal.formula1.repository.PilotoDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class PilotoService {
    @Autowired
    private PilotoDAO pilotoDAO;

    @Autowired
    private CocheDAO cocheDAO;

    @Autowired
    private EncuestaService encuestaService;

    public Piloto guardarPiloto(Piloto piloto) {
        return pilotoDAO.save(piloto);
    }

    public void eliminarPiloto(Integer dorsal) {
        Piloto piloto = pilotoDAO.findById(dorsal)
                .orElseThrow(() -> new NoSuchElementException("Piloto no encontrado"));

        if (encuestaService.estaPilotoEnEncuestaActiva(piloto.getDorsal())) {
            throw new IllegalStateException("El piloto está en una encuesta activa y no puede ser eliminado.");
        }

        pilotoDAO.deleteById(dorsal);
    }

    public List<Piloto> listarPilotosPorEquipo(Long equipoId) {
        return pilotoDAO.findByEquipo_Id(equipoId);
    }

    public Optional<Piloto> obtenerPilotoPorDorsal(Integer dorsal) {
        return pilotoDAO.findById(dorsal);
    }

    public boolean existeDorsal(Integer dorsal) {
        return pilotoDAO.existsByDorsal(dorsal);
    }

    public List<Piloto> listarPilotosPorEquipoSinCoche(long idEquipo) {
        return pilotoDAO.findByEquipo_IdAndCocheIsNull(idEquipo);
    }

    public void asignarCoche(Integer idPiloto, String codigo){
        Coches coche = cocheDAO.findCocheByCodigo(codigo);

        Piloto piloto = pilotoDAO.findPilotoByDorsal(idPiloto);

        piloto.setCoche(coche);
        cocheDAO.save(coche);
    }
}
