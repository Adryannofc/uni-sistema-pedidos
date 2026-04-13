package com.pedidos.domain.repository;
import com.pedidos.domain.entities.CategoriaGlobal;

import java.util.List;
import java.util.Optional;

public interface CategoriaGlobalRepository {
    void salvar(CategoriaGlobal categoria);

    Optional<CategoriaGlobal> buscarPorId(String id);

    Optional<CategoriaGlobal> buscarPorNome(String nome);

    List<CategoriaGlobal> listarTodos();

    void remover(String id);

}
