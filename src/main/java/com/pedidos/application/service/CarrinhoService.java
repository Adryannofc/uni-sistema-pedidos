package com.pedidos.application.service;

import com.pedidos.domain.entities.CarrinhoEntity;
import com.pedidos.domain.entities.ProdutoEntity;

public class CarrinhoService {

    private CarrinhoEntity carrinhoEntity;

    /**
     * Inicia um novo carrinho para o restaurante escolhido.
     */
    public CarrinhoEntity iniciarCarrinho(String clienteId, String restauranteId) {
        carrinhoEntity = new CarrinhoEntity(clienteId, restauranteId);
        return carrinhoEntity;
    }

    /**
     * Valida produto no carrinha e adiciona
     *
     * @param produtoEntity    Produto fornecido pelo usuario
     * @param quantidade Numero de unidades do produto
     * @throws IllegalArgumentException Produto deve ser maior que zero
     */
    public void adicionarItem(ProdutoEntity produtoEntity, int quantidade) {
        try {
            validarCarrinhoAtivo();
            if (!produtoEntity.isStatusAtivo()) {
                throw new IllegalStateException(
                        "Produto \"" + produtoEntity.getNome() + "\" esta indisponivel.");
            }
            if (quantidade <= 0) {
                throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
            }
            if (!produtoEntity.getRestauranteId().equals(carrinhoEntity.getRestauranteId())) {
                throw new IllegalStateException(
                        "Produto pertence a outro restaurante. Esvazie o carrinho antes.");
            }
            carrinhoEntity.adicionarItem(
                    produtoEntity.getId(),
                    produtoEntity.getNome(),
                    quantidade,
                    produtoEntity.getPreco()
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
            carrinhoEntity.removerItem(produtoId);
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
            carrinhoEntity.limpar();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Retorna o carrinho atual.
     */
    public CarrinhoEntity getCarrinho() {
        try {
            validarCarrinhoAtivo();
            return carrinhoEntity;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Retorna true se há carrinho ativo com itens.
     */
    public boolean temCarrinhoAtivo() {
        try {
            return carrinhoEntity != null && !carrinhoEntity.estaVazio();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Zera a sessão — chamado após checkout ou logout.
     */
    public void encerrarCarrinho() {
        carrinhoEntity = null;
    }

    private void validarCarrinhoAtivo() {
        try {
            if (carrinhoEntity == null) {
                throw new IllegalStateException(
                        "Nenhum carrinho ativo. Escolha um restaurante primeiro.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}