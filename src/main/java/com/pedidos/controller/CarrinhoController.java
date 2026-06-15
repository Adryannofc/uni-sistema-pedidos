package com.pedidos.controller;

import com.pedidos.model.entity.Carrinho;
import com.pedidos.model.entity.Produto;
import com.pedidos.model.service.CarrinhoService;

import java.math.BigDecimal;

public class CarrinhoController {
    private final CarrinhoService carrinhoService;


    public CarrinhoController(CarrinhoService carrinhoService) {
        this.carrinhoService = carrinhoService;
    }

    public void iniciarCarrinho(String clienteId, String restauranteId){
        carrinhoService.iniciarCarrinho(clienteId,restauranteId);
    }

    public void adicionarItem(Produto produto, int quantidade){
      carrinhoService.adicionarItem(produto,quantidade);
    };

    public void removerItem(String produtoId)
    {
        carrinhoService.removerItem(produtoId);
    };

    public void limpar(){
        carrinhoService.limpar();
    };

    public Carrinho obterCarrinho(){
        return carrinhoService.getCarrinho();
    }

    public BigDecimal obterTotal() {
        Carrinho c = carrinhoService.getCarrinho();
        return (c != null) ? c.calcularSubtotal() : BigDecimal.ZERO;
    }


}
