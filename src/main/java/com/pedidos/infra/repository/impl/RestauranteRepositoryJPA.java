package com.pedidos.infra.repository.impl;

import com.pedidos.domain.entities.RestauranteEntity;
import com.pedidos.domain.entities.UsuarioEntity;
import com.pedidos.domain.repository.RestauranteRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.Optional;

public class RestauranteRepositoryJPA implements RestauranteRepository {

    private final EntityManager em;

    public RestauranteRepositoryJPA (EntityManager em)
    {
        this.em = em;
    };


    @Override
    public void salvar(UsuarioEntity usuarioEntity)
    {
        try {
            em.getTransaction().begin();
            em.persist(usuarioEntity);

            em.getTransaction().commit();

        }catch (Exception e){
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao salvar restaurante", e);
        };

    };

    @Override
    public Optional<UsuarioEntity> buscarPorId(String id){
            return Optional.ofNullable(em.find(UsuarioEntity.class, id));
    }

    @Override
    public Optional<UsuarioEntity> buscarPorEmail(String email) {
        try {
            return Optional.ofNullable(em.createQuery("select r from Restaurante r where r.email = :email", RestauranteEntity.class)
                    .setParameter("email", email)
                    .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<UsuarioEntity> listarTodos() {
        return em.createQuery("select r from Restaurante r", UsuarioEntity.class).getResultList();
    }

    ;

    @Override
    public List<RestauranteEntity> listarRestaurantes(){
        return em.createQuery("SELECT r FROM Restaurante r", RestauranteEntity.class).getResultList();
    };

    @Override
    public void deletar (String id) {
        try {
            em.getTransaction().begin();
            RestauranteEntity restauranteEntity = em.find(RestauranteEntity.class, id);
            if (restauranteEntity != null) {
                em.remove(restauranteEntity);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erro ao deletar produto", e);
        }
    }

    @Override
    public UsuarioEntity buscarPorEmailSenha(String email, String senha) {
        try {
            String jpql = "SELECT u FROM Usuario u WHERE u.email = :email AND u.senhaHash = :senha";

            return em.createQuery(jpql, UsuarioEntity.class)
                    .setParameter("email", email) // Define o valor do parâmetro :email
                    .setParameter("senha", senha) // Define o valor do parâmetro :senha
                    .getSingleResult(); // Busca apenas um resultado


        } catch (NoResultException e) {

            return null;
        }
    }
}

