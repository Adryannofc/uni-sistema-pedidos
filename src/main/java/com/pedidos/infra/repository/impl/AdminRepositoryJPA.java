package com.pedidos.infra.repository.impl;

import com.pedidos.domain.model.Usuario;
import com.pedidos.domain.repository.AdminRepository;
import com.pedidos.domain.repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.*;

public class AdminRepositoryJPA implements AdminRepository {

    private EntityManager em;

    public AdminRepositoryJPA (EntityManager em) { this.em = em; }

    public void salvar(Usuario usuario) {
        try {
            em.getTransaction().begin();
            em.merge(usuario);
            em.getTransaction().commit();
        } catch (Exception e){
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao salvar o usuário", e);
        }
    }

    public void atualizar(Usuario usuario) {
        try {
            em.getTransaction().begin();
            em.merge(usuario);
            em.getTransaction().commit();
        } catch (Exception e){
            em.getTransaction().rollback();
            throw new RuntimeException("Erro em atualizar o usuário", e);
        }
    }

    @Override
    public void deletar(String id) {
        try {
            em.getTransaction().begin();
            Usuario usuario = em.find(Usuario.class, id);
            if (usuario != null) {
                em.remove(usuario);
            }
            em.getTransaction().commit();
        }
        catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao remover admin", e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorId(String id) {
        return Optional.ofNullable(em.find(Usuario.class, id));
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email){
        try {
            Usuario usuario = em.createQuery("select u from Usuario u where u.email = :email",Usuario.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return Optional.of(usuario);
        } catch (NoResultException e){
            return Optional.empty();
        }
    }

    @Override
    public Usuario buscarPorEmailSenha(String email, String senha) {
        try {
            return em.createQuery("select u from Usuario u where u.email = :email and u.senhaHash = :senha", Usuario.class)
                    .setParameter("email", email)
                    .setParameter("senha", senha)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }

    @Override
    public List<Usuario> listarTodos() {
        return em.createQuery("select u from Usuario u", Usuario.class).getResultList();
    }
}

