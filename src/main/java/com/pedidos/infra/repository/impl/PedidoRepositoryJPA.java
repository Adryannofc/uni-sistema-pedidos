package com.pedidos.infra.repository.impl;

import com.pedidos.domain.model.Pedido;
import com.pedidos.domain.enums.StatusPedido;
import com.pedidos.domain.repository.PedidoRepository;

import java.util.HashMap;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.List;


public class PedidoRepositoryJPA implements PedidoRepository {

    private final Map<String, Pedido> pedidos = new HashMap<>();

    @Override
    public void salvar(Pedido pedido) {
        pedidos.put(pedido.getId(), pedido);
    }

    @Override
    public Optional<Pedido> buscarPorId(String id) {
        return Optional.ofNullable(pedidos.get(id));
    }

    @Override
    public List<Pedido> listarTodos() {
        return new ArrayList<>(pedidos.values());
    }

    @Override
    public List<Pedido> buscarPorCliente(String clienteId) {
        return pedidos.values()
                .stream()
                .filter(p -> p.getClienteId().equals(clienteId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Pedido> buscarPorRestaurante(String restauranteId) {
        return pedidos.values()
                .stream()
                .filter(p -> p.getRestauranteId().equals(restauranteId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Pedido> listarAtivosPorRestaurante(String restauranteId) {
        return pedidos.values()
                .stream()
                .filter(p -> p.getRestauranteId().equals(restauranteId))
                .filter(p -> p.getStatus() != StatusPedido.ENTREGUE
                                    && p.getStatus() != StatusPedido.CANCELADO)
                .collect(Collectors.toList());
    }

    @Override
    public List<Pedido> filtrarPorStatus(String restauranteId, StatusPedido status) {
        return pedidos.values()
                .stream()
                .filter(p -> p.getRestauranteId().equals(restauranteId))
                .filter(p -> p.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public void deletar(String id) {
        pedidos.remove(id);
    }

    public List<Pedido> buscarPorStatus(StatusPedido status) {
        return pedidos.values()
                .stream()
                .filter(p -> p.getStatus() == status)
                .collect(Collectors.toList());
    }

}
