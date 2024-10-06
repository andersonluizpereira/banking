package com.example.banking.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiPathsTest {
    @Test
    public void testApiPaths() {
        assertEquals("/api/v1/clientes", ApiPaths.API_V_1_CLIENTES,
                "API_V_1_CLIENTES deveria ser '/api/v1/clientes'");

        String numeroConta = "12345";
        String expectedPath = String.format("/api/v1/clientes/%s", numeroConta);
        String actualPath = String.format(ApiPaths.API_V_1_CLIENTES_COM_NUMERO_CONTA, numeroConta);

        assertEquals(expectedPath, actualPath,
                "API_V_1_CLIENTES_COM_NUMERO_CONTA deveria ser '/api/v1/clientes/{numeroConta}'");
    }
}