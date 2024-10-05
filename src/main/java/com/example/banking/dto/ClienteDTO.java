package com.example.banking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteDTO {
    private String id;
    private String nome;
    private String numeroConta;
    private Double saldo;
}
