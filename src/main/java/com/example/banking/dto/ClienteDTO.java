package com.example.banking.dto;

import lombok.*;

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
