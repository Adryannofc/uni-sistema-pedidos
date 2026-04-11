package com.pedidos.infra.repository.impl;

import com.pedidos.domain.entities.UsuarioEntity;
import com.pedidos.domain.repository.AdminRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.*;

public class AdminRepositoryJPA implements AdminRepository {

    private EntityManager em;

    public AdminRepositoryJPA (EntityManager em) { this.em = em; }

    public void salvar(UsuarioEntity usuarioEntity) {
        try {
            em.getTransaction().begin();
            em.merge(usuarioEntity);
            em.getTransaction().commit();
        } catch (Exception e){
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao salvar o usuário", e);
        }
    }

    public void atualizar(UsuarioEntity usuarioEntity) {
        try {
            em.getTransaction().begin();
            em.merge(usuarioEntity);
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
            UsuarioEntity usuarioEntity = em.find(UsuarioEntity.class, id);
            if (usuarioEntity != null) {
                em.remove(usuarioEntity);
            }
            em.getTransaction().commit();
        }
        catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao remover admin", e);
        }
    }

    @Override
    public Optional<UsuarioEntity> buscarPorId(String id) {
        return Optional.ofNullable(em.find(UsuarioEntity.class, id));
    }

    @Override
    public Optional<UsuarioEntity> buscarPorEmail(String email){
        try {
            UsuarioEntity usuarioEntity = em.createQuery("select u from Usuario u where u.email = :email", UsuarioEntity.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return Optional.of(usuarioEntity);
        } catch (NoResultException e){
            return Optional.empty();
        }
    }

    @Override
    public UsuarioEntity buscarPorEmailSenha(String email, String senha) {
        try {
            return em.createQuery("select u from Usuario u where u.email = :email and u.senhaHash = :senha", UsuarioEntity.class)
                    .setParameter("email", email)
                    .setParameter("senha", senha)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }

    @Override
    public List<UsuarioEntity> listarTodos() {
        return em.createQuery("select u from Usuario u", UsuarioEntity.class).getResultList();
    }
}

