package com.example.banking.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transferencias")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transferencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conta_origem", nullable = false)
    private String contaOrigem;

    @Column(name = "conta_destino", nullable = false)
    private String contaDestino;

    @Column(name = "valor", nullable = false)
    private Double valor;

    @Column(name = "data_transferencia", nullable = false)
    private LocalDateTime dataTransferencia;

    @Getter
    @Column(name = "sucesso", nullable = false)
    private Boolean sucesso;

    @Column(name = "mensagem")
    private String mensagem;

}
