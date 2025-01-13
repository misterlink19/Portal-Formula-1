package com.portal.formula1.service;

import com.portal.formula1.model.Coches;
import com.portal.formula1.repository.CocheDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CocheService {
    @Autowired
    private CocheDAO cocheDAO;

    public void guardarCoche(Coches coche) {
        cocheDAO.save(coche);
    }

    public List<Coches> listarCochesPorEquipos(long idEquipo) {
        return cocheDAO.findCocheByEquipo_Id(idEquipo);
    }

    public boolean existeCocheByCodigo(String codigo) {
        return cocheDAO.existsCocheByCodigo(codigo);
    }
}
