package com.example.banking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferenciaDTO {
    private String contaOrigem;
    private String contaDestino;
    private Double valor;
}
