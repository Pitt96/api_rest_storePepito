package com.ventas.apirest_ventas.controllers;

import com.ventas.apirest_ventas.aggregates.RetailSaleRequest;
import com.ventas.apirest_ventas.entities.Product;
import com.ventas.apirest_ventas.entities.RetailSale;
import com.ventas.apirest_ventas.entities.Sale;
import com.ventas.apirest_ventas.repositories.ProductRepository;
import com.ventas.apirest_ventas.repositories.RetailRepository;
import com.ventas.apirest_ventas.repositories.SaleRepository;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/retail/v1")
public class RetailSaleController {
    private final RetailRepository retailRepository;
    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;

    public RetailSaleController(RetailRepository retailRepository, SaleRepository saleRepository, ProductRepository productRepository) {
        this.retailRepository = retailRepository;
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
    }

    @PostMapping("/create/{idSale}")
    @Transactional
    public ResponseEntity<?> createRetailSale(@PathVariable("idSale") Long idSale, @RequestBody List<RetailSaleRequest> retailSaleListRequest){
        // Buscar la venta
        Sale sale = saleRepository.findById(idSale).orElseThrow(()-> new RuntimeException("Venta no encontrado"));

        // Procesar cada detalle de la venta
        retailSaleListRequest.forEach(retailSaleRequest -> {
            RetailSale retailSale = buildRetailSale(retailSaleRequest);
            retailSale.setSale(sale);
            sale.getRetailSales().add(retailSale);
            sale.incrementTotal(retailSale.getPrice());
        });

        // Guardar y retornar la venta
        return  new ResponseEntity<>(saleRepository.save(sale), HttpStatus.CREATED);
    }

    @GetMapping("/list/{idSale}")
    public ResponseEntity<?> listRetailSale(@PathVariable("idSale") Long idSale){
        //Sale sale = saleRepository.findById(idSale).orElseThrow(()-> new RuntimeException("Venta no encontrado"));
        //return new ResponseEntity<>(sale.getRetailSales(), HttpStatus.OK);
        List<RetailSale> retailSalesList = retailRepository.findAllBySaleId(idSale);
        return new ResponseEntity<>(retailSalesList, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{idSale}/{idRetailSale}")
    @Transactional
    public ResponseEntity<?> deleteRetailSale(@PathVariable("idSale") Long idSale,@PathVariable("idRetailSale") Long idReailSale){
        RetailSale retailSale = retailRepository.findById(idReailSale).orElseThrow(()-> new RuntimeException("DetalleVenta no encontrado"));
        Sale sale = saleRepository.findById(idSale).orElseThrow(()-> new RuntimeException("Venta no encontrado"));

        //validar si retail pertenece a la venta
        if(!sale.getRetailSales().contains(retailSale)){
            throw new RuntimeException("DetalleVenta no pertenece a la venta");
        }

        sale.decrementTotal(retailSale.getPrice());
        sale.getRetailSales().remove(retailSale);
        retailRepository.deleteById(idReailSale);
        return new ResponseEntity<>(saleRepository.save(sale), HttpStatus.OK);
    }

    @PutMapping("/update/{idSale}/{idReailSale}")
    @Transactional
    public ResponseEntity<?> updateRetailSale(@PathVariable("idSale") Long idSale, @PathVariable("idReailSale") Long idReailSale, @RequestBody RetailSaleRequest retailSaleRequest){
        RetailSale retailSale = retailRepository.findById(idReailSale).orElseThrow(()-> new RuntimeException("DetalleVenta no encontrado"));
        Sale sale = saleRepository.findById(idSale).orElseThrow(()-> new RuntimeException("Venta no encontrado"));

        //validar si retail pertenece a la venta
        if(!sale.getRetailSales().contains(retailSale)){
            throw new RuntimeException("DetalleVenta no pertenece a la venta");
        }

        sale.decrementTotal(retailSale.getPrice());
        sale.getRetailSales().remove(retailSale);
        // Buscar el producto
        Product product = productRepository.findById(retailSaleRequest.getIdProduct())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Crear el detalle de venta
        double itemSubtotal = product.getPrice() * retailSaleRequest.getQuantity();
        retailSale.setProduct(product);
        retailSale.setQuantity(retailSaleRequest.getQuantity());
        retailSale.setPrice(itemSubtotal);
        sale.incrementTotal(itemSubtotal);
        sale.getRetailSales().add(retailSale);

        new ResponseEntity<>(saleRepository.save(sale), HttpStatus.OK);

        return new ResponseEntity<>(retailRepository.save(retailSale), HttpStatus.OK);
    }

    private RetailSale buildRetailSale(RetailSaleRequest retailSaleRequest) {
        // Buscar el producto
        Product product = productRepository.findById(retailSaleRequest.getIdProduct())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Crear el detalle de venta
        RetailSale retailSale = new RetailSale();
        double itemSubtotal = product.getPrice() * retailSaleRequest.getQuantity();
        retailSale.setProduct(product);
        retailSale.setQuantity(retailSaleRequest.getQuantity());
        retailSale.setPrice(itemSubtotal);

        return retailSale;
    }
}
