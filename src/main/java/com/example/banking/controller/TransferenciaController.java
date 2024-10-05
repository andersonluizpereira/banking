package com.example.banking.controller;

import com.example.banking.dto.TransferenciaDTO;
import com.example.banking.model.Transferencia;
import com.example.banking.service.TransferenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/transferencias")
@Tag(name = "Transferências", description = "Operações relacionadas a transferências")
public class TransferenciaController {

    @Autowired
    private TransferenciaService transferenciaService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Realizar uma transferência entre contas")
    public ResponseEntity<Transferencia> realizarTransferencia(@Valid @RequestBody TransferenciaDTO transferenciaDTO) {
        Transferencia transferencia = transferenciaService.realizarTransferencia(transferenciaDTO);
        if (transferencia.getSucesso()) {
            return new ResponseEntity<>(transferencia, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(transferencia, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/historico/{numeroConta}", produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Buscar histórico de transferências de uma conta")
    public ResponseEntity<List<Transferencia>> buscarHistoricoTransferencias(@PathVariable String numeroConta) {
        List<Transferencia> historico = transferenciaService.buscarHistoricoTransferencias(numeroConta);
        return ResponseEntity.ok(historico);
    }
}

