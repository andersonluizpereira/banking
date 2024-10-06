package com.example.banking.service;

import com.example.banking.dto.TransferenciaDTO;
import com.example.banking.model.Cliente;
import com.example.banking.model.Transferencia;
import com.example.banking.repository.TransferenciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransferenciaServiceTest {

    @InjectMocks
    private TransferenciaService transferenciaService;

    @Mock
    private ClienteService clienteService;

    private static String ORIGEM_ID;

    @Mock
    private TransferenciaRepository transferenciaRepository;
    private static String DESTINO_ID;
    private TransferenciaDTO transferenciaDTO;

    private static Transferencia getTransferenciaConcluidaBuilder() {
        return Transferencia.builder()
                .id(1L)
                .contaOrigem(ORIGEM_ID)
                .contaDestino(DESTINO_ID)
                .valor(500.0)
                .dataTransferencia(LocalDateTime.now().minusDays(1))
                .sucesso(true)
                .mensagem("Transferência concluída")
                .build();
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ORIGEM_ID = String.valueOf(UUID.randomUUID());
        DESTINO_ID = String.valueOf(UUID.randomUUID());

        transferenciaDTO = new TransferenciaDTO();
        transferenciaDTO.setContaOrigem(ORIGEM_ID);
        transferenciaDTO.setContaDestino(DESTINO_ID);
        transferenciaDTO.setValor(5000.0);
    }

    @Test
    void testRealizarTransferenciaSuccess() {

        var origem = new Cliente();
        origem.setNumeroConta(ORIGEM_ID);
        origem.setSaldo(10000.0);

        var destino = new Cliente();
        destino.setNumeroConta(DESTINO_ID);
        destino.setSaldo(5000.0);

        when(clienteService.getClienteEntityByNumeroConta(ORIGEM_ID)).thenReturn(origem);
        when(clienteService.getClienteEntityByNumeroConta(DESTINO_ID)).thenReturn(destino);

        Transferencia transferencia = transferenciaService.realizarTransferencia(transferenciaDTO);

        assertTrue(transferencia.getSucesso());
        assertEquals("Transferência realizada com sucesso", transferencia.getMensagem());
        verify(clienteService).atualizarSaldo(origem);
        verify(clienteService).atualizarSaldo(destino);
        verify(transferenciaRepository).save(any(Transferencia.class));
    }

    @Test
    void testRealizarTransferenciaInsufficientFunds() {
        TransferenciaDTO transferenciaDTO = new TransferenciaDTO();
        transferenciaDTO.setContaOrigem(ORIGEM_ID);
        transferenciaDTO.setContaDestino(DESTINO_ID);
        transferenciaDTO.setValor(15000.0);

        Cliente origem = new Cliente();
        origem.setNumeroConta(ORIGEM_ID);
        origem.setSaldo(5000.0);

        when(clienteService.getClienteEntityByNumeroConta(ORIGEM_ID)).thenReturn(origem);

        verify(transferenciaRepository, never()).save(any(Transferencia.class));
    }

    @Test
    void testRealizarTransferenciaExceedsLimit() {
        TransferenciaDTO transferenciaDTO = new TransferenciaDTO();
        transferenciaDTO.setContaOrigem(ORIGEM_ID);
        transferenciaDTO.setContaDestino(DESTINO_ID);
        transferenciaDTO.setValor(15000.0);

        verify(transferenciaRepository, never()).save(any(Transferencia.class));
    }

    @Test
    public void testBuscarHistoricoTransferencias() {
        Transferencia transferencia1 = getTransferenciaConcluidaBuilder();

        Transferencia transferencia2 = Transferencia.builder()
                .id(2L)
                .contaOrigem(DESTINO_ID)
                .contaDestino(ORIGEM_ID)
                .valor(300.0)
                .dataTransferencia(LocalDateTime.now().minusDays(2))
                .sucesso(true)
                .mensagem("Transferência concluída")
                .build();

        when(transferenciaRepository.findByContaOrigemOrContaDestinoOrderByDataTransferenciaDesc(ORIGEM_ID, ORIGEM_ID))
                .thenReturn(Arrays.asList(transferencia1, transferencia2));

        List<Transferencia> historicoTransferencias = transferenciaService.buscarHistoricoTransferencias(ORIGEM_ID);

        assertNotNull(historicoTransferencias);
        assertEquals(2, historicoTransferencias.size());

        Transferencia primeiraTransferencia = historicoTransferencias.get(0);
        assertEquals(1L, primeiraTransferencia.getId());
        assertEquals(ORIGEM_ID, primeiraTransferencia.getContaOrigem());
        assertEquals(DESTINO_ID, primeiraTransferencia.getContaDestino());
        assertEquals(500.0, primeiraTransferencia.getValor());

        Transferencia segundaTransferencia = historicoTransferencias.get(1);
        assertEquals(2L, segundaTransferencia.getId());
        assertEquals(DESTINO_ID, segundaTransferencia.getContaOrigem());
        assertEquals(ORIGEM_ID, segundaTransferencia.getContaDestino());
        assertEquals(300.0, segundaTransferencia.getValor());
    }

    @Test
    public void testTransferenciaValorExcedeLimite() {
        TransferenciaDTO transferenciaDTO = TransferenciaDTO.builder()
                .contaOrigem(ORIGEM_ID)
                .contaDestino(DESTINO_ID)
                .valor(15000.0) // Valor acima do limite de R$ 10.000,00
                .build();


        var transferencia = transferenciaService.realizarTransferencia(transferenciaDTO);

        String expectedMessage = "Valor da transferência excede o limite de R$ 10.000,00";
        String actualMessage = transferencia.getMensagem();

        assert (actualMessage.contains(expectedMessage));
    }

    @Test
    public void testBuscarHistoricoListaTransferencias() {
        Transferencia transferencia1 = Transferencia.builder()
                .id(1L)
                .contaOrigem(ORIGEM_ID)
                .contaDestino(DESTINO_ID)
                .valor(500.0)
                .dataTransferencia(LocalDateTime.now().minusDays(1))
                .sucesso(true)
                .mensagem("Transferência realizada com sucesso")
                .build();

        Transferencia transferencia2 = Transferencia.builder()
                .id(2L)
                .contaOrigem(DESTINO_ID)
                .contaDestino(ORIGEM_ID)
                .valor(300.0)
                .dataTransferencia(LocalDateTime.now().minusDays(2))
                .sucesso(true)
                .mensagem("Transferência realizada com sucesso")
                .build();

        when(transferenciaRepository.findByContaOrigemOrContaDestinoOrderByDataTransferenciaDesc(ORIGEM_ID, ORIGEM_ID))
                .thenReturn(Arrays.asList(transferencia1, transferencia2));
        List<Transferencia> historicoTransferencias = transferenciaService.buscarHistoricoTransferencias(ORIGEM_ID);

        assertNotNull(historicoTransferencias);
        assertEquals(2, historicoTransferencias.size());

        Transferencia primeiraTransferencia = historicoTransferencias.get(0);
        assertEquals(1L, primeiraTransferencia.getId());
        assertEquals(ORIGEM_ID, primeiraTransferencia.getContaOrigem());
        assertEquals(DESTINO_ID, primeiraTransferencia.getContaDestino());

        Transferencia segundaTransferencia = historicoTransferencias.get(1);
        assertEquals(2L, segundaTransferencia.getId());
        assertEquals(DESTINO_ID, segundaTransferencia.getContaOrigem());
        assertEquals(ORIGEM_ID, segundaTransferencia.getContaDestino());
    }
}