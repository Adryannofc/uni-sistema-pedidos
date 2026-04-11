package com.pedidos.infra.repository.impl;

import com.pedidos.domain.entities.ClienteEntity;
import com.pedidos.domain.entities.UsuarioEntity;
import com.pedidos.domain.repository.ClienteRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.*;

public class ClienteRepositoryJPA implements ClienteRepository {

    private final EntityManager em;

    public ClienteRepositoryJPA(EntityManager em){this.em = em;}

    @Override
    public void salvar(UsuarioEntity usuarioEntity){
        try {
            em.getTransaction().begin();
            em.persist(usuarioEntity);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao salvar cliente", e);
        }
    }

    @Override
    public Optional<UsuarioEntity> buscarPorId (String id){
        return Optional.ofNullable(em.find(UsuarioEntity.class, id));
    }

    @Override
    public Optional<UsuarioEntity> buscarPorEmail (String email){
        try {
            UsuarioEntity usuarioEntity = em.createQuery("SELECT u FROM Cliente u WHERE LOWER(u.email) = LOWER(:email)", UsuarioEntity.class)
                    .setParameter("email", email)
                    .getSingleResult();
                return  Optional.of(usuarioEntity);
        } catch (NoResultException e) {
            return Optional.empty();

        }
    }

    @Override
    public List<UsuarioEntity> listarTodos(){
        return em.createQuery("SELECT u FROM Cliente u", UsuarioEntity.class).getResultList();
    }

    @Override
    public void deletar (String id){
        try {
            em.getTransaction().begin();
            UsuarioEntity usuarioEntity = em.find(ClienteEntity.class, id);
            if (usuarioEntity != null){
                em.remove(usuarioEntity);
            }
            em.getTransaction().commit();
        }catch (Exception e){
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao deletar cliente", e);
        }
    }

    @Override
    public UsuarioEntity buscarPorEmailSenha(String email, String senhaHash){
        try {
            return em.createQuery("SELECT u FROM Cliente u WHERE LOWER(u.email) = LOWER(:email) AND u.senhaHash = :senha", UsuarioEntity.class)
                    .setParameter("email", email)
                    .setParameter("senha", senhaHash)
                    .getSingleResult();

        }catch (NoResultException e){
            return null;
        }


    }
}



