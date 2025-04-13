package com.example.crudproject.controller;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import com.example.crudproject.model.Categoria;
import com.example.crudproject.model.Produto;

import com.example.crudproject.util.ArquivoUtil;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainController {

    @FXML private TextField txtCodigo, txtNome, txtDescricao, txtPrecoCompra, txtPrecoVenda, txtEstoque, txtQuantidade;
    @FXML private DatePicker dpDataFabricacao, dpDataValidade;
    @FXML private ComboBox<Categoria> cbCategoria;
    @FXML private TextArea txtAreaResultado;

    private List<Produto> produtos = new ArrayList<>();


    @FXML
    public void initialize() {
        // Inicializa categorias (fictícias por enquanto)
        cbCategoria.getItems().addAll(
                new Categoria(1, "Alimentos", "Perecíveis", "Supermercado"),
                new Categoria(2, "Higiene", "Pessoais", "Farmácia")
        );

        // Carrega produtos do CSV
        produtos = ArquivoUtil.carregarProdutos();
        txtAreaResultado.setText("Produtos carregados do arquivo. Total: " + produtos.size());
    }


    @FXML
    public void cadastrarProduto() {
        try {
            String codigo = txtCodigo.getText();
            String nome = txtNome.getText();
            String descricao = txtDescricao.getText();
            LocalDate dataFab = dpDataFabricacao.getValue();
            LocalDate dataVal = dpDataValidade.getValue();
            BigDecimal precoCompra = new BigDecimal(txtPrecoCompra.getText());
            BigDecimal precoVenda = new BigDecimal(txtPrecoVenda.getText());
            int quantidade = Integer.parseInt(txtQuantidade.getText());
            Categoria categoria = cbCategoria.getValue();

            // Validações
            if (!codigo.matches("[a-zA-Z0-9]{8}")) {
                txtAreaResultado.setText("Código inválido. Use 8 caracteres alfanuméricos.");
                return;
            }
            if (nome.isBlank() || nome.length() < 3) {
                txtAreaResultado.setText("Nome inválido. Preencha com ao menos 3 caracteres.");
                return;
            }
            if (dataFab == null || dataFab.isAfter(LocalDate.now())) {
                txtAreaResultado.setText("Data de fabricação inválida.");
                return;
            }
            if (dataVal == null || dataVal.isBefore(dataFab)) {
                txtAreaResultado.setText("Data de validade não pode ser antes da fabricação.");
                return;
            }
            if (precoCompra.compareTo(BigDecimal.ZERO) <= 0 || precoVenda.compareTo(BigDecimal.ZERO) <= 0) {
                txtAreaResultado.setText("Preços devem ser positivos.");
                return;
            }
            if (precoVenda.compareTo(precoCompra) <= 0) {
                txtAreaResultado.setText("Preço de venda deve ser maior que o de compra.");
                return;
            }
            if (quantidade < 0) {
                txtAreaResultado.setText("Quantidade não pode ser negativa.");
                return;
            }

            Produto p = new Produto(codigo, nome, descricao, dataFab, dataVal, precoCompra, precoVenda, quantidade, categoria);
            produtos.add(p);
            ArquivoUtil.salvarProdutos(produtos);

            txtAreaResultado.setText("Produto cadastrado com sucesso!");
            limparCampos();
        } catch (Exception e) {
            txtAreaResultado.setText("Erro ao cadastrar produto: " + e.getMessage());
        }
    }

    private void limparCampos() {
        txtCodigo.clear();
        txtNome.clear();
        txtDescricao.clear();
        dpDataFabricacao.setValue(null);
        dpDataValidade.setValue(null);
        txtPrecoCompra.clear();
        txtPrecoVenda.clear();
        txtQuantidade.clear();
        cbCategoria.getSelectionModel().clearSelection();
    }



    // métodos excluirProduto, consultarProduto, listarProdutos ainda serão implementados
    @FXML
    public void excluirProduto() {
        String codigo = txtCodigo.getText();

        Produto produtoParaRemover = produtos.stream()
                .filter(p -> p.getCodigo().equalsIgnoreCase(codigo))
                .findFirst()
                .orElse(null);

        if (produtoParaRemover != null) {
            produtos.remove(produtoParaRemover);
            ArquivoUtil.salvarProdutos(produtos);

            txtAreaResultado.setText("Produto removido com sucesso: " + produtoParaRemover.getNome());
        } else {
            txtAreaResultado.setText("Produto com código " + codigo + " não encontrado.");
        }
    }

    @FXML
    public void consultarProduto() {
        String codigo = txtCodigo.getText();

        Produto produtoEncontrado = produtos.stream()
                .filter(p -> p.getCodigo().equalsIgnoreCase(codigo))
                .findFirst()
                .orElse(null);

        if (produtoEncontrado != null) {
            txtAreaResultado.setText("Produto encontrado:\n" + formatarProduto(produtoEncontrado));
        } else {
            txtAreaResultado.setText("Produto com código " + codigo + " não encontrado.");
        }
    }

    @FXML
    public void listarProdutos() {
        if (produtos.isEmpty()) {
            txtAreaResultado.setText("Nenhum produto cadastrado.");
        } else {
            StringBuilder sb = new StringBuilder("Lista de Produtos:\n");
            produtos.forEach(p -> sb.append(formatarProduto(p)).append("\n------------------\n"));
            txtAreaResultado.setText(sb.toString());
        }
    }

    @FXML
    public void filtrarVencimento() {
        LocalDate hoje = LocalDate.now();
        List<Produto> proximosAVencer = produtos.stream()
                .filter(p -> !p.getDataValidade().isBefore(hoje) &&
                        !p.getDataValidade().isAfter(hoje.plusDays(60)))
                .toList();

        if (proximosAVencer.isEmpty()) {
            txtAreaResultado.setText("Nenhum produto próximo ao vencimento.");
        } else {
            StringBuilder sb = new StringBuilder("Produtos próximos ao vencimento:\n");
            proximosAVencer.forEach(p -> sb.append(p.getNome())
                    .append(" - Vence em ").append(p.getDataValidade()).append("\n"));
            txtAreaResultado.setText(sb.toString());
        }
    }

    @FXML
    public void filtrarEstoqueBaixo() {
        List<Produto> estoqueBaixo = produtos.stream()
                .filter(p -> p.getQuantidadeEstoque() < 50)
                .toList();

        if (estoqueBaixo.isEmpty()) {
            txtAreaResultado.setText("Todos os produtos têm estoque adequado.");
        } else {
            StringBuilder sb = new StringBuilder("Produtos com estoque baixo:\n");
            estoqueBaixo.forEach(p -> sb.append(p.getNome())
                    .append(" - ").append(p.getQuantidadeEstoque()).append(" unidades\n"));
            txtAreaResultado.setText(sb.toString());
        }
    }

    @FXML
    public void calcularLucroMedio() {
        var lucroPorCategoria = produtos.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getCategoria().getNome(),
                        Collectors.averagingDouble(p -> p.getPrecoVenda().subtract(p.getPrecoCompra()).doubleValue())
                ));

        if (lucroPorCategoria.isEmpty()) {
            txtAreaResultado.setText("Nenhum produto cadastrado.");
        } else {
            StringBuilder sb = new StringBuilder("Lucro médio por categoria:\n");
            lucroPorCategoria.forEach((categoria, lucro) ->
                    sb.append(categoria).append(": R$ ").append(String.format("%.2f", lucro)).append("\n")
            );
            txtAreaResultado.setText(sb.toString());
        }
    }



    @FXML
    public void agruparPorSetor() {
        // Agrupando os produtos por setor
        var produtosPorSetor = produtos.stream()
                .collect(Collectors.groupingBy(p -> p.getCategoria().getSetor()));

        if (produtosPorSetor.isEmpty()) {
            txtAreaResultado.setText("Nenhum produto cadastrado.");
        } else {
            // Construindo a string de exibição
            StringBuilder sb = new StringBuilder("Produtos agrupados por setor:\n");
            produtosPorSetor.forEach((setor, produtos) -> {
                sb.append("Setor: ").append(setor).append("\n");
                produtos.forEach(p -> sb.append(" - ").append(p.getNome()).append("\n"));
                sb.append("\n");
            });
            // Exibindo os resultados na área de texto
            txtAreaResultado.setText(sb.toString());
        }
    }





    // Método auxiliar para formatar o produto bonitinho
    private String formatarProduto(Produto p) {
        return "Código: " + p.getCodigo() +
                "\nNome: " + p.getNome() +
                "\nDescrição: " + p.getDescricao() +
                "\nFabricação: " + p.getDataFabricacao() +
                "\nValidade: " + p.getDataValidade() +
                "\nPreço Compra: R$ " + p.getPrecoCompra() +
                "\nPreço Venda: R$ " + p.getPrecoVenda() +
                "\nQuantidade: " + p.getQuantidadeEstoque() +
                "\nCategoria: " + p.getCategoria().getNome();
    }

}