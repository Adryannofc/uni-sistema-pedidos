package com.pedidos.domain.repository;
import com.pedidos.domain.entities.Pedido;
import com.pedidos.domain.entities.Produto;
import com.pedidos.domain.enums.StatusPedido;

import java.util.List;
import java.util.Optional;

public interface ProdutoRepository {

    void salvar(Produto produto);

    Optional<Produto> buscarPorId(String id);

    List<Pedido> buscarPorStatus(StatusPedido statusPedido, String restauranteId);

    List<Produto> listarTodos();

    void deletar(String id);
}
