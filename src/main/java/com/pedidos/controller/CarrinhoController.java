package com.pedidos.controller;

import com.pedidos.model.entity.Carrinho;
import com.pedidos.model.entity.ItemPedido;
import com.pedidos.model.entity.Produto;
import com.pedidos.model.service.CarrinhoService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class CarrinhoController {

    private final CarrinhoService carrinhoService;

    public CarrinhoController(CarrinhoService carrinhoService) {
        this.carrinhoService = carrinhoService;
    }

    public void iniciar(String clienteId, String restauranteId, BigDecimal taxaEntrega) {
        carrinhoService.setClienteLogado(clienteId);
        carrinhoService.iniciarCarrinho(clienteId, restauranteId);
        carrinhoService.setTaxaEntrega(taxaEntrega);
    }

    public void adicionarItem(Produto produto, int quantidade) {
        carrinhoService.adicionarItem(produto, quantidade);
    }

    public void removerItem(String produtoId) {
        carrinhoService.removerItem(produtoId);
    }

    public void esvaziar() {
        carrinhoService.encerrarCarrinho();
    }

    public boolean estaVazio() {
        return !carrinhoService.temCarrinhoAtivo();
    }

    public List<ItemPedido> getItens() {
        return estaVazio() ? Collections.emptyList() : carrinhoService.getCarrinho().getItens();
    }

    public Carrinho getCarrinho() {
        return estaVazio() ? null : carrinhoService.getCarrinho();
    }

    public String getRestauranteId() {
        return estaVazio() ? null : carrinhoService.getCarrinho().getRestauranteId();
    }

    public BigDecimal calcularSubtotal() {
        return estaVazio() ? BigDecimal.ZERO : carrinhoService.getCarrinho().calcularSubtotal();
    }

    public BigDecimal calcularTotal() {
        return calcularSubtotal().add(carrinhoService.getTaxaEntrega());
    }

    public BigDecimal getTaxaEntrega() {
        return carrinhoService.getTaxaEntrega();
    }
}
