package com.pedidos.infra.repository.impl;

import com.pedidos.domain.model.Restaurante;
import com.pedidos.domain.model.Usuario;
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
    public void salvar(Usuario usuario)
    {
        try {
            em.getTransaction().begin();
            em.persist(usuario);

            em.getTransaction().commit();

        }catch (Exception e){
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao salvar restaurante", e);
        };

    };

    @Override
    public Optional<Usuario> buscarPorId(String id){
            return Optional.ofNullable(em.find(Usuario.class, id));
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        try {
            return Optional.ofNullable(em.createQuery("select r from Restaurante r where r.email = :email", Restaurante.class)
                    .setParameter("email", email)
                    .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Usuario> listarTodos() {
        return em.createQuery("select r from Restaurante r", Usuario.class).getResultList();
    }

    ;

    @Override
    public List<Restaurante> listarRestaurantes(){
        return em.createQuery("SELECT r FROM Restaurante r", Restaurante.class).getResultList();
    };

    @Override
    public void deletar (String id) {
        try {
            em.getTransaction().begin();
            Restaurante restaurante = em.find(Restaurante.class, id);
            if (restaurante != null) {
                em.remove(restaurante);
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
    public Usuario buscarPorEmailSenha(String email, String senha) {
        try {
            String jpql = "SELECT u FROM Usuario u WHERE u.email = :email AND u.senhaHash = :senha";

            return em.createQuery(jpql, Usuario.class)
                    .setParameter("email", email) // Define o valor do parâmetro :email
                    .setParameter("senha", senha) // Define o valor do parâmetro :senha
                    .getSingleResult(); // Busca apenas um resultado


        } catch (NoResultException e) {

            return null;
        }
    }
}

