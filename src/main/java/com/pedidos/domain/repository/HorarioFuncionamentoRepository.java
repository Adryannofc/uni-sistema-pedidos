package com.pedidos.domain.repository;

import com.pedidos.domain.entities.HorarioFuncionamento;

import java.util.List;
import java.util.Optional;

public interface HorarioFuncionamentoRepository {

    void salvar(HorarioFuncionamento horario);

    Optional<HorarioFuncionamento> buscarPorId(String id);

    List<HorarioFuncionamento> listarTodos();

    List<HorarioFuncionamento> buscarPorRestauranteId(String restauranteId);

    void deletar(String id);
}
