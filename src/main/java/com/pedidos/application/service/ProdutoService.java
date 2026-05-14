package com.pedidos.application.service;

import com.pedidos.domain.entities.Produto;
import com.pedidos.domain.entities.Restaurante;
import com.pedidos.domain.repository.ProdutoRepository;
import com.pedidos.domain.repository.RestauranteRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class ProdutoService {
    private final ProdutoRepository produtoRepository;
    private final RestauranteRepository restauranteRepository;

    public ProdutoService(ProdutoRepository produtoRepository, RestauranteRepository restauranteRepository) {
        this.produtoRepository = produtoRepository;
        this.restauranteRepository = restauranteRepository;
    }

    /**
     * Cria e persiste um novo produto. Lança exceção se preço for zero ou negativo.
     */
    public Produto criarProduto(String nome, String descricao, BigDecimal preco, String categoriaCardapioId, String restauranteId) {
        try {
            validarPreco(preco);
            Restaurante restaurante = (Restaurante) restauranteRepository
                    .buscarPorId(restauranteId)
                    .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado"));
            Produto produto = new Produto(nome, descricao, preco, categoriaCardapioId);
            produto.setRestaurante(restaurante);
            produtoRepository.salvar(produto);
            return produto;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Lista todos os produtos do restaurante, ativos e inativos.
     */
    public List<Produto> listarPorRestaurante(String restauranteId) {
        try {
            return produtoRepository.listarTodos().stream().filter(p -> p.getRestauranteId().equals(restauranteId)).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Lista apenas produtos ativos do restaurante — exibidos para o cliente.
     */
    public List<Produto> listarAtivosPorRestaurante(String restauranteId) {
        try {
            return produtoRepository.listarTodos().stream().filter(p -> p.getRestauranteId().equals(restauranteId) && p.isStatusAtivo()).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Edita campos do produto. Campos nulos ou em branco são ignorados.
     */
    public void editarProduto(String produtoId, String restauranteId, String novoNome, String novaDescricao, BigDecimal novoPreco, String novaCategoriaCardapioId) {
        try {
            Produto produto = buscarProdutoDono(produtoId, restauranteId);

            if (novoNome != null && !novoNome.isBlank()) {
                produto.setNome(novoNome);
            }

            if (novaDescricao != null && !novaDescricao.isBlank()) {
                produto.setDescricao(novaDescricao);
            }

            if (novoPreco != null) {
                validarPreco(novoPreco);
                produto.setPreco(novoPreco);
            }

            produto.setCategoriaCardapioId(novaCategoriaCardapioId);
            produtoRepository.salvar(produto);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Inverte o statusAtivo do produto (toggle).
     */
    public void ativarInativar(String produtoId, String restauranteId) {
        try {
            Produto produto = buscarProdutoDono(produtoId, restauranteId);
            produto.setStatusAtivo(!produto.isStatusAtivo()); // toggle
            produtoRepository.salvar(produto);
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
    private Produto buscarProdutoDono(String produtoId, String restauranteId) {
        try {
            Produto produto = produtoRepository.buscarPorId(produtoId).orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

            if (!produto.getRestauranteId().equals(restauranteId)) {
                throw new IllegalArgumentException("Produto não pertence a este restaurante.");
            }

            return produto;
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

    public Produto buscarPorId(String produtoId) {
        try {
            return produtoRepository.buscarPorId(produtoId).orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
