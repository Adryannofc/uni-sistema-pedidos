package com.pedidos.model.infra.repository.impl;

import com.pedidos.model.entity.AreaEntrega;
import com.pedidos.model.repository.AreaEntregaRepository;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class AreaEntregaRepositoryJPA implements AreaEntregaRepository {

    private EntityManager em;

    public AreaEntregaRepositoryJPA(EntityManager em) { this.em = em; }

    @Override
    public void salvar(AreaEntrega area) {
        try {
            em.getTransaction().begin();
            em.merge(area);
            em.getTransaction().commit();
        } catch (Exception e){
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao salvar a area", e);
        }
    }

    @Override
    public Optional<AreaEntrega> buscarPorId(String id) {
        return Optional.ofNullable(em.find(AreaEntrega.class, id));
    }

    @Override
    public List<AreaEntrega> buscarPorRestauranteId(String restauranteId) {
            return em.createQuery("SELECT a FROM AreaEntrega a WHERE a.restaurante.id = :rid", AreaEntrega.class)
                    .setParameter("rid", restauranteId)
                    .getResultList();
    }

    @Override
    public void deletar(String id) {
        try {
            em.getTransaction().begin();
            AreaEntrega area = em.find(AreaEntrega.class, id);
            if (area != null) {
                em.remove(area);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao deletar Area de Entrega", e);
        }
    }
}
