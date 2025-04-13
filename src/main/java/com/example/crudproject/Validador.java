package com.example.crudproject;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.math.BigDecimal;

public class Validador {
    public static boolean validarCodigo(String codigo) {
        return codigo != null && codigo.matches("[A-Za-z0-9]{8}");
    }

    public static boolean validarDataFabricacao(LocalDate dataFabricacao) {
        return dataFabricacao != null && !dataFabricacao.isAfter(LocalDate.now());
    }

    public static boolean validarDataValidade(LocalDate dataFabricacao, LocalDate dataValidade) {
        return dataValidade != null && !dataValidade.isBefore(dataFabricacao);
    }

    public static boolean validarPreco(BigDecimal precoCompra, BigDecimal precoVenda) {
        return precoCompra != null && precoVenda != null && precoVenda.compareTo(precoCompra) > 0;
    }

    public static boolean validarQuantidadeEstoque(int quantidadeEstoque) {
        return quantidadeEstoque >= 0;
    }
}

