package com.pedidos.controller;

import com.pedidos.model.entity.Produto;
import com.pedidos.view.util.session.CarrinhoManager;

import java.math.BigDecimal;
import java.util.Collection;

public class CarrinhoController {

    private final CarrinhoManager carrinhoManager;

    public CarrinhoController(CarrinhoManager carrinhoManager) {
        this.carrinhoManager = carrinhoManager;
    }

    public void iniciar(String clienteId, String restauranteId, BigDecimal taxaEntrega) {
        carrinhoManager.iniciar(clienteId, restauranteId, taxaEntrega);
    }

    public void adicionarItem(Produto produto, int quantidade) {
        carrinhoManager.adicionarItem(produto, quantidade);
    }

    public void removerItem(String produtoId) {
        carrinhoManager.removerItem(produtoId);
    }

    public void esvaziar() {
        carrinhoManager.esvaziar();
    }

    public boolean estaVazio() {
        return carrinhoManager.estaVazio();
    }

    public Collection<CarrinhoManager.ItemCarrinho> getItens() {
        return carrinhoManager.getItens();
    }

    public String getRestauranteId() {
        return carrinhoManager.getRestauranteId();
    }

    public BigDecimal calcularSubtotal() {
        return carrinhoManager.calcularSubtotal();
    }

    public BigDecimal calcularTotal() {
        return carrinhoManager.calcularTotal();
    }

    public BigDecimal getTaxaEntrega() {
        return carrinhoManager.getTaxaEntrega();
    }
}
