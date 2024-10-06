package com.example.banking.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiPathsTest {
    @Test
    public void testApiPaths() {
        assertEquals("/api/v1/clientes", ApiPaths.API_V_1_CLIENTES,
                "API_V_1_CLIENTES deveria ser '/api/v1/clientes'");

        assertEquals("/api/v1/transferencias", ApiPaths.API_V_1_TRANSFERENCIAS,
                "API_V_1_TRANSFERENCIAS deveria ser '/api/v1/transferencias'");

        String numeroContaCliente = "12345";
        String expectedClientePath = String.format("/api/v1/clientes/%s", numeroContaCliente);
        String actualClientePath = String.format(ApiPaths.API_V_1_CLIENTES_COM_NUMERO_CONTA, numeroContaCliente);

        assertEquals(expectedClientePath, actualClientePath,
                "API_V_1_CLIENTES_COM_NUMERO_CONTA deveria formatar '/api/v1/clientes/{numeroConta}' corretamente");

        String numeroContaTransferencia = "54321";
        String expectedTransferenciaPath = String.format("/api/v1/transferencias/historico/%s", numeroContaTransferencia);
        String actualTransferenciaPath = String.format(ApiPaths.API_V_1_TRANSFERENCIAS_COM_NUMERO_CONTA, numeroContaTransferencia);

        assertEquals(expectedTransferenciaPath, actualTransferenciaPath,
                "API_V_1_TRANSFERENCIAS_COM_NUMERO_CONTA deveria formatar '/api/v1/transferencias/historico/{numeroConta}' corretamente");
    }
}