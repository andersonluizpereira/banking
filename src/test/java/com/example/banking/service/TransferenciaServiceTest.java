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

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    @Test
    public void testBuscarHistoricoTransferencias() {
        // Transferências simuladas
        Transferencia transferencia1 = Transferencia.builder()
                .id(1L)
                .contaOrigem("123456")
                .contaDestino("654321")
                .valor(500.0)
                .dataTransferencia(LocalDateTime.now().minusDays(1))
                .sucesso(true)
                .mensagem("Transferência concluída")
                .build();

        Transferencia transferencia2 = Transferencia.builder()
                .id(2L)
                .contaOrigem("654321")
                .contaDestino("123456")
                .valor(300.0)
                .dataTransferencia(LocalDateTime.now().minusDays(2))
                .sucesso(true)
                .mensagem("Transferência concluída")
                .build();

        // Simulando o comportamento do repositório para retornar uma lista de transferências
        when(transferenciaRepository.findByContaOrigemOrContaDestinoOrderByDataTransferenciaDesc("123456", "123456"))
                .thenReturn(Arrays.asList(transferencia1, transferencia2));

        // Executar o método a ser testado
        List<Transferencia> historicoTransferencias = transferenciaService.buscarHistoricoTransferencias("123456");

        // Verificações
        assertNotNull(historicoTransferencias);
        assertEquals(2, historicoTransferencias.size());

        // Verificar a primeira transferência (deve ser a mais recente)
        Transferencia primeiraTransferencia = historicoTransferencias.get(0);
        assertEquals(1L, primeiraTransferencia.getId());
        assertEquals("123456", primeiraTransferencia.getContaOrigem());
        assertEquals("654321", primeiraTransferencia.getContaDestino());
        assertEquals(500.0, primeiraTransferencia.getValor());

        // Verificar a segunda transferência
        Transferencia segundaTransferencia = historicoTransferencias.get(1);
        assertEquals(2L, segundaTransferencia.getId());
        assertEquals("654321", segundaTransferencia.getContaOrigem());
        assertEquals("123456", segundaTransferencia.getContaDestino());
        assertEquals(300.0, segundaTransferencia.getValor());
    }
    @Test
    public void testTransferenciaValorExcedeLimite() {
        // Transferência DTO simulada com valor acima do limite
        TransferenciaDTO transferenciaDTO = TransferenciaDTO.builder()
                .contaOrigem("123456")
                .contaDestino("654321")
                .valor(15000.0) // Valor acima do limite de R$ 10.000,00
                .build();

        // Executar e verificar se a exceção IllegalArgumentException é lançada

        var transf =  transferenciaService.realizarTransferencia(transferenciaDTO);

        // Verificar se a mensagem da exceção é a esperada
        String expectedMessage = "Valor da transferência excede o limite de R$ 10.000,00";
        String actualMessage = transf.getMensagem();

        assert(actualMessage.contains(expectedMessage));
    }
    @Test
    public void testBuscarHistoricoListaTransferencias() {
        // Simulando duas transferências para uma mesma conta
        Transferencia transferencia1 = Transferencia.builder()
                .id(1L)
                .contaOrigem("123456")
                .contaDestino("654321")
                .valor(500.0)
                .dataTransferencia(LocalDateTime.now().minusDays(1))
                .sucesso(true)
                .mensagem("Transferência realizada com sucesso")
                .build();

        Transferencia transferencia2 = Transferencia.builder()
                .id(2L)
                .contaOrigem("654321")
                .contaDestino("123456")
                .valor(300.0)
                .dataTransferencia(LocalDateTime.now().minusDays(2))
                .sucesso(true)
                .mensagem("Transferência realizada com sucesso")
                .build();

        // Simulando o comportamento do repositório para retornar as transferências
        when(transferenciaRepository.findByContaOrigemOrContaDestinoOrderByDataTransferenciaDesc("123456", "123456"))
                .thenReturn(Arrays.asList(transferencia1, transferencia2));

        // Executar o método que estamos testando
        List<Transferencia> historicoTransferencias = transferenciaService.buscarHistoricoTransferencias("123456");

        // Verificações
        assertNotNull(historicoTransferencias);
        assertEquals(2, historicoTransferencias.size());

        // Verificar se a primeira transferência é a mais recente
        Transferencia primeiraTransferencia = historicoTransferencias.get(0);
        assertEquals(1L, primeiraTransferencia.getId());
        assertEquals("123456", primeiraTransferencia.getContaOrigem());
        assertEquals("654321", primeiraTransferencia.getContaDestino());

        // Verificar a segunda transferência
        Transferencia segundaTransferencia = historicoTransferencias.get(1);
        assertEquals(2L, segundaTransferencia.getId());
        assertEquals("654321", segundaTransferencia.getContaOrigem());
        assertEquals("123456", segundaTransferencia.getContaDestino());
    }

}