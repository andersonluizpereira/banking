package com.example.banking.repository;

import com.example.banking.model.Transferencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransferenciaRepository extends JpaRepository<Transferencia, Long> {
    List<Transferencia> findByContaOrigemOrContaDestinoOrderByDataTransferenciaDesc(String contaOrigem, String contaDestino);
}
