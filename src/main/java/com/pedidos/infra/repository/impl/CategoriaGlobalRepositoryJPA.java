package com.pedidos.infra.repository.impl;

import com.pedidos.domain.model.CategoriaCardapio;
import com.pedidos.domain.model.CategoriaGlobal;
import com.pedidos.domain.repository.CategoriaGlobalRepository;
import jakarta.persistence.EntityManager;

import java.util.*;

public class CategoriaGlobalRepositoryJPA implements CategoriaGlobalRepository {
    private EntityManager em;

    public CategoriaGlobalRepositoryJPA (EntityManager em) { this.em = em; }

    public void salvar(CategoriaGlobal categoria) {
        try {
            em.getTransaction().begin();
            em.persist(categoria);
            em.getTransaction().commit();
        } catch (Exception e){
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao salvar o usuário", e);
        }
    }

    public void atualizar(CategoriaGlobal categoria) {
        try {
            em.getTransaction().begin();
            em.merge(categoria);
            em.getTransaction().commit();
        } catch (Exception e){
            em.getTransaction().rollback();
            throw new RuntimeException("Erro em atualizar o usuário", e);
        }
    }

    @Override
    public void remover(String id) {
        try {
            em.getTransaction().begin();
            CategoriaGlobal categoria = em.find(CategoriaGlobal.class, id);
            if (categoria != null) {
                em.remove(categoria);
            }
            em.getTransaction().commit();
        }
        catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao deletar admin", e);
        }
    }

    @Override
    public Optional<CategoriaGlobal> buscarPorId(String id) {
        return Optional.ofNullable(em.find(CategoriaGlobal.class, id));
    }

    @Override
    public Optional<CategoriaGlobal> buscarPorNome(String nome) {
        return Optional.ofNullable(em.find(CategoriaGlobal.class, nome));
    }

    @Override
    public List<CategoriaGlobal> listarTodos() {
        return em.createQuery("select c from categorias_globais c").getResultList();
    }
}