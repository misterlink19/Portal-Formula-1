package com.portal.formula1.service;

import com.portal.formula1.model.Equipo;
import com.portal.formula1.model.UsuarioRegistrado;
import com.portal.formula1.repository.EquipoDAO;
import com.portal.formula1.repository.UsuarioRegistradoDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EquipoService {

    @Autowired
    private EquipoDAO equipoDAO;

    @Autowired
    private UsuarioRegistradoDAO usuarioRegistradoDAO;

    public Equipo crearEquipo(Equipo equipo) {
        return equipoDAO.save(equipo);
    }

    public Optional<Equipo> obtenerEquipoPorId(Long id) {
        return equipoDAO.findById(id);
    }

    public List<Equipo> obtenerTodosLosEquipos() {
        return equipoDAO.findAll();
    }

    public boolean existePorNombre(String nombre) { return equipoDAO.existsByNombre(nombre); }

    public Equipo addResponsableToEquipo(Long equipoId, String usuario) {
        Equipo equipo = equipoDAO.findById(equipoId).orElse(null);
        UsuarioRegistrado responsable = usuarioRegistradoDAO.findById(usuario).orElse(null);

        if (equipo != null && responsable != null) {
            responsable.setEquipo(equipo);
            equipo.getResponsables().add(responsable);
            usuarioRegistradoDAO.save(responsable);
            return equipoDAO.save(equipo);
        }
        return null;
    }

    public void eliminarEquipo(Long id) {
        equipoDAO.deleteById(id);
    }
//Metodos de editar y Eliminar comentados para implementarlos en la historia de eliminar y editar equipo
//    public Equipo removeResponsableFromEquipo(Long equipoId, String usuario) {
//        Equipo equipo = equipoDAO.findById(equipoId).orElse(null);
//        UsuarioRegistrado responsable = usuarioRegistradoDAO.findById(usuario).orElse(null);
//
//        if (equipo != null && responsable != null) {
//            equipo.getResponsables().remove(responsable);
//            responsable.setEquipo(null);
//            usuarioRegistradoDAO.save(responsable);
//            return equipoDAO.save(equipo);
//        }
//        return null;
//    }
}
