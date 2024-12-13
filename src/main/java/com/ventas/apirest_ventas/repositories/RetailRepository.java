package com.ventas.apirest_ventas.repositories;

import com.ventas.apirest_ventas.entities.RetailSale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RetailRepository extends JpaRepository<RetailSale, Long> {

    //Obtener todo el detalle de una venta
    List<RetailSale> findAllBySaleId(Long idSale);
}
