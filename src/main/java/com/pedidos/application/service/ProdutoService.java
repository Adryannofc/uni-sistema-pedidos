package com.pedidos.application.service;

import com.pedidos.domain.model.Produto;
import com.pedidos.domain.repository.ProdutoRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class ProdutoService {
    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    /** Cria e persiste um novo produto. Lança exceção se preço for zero ou negativo. */
    public Produto criarProduto(String nome, String descricao, BigDecimal preco, String categoriaCardapioId, String restauranteId) {
        validarPreco(preco);

        Produto produto = new Produto(nome, descricao, preco, categoriaCardapioId, restauranteId);
        produtoRepository.salvar(produto);
        return produto;
    }

    /** Lista todos os produtos do restaurante, ativos e inativos. */
    public List<Produto> listarPorRestaurante(String restauranteId) {
        return produtoRepository.listarTodos().stream()
                .filter(p -> p.getRestauranteId().equals(restauranteId))
                .collect(Collectors.toList());
    }

    /** Lista apenas produtos ativos do restaurante — exibidos para o cliente. */
    public List<Produto> listarAtivosPorRestaurante(String restauranteId) {
        return produtoRepository.listarTodos().stream()
                .filter(p -> p.getRestauranteId().equals(restauranteId) && p.isStatusAtivo())
                .collect(Collectors.toList());
    }

    /** Edita campos do produto. Campos nulos ou em branco são ignorados. */
    public void editarProduto(String produtoId, String restauranteId, String novoNome, String novaDescricao, BigDecimal novoPreco, String novaCategoriaCardapioId) {

        Produto produto = buscarProdutoDono(produtoId, restauranteId);

        if(novoNome != null && !novoNome.isBlank()) {
            produto.setNome(novoNome);
        }

        if(novaDescricao != null && !novaDescricao.isBlank()) {
            produto.setDescricao(novaDescricao);
        }

        if(novoPreco != null) {
            validarPreco(novoPreco);
            produto.setPreco(novoPreco);
        }

        if (novaCategoriaCardapioId != null && !novaCategoriaCardapioId.isBlank()) {
            produto.setCategoriaCardapioId(novaCategoriaCardapioId);
        }

        produtoRepository.salvar(produto);
    }

    /** Inverte o statusAtivo do produto (toggle). */
    public void ativarInativar(String produtoId, String restauranteId) {
        Produto produto = buscarProdutoDono(produtoId, restauranteId);
        produto.setStatusAtivo(!produto.isStatusAtivo()); // toggle
        produtoRepository.salvar(produto);
    }

    /** Remove o produto. Lança exceção se não existir ou não pertencer ao restaurante. */
    public void removerProduto(String produtoId, String restauranteId) {
        buscarProdutoDono(produtoId, restauranteId); // garante que existe e pertence ao restaurante
        produtoRepository.deletar(produtoId);
    }

    /** Busca produto e valida que pertence ao restaurante informado. */
    private Produto buscarProdutoDono(String produtoId, String restauranteId) {
        Produto produto = produtoRepository.buscarPorId(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        if (!produto.getRestauranteId().equals(restauranteId)) {
            throw new IllegalArgumentException("Produto não pertence a este restaurante.");
        }

        return produto;
    }

    /** Valida que o preço é maior que zero. */
    private void validarPreco(BigDecimal preco) {
        if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço deve ser maior que zero.");
        }
    }

    public Produto buscarPorId(String produtoId) {
        return produtoRepository.buscarPorId(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
    }
}
