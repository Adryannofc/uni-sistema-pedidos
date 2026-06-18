package com.pedidos.controller;

import com.pedidos.model.entity.CategoriaCardapio;
import com.pedidos.model.entity.CategoriaGlobal;
import com.pedidos.model.service.CategoriaService;

import java.util.List;

public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    // Categoria Global

    public void criarCategoriaGlobal(String nome, String descricao) {
        categoriaService.criarCategoriaGlobal(nome, descricao);
    }

    public List<CategoriaGlobal> listarCategoriasGlobais() {
        return categoriaService.listarCategoriasGlobais();
    }

    public void editarCategoriaGlobal(String id, String novoNome, String novaDescricao) {
        categoriaService.editarCategoriaGlobal(id, novoNome, novaDescricao);
    }

    public void removerCategoriaGlobal(String id) {
        categoriaService.removerCategoriaGlobal(id);
    }

    // Categoria Cardapio

    public void criarCategoriaCardapio(String nome, String descricao, String restauranteId) {
        categoriaService.criarCategoriaCardapio(nome, descricao, restauranteId);
    }

    public List<CategoriaCardapio> listarCategoriasCardapio(String restauranteId) {
        return categoriaService.listarCategoriasCardapio(restauranteId);
    }

    public void editarCategoriaCardapio(String id, String novoNome, String novaDescricao) {
        categoriaService.editarCategoriaCardapio(id, novoNome, novaDescricao);
    }

    public void removerCategoriaCardapio(String id) {
        categoriaService.removerCategoriaCardapio(id);
    }
}
