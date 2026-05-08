package com.pedidos.application.service;

import com.pedidos.domain.entities.HorarioFuncionamento;
import com.pedidos.domain.entities.Restaurante;
import com.pedidos.domain.repository.HorarioFuncionamentoRepository;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HorarioService {

    private final HorarioFuncionamentoRepository horarioRepository;

    public HorarioService(HorarioFuncionamentoRepository horarioRepository) {
        this.horarioRepository = horarioRepository;
    }

    public HorarioFuncionamento criarHorario(Restaurante restaurante,
                                             DayOfWeek diaSemana,
                                             LocalTime horaInicio,
                                             LocalTime horaFim) {
        if (diaSemana == null)
            throw new IllegalArgumentException("Dia da semana é obrigatório");
        if (horaInicio == null)
            throw new IllegalArgumentException("Hora de início é obrigatória");
        if (horaFim == null)
            throw new IllegalArgumentException("Hora de fim é obrigatória");
        if (!horaFim.isAfter(horaInicio))
            throw new IllegalArgumentException("Hora de fim deve ser posterior à hora de início");

        boolean jaExiste = horarioRepository.buscarPorRestauranteId(restaurante.getId())
                .stream()
                .anyMatch(h -> h.getDiaSemana() == diaSemana);

        if (jaExiste)
            throw new IllegalStateException("Já existe horário cadastrado para este dia da semana");

        HorarioFuncionamento horario = new HorarioFuncionamento(
                restaurante, diaSemana, horaInicio, horaFim);
        horarioRepository.salvar(horario);
        return horario;
    }

    public List<HorarioFuncionamento> listarHorarioPorRestaurante(String restauranteId) {
        return horarioRepository.buscarPorRestauranteId(restauranteId)
                .stream()
                .sorted(Comparator.comparing(HorarioFuncionamento::getDiaSemana))
                .collect(Collectors.toList());
    }

    public HorarioFuncionamento buscarPorId(String id) {
        return horarioRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Horário não encontrado"));
    }

    public void editarHorario(String id,
                              LocalTime novaHoraInicio,
                              LocalTime novaHoraFim) {
        if (novaHoraInicio == null || novaHoraFim == null)
            throw new IllegalArgumentException("Horários não podem ser nulos");
        if (!novaHoraFim.isAfter(novaHoraInicio))
            throw new IllegalArgumentException("Hora de fim deve ser posterior à hora de início");

        HorarioFuncionamento horario = buscarPorId(id);
        horario.setHoraInicio(novaHoraInicio);
        horario.setHoraFim(novaHoraFim);
        horarioRepository.salvar(horario);
    }

    public void removerHorario(String id) {
        buscarPorId(id);
        horarioRepository.deletar(id);
    }

    public boolean restauranteEstaAberto(String restauranteId, LocalDateTime agora) {
        return horarioRepository.buscarPorRestauranteId(restauranteId)
                .stream()
                .anyMatch(h -> h.estaAberto(agora));
    }

    public List<DayOfWeek> listarDiasDisponiveis(String restauranteId) {
        Set<DayOfWeek> diasUsados = horarioRepository
                .buscarPorRestauranteId(restauranteId)
                .stream()
                .map(HorarioFuncionamento::getDiaSemana)
                .collect(Collectors.toSet());

        return Arrays.stream(DayOfWeek.values())
                .filter(d -> !diasUsados.contains(d))
                .collect(Collectors.toList());
    }
}