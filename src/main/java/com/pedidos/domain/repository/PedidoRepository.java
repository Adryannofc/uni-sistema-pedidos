package com.pedidos.domain.repository;

import com.pedidos.domain.enums.StatusPedido;
import com.pedidos.domain.entities.Pedido;
import java.util.List;
import java.util.Optional;

public interface PedidoRepository {

    void salvar(Pedido pedido);

    Optional<Pedido> buscarPorId(String id);

    List<Pedido> listarTodos();

    List<Pedido> buscarPorCliente(String clienteId);

    List<Pedido> buscarPorRestaurante(String restauranteId);

    List<Pedido> listarAtivosPorRestaurante(String restauranteId);

    List<Pedido> filtrarPorStatus(String restauranteId, StatusPedido status);

    void deletar(String id);


    List<Pedido> buscarHistoricoFinalizado(String restauranteId);
}
