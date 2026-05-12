package com.pedidos.domain.repository;

import com.pedidos.domain.entities.HorarioFuncionamento;
import java.util.List;
import java.util.Optional;

/**
 * Interface de repositório para HorarioFuncionamento.
 * Define operações de persistência para horários de funcionamento.
 */
public interface HorarioFuncionamentoRepository {

    /**
     * Salva um horário de funcionamento.
     *
     * @param horario horário a ser salvo
     */
    void salvar(HorarioFuncionamento horario);

    /**
     * Busca um horário de funcionamento pelo ID.
     *
     * @param id ID do horário
     * @return Optional contendo o horário, ou vazio se não encontrado
     */
    Optional<HorarioFuncionamento> buscarPorId(String id);

    /**
     * Busca todos os horários de funcionamento de um restaurante.
     *
     * @param restauranteId ID do restaurante
     * @return lista de horários do restaurante
     */
    List<HorarioFuncionamento> buscarPorRestauranteId(String restauranteId);

    /**
     * Lista todos os horários de funcionamento.
     *
     * @return lista com todos os horários
     */
    List<HorarioFuncionamento> listarTodos();

    /**
     * Remove um horário de funcionamento pelo ID.
     *
     * @param id ID do horário a remover
     */
    void remover(String id);
}

