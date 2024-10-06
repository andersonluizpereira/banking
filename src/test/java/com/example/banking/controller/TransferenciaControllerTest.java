package com.example.banking.controller;
import com.example.banking.dto.ClienteDTO;
import com.example.banking.model.Cliente;
import com.example.banking.repository.ClienteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.banking.dto.TransferenciaDTO;
import com.example.banking.repository.TransferenciaRepository;
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

import static com.example.banking.utils.ApiPaths.API_V_1_TRANSFERENCIAS;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class TransferenciaControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransferenciaRepository transferenciaRepository;
    @Autowired
    private ClienteRepository clienteRepository;


    private TransferenciaDTO transferenciaDTO;
    private ClienteDTO clienteDTO;
    private String ORIGEM_ID;
    private String DESTINO_ID;
    @BeforeEach
    public void setUp() {
        ORIGEM_ID = String.valueOf(UUID.randomUUID());
        DESTINO_ID = String.valueOf(UUID.randomUUID());

        clienteDTO = getClienteBuilder("Maria", ORIGEM_ID, 6500.0);
        transferenciaDTO = getTransferenciaBuild(ORIGEM_ID, DESTINO_ID, 5000.0);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        clienteRepository.deleteAll();
        transferenciaRepository.deleteAll();
    }

    private TransferenciaDTO getTransferenciaBuild(String contaOrigem, String contaDestino, double valor) {
        return TransferenciaDTO.builder()
                .contaOrigem(contaOrigem)
                .contaDestino(contaDestino)
                .valor(valor)
                .build();
    }

    private ClienteDTO getClienteBuilder(String nome, String ID, double saldo) {
        return ClienteDTO.builder()
                .nome(nome)
                .numeroConta(ID)
                .saldo(saldo)
                .build();
    }

    private void salvarClienteNoRepositorio(ClienteDTO cliente) {
        clienteRepository.save(Cliente.builder()
                .id(UUID.randomUUID().toString())
                .nome(cliente.getNome())
                .numeroConta(cliente.getNumeroConta())
                .saldo(cliente.getSaldo())
                .build());
    }

    @Test
    public void testRealizarTransferencia_Sucesso() throws Exception {
        var cliente2 = getClienteBuilder("Pedro", DESTINO_ID, 2500.0);

        salvarClienteNoRepositorio(clienteDTO);

        salvarClienteNoRepositorio(cliente2);

        mockMvc.perform(post(API_V_1_TRANSFERENCIAS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferenciaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.contaOrigem", is(ORIGEM_ID)))
                .andExpect(jsonPath("$.contaDestino", is(DESTINO_ID)))
                .andExpect(jsonPath("$.valor", is(5000.0)))
                .andExpect(jsonPath("$.sucesso", is(true)));
    }
}