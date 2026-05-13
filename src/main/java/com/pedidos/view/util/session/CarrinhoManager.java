package com.pedidos.view.util.session;

import com.pedidos.domain.entities.Produto;
import java.math.BigDecimal;
import java.util.*;

/**
 * Gerenciador de carrinho em memória (sessão).
 * Mantém o estado do carrinho do cliente sem persistir no banco de dados.
 */
public class CarrinhoManager {
    private String restauranteId;
    private String clienteId;
    private final Map<String, ItemCarrinho> itens;
    private BigDecimal taxaEntrega;

    public CarrinhoManager() {
        this.itens = new LinkedHashMap<>();
        this.taxaEntrega = BigDecimal.ZERO;
    }

    public void iniciar(String clienteId, String restauranteId, BigDecimal taxaEntrega) {
        this.clienteId = clienteId;
        this.restauranteId = restauranteId;
        this.taxaEntrega = taxaEntrega != null ? taxaEntrega : BigDecimal.ZERO;
        this.itens.clear();
    }

    public void adicionarItem(Produto produto, int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que 0");
        }

        if (!produto.getRestauranteId().equals(restauranteId)) {
            throw new IllegalArgumentException("Produto não pertence ao restaurante selecionado");
        }

        ItemCarrinho item = itens.get(produto.getId());
        if (item != null) {
            item.setQuantidade(item.getQuantidade() + quantidade);
        } else {
            itens.put(produto.getId(), new ItemCarrinho(produto, quantidade));
        }
    }

    public void removerItem(String produtoId) {
        itens.remove(produtoId);
    }

    public void atualizarQuantidade(String produtoId, int novaQuantidade) {
        if (novaQuantidade <= 0) {
            removerItem(produtoId);
        } else {
            ItemCarrinho item = itens.get(produtoId);
            if (item != null) {
                item.setQuantidade(novaQuantidade);
            }
        }
    }

    public void esvaziar() {
        itens.clear();
    }

    public BigDecimal calcularSubtotal() {
        return itens.values().stream()
                .map(ItemCarrinho::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularTotal() {
        return calcularSubtotal().add(taxaEntrega);
    }

    public int contarItens() {
        return itens.values().stream()
                .mapToInt(ItemCarrinho::getQuantidade)
                .sum();
    }

    public boolean estaVazio() {
        return itens.isEmpty();
    }

    // Getters
    public String getRestauranteId() { return restauranteId; }
    public String getClienteId() { return clienteId; }
    public Collection<ItemCarrinho> getItens() { return itens.values(); }
    public ItemCarrinho getItem(String produtoId) { return itens.get(produtoId); }
    public BigDecimal getTaxaEntrega() { return taxaEntrega; }
    public void setTaxaEntrega(BigDecimal taxa) { this.taxaEntrega = taxa; }

    // ─────────────────────────────────────────────────────────────
    // CLASSE INTERNA — ItemCarrinho
    // ─────────────────────────────────────────────────────────────
    public static class ItemCarrinho {
        private final Produto produto;
        private int quantidade;

        public ItemCarrinho(Produto produto, int quantidade) {
            this.produto = produto;
            this.quantidade = quantidade;
        }

        public BigDecimal calcularSubtotal() {
            return produto.getPreco().multiply(new BigDecimal(quantidade));
        }

        public Produto getProduto() { return produto; }
        public int getQuantidade() { return quantidade; }
        public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    }
}


