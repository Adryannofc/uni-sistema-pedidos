package com.pedidos.infra.repository.impl;

import com.pedidos.domain.model.Cliente;
import com.pedidos.domain.model.Usuario;
import com.pedidos.domain.repository.ClienteRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.*;

public class ClienteRepositoryJPA implements ClienteRepository {

    private final EntityManager em;

    public ClienteRepositoryJPA(EntityManager em){this.em = em;}

    @Override
    public void salvar(Usuario usuario){
        try {
            em.getTransaction().begin();
            em.persist(usuario);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao salvar cliente", e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorId (String id){
        return Optional.ofNullable(em.find(Usuario.class, id));
    }

    @Override
    public Optional<Usuario> buscarPorEmail (String email){
        try {
            Usuario usuario = em.createQuery("SELECT u FROM Cliente u WHERE LOWER(u.email) = LOWER(:email)", Usuario.class)
                    .setParameter("email", email)
                    .getSingleResult();
                return  Optional.of(usuario);
        } catch (NoResultException e) {
            return Optional.empty();

        }
    }

    @Override
    public List<Usuario> listarTodos(){
        return em.createQuery("SELECT u FROM Cliente u", Usuario.class).getResultList();
    }

    @Override
    public void deletar (String id){
        try {
            em.getTransaction().begin();
            Usuario usuario = em.find(Cliente.class, id);
            if (usuario != null){
                em.remove(usuario);
            }
            em.getTransaction().commit();
        }catch (Exception e){
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao deletar cliente", e);
        }
    }

    @Override
    public Usuario buscarPorEmailSenha(String email, String senhaHash){
        try {
            return em.createQuery("SELECT u FROM Cliente u WHERE LOWER(u.email) = LOWER(:email) AND u.senhaHash = :senha", Usuario.class)
                    .setParameter("email", email)
                    .setParameter("senha", senhaHash)
                    .getSingleResult();

        }catch (NoResultException e){
            return null;
        }


    }
}



