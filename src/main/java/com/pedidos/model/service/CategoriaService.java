package com.pedidos.model.service;

import com.pedidos.model.entity.CategoriaCardapio;
import com.pedidos.model.entity.CategoriaGlobal;
import com.pedidos.model.entity.Restaurante;
import com.pedidos.model.repository.CategoriaCardapioRepository;
import com.pedidos.model.repository.CategoriaGlobalRepository;
import com.pedidos.model.repository.ProdutoRepository;
import com.pedidos.model.repository.RestauranteRepository;

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

    // CATEGORIA GLOBAL

    public void criarCategoriaGlobal(String nome, String descricao) {
        try {
            validarNomeGlobalUnico(nome, null);
            categoriaGlobalRepository.salvar(new CategoriaGlobal(nome, descricao));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<CategoriaGlobal> listarCategoriasGlobais() {
        try {
            return categoriaGlobalRepository.listarTodos();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void editarCategoriaGlobal(String id, String novoNome, String novaDescricao) {
        try {
            CategoriaGlobal categoria = categoriaGlobalRepository.buscarPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada."));

            validarNomeGlobalUnico(novoNome, id);

            categoria.setNome(novoNome);
            categoria.setDescricao(novaDescricao);
            categoriaGlobalRepository.salvar(categoria);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void removerCategoriaGlobal(String id) {
        try {
            categoriaGlobalRepository.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada."));

            boolean temRestauranteVinculado = restauranteRepository.listarRestaurantes().stream()
                    .anyMatch(r -> id.equals(r.getCategoriaGlobalId()));
            if (temRestauranteVinculado) {
                throw new IllegalArgumentException("Categoria em uso por um restaurante — remoção bloqueada.");
            }
            categoriaGlobalRepository.remover(id);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    // CATEGORIA CARDAPIO

    public void criarCategoriaCardapio(String nome, String descricao, String restauranteId) {
        try {
            validarNomeCardapioUnico(nome, restauranteId, null);
            Restaurante restaurante = (Restaurante) restauranteRepository
                    .buscarPorId(restauranteId)
                    .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado."));
            categoriaCardapioRepository.salvar(new CategoriaCardapio(nome, descricao, restaurante));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<CategoriaCardapio> listarCategoriasCardapio(String restauranteId) {
        try {
            return categoriaCardapioRepository.buscarPorRestauranteId(restauranteId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void editarCategoriaCardapio(String id, String novoNome, String novaDescricao) {
        try {
            CategoriaCardapio categoria = categoriaCardapioRepository.buscarPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada."));

            validarNomeCardapioUnico(novoNome, categoria.getRestauranteId(), id);

            categoria.setNome(novoNome);
            categoria.setDescricao(novaDescricao);
            categoriaCardapioRepository.salvar(categoria);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void removerCategoriaCardapio(String id) {
        try {
            categoriaCardapioRepository.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada."));

            boolean temProdutoVinculado = produtoRepository.listarTodos().stream()
                    .anyMatch(p -> id.equals(p.getCategoriaCardapioId()));
            if (temProdutoVinculado) {
                throw new IllegalArgumentException("Categoria em uso por um produto — remoção bloqueada.");
            }

            categoriaCardapioRepository.remover(id);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    // VALIDACOES PRIVADAS
    private void validarNomeGlobalUnico(String nome, String ignorarId) {
        try {
            boolean nomeExiste = categoriaGlobalRepository.listarTodos().stream()
                    .anyMatch(c -> c.getNome().equalsIgnoreCase(nome)
                            && !c.getId().equals(ignorarId));
            if (nomeExiste) {
                throw new IllegalArgumentException("Já existe uma categoria global com esse nome.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void validarNomeCardapioUnico(String nome, String restauranteId, String ignorarId) {
        try {
            boolean nomeExiste = categoriaCardapioRepository.buscarPorRestauranteId(restauranteId).stream()
                    .anyMatch(c -> c.getNome().equalsIgnoreCase(nome)
                            && !c.getId().equals(ignorarId));
            if (nomeExiste) {
                throw new IllegalArgumentException("Já existe uma categoria com esse nome neste cardápio.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
