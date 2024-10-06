package com.example.banking.service;

import com.example.banking.dto.ClienteDTO;
import com.example.banking.exception.ResourceNotFoundException;
import com.example.banking.model.Cliente;
import com.example.banking.repository.ClienteRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private com.example.banking.service.ClienteService clienteService;

    private Cliente cliente;
    private ClienteDTO clienteDTO;

    private static String ORIGEM_ID;

    private static Cliente getClienteSalvoBuilder(String id, String nome, String numeroConta, Double saldo) {
        return Cliente.builder()
                .id(id)
                .nome(nome)
                .numeroConta(numeroConta)
                .saldo(saldo)
                .build();
    }

    private static ClienteDTO getClientDTOBuilder() {
        return ClienteDTO.builder()
                .nome("João Silva")
                .numeroConta(ORIGEM_ID)
                .saldo(1000.0)
                .build();
    }

    @Test
    public void testarBuscarPorNumeroConta_NaoEncontrado() {
        when(clienteRepository.findByNumeroConta("99999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            clienteService.buscarPorNumeroConta("99999");
        });
    }

    @BeforeEach
    public void setup() {
        ORIGEM_ID = String.valueOf(UUID.randomUUID());

        cliente = Cliente.builder()
                .id("1")
                .nome("João")
                .numeroConta(ORIGEM_ID)
                .saldo(1000.0)
                .build();

        clienteDTO = getClientDTOBuilder();

        when(clienteRepository.findByNumeroConta(ORIGEM_ID)).thenReturn(Optional.of(cliente));
    }

    @Test
    public void testarBuscarPorNumeroConta_Sucesso() {
        when(clienteRepository.findByNumeroConta(ORIGEM_ID)).thenReturn(Optional.of(cliente));

        var clienteDTO = clienteService.buscarPorNumeroConta(ORIGEM_ID);

        assertNotNull(clienteDTO);
        assertEquals("João", clienteDTO.getNome());
    }

    @Test
    public void testCadastrarCliente() {
        var clienteDTO = getClientDTOBuilder();

        var clienteSalvo = getClienteSalvoBuilder("1", "João Silva", ORIGEM_ID, 1000.0);

        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);

        ClienteDTO clienteRetornado = clienteService.cadastrarCliente(clienteDTO);

        assertNotNull(clienteRetornado);
        assertEquals("João Silva", clienteRetornado.getNome());
        assertEquals(ORIGEM_ID, clienteRetornado.getNumeroConta());
        assertEquals(1000.0, clienteRetornado.getSaldo());
    }

    @Test
    public void testListarClientes() {

        var clienteJoao = getClienteSalvoBuilder("1", "João Silva", ORIGEM_ID, 1000.0);

        var clienteMaria = getClienteSalvoBuilder("2", "Maria Oliveira", "654321", 2000.0);

        when(clienteRepository.findAll()).thenReturn(Arrays.asList(clienteJoao, clienteMaria));

        List<ClienteDTO> clientesRetornados = clienteService.listarClientes();

        assertNotNull(clientesRetornados);
        assertEquals(2, clientesRetornados.size());

        var clienteDTOJoao = clientesRetornados.get(0);
        assertEquals("João Silva", clienteDTOJoao.getNome());
        assertEquals(ORIGEM_ID, clienteDTOJoao.getNumeroConta());
        assertEquals(1000.0, clienteDTOJoao.getSaldo());

        var clienteDTOMaria = clientesRetornados.get(1);
        assertEquals("Maria Oliveira", clienteDTOMaria.getNome());
        assertEquals("654321", clienteDTOMaria.getNumeroConta());
        assertEquals(2000.0, clienteDTOMaria.getSaldo());
    }

    @Test
    @Transactional
    public void testAtualizarSaldo() {

        var cliente = Cliente.builder()
                .id("1")
                .nome("João Silva")
                .numeroConta(ORIGEM_ID)
                .saldo(2000.0)
                .build();

        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        cliente.setSaldo(3000.0);
        clienteService.atualizarSaldo(cliente);

        verify(clienteRepository, times(1)).save(cliente);
    }

    @Test
    public void testGetClienteEntityByNumeroConta_Success() {

        var cliente = getClienteSalvoBuilder("1", "João Silva", ORIGEM_ID, 1000.0);

        when(clienteRepository.findByNumeroConta(ORIGEM_ID)).thenReturn(Optional.of(cliente));

        var clienteRetornado = clienteService.getClienteEntityByNumeroConta(ORIGEM_ID);

        assertNotNull(clienteRetornado);
        assertEquals("João Silva", clienteRetornado.getNome());
        assertEquals(ORIGEM_ID, clienteRetornado.getNumeroConta());
        assertEquals(1000.0, clienteRetornado.getSaldo());
    }

    @Test
    public void testGetClienteEntityByNumeroConta_NotFound() {
        when(clienteRepository.findByNumeroConta("999999")).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            clienteService.getClienteEntityByNumeroConta("999999");
        });

        String expectedMessage = "Cliente não encontrado para a conta: 999999";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}