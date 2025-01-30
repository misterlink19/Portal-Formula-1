package com.portal.formula1.service;

import com.portal.formula1.model.Coches;
import com.portal.formula1.model.Piloto;
import com.portal.formula1.repository.CocheDAO;
import com.portal.formula1.repository.PilotoDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CocheService {
    @Autowired
    private CocheDAO cocheDAO;
    @Autowired
    private PilotoDAO pilotoDAO;

    public void guardarCoche(Coches coche) {
        cocheDAO.save(coche);
    }

    public List<Coches> listarCochesPorEquipos(long idEquipo) {
        return cocheDAO.findCocheByEquipo_Id(idEquipo);
    }

    public List<Coches> listarCochesPorEquipoSinPiloto(long idEquipo) {
        return cocheDAO.findCocheByEquipo_IdAndPilotoIsNull(idEquipo);
    }

    public boolean existeCocheByCodigo(String codigo) {
        return cocheDAO.existsCocheByCodigo(codigo);
    }

    public Coches buscarCochePorPiloto(Integer dorsalPiloto) {
        return cocheDAO.findCocheByPiloto_Dorsal(dorsalPiloto);
    }

    public void asignarPiloto(Integer idPiloto, String codigo){
        Coches coche = cocheDAO.findCocheByCodigo(codigo);

        Piloto piloto = pilotoDAO.findPilotoByDorsal(idPiloto);

        coche.setPiloto(piloto);
        cocheDAO.save(coche);
    }

}
