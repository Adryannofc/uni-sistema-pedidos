package com.pedidos.model.repository;

import com.pedidos.model.enums.StatusPedido;
import com.pedidos.model.entity.Pedido;
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
