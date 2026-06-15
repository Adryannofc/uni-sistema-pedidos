package com.pedidos.model.infra.repository.impl;

import com.pedidos.model.entity.HorarioFuncionamento;
import com.pedidos.model.repository.HorarioFuncionamentoRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class HorarioFuncionamentoRepositoryJPA implements HorarioFuncionamentoRepository {

    private final EntityManager em;

    public HorarioFuncionamentoRepositoryJPA(EntityManager em) {
        this.em = em;
    }

    @Override
    public void salvar(HorarioFuncionamento horario) {
        try {
            em.getTransaction().begin();
            // ID sempre gerado via UUID no construtor — controle de persist vs merge via em.find()
            if (em.find(HorarioFuncionamento.class, horario.getId()) == null) {
                em.merge(horario);
            } else {
                em.merge(horario);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erro ao salvar horário de funcionamento: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<HorarioFuncionamento> buscarPorId(String id) {
        return Optional.ofNullable(em.find(HorarioFuncionamento.class, id));
    }

    @Override
    public List<HorarioFuncionamento> buscarPorRestauranteId(String restauranteId) {
        return em.createQuery(
                "SELECT h FROM HorarioFuncionamento h WHERE h.restaurante.id = :restauranteId",
                HorarioFuncionamento.class
        ).setParameter("restauranteId", restauranteId).getResultList();
    }

    @Override
    public List<HorarioFuncionamento> listarTodos() {
        return em.createQuery("SELECT h FROM HorarioFuncionamento h", HorarioFuncionamento.class)
                .getResultList();
    }

    @Override
    public void remover(String id) {
        try {
            em.getTransaction().begin();
            HorarioFuncionamento horario = em.find(HorarioFuncionamento.class, id);
            if (horario != null) {
                em.remove(horario);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erro ao remover horário: " + e.getMessage(), e);
        }
    }
}


