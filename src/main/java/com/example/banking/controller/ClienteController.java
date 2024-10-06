package com.example.banking.controller;

import com.example.banking.dto.ClienteDTO;
import com.example.banking.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.banking.utils.ApiPaths.API_V_1_CLIENTES;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(API_V_1_CLIENTES)
@Tag(name = "Clientes", description = "Operações relacionadas a clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Cadastrar um novo cliente")
    public ResponseEntity<ClienteDTO> cadastrarCliente(@Valid @RequestBody ClienteDTO clienteDTO) {
        ClienteDTO criado = clienteService.cadastrarCliente(clienteDTO);
        return new ResponseEntity<>(criado, HttpStatus.CREATED);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Listar todos os clientes")
    public ResponseEntity<List<ClienteDTO>> listarClientes() {
        List<ClienteDTO> clientes = clienteService.listarClientes();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping(value = "/{numeroConta}", produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Buscar cliente por número da conta")
    public ResponseEntity<ClienteDTO> buscarPorNumeroConta(@PathVariable String numeroConta) {
        ClienteDTO cliente = clienteService.buscarPorNumeroConta(numeroConta);
        return ResponseEntity.ok(cliente);
    }
}
