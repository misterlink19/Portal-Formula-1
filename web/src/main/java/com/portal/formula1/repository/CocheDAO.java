package com.portal.formula1.repository;


import com.portal.formula1.model.Coches;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CocheDAO extends JpaRepository<Coches, String> {
    boolean existsCocheByCodigo(String codigo);

    List<Coches> findCocheByEquipo_Id(Long equipoId);
}
