package com.ventas.apirest_ventas.controllers;

import com.ventas.apirest_ventas.aggregates.RetailSaleRequest;
import com.ventas.apirest_ventas.aggregates.SaleRequest;
import com.ventas.apirest_ventas.entities.Client;
import com.ventas.apirest_ventas.entities.Product;
import com.ventas.apirest_ventas.entities.RetailSale;
import com.ventas.apirest_ventas.entities.Sale;
import com.ventas.apirest_ventas.repositories.ClientRepository;
import com.ventas.apirest_ventas.repositories.ProductRepository;
import com.ventas.apirest_ventas.repositories.RetailRepository;
import com.ventas.apirest_ventas.repositories.SaleRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sales/v1")
public class SaleController {
    private final SaleRepository saleRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final RetailRepository retailRepository;

    public SaleController(SaleRepository saleRepository, ClientRepository clientRepository, ProductRepository productRepository, RetailRepository retailRepository) {
        this.saleRepository = saleRepository;
        this.clientRepository = clientRepository;
        this.productRepository = productRepository;
        this.retailRepository = retailRepository;
    }

    //Se esta pasando SaleRequest(fecha - iDetalle de ventas)
    @PostMapping("/create/{idClient}")
    @Transactional
    public ResponseEntity<?> createSale(@PathVariable("idClient") Long idClient, @RequestBody SaleRequest saleRequest){
        // Buscar el cliente
        Client client = clientRepository.findById(idClient).orElseThrow(()-> new RuntimeException("Cliente no encontrado"));
        // Crear la venta
        Sale sale = new Sale();
        sale.setClient(client);
        sale.setFecha(saleRequest.getFecha());

        // Procesar cada detalle de la venta
        saleRequest.getRetailSales().forEach(retailSaleRequest -> {
            RetailSale retailSale = buildRetailSale(retailSaleRequest);
            retailSale.setSale(sale);
            sale.getRetailSales().add(retailSale);
            sale.incrementTotal(retailSale.getPrice());
        });

        // Guardar y retornar la venta
        return  new ResponseEntity<>(saleRepository.save(sale), HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<?> listSales(){
        return new ResponseEntity<>(saleRepository.findAll(), HttpStatus.OK);
    }

    //Se esta pasando SaleRequest(fecha - idCliente - Detalle de ventas)
    @PutMapping("/update/{idSale}")
    @Transactional
    public ResponseEntity<?> updateSale(@PathVariable("idSale") Long idSale, @RequestBody SaleRequest saleRequest){
        //Buscar la venta
        Sale sale = saleRepository.findById(idSale).orElseThrow(()-> new RuntimeException("Venta no encontrada"));
        //List<RetailSale> retailSalesList = new ArrayList<>();
        // Actualizar los campos de la venta si están presentes
        /*if(saleRequest.getFecha() != null){
            sale.setFecha(saleRequest.getFecha());
        }*/
        Optional.ofNullable(saleRequest.getFecha()).ifPresent(sale::setFecha);
        /*if(saleRequest.getIdClient() != null){
            Client client = clientRepository.findById(saleRequest.getIdClient()).orElseThrow(()-> new RuntimeException("Cliente no encontrado"));
            sale.setClient(client);
        }*/
        Optional.ofNullable(saleRequest.getIdClient()).ifPresent(idClient -> {
            Client client = clientRepository.findById(idClient)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            sale.setClient(client);
        });
        // Procesar cada detalle de la venta para actualizar
        if (saleRequest.getRetailSales() != null) {
            sale.setTotal(0.0);
            List<RetailSale> retailSalesList = new ArrayList<>();
            saleRequest.getRetailSales().forEach(retailSaleRequest -> {
                RetailSale retailSale = retailRepository.findById(retailSaleRequest.getIdRetailSale())
                        .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
                retailSale.setQuantity(retailSaleRequest.getQuantity());
                Product product = productRepository.findById(retailSaleRequest.getIdProduct())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
                retailSale.setProduct(product);
                double itemSubtotal = product.getPrice() * retailSaleRequest.getQuantity();
                retailSale.setPrice(itemSubtotal);
                retailSalesList.add(retailSale);
                sale.incrementTotal(itemSubtotal);
                new ResponseEntity<>(retailRepository.save(retailSale), HttpStatus.OK);
            });
            sale.setRetailSales(retailSalesList);
        }

        return new ResponseEntity<>(saleRepository.save(sale), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{idSale}")
    public ResponseEntity<?> deleteSale(@PathVariable("idSale") Long idSale){
        Optional<Sale> sale = saleRepository.findById(idSale);
        if(sale.isPresent()){
            saleRepository.deleteById(idSale);
            return ResponseEntity.ok().build();
        }
        return new ResponseEntity<>(HttpStatus.CONFLICT);
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
