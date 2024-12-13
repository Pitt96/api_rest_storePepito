package com.ventas.apirest_ventas.controllers;
import com.ventas.apirest_ventas.entities.Product;
import com.ventas.apirest_ventas.repositories.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/products/v1")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<Product> createClient(@RequestBody Product product){
        return new ResponseEntity<>(productRepository.save(product), HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<?> listProducts(){
        return new ResponseEntity<>(productRepository.findAll() , HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<?> findProduct(@PathVariable Long id){
        return  new ResponseEntity<>(productRepository.findById(id).get(), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product){
        Optional<Product> productWanted= productRepository.findById(id);
        if(productWanted.isPresent()){
            Product productToUpdate = productWanted.get();
            productToUpdate.setName(product.getName());
            productToUpdate.setPrice(product.getPrice());
            return new ResponseEntity<>(productRepository.save(productToUpdate), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id){
        Optional<Product> productWanted= productRepository.findById(id);
        if(productWanted.isPresent()){
            productRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
}
