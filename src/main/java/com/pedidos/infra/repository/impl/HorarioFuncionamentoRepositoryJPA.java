package com.pedidos.infra.repository.impl;

import com.pedidos.domain.entities.AreaEntrega;
import com.pedidos.domain.entities.HorarioFuncionamento;
import com.pedidos.domain.entities.Produto;
import com.pedidos.domain.repository.HorarioFuncionamentoRepository;
import jakarta.persistence.EntityManager;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

public class HorarioFuncionamentoRepositoryJPA implements HorarioFuncionamentoRepository {

    private final EntityManager em;

    // Motor para realizarmos as Quarrys
    public HorarioFuncionamentoRepositoryJPA(EntityManager em) {
        this.em = em;
    }

    @Override
    public void salvar(HorarioFuncionamento horarioFuncionamento) {
        try {
            em.getTransaction().begin();
            em.merge(horarioFuncionamento);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erro ao salvar produto", e);
        }
    }

    @Override
    public Optional<HorarioFuncionamento> buscarPorId(String id) {
        return Optional.ofNullable(em.find(HorarioFuncionamento.class, id));
    }

    @Override
    public List<HorarioFuncionamento> buscarPorRestauranteId(String restauranteId) {
        return em.createQuery("SELECT h FROM HorarioFuncionamento h WHERE h.restaurante.id = :rid", HorarioFuncionamento.class)
                .setParameter("rid", restauranteId)
                .getResultList();
    }

    public List<HorarioFuncionamento> buscarPorRestauranteEdiaSemana(String restauranteId, DayOfWeek diaSemana) {
        return em.createQuery("SELECT h FROM HorarioFuncionamento h WHERE h.restaurante.id = :rid AND h.diaSemana = :dia", HorarioFuncionamento.class)
                .setParameter("rid", restauranteId)
                .setParameter("dia", diaSemana)
                .getResultList();
    }

    @Override
    public List<HorarioFuncionamento> listarTodos() {
        return em.createQuery("SELECT h FROM HorarioFuncionamento h", HorarioFuncionamento.class)
                .getResultList();
    }

    @Override
    public void deletar(String id) {
        try {
            em.getTransaction().begin();
            HorarioFuncionamento horarioFuncionamento = em.find(HorarioFuncionamento.class, id);
            if (horarioFuncionamento != null) {
                em.remove(horarioFuncionamento);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao deletar Horario de Funcionamento", e);
        }
    }
}
