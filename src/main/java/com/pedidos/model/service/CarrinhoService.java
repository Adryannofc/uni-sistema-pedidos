package com.pedidos.model.service;

import com.pedidos.model.entity.Carrinho;
import com.pedidos.model.entity.Produto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class CarrinhoService {

    private final Map<String, Carrinho> carrinhosAtivos = new HashMap<>();

    private String clienteLogadoId;
    private BigDecimal taxaEntrega = BigDecimal.ZERO;

    public void setClienteLogado(String clienteId)
    {
        this.clienteLogadoId = clienteId;
    }

    private Carrinho getCarrinhoAtual(){
        if (clienteLogadoId == null) return null;
        return carrinhosAtivos.get(clienteLogadoId);
    }

    /**
     * Inicia um novo carrinho para o restaurante escolhido.
     */
    public Carrinho iniciarCarrinho(String clienteId, String restauranteId) {
        Carrinho novoCarrinho = new Carrinho(clienteId, restauranteId);
        carrinhosAtivos.put(clienteId, novoCarrinho);
        return novoCarrinho;
    }

    /**
     * Valida produto no carrinha e adiciona
     *
     * @param produto    Produto fornecido pelo usuario
     * @param quantidade Numero de unidades do produto
     * @throws IllegalArgumentException Produto deve ser maior que zero
     */
    public void adicionarItem(Produto produto, int quantidade) {
        Carrinho c = getCarrinhoAtual();
        if (c == null) throw new IllegalStateException("Inicie um carrinho primeiro.");

        try {
            validarCarrinhoAtivo();
            if (!produto.isStatusAtivo()) {
                throw new IllegalStateException(
                        "Produto \"" + produto.getNome() + "\" esta indisponivel.");
            }
            if (quantidade <= 0) {
                throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
            }
            if (!produto.getRestauranteId().equals(c.getRestauranteId())) {
                throw new IllegalStateException(
                        "Produto pertence a outro restaurante. Esvazie o carrinho antes.");
            }
            c.adicionarItem(
                    produto.getId(),
                    produto.getNome(),
                    quantidade,
                    produto.getPreco()
            );
        } catch (Exception e) {
            if (e instanceof IllegalStateException || e instanceof IllegalArgumentException) {
                throw e;
                }
            throw new RuntimeException("Erro ao adicionar item: " + e.getMessage(), e);
        }
    }

    /**
     * Remove um item do carrinho pelo ID do produto.
     */
    public void removerItem(String produtoId) {
        Carrinho c = getCarrinhoAtual();
        try {
            validarCarrinhoAtivo();
            c.removerItem(produtoId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Esvazia o carrinho atual.
     */
    public void limpar() {
        Carrinho c = getCarrinhoAtual();
        try {
            validarCarrinhoAtivo();
             c.limpar();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Retorna o carrinho atual.
     */
    public Carrinho getCarrinho() {
        Carrinho c = getCarrinhoAtual();
        try {
            validarCarrinhoAtivo();
            return c;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Retorna true se há carrinho ativo com itens.
     */
    public boolean temCarrinhoAtivo() {
        Carrinho c = getCarrinhoAtual();
        try {
            return c != null && !c.estaVazio();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Zera a sessão — chamado após checkout ou logout.
     */
    public void encerrarCarrinho() {
        Carrinho c = getCarrinhoAtual();
        if (clienteLogadoId != null) {
            carrinhosAtivos.remove(clienteLogadoId);
        }
    }

    public void setTaxaEntrega(BigDecimal taxa) {
        this.taxaEntrega = taxa != null ? taxa : BigDecimal.ZERO;
    }

    public BigDecimal getTaxaEntrega() {
        return taxaEntrega;
    }

    private void validarCarrinhoAtivo() {
        Carrinho c = getCarrinhoAtual();
            if (c == null) {
                throw new IllegalStateException(
                        "Nenhum carrinho ativo. Escolha um restaurante primeiro.");
            }
    }
}