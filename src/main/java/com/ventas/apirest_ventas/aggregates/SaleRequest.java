package com.ventas.apirest_ventas.aggregates;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class SaleRequest {
    private Date fecha;
    private Long idClient;
    private List<RetailSaleRequest> retailSales;
}
