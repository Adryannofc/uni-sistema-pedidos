package com.pedidos.application.service;

import com.pedidos.domain.entities.ProdutoEntity;
import com.pedidos.domain.repository.ProdutoRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class ProdutoService {
    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    /**
     * Cria e persiste um novo produto. Lança exceção se preço for zero ou negativo.
     */
    public ProdutoEntity criarProduto(String nome, String descricao, BigDecimal preco, String categoriaCardapioId, String restauranteId) {
        try {
            validarPreco(preco);

            ProdutoEntity produtoEntity = new ProdutoEntity(nome, descricao, preco, categoriaCardapioId, restauranteId);
            produtoRepository.salvar(produtoEntity);
            return produtoEntity;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Lista todos os produtos do restaurante, ativos e inativos.
     */
    public List<ProdutoEntity> listarPorRestaurante(String restauranteId) {
        try {
            return produtoRepository.listarTodos().stream()
                    .filter(p -> p.getRestauranteId().equals(restauranteId))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Lista apenas produtos ativos do restaurante — exibidos para o cliente.
     */
    public List<ProdutoEntity> listarAtivosPorRestaurante(String restauranteId) {
        try {
            return produtoRepository.listarTodos().stream()
                    .filter(p -> p.getRestauranteId().equals(restauranteId) && p.isStatusAtivo())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Edita campos do produto. Campos nulos ou em branco são ignorados.
     */
    public void editarProduto(String produtoId, String restauranteId, String novoNome, String novaDescricao, BigDecimal novoPreco, String novaCategoriaCardapioId) {
        try {
            ProdutoEntity produtoEntity = buscarProdutoDono(produtoId, restauranteId);

            if (novoNome != null && !novoNome.isBlank()) {
                produtoEntity.setNome(novoNome);
            }

            if (novaDescricao != null && !novaDescricao.isBlank()) {
                produtoEntity.setDescricao(novaDescricao);
            }

            if (novoPreco != null) {
                validarPreco(novoPreco);
                produtoEntity.setPreco(novoPreco);
            }

            if (novaCategoriaCardapioId != null && !novaCategoriaCardapioId.isBlank()) {
                produtoEntity.setCategoriaCardapioId(novaCategoriaCardapioId);
            }

            produtoRepository.salvar(produtoEntity);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Inverte o statusAtivo do produto (toggle).
     */
    public void ativarInativar(String produtoId, String restauranteId) {
        try {
            ProdutoEntity produtoEntity = buscarProdutoDono(produtoId, restauranteId);
            produtoEntity.setStatusAtivo(!produtoEntity.isStatusAtivo()); // toggle
            produtoRepository.salvar(produtoEntity);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Remove o produto. Lança exceção se não existir ou não pertencer ao restaurante.
     */
    public void removerProduto(String produtoId, String restauranteId) {
        try {
            buscarProdutoDono(produtoId, restauranteId);
            produtoRepository.deletar(produtoId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Busca produto e valida que pertence ao restaurante informado.
     */
    private ProdutoEntity buscarProdutoDono(String produtoId, String restauranteId) {
        try {
            ProdutoEntity produtoEntity = produtoRepository.buscarPorId(produtoId)
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

            if (!produtoEntity.getRestauranteId().equals(restauranteId)) {
                throw new IllegalArgumentException("Produto não pertence a este restaurante.");
            }

            return produtoEntity;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Valida que o preço é maior que zero.
     */
    private void validarPreco(BigDecimal preco) {
        try {
            if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Preço deve ser maior que zero.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public ProdutoEntity buscarPorId(String produtoId) {
        try {
            return produtoRepository.buscarPorId(produtoId)
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
