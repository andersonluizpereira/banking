package com.example.banking.repository;

import com.example.banking.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, String> {
    Optional<Cliente> findByNumeroConta(String numeroConta);
}
