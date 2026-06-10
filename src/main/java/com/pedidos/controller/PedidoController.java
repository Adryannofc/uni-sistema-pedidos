package com.pedidos.controller;

import com.pedidos.model.entity.Carrinho;
import com.pedidos.model.entity.Cliente;
import com.pedidos.model.entity.Endereco;
import com.pedidos.model.entity.Pedido;
import com.pedidos.model.entity.Restaurante;
import com.pedidos.model.enums.StatusPedido;
import com.pedidos.model.service.PedidoService;

import java.math.BigDecimal;
import java.util.List;

public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    public Pedido criarPedido(Cliente cliente, Restaurante restaurante, Carrinho carrinho, Endereco enderecoEntrega, String codigoConfirmacao, BigDecimal taxaEntrega) {
        return pedidoService.criarPedido(cliente, restaurante, carrinho, enderecoEntrega, codigoConfirmacao, taxaEntrega);
    }

    public void confirmarEntrega(String pedidoId, String codigoDigitado) {
        pedidoService.confirmarEntrega(pedidoId, codigoDigitado);
    }

    public void atualizarStatus(String pedidoId, StatusPedido novoStatus) {
        pedidoService.atualizarStatus(pedidoId, novoStatus);
    }

    public Pedido buscarPorId(String pedidoId) {
        return pedidoService.buscarPorId(pedidoId);
    }

    public List<Pedido> listarPorCliente(String clienteId) {
        return pedidoService.listarPorCliente(clienteId);
    }

    public List<Pedido> listarPorRestaurante(String restauranteId) {
        return pedidoService.listarPorRestaurante(restauranteId);
    }

    public List<Pedido> filtrarPorStatus(String restauranteId, StatusPedido status) {
        return pedidoService.filtrarPorStatus(restauranteId, status);
    }

    public List<Pedido> obterHistoricoFinalizado(String restauranteId) {
        return pedidoService.obterHistoricoFinalizado(restauranteId);
    }
}
