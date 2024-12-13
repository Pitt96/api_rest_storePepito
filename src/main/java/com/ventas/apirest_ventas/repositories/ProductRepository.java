package com.ventas.apirest_ventas.repositories;

import com.ventas.apirest_ventas.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
