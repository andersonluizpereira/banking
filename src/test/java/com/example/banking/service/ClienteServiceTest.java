package com.example.banking.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import com.example.banking.dto.ClienteDTO;
import com.example.banking.exception.ResourceNotFoundException;
import com.example.banking.model.Cliente;
import com.example.banking.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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

        Mockito.when(clienteRepository.findByNumeroConta("12345")).thenReturn(Optional.of(cliente));

        ClienteDTO clienteDTO = clienteService.buscarPorNumeroConta("12345");

        assertNotNull(clienteDTO);
        assertEquals("João", clienteDTO.getNome());
    }

    @Test
    public void testarBuscarPorNumeroConta_NaoEncontrado() {
        Mockito.when(clienteRepository.findByNumeroConta("99999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            clienteService.buscarPorNumeroConta("99999");
        });
    }
}