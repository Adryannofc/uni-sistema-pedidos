package com.pedidos.infra.repository.impl;

import com.pedidos.domain.entities.Produto;
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
    public void salvar(Produto produto) {
        try {
            em.getTransaction().begin();

            em.merge(produto);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erro ao salvar produto", e);
        }
    }

    @Override
    public Optional<Produto> buscarPorId(String id) {
        return Optional.ofNullable(em.find(Produto.class, id));
    }

    @Override
    public List<Produto> listarTodos() {
        return em.createQuery("SELECT p FROM Produto p", Produto.class)
                .getResultList();
    }

    @Override
    public void deletar(String id) {
        try {
            em.getTransaction().begin();
            Produto produto = em.find(Produto.class, id);
            if (produto != null) {
                em.remove(produto);
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
