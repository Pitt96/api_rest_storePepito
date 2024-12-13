package com.ventas.apirest_ventas.controllers;

import com.ventas.apirest_ventas.aggregates.ClientRequest;
import com.ventas.apirest_ventas.entities.Client;
import com.ventas.apirest_ventas.repositories.ClientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/clients/v1")
public class ClientController {

    private final ClientRepository clientRepository;

    public ClientController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createClient(@RequestBody ClientRequest clientRequest){
        Client client = new Client();
        client.setName(clientRequest.getName());
        client.setEmail(clientRequest.getEmail());
        return new ResponseEntity<>(clientRepository.save(client), HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<?> listClients(){
        return new ResponseEntity<>(clientRepository.findAll() , HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<Client> findClient(@PathVariable Long id){
        return ResponseEntity.ok(clientRepository.findById(id).get());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable Long id, @RequestBody ClientRequest clientRequest){
        Optional<Client> clientWanted= clientRepository.findById(id);
        if(clientWanted.isPresent()){
            Client client = clientWanted.get();
            client.setName(clientRequest.getName());
            client.setEmail(clientRequest.getEmail());
            return ResponseEntity.ok(clientRepository.save(client));
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new Client());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteClient(@PathVariable Long id){
        Optional<Client> clientWanted= clientRepository.findById(id);
        if(clientWanted.isPresent()){
            clientRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new Client());
    }
}
