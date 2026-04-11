package com.pedidos.domain.repository;
import com.pedidos.domain.entities.CategoriaGlobalEntity;

import java.util.List;
import java.util.Optional;

public interface CategoriaGlobalRepository {
    void salvar(CategoriaGlobalEntity categoria);

    Optional<CategoriaGlobalEntity> buscarPorId(String id);

    Optional<CategoriaGlobalEntity> buscarPorNome(String nome);

    List<CategoriaGlobalEntity> listarTodos();

    void remover(String id);

}
