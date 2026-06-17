package com.pedidos.model.repository;

import com.pedidos.model.entity.CategoriaCardapio;
import java.util.List;
import java.util.Optional;

public interface CategoriaCardapioRepository {

    void salvar(CategoriaCardapio categoria);

    void remover(String id);

    Optional<CategoriaCardapio> buscarPorId(String id)
            ;
    List<CategoriaCardapio> buscarPorRestauranteId(String restauranteId);

    List<CategoriaCardapio> listarTodos();


}