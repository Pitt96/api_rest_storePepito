package com.ventas.apirest_ventas.repositories;

import com.ventas.apirest_ventas.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long>{
}
