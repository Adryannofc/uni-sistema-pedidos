package com.pedidos.infra.repository.impl;

import com.pedidos.domain.entities.ProdutoEntity;
import com.pedidos.domain.repository.ProdutoRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class ProdutoRepositoryJPA implements ProdutoRepository {

    private final EntityManager em;

    // Motor para realizarmos as Quarrys
    public ProdutoRepositoryJPA(EntityManager em) {
        this.em = em;
    }

    @Override
    public void salvar(ProdutoEntity produtoEntity) {
        try {
            em.getTransaction().begin();

            em.persist(produtoEntity);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erro ao salvar produto", e);
        }
    }

    @Override
    public Optional<ProdutoEntity> buscarPorId(String id) {
        return Optional.ofNullable(em.find(ProdutoEntity.class, id));
    }

    @Override
    public List<ProdutoEntity> listarTodos() {
        return em.createQuery("SELECT p FROM Produto p", ProdutoEntity.class)
                .getResultList();
    }

    @Override
    public void deletar(String id) {
        try {
            em.getTransaction().begin();
            ProdutoEntity produtoEntity = em.find(ProdutoEntity.class, id);
            if (produtoEntity != null) {
                em.remove(produtoEntity);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erro ao deletar produto", e);
        }
    }
}
