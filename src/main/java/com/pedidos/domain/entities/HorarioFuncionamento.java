package com.pedidos.domain.entities;

import jakarta.persistence.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table (name = "horarios_funcionamento")
public class HorarioFuncionamento {

    @Id
    @Column (name = "Id")
    private String id;

    @ManyToOne
    @JoinColumn (name = "restaurante_id", nullable = false)
    private Restaurante restaurante;

    @Enumerated (EnumType.STRING)
    @Column (name = "dia_semana")
    private DayOfWeek diaSemana;

    @Column (name = "hora_inicio")
    private LocalTime horaInicio;

    @Column (name = "hora_fim")
    private LocalTime horaFim;


    public HorarioFuncionamento() {
    }

    public HorarioFuncionamento(Restaurante restaurante, DayOfWeek diaSemana, LocalTime horaInicio, LocalTime horaFim) {
        this.restaurante = restaurante;
        this.horaFim = horaFim;
        this.horaInicio = horaInicio;
        this.diaSemana = diaSemana;
        this.id = UUID.randomUUID().toString();
    }

    public String getRestauranteId() { return restaurante != null ? restaurante.getId() : null; }

    public void setRestaurante(Restaurante restaurante) {
        this.restaurante = restaurante;
    }

    public String getId() {
        return id;
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

    public boolean estaAberto(LocalDateTime dataHora) {
            return dataHora.getDayOfWeek() == diaSemana &&
                    !dataHora.toLocalTime().isBefore(horaInicio) &&
                    !dataHora.toLocalTime().isAfter(horaFim);
        }

    @Override
    public String toString() {
        return "HorarioFuncionamento{" +
                "diaSemana=" + diaSemana +
                ", horaInicio=" + horaInicio +
                ", horaFim=" + horaFim +
                '}';
    }
}

