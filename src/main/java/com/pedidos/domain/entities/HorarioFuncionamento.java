package com.pedidos.domain.entities;

import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Entidade que representa o horário de funcionamento de um restaurante para um dia da semana.
 */
@Entity
@Table(name = "horarios_funcionamento")
public class HorarioFuncionamento {

    @Id
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurante_id", nullable = false)
    private Restaurante restaurante;

    @Column(name = "dia_semana", nullable = false)
    @Enumerated(EnumType.STRING)
    private DayOfWeek diaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fim", nullable = false)
    private LocalTime horaFim;

    protected HorarioFuncionamento() {
        this.id = UUID.randomUUID().toString();
    }

    /**
     * Construtor para criar um novo horário de funcionamento.
     *
     * @param restaurante restaurante ao qual pertence este horário
     * @param diaSemana dia da semana
     * @param horaInicio hora de início do funcionamento
     * @param horaFim hora de fim do funcionamento
     */
    public HorarioFuncionamento(Restaurante restaurante, DayOfWeek diaSemana, LocalTime horaInicio, LocalTime horaFim) {
        this();
        this.restaurante = restaurante;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
    }

    /**
     * Verifica se o restaurante está aberto em um determinado horário.
     *
     * @param horario horário a verificar
     * @return true se o horário está dentro da janela de funcionamento
     */
    public boolean estaAberto(LocalTime horario) {
        return !horario.isBefore(horaInicio) && !horario.isAfter(horaFim);
    }

    // Getters e Setters

    public String getId() {
        return id;
    }

    public Restaurante getRestaurante() {
        return restaurante;
    }

    public void setRestaurante(Restaurante restaurante) {
        this.restaurante = restaurante;
    }

    public DayOfWeek getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(DayOfWeek diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(LocalTime horaFim) {
        this.horaFim = horaFim;
    }

    @Override
    public String toString() {
        return String.format("HorarioFuncionamento{dia=%s, %s-%s}", diaSemana, horaInicio, horaFim);
    }
}



