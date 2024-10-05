package com.example.banking.service;

import com.example.banking.dto.TransferenciaDTO;
import com.example.banking.exception.InsufficientFundsException;
import com.example.banking.model.Cliente;
import com.example.banking.model.Transferencia;
import com.example.banking.repository.TransferenciaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransferenciaServiceTest {

    @InjectMocks
    private TransferenciaService transferenciaService;

    @Mock
    private ClienteService clienteService;

    @Mock
    private TransferenciaRepository transferenciaRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRealizarTransferenciaSuccess() {
        // Arrange
        var transferenciaDTO = new TransferenciaDTO();
        transferenciaDTO.setContaOrigem("12345");
        transferenciaDTO.setContaDestino("67890");
        transferenciaDTO.setValor(5000.0);

        var origem = new Cliente();
        origem.setNumeroConta("12345");
        origem.setSaldo(10000.0);

        var destino = new Cliente();
        destino.setNumeroConta("67890");
        destino.setSaldo(5000.0);

        when(clienteService.getClienteEntityByNumeroConta("12345")).thenReturn(origem);
        when(clienteService.getClienteEntityByNumeroConta("67890")).thenReturn(destino);

        // Act
        Transferencia transferencia = transferenciaService.realizarTransferencia(transferenciaDTO);

        // Assert
        assertTrue(transferencia.getSucesso());
        assertEquals("Transferência realizada com sucesso", transferencia.getMensagem());
        verify(clienteService).atualizarSaldo(origem);
        verify(clienteService).atualizarSaldo(destino);
        verify(transferenciaRepository).save(any(Transferencia.class));
    }

    @Test
    void testRealizarTransferenciaInsufficientFunds() {
        // Arrange
        TransferenciaDTO transferenciaDTO = new TransferenciaDTO();
        transferenciaDTO.setContaOrigem("12345");
        transferenciaDTO.setContaDestino("67890");
        transferenciaDTO.setValor(15000.0);

        Cliente origem = new Cliente();
        origem.setNumeroConta("12345");
        origem.setSaldo(5000.0);

        when(clienteService.getClienteEntityByNumeroConta("12345")).thenReturn(origem);

        verify(transferenciaRepository, never()).save(any(Transferencia.class));
    }

    @Test
    void testRealizarTransferenciaExceedsLimit() {
        // Arrange
        TransferenciaDTO transferenciaDTO = new TransferenciaDTO();
        transferenciaDTO.setContaOrigem("12345");
        transferenciaDTO.setContaDestino("67890");
        transferenciaDTO.setValor(15000.0);  // Exceeds the transfer limit

        verify(transferenciaRepository, never()).save(any(Transferencia.class));
    }
}