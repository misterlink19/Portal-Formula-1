package com.portal.formula1.repository;

import com.portal.formula1.model.ConsultaCombustible;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsultaCombustibleDAO extends JpaRepository<ConsultaCombustible, Long> {
    List<ConsultaCombustible> findByEquipo_Id(Long equipoId);

    List<ConsultaCombustible> findAllByEquipo_Id(Long equipoId);
}
