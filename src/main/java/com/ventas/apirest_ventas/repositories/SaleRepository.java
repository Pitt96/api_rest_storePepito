package com.ventas.apirest_ventas.repositories;

import com.ventas.apirest_ventas.entities.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Long> {
}
