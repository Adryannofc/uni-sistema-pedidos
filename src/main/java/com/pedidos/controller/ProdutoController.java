package com.pedidos.controller;

import com.pedidos.model.entity.Produto;
import com.pedidos.model.service.ProdutoService;

import java.math.BigDecimal;
import java.util.List;

public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    public Produto criarProduto(String nome, String descricao, BigDecimal preco, String categoriaCardapioId, String restauranteId) {
        return produtoService.criarProduto(nome, descricao, preco, categoriaCardapioId, restauranteId);
    }

    public List<Produto> listarPorRestaurante(String restauranteId) {
        return produtoService.listarPorRestaurante(restauranteId);
    }

    public List<Produto> listarAtivosPorRestaurante(String restauranteId) {
        return produtoService.listarAtivosPorRestaurante(restauranteId);
    }

    public void editarProduto(String produtoId, String restauranteId, String novoNome, String novaDescricao, BigDecimal novoPreco, String novaCategoriaCardapioId) {
        produtoService.editarProduto(produtoId, restauranteId, novoNome, novaDescricao, novoPreco, novaCategoriaCardapioId);
    }

    public void ativarInativar(String produtoId, String restauranteId) {
        produtoService.ativarInativar(produtoId, restauranteId);
    }

    public void removerProduto(String produtoId, String restauranteId) {
        produtoService.removerProduto(produtoId, restauranteId);
    }

    public Produto buscarPorId(String produtoId) {
        return produtoService.buscarPorId(produtoId);
    }
}
