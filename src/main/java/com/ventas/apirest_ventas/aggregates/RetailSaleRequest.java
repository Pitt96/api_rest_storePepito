package com.ventas.apirest_ventas.aggregates;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RetailSaleRequest {
    private Long idRetailSale;
    private int quantity;
    private Long idProduct;
}
