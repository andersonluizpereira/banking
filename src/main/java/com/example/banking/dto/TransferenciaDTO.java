package com.example.banking.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferenciaDTO {
    private String contaOrigem;
    private String contaDestino;
    private Double valor;
}
