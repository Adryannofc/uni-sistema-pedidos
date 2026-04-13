package com.pedidos.domain.repository;
import com.pedidos.domain.entities.Produto;

import java.util.List;
import java.util.Optional;

public interface ProdutoRepository {

    void salvar(Produto produto);

    Optional<Produto> buscarPorId(String id);

    List<Produto> listarTodos();

    void deletar(String id);
}
