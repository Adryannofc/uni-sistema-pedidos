package com.pedidos.domain.repository;
import com.pedidos.domain.entities.ProdutoEntity;

import java.util.List;
import java.util.Optional;

public interface ProdutoRepository {

    void salvar(ProdutoEntity produtoEntity);

    Optional<ProdutoEntity> buscarPorId(String id);

    List<ProdutoEntity> listarTodos();

    void deletar(String id);
}
