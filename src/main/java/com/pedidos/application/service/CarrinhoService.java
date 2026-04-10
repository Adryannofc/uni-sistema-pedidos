package com.pedidos.application.service;

import com.pedidos.domain.model.Carrinho;
import com.pedidos.domain.model.Produto;

import java.math.BigDecimal;

public class CarrinhoService {

    private Carrinho carrinho;

    /**
     * Inicia um novo carrinho para o restaurante escolhido.
     */
    public Carrinho iniciarCarrinho(String clienteId, String restauranteId) {
        carrinho = new Carrinho(clienteId, restauranteId);
        return carrinho;
    }

    /**
     * Valida produto no carrinha e adiciona
     *
     * @param produto    Produto fornecido pelo usuario
     * @param quantidade Numero de unidades do produto
     * @throws IllegalArgumentException Produto deve ser maior que zero
     */
    public void adicionarItem(Produto produto, int quantidade) {
        try {
            validarCarrinhoAtivo();
            if (!produto.isStatusAtivo()) {
                throw new IllegalStateException(
                        "Produto \"" + produto.getNome() + "\" esta indisponivel.");
            }
            if (quantidade <= 0) {
                throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
            }
            if (!produto.getRestauranteId().equals(carrinho.getRestauranteId())) {
                throw new IllegalStateException(
                        "Produto pertence a outro restaurante. Esvazie o carrinho antes.");
            }
            carrinho.adicionarItem(
                    produto,
                    produto.getNome(),
                    quantidade,
                    produto.getPreco()
            );
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Remove um item do carrinho pelo ID do produto.
     */
    public void removerItem(String produtoId) {
        try {
            validarCarrinhoAtivo();
            carrinho.removerItem(produtoId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Esvazia o carrinho atual.
     */
    public void limpar() {
        try {
            validarCarrinhoAtivo();
            carrinho.limpar();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Retorna o carrinho atual.
     */
    public Carrinho getCarrinho() {
        try {
            validarCarrinhoAtivo();
            return carrinho;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Retorna true se há carrinho ativo com itens.
     */
    public boolean temCarrinhoAtivo() {
        try {
            return carrinho != null && !carrinho.estaVazio();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Zera a sessão — chamado após checkout ou logout.
     */
    public void encerrarCarrinho() {
        carrinho = null;
    }

    private void validarCarrinhoAtivo() {
        try {
            if (carrinho == null) {
                throw new IllegalStateException(
                        "Nenhum carrinho ativo. Escolha um restaurante primeiro.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}