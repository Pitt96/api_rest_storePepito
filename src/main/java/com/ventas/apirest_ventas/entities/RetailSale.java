package com.ventas.apirest_ventas.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "retail_sales")
@Getter
@Setter
public class RetailSale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private int quantity;
    @Column(nullable = false)
    private double price;

    @ManyToOne
    @JoinColumn(name = "sale_id", referencedColumnName = "id", nullable = false)
    @JsonBackReference
    private Sale sale;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false)
    private Product product;
}
