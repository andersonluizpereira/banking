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

import static com.example.banking.utils.ApiPaths.API_V_1_CLIENTES;
import static com.example.banking.utils.ApiPaths.API_V_1_CLIENTES_COM_NUMERO_CONTA;
import static org.hamcrest.Matchers.is;
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
        clienteDTO = getClienteBuilder("Maria", ID, 2000.0);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        clienteRepository.deleteAll();
    }

    @Test
    public void testarCadastrarCliente() throws Exception {

        mockMvc.perform(post(API_V_1_CLIENTES)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Maria"))
                .andExpect(jsonPath("$.numeroConta").value(ID));
    }

    @Test
    public void testarListarClientes() throws Exception {
        var cliente1 = getClienteBuilder("Ana", ID, 1500.0);

        var cliente2 = getClienteBuilder("Pedro", String.valueOf(UUID.randomUUID()), 2500.0);

        salvarClienteNoRepositorio(cliente1);

        salvarClienteNoRepositorio(cliente2);

        mockMvc.perform(get(API_V_1_CLIENTES))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    private void salvarClienteNoRepositorio(ClienteDTO cliente) {
        clienteRepository.save(Cliente.builder()
                .id(UUID.randomUUID().toString())
                .nome(cliente.getNome())
                .numeroConta(cliente.getNumeroConta())
                .saldo(cliente.getSaldo())
                .build());
    }

    private ClienteDTO getClienteBuilder(String nome, String ID, double saldo) {
        return ClienteDTO.builder()
                .nome(nome)
                .numeroConta(ID)
                .saldo(saldo)
                .build();
    }

    @Test
    public void testBuscarPorNumeroConta_Sucesso() throws Exception {
        mockMvc.perform(post(API_V_1_CLIENTES)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isCreated());
        var url = String.format(API_V_1_CLIENTES_COM_NUMERO_CONTA, ID);
        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Maria")))
                .andExpect(jsonPath("$.numeroConta", is(ID)))
                .andExpect(jsonPath("$.saldo", is(2000.0)));
    }

    @Test
    public void testBuscarPorNumeroConta_NaoEncontrado() throws Exception {
        var url = String.format(API_V_1_CLIENTES_COM_NUMERO_CONTA, "99999");
        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Cliente não encontrado para a conta: 99999")));
    }
}