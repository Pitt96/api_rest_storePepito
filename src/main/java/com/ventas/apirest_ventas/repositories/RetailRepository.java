package com.ventas.apirest_ventas.repositories;

import com.ventas.apirest_ventas.entities.RetailSale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RetailRepository extends JpaRepository<RetailSale, Long> {
}
