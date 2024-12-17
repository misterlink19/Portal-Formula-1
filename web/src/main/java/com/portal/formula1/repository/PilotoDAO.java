package com.portal.formula1.repository;

import com.portal.formula1.model.Piloto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PilotoDAO extends JpaRepository<Piloto, Integer> {
    boolean existsByDorsal(Integer dorsal);
    List<Piloto> findByEquipo_Id(Long equipoId);
}
