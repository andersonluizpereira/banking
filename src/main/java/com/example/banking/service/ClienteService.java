package com.example.banking.service;

import com.example.banking.dto.ClienteDTO;
import com.example.banking.exception.ResourceNotFoundException;
import com.example.banking.model.Cliente;
import com.example.banking.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public ClienteDTO cadastrarCliente(ClienteDTO clienteDTO) {
        Cliente cliente = Cliente.builder()
                .nome(clienteDTO.getNome())
                .numeroConta(clienteDTO.getNumeroConta())
                .saldo(clienteDTO.getSaldo())
                .build();
        Cliente salvo = clienteRepository.save(cliente);
        return mapToDTO(salvo);
    }

    public List<ClienteDTO> listarClientes() {
        return clienteRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ClienteDTO buscarPorNumeroConta(String numeroConta) {
        Cliente cliente = clienteRepository.findByNumeroConta(numeroConta)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado para a conta: " + numeroConta));
        return mapToDTO(cliente);
    }

    public Cliente getClienteEntityByNumeroConta(String numeroConta) {
        return clienteRepository.findByNumeroConta(numeroConta)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado para a conta: " + numeroConta));
    }

    @Transactional
    public void atualizarSaldo(Cliente cliente) {
        clienteRepository.save(cliente);
    }

    private ClienteDTO mapToDTO(Cliente cliente) {
        return ClienteDTO.builder()
                .id(cliente.getId())
                .nome(cliente.getNome())
                .numeroConta(cliente.getNumeroConta())
                .saldo(cliente.getSaldo())
                .build();
    }
}
