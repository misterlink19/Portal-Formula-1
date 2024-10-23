/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.portal.formula1.repository;

import com.portal.formula1.model.Aficionado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author fjavi
 */
@Repository
public interface AficionadoDAO extends JpaRepository<Aficionado,String> {

}
