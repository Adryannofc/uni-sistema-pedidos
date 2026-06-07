package com.pedidos.model.infra.repository.impl;

import com.pedidos.model.entity.CategoriaCardapio;
import com.pedidos.model.repository.CategoriaCardapioRepository;
import jakarta.persistence.EntityManager;

import java.util.*;

public class    CategoriaCardapioRepositoryJPA implements CategoriaCardapioRepository {

    private EntityManager em;

    public CategoriaCardapioRepositoryJPA (EntityManager em) { this.em = em; }

    public void salvar(CategoriaCardapio categoria) {
        try {
            em.getTransaction().begin();
            em.merge(categoria);
            em.getTransaction().commit();
        } catch (Exception e){
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao salvar a categoria no cardapio", e);
        }
    }

    @Override
    public void remover(String id) {
        try {
            em.getTransaction().begin();

            em.createQuery("DELETE FROM cliente_restaurantes_favoritos WHERE CAST(restaurante_id AS varchar) = :restId")
                    .setParameter("restId", id)
                    .executeUpdate();

            em.createQuery("DELETE FROM itens_pedido WHERE produto_id IN (" +
                            "SELECT id FROM produtos WHERE categoria_cardapio_id IN (" +
                            "SELECT id FROM categorias_cardapio WHERE CAST(restaurante_id AS varchar) = :restId))")
                    .setParameter("restId", id)
                    .executeUpdate();

            em.createQuery("DELETE FROM produtos WHERE categoria_cardapio_id IN (SELECT id FROM categorias_cardapio WHERE CAST(restaurante_id AS varchar) = :restId)")
                    .setParameter("restId", id)
                    .executeUpdate();

            em.createQuery("DELETE FROM categorias_cardapio WHERE CAST(restaurante_id AS varchar) = :restId")
                    .setParameter("restId", id)
                    .executeUpdate();

            em.createQuery("DELETE FROM areas_entrega WHERE CAST(restaurante_id AS varchar) = :restId")
                    .setParameter("restId", id)
                    .executeUpdate();

            em.createQuery("DELETE FROM horarios_funcionamento WHERE CAST(restaurante_id AS varchar) = :restId")
                    .setParameter("restId", id)
                    .executeUpdate();

            em.getTransaction().commit();
            System.out.println("DEBUG: Todas as dependências do restaurante foram limpas com sucesso.");
        }
        catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erro ao limpar dependências do restaurante: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<CategoriaCardapio> buscarPorId(String id) {
        return Optional.ofNullable(em.find(CategoriaCardapio.class, id));
    }

    @Override
    public List<CategoriaCardapio> buscarPorRestauranteId(String restauranteId){
        return em.createQuery("select c from CategoriaCardapio c where c.restaurante.id = :restauranteId",CategoriaCardapio.class)
                    .setParameter("restauranteId", restauranteId)
                    .getResultList();
    }

    @Override
    public List<CategoriaCardapio> listarTodos() {
        return em.createQuery("select u from CategoriaCardapio u", CategoriaCardapio.class).getResultList();
    }
}