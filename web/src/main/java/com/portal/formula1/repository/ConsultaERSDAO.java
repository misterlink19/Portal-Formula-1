package com.portal.formula1.repository;

import com.portal.formula1.model.ConsultaERS;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsultaERSDAO extends JpaRepository<ConsultaERS, Long>{
    List<ConsultaERS> findAllByEquipo_Id(Long equipoId);
}
