package com.pedidos.domain.repository;

import com.pedidos.domain.entities.PedidoEntity;
import com.pedidos.domain.enums.StatusPedido;

import java.util.List;
import java.util.Optional;

public interface PedidoRepository {

    void salvar(PedidoEntity pedidoEntity);

    Optional<PedidoEntity> buscarPorId(String id);

    List<PedidoEntity> listarTodos();

    List<PedidoEntity> buscarPorCliente(String clienteId);

    List<PedidoEntity> buscarPorRestaurante(String restauranteId);

    List<PedidoEntity> listarAtivosPorRestaurante(String restauranteId);

    List<PedidoEntity> filtrarPorStatus(String restauranteId, StatusPedido status);

    void deletar(String id);

    List<PedidoEntity> buscarPorStatus(StatusPedido status);
}
