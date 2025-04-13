package com.example.crudproject.util;

import com.example.crudproject.model.Produto;
import com.example.crudproject.model.Categoria;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ArquivoUtil {

    private static final String CAMINHO_ARQUIVO = "produtos.csv";

    public static void salvarProdutos(List<Produto> produtos) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CAMINHO_ARQUIVO))) {
            for (Produto p : produtos) {
                writer.write(String.join(";",
                        p.getCodigo(),
                        p.getNome(),
                        p.getDescricao(),
                        p.getDataFabricacao().toString(),
                        p.getDataValidade().toString(),
                        p.getPrecoCompra().toString(),
                        p.getPrecoVenda().toString(),
                        String.valueOf(p.getQuantidadeEstoque()),
                        String.valueOf(p.getCategoria().getId()),
                        p.getCategoria().getNome(),
                        p.getCategoria().getDescricao(),
                        p.getCategoria().getSetor()
                ));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar produtos: " + e.getMessage());
        }
    }

    public static List<Produto> carregarProdutos() {
        List<Produto> produtos = new ArrayList<>();
        File arquivo = new File(CAMINHO_ARQUIVO);

        if (!arquivo.exists()) return produtos;

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(";");
                Produto p = new Produto(
                        partes[0],
                        partes[1],
                        partes[2],
                        LocalDate.parse(partes[3]),
                        LocalDate.parse(partes[4]),
                        new BigDecimal(partes[5]),
                        new BigDecimal(partes[6]),
                        Integer.parseInt(partes[7]),
                        new Categoria(
                                Integer.parseInt(partes[8]),
                                partes[9],
                                partes[10],
                                partes[11]
                        )
                );
                produtos.add(p);
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar produtos: " + e.getMessage());
        }

        return produtos;
    }
}