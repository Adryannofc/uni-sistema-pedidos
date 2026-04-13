package com.pedidos.infra.repository.impl;

import com.pedidos.domain.entities.CategoriaCardapio;
import com.pedidos.domain.repository.CategoriaCardapioRepository;
import jakarta.persistence.EntityManager;

import java.util.*;

public class CategoriaCardapioRepositoryJPA implements CategoriaCardapioRepository {

    private EntityManager em;

    public CategoriaCardapioRepositoryJPA (EntityManager em) { this.em = em; }

    public void salvar(CategoriaCardapio categoria) {
        try {
            em.getTransaction().begin();
            em.persist(categoria);
            em.getTransaction().commit();
        } catch (Exception e){
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao salvar a categoria no cardapio", e);
        }
    }

    public void atualizar(CategoriaCardapio categoria) {
        try {
            em.getTransaction().begin();
            em.merge(categoria);
            em.getTransaction().commit();
        } catch (Exception e){
            em.getTransaction().rollback();
            throw new RuntimeException("Erro em atualizar a categoria no cardapio", e);
        }
    }

    @Override
    public void remover(String id) {
        try {
            em.getTransaction().begin();
            CategoriaCardapio categoria = em.find(CategoriaCardapio.class, id);
            if (categoria != null) {
                em.remove(categoria);
            }
            em.getTransaction().commit();
        }
        catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao remover a categoria no cardapio", e);
        }
    }

    @Override
    public Optional<CategoriaCardapio> buscarPorId(String id) {
        return Optional.ofNullable(em.find(CategoriaCardapio.class, id));
    }

    @Override
    public List<CategoriaCardapio> buscarPorRestauranteId(String restauranteId){
        return em.createQuery("select c from CategoriaCardapio c where c.restauranteId = :restauranteId",CategoriaCardapio.class)
                    .setParameter("restauranteId", restauranteId)
                    .getResultList();
    }

    @Override
    public List<CategoriaCardapio> listarTodos() {
        return em.createQuery("select u from CategoriaCardapio u", CategoriaCardapio.class).getResultList();
    }
}