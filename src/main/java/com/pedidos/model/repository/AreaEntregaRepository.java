package com.pedidos.model.repository;

import com.pedidos.model.entity.AreaEntrega;

import java.util.List;
import java.util.Optional;

public interface AreaEntregaRepository {

    void salvar(AreaEntrega area);

    Optional<AreaEntrega> buscarPorId(String id);

    List<AreaEntrega> buscarPorRestauranteId(String restauranteId);

    void deletar(String id);
}