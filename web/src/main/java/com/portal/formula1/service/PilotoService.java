package com.portal.formula1.service;

import com.portal.formula1.model.Piloto;
import com.portal.formula1.repository.PilotoDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PilotoService {
    @Autowired
    private PilotoDAO pilotoDAO;

    public Piloto guardarPiloto(Piloto piloto) {
        return pilotoDAO.save(piloto);
    }

    public void eliminarPiloto(Integer dorsal) {
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
}
