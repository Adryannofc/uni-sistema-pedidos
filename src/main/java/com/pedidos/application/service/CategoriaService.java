package com.pedidos.application.service;

import com.pedidos.domain.model.CategoriaCardapio;
import com.pedidos.domain.model.CategoriaGlobal;
import com.pedidos.domain.repository.CategoriaCardapioRepository;
import com.pedidos.domain.repository.CategoriaGlobalRepository;
import com.pedidos.domain.repository.ProdutoRepository;
import com.pedidos.domain.repository.RestauranteRepository;

import java.util.List;

public class CategoriaService {
    private final CategoriaCardapioRepository categoriaCardapioRepository;
    private final CategoriaGlobalRepository categoriaGlobalRepository;
    private final RestauranteRepository restauranteRepository;
    private final ProdutoRepository produtoRepository;

    public CategoriaService(CategoriaGlobalRepository categoriaGlobalRepository,
                            CategoriaCardapioRepository categoriaCardapioRepository,
                            RestauranteRepository restauranteRepository,
                            ProdutoRepository produtoRepository) {
        this.categoriaGlobalRepository = categoriaGlobalRepository;
        this.categoriaCardapioRepository = categoriaCardapioRepository;
        this.restauranteRepository = restauranteRepository;
        this.produtoRepository = produtoRepository;
    }

    // -------------------------------------------------------------------------
    // Categoria Global
    // -------------------------------------------------------------------------

    public void criarCategoriaGlobal(String nome, String descricao) {
        validarNomeGlobalUnico(nome, null);
        categoriaGlobalRepository.salvar(new CategoriaGlobal(nome, descricao));
    }

    public List<CategoriaGlobal> listarCategoriasGlobais() {
        return categoriaGlobalRepository.listarTodos();
    }

    public void editarCategoriaGlobal(String id, String novoNome, String novaDescricao) {
        CategoriaGlobal categoria = categoriaGlobalRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada."));

        validarNomeGlobalUnico(novoNome, id);

        categoria.setNome(novoNome);
        categoria.setDescricao(novaDescricao);
        categoriaGlobalRepository.salvar(categoria);
    }

    public void removerCategoriaGlobal(String id) {
        categoriaGlobalRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada."));

        boolean temRestauranteVinculado = restauranteRepository.listarRestaurantes().stream()
                .anyMatch(r -> id.equals(r.getCategoriaGlobalId()));
        if (temRestauranteVinculado) {
            throw new IllegalArgumentException("Categoria em uso por um restaurante — remoção bloqueada.");
        }

        categoriaGlobalRepository.remover(id);
    }

    // -------------------------------------------------------------------------
    // Categoria Cardápio
    // -------------------------------------------------------------------------

    public void criarCategoriaCardapio(String nome, String descricao, String restauranteId) {
        validarNomeCardapioUnico(nome, restauranteId, null);
        categoriaCardapioRepository.salvar(new CategoriaCardapio(nome, descricao, restauranteId));
    }

    public List<CategoriaCardapio> listarCategoriasCardapio(String restauranteId) {
        return categoriaCardapioRepository.buscarPorRestauranteId(restauranteId);
    }

    public void editarCategoriaCardapio(String id, String novoNome, String novaDescricao) {
        CategoriaCardapio categoria = categoriaCardapioRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada."));

        validarNomeCardapioUnico(novoNome, categoria.getRestauranteId(), id);

        categoria.setNome(novoNome);
        categoria.setDescricao(novaDescricao);
        categoriaCardapioRepository.salvar(categoria);
    }

    public void removerCategoriaCardapio(String id) {
        categoriaCardapioRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada."));

        boolean temProdutoVinculado = produtoRepository.listarTodos().stream()
                .anyMatch(p -> id.equals(p.getCategoriaCardapioId()));
        if (temProdutoVinculado) {
            throw new IllegalArgumentException("Categoria em uso por um produto — remoção bloqueada.");
        }

        categoriaCardapioRepository.remover(id);
    }

    // -------------------------------------------------------------------------
    // Validações privadas
    // -------------------------------------------------------------------------

    private void validarNomeGlobalUnico(String nome, String ignorarId) {
        boolean nomeExiste = categoriaGlobalRepository.listarTodos().stream()
                .anyMatch(c -> c.getNome().equalsIgnoreCase(nome)
                        && !c.getId().equals(ignorarId));
        if (nomeExiste) {
            throw new IllegalArgumentException("Já existe uma categoria global com esse nome.");
        }
    }

    private void validarNomeCardapioUnico(String nome, String restauranteId, String ignorarId) {
        boolean nomeExiste = categoriaCardapioRepository.buscarPorRestauranteId(restauranteId).stream()
                .anyMatch(c -> c.getNome().equalsIgnoreCase(nome)
                        && !c.getId().equals(ignorarId));
        if (nomeExiste) {
            throw new IllegalArgumentException("Já existe uma categoria com esse nome neste cardápio.");
        }
    }
}
