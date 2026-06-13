package com.pedidos.controller;

import com.pedidos.model.entity.HorarioFuncionamento;
import com.pedidos.model.entity.Restaurante;
import com.pedidos.model.service.HorarioService;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public class HorarioController {

    private final HorarioService horarioService;

    public HorarioController(HorarioService horarioService) {
        this.horarioService = horarioService;
    }

    public HorarioFuncionamento criarHorario(Restaurante restaurante, DayOfWeek diaSemana, LocalTime horaInicio, LocalTime horaFim) {
        return horarioService.criarHorario(restaurante, diaSemana, horaInicio, horaFim);
    }

    public List<HorarioFuncionamento> listarPorRestaurante(String restauranteId) {
        return horarioService.listarHorarioPorRestaurante(restauranteId);
    }

    public HorarioFuncionamento buscarPorId(String id) {
        return horarioService.buscarPorId(id);
    }

    public void editarHorario(String id, LocalTime novaHoraInicio, LocalTime novaHoraFim) {
        horarioService.editarHorario(id, novaHoraInicio, novaHoraFim);
    }

    public void removerHorario(String id) {
        horarioService.removerHorario(id);
    }

    public boolean restauranteEstaAberto(String restauranteId, LocalTime agora) {
        return horarioService.restauranteEstaAberto(restauranteId, agora);
    }

    public List<DayOfWeek> listarDiasDisponiveis(String restauranteId) {
        return horarioService.listarDiasDisponiveis(restauranteId);
    }
}
