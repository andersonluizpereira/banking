package com.example.banking.controller;

import com.example.banking.dto.ClienteDTO;
import com.example.banking.model.Cliente;
import com.example.banking.repository.ClienteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ClienteControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private ClienteDTO clienteDTO;

    private String ID;

    @BeforeEach
    public void setUp() {
        ID = String.valueOf(UUID.randomUUID());
        clienteDTO = ClienteDTO.builder()
                .nome("Maria")
                .numeroConta(ID)
                .saldo(2000.0)
                .build();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        clienteRepository.deleteAll();
    }

    @Test
    public void testarCadastrarCliente() throws Exception {

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Maria"))
                .andExpect(jsonPath("$.numeroConta").value(ID));
    }

    @Test
    public void testarListarClientes() throws Exception {
        var cliente1 = ClienteDTO.builder()
                .nome("Ana")
                .numeroConta(ID)
                .saldo(1500.0)
                .build();

        var cliente2 = ClienteDTO.builder()
                .nome("Pedro")
                .numeroConta(String.valueOf(UUID.randomUUID()))
                .saldo(2500.0)
                .build();

        clienteRepository.save(Cliente.builder()
                .id(UUID.randomUUID().toString())
                .nome(cliente1.getNome())
                .numeroConta(cliente1.getNumeroConta())
                .saldo(cliente1.getSaldo())
                .build());

        clienteRepository.save(Cliente.builder()
                .id(UUID.randomUUID().toString())
                .nome(cliente2.getNome())
                .numeroConta(cliente2.getNumeroConta())
                .saldo(cliente2.getSaldo())
                .build());

        mockMvc.perform(get("/api/v1/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}