package com.pedidos.model.repository;
import com.pedidos.model.entity.CategoriaGlobal;

import java.util.List;
import java.util.Optional;

public interface CategoriaGlobalRepository {
    void salvar(CategoriaGlobal categoria);

    Optional<CategoriaGlobal> buscarPorId(String id);

    Optional<CategoriaGlobal> buscarPorNome(String nome);

    List<CategoriaGlobal> listarTodos();

    void remover(String id);

}
