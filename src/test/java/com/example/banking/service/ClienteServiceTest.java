package com.example.banking.service;

import com.example.banking.dto.ClienteDTO;
import com.example.banking.exception.ResourceNotFoundException;
import com.example.banking.model.Cliente;
import com.example.banking.repository.ClienteRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private com.example.banking.service.ClienteService clienteService;

    @Test
    public void testarBuscarPorNumeroConta_Sucesso() {
        Cliente cliente = Cliente.builder()
                .id("1")
                .nome("João")
                .numeroConta("12345")
                .saldo(1000.0)
                .build();

        when(clienteRepository.findByNumeroConta("12345")).thenReturn(Optional.of(cliente));

        ClienteDTO clienteDTO = clienteService.buscarPorNumeroConta("12345");

        assertNotNull(clienteDTO);
        assertEquals("João", clienteDTO.getNome());
    }

    @Test
    public void testarBuscarPorNumeroConta_NaoEncontrado() {
        when(clienteRepository.findByNumeroConta("99999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            clienteService.buscarPorNumeroConta("99999");
        });
    }
    @Test
    public void testCadastrarCliente() {
        // Dados de entrada (DTO)
        ClienteDTO clienteDTO = ClienteDTO.builder()
                .nome("João Silva")
                .numeroConta("123456")
                .saldo(1000.0)
                .build();

        // Cliente salvo esperado (entidade)
        Cliente clienteSalvo = Cliente.builder()
                .id("1")
                .nome("João Silva")
                .numeroConta("123456")
                .saldo(1000.0)
                .build();

        // Simulando o comportamento do repositório para retornar o cliente salvo
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);

        // Executar o método a ser testado
        ClienteDTO clienteRetornado = clienteService.cadastrarCliente(clienteDTO);

        // Verificações
        assertNotNull(clienteRetornado);
        assertEquals("João Silva", clienteRetornado.getNome());
        assertEquals("123456", clienteRetornado.getNumeroConta());
        assertEquals(1000.0, clienteRetornado.getSaldo());
    }
    @Test
    public void testListarClientes() {
        // Clientes simulados
        Cliente cliente1 = Cliente.builder()
                .id("1")
                .nome("João Silva")
                .numeroConta("123456")
                .saldo(1000.0)
                .build();

        Cliente cliente2 = Cliente.builder()
                .id("2")
                .nome("Maria Oliveira")
                .numeroConta("654321")
                .saldo(2000.0)
                .build();

        // Simulando o comportamento do repositório para retornar uma lista de clientes
        when(clienteRepository.findAll()).thenReturn(Arrays.asList(cliente1, cliente2));

        // Executar o método a ser testado
        List<ClienteDTO> clientesRetornados = clienteService.listarClientes();

        // Verificações
        assertNotNull(clientesRetornados);
        assertEquals(2, clientesRetornados.size());

        // Verificar o primeiro cliente
        ClienteDTO clienteDTO1 = clientesRetornados.get(0);
        assertEquals("João Silva", clienteDTO1.getNome());
        assertEquals("123456", clienteDTO1.getNumeroConta());
        assertEquals(1000.0, clienteDTO1.getSaldo());

        // Verificar o segundo cliente
        ClienteDTO clienteDTO2 = clientesRetornados.get(1);
        assertEquals("Maria Oliveira", clienteDTO2.getNome());
        assertEquals("654321", clienteDTO2.getNumeroConta());
        assertEquals(2000.0, clienteDTO2.getSaldo());
    }
    @Test
    @Transactional
    public void testAtualizarSaldo() {
        // Cliente simulado
        Cliente cliente = Cliente.builder()
                .id("1")
                .nome("João Silva")
                .numeroConta("123456")
                .saldo(2000.0)
                .build();

        // Simulando o comportamento do repositório
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        // Executar o método a ser testado
        cliente.setSaldo(3000.0); // Atualizar o saldo
        clienteService.atualizarSaldo(cliente);

        // Verificar se o método save foi chamado uma vez com o cliente correto
        verify(clienteRepository, times(1)).save(cliente);

    }
    @Test
    public void testGetClienteEntityByNumeroConta_Success() {
        // Cliente simulado
        Cliente cliente = Cliente.builder()
                .id("1")
                .nome("João Silva")
                .numeroConta("123456")
                .saldo(1000.0)
                .build();

        // Simulando o comportamento do repositório para encontrar o cliente
        when(clienteRepository.findByNumeroConta("123456")).thenReturn(Optional.of(cliente));

        // Executar o método a ser testado
        Cliente clienteRetornado = clienteService.getClienteEntityByNumeroConta("123456");

        // Verificações
        assertNotNull(clienteRetornado);
        assertEquals("João Silva", clienteRetornado.getNome());
        assertEquals("123456", clienteRetornado.getNumeroConta());
        assertEquals(1000.0, clienteRetornado.getSaldo());
    }

    @Test
    public void testGetClienteEntityByNumeroConta_NotFound() {
        // Simulando o comportamento do repositório para não encontrar o cliente
        when(clienteRepository.findByNumeroConta("999999")).thenReturn(Optional.empty());

        // Executar e verificar a exceção
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            clienteService.getClienteEntityByNumeroConta("999999");
        });

        // Verificar a mensagem da exceção
        String expectedMessage = "Cliente não encontrado para a conta: 999999";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}