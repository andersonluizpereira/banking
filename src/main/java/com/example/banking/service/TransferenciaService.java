package com.example.banking.service;

import com.example.banking.dto.TransferenciaDTO;
import com.example.banking.exception.InsufficientFundsException;
import com.example.banking.model.Cliente;
import com.example.banking.model.Transferencia;
import com.example.banking.repository.TransferenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransferenciaService {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private TransferenciaRepository transferenciaRepository;

    private static final Double LIMITE_TRANSFERENCIA = 10000.0;

    @Transactional
    public Transferencia realizarTransferencia(TransferenciaDTO transferenciaDTO) {
        Transferencia transferencia = Transferencia.builder()
                .contaOrigem(transferenciaDTO.getContaOrigem())
                .contaDestino(transferenciaDTO.getContaDestino())
                .valor(transferenciaDTO.getValor())
                .dataTransferencia(LocalDateTime.now())
                .sucesso(false)
                .build();

        try {
            if (transferenciaDTO.getValor() > LIMITE_TRANSFERENCIA) {
                throw new IllegalArgumentException("Valor da transferência excede o limite de R$ 10.000,00");
            }

            Cliente origem = clienteService.getClienteEntityByNumeroConta(transferenciaDTO.getContaOrigem());
            Cliente destino = clienteService.getClienteEntityByNumeroConta(transferenciaDTO.getContaDestino());

            if (origem.getSaldo() < transferenciaDTO.getValor()) {
                throw new InsufficientFundsException("Saldo insuficiente para a transferência");
            }

            origem.setSaldo(origem.getSaldo() - transferenciaDTO.getValor());
            destino.setSaldo(destino.getSaldo() + transferenciaDTO.getValor());

            clienteService.atualizarSaldo(origem);
            clienteService.atualizarSaldo(destino);

            transferencia.setSucesso(true);
            transferencia.setMensagem("Transferência realizada com sucesso");

        } catch (Exception e) {
            transferencia.setMensagem(e.getMessage());
        } finally {
            transferenciaRepository.save(transferencia);
        }

        return transferencia;
    }

    public List<Transferencia> buscarHistoricoTransferencias(String numeroConta) {
        return transferenciaRepository.findByContaOrigemOrContaDestinoOrderByDataTransferenciaDesc(numeroConta, numeroConta);
    }
}
