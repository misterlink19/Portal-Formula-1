package com.portal.formula1.repository;


import com.portal.formula1.model.Coches;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CocheDAO extends JpaRepository<Coches, String> {
    boolean existsCocheByCodigo(String codigo);

    Coches findCocheByCodigo(String codigo);

    List<Coches> findCocheByEquipo_Id(long equipoId);

    Coches findCocheByPiloto_Dorsal(Integer pilotoDorsal);

    List<Coches> findCocheByEquipo_IdAndPilotoIsNull(long equipoId);
}
