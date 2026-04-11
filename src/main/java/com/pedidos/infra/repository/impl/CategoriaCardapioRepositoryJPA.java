package com.pedidos.infra.repository.impl;

import com.pedidos.domain.entities.CategoriaCardapioEntity;
import com.pedidos.domain.repository.CategoriaCardapioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;

import java.util.*;

public class CategoriaCardapioRepositoryJPA implements CategoriaCardapioRepository {

    private EntityManager em;

    public CategoriaCardapioRepositoryJPA (EntityManager em) { this.em = em; }

    public void salvar(CategoriaCardapioEntity categoria) {
        try {
            em.getTransaction().begin();
            em.persist(categoria);
            em.getTransaction().commit();
        } catch (Exception e){
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao salvar a categoria no cardapio", e);
        }
    }

    public void atualizar(CategoriaCardapioEntity categoria) {
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
            CategoriaCardapioEntity categoria = em.find(CategoriaCardapioEntity.class, id);
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
    public Optional<CategoriaCardapioEntity> buscarPorId(String id) {
        try {
            return Optional.ofNullable(em.find(CategoriaCardapioEntity.class, id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("ID inválido para busca: " + id, e);
        } catch (PersistenceException e) {
            throw new RuntimeException("Erro ao buscar categoria com id: " + id, e);
        }
    }

    @Override
    public List<CategoriaCardapioEntity> buscarPorRestauranteId(String restauranteId) {
        try {
            return em.createQuery(
                            "select c from CategoriaCardapio c where c.restaurante.id = :restauranteId",
                            CategoriaCardapioEntity.class)
                    .setParameter("restauranteId", restauranteId)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public List<CategoriaCardapioEntity> listarTodos() {
        try {
            return em.createQuery("select u from CategoriaCardapio u", CategoriaCardapioEntity.class)
                    .getResultList();
        } catch (PersistenceException e) {
            throw new RuntimeException("Erro ao listar categorias do cardápio", e);
        }
    }
}