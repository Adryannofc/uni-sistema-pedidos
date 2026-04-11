package com.pedidos.domain.repository;

import com.pedidos.domain.entities.CategoriaCardapioEntity;
import java.util.List;
import java.util.Optional;

public interface CategoriaCardapioRepository {

    void salvar(CategoriaCardapioEntity categoria);

    Optional<CategoriaCardapioEntity> buscarPorId(String id)
            ;
    List<CategoriaCardapioEntity> buscarPorRestauranteId(String restauranteId);

    List<CategoriaCardapioEntity> listarTodos();

    void remover(String id);
}