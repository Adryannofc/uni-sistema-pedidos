package com.pedidos.infra.repository.impl;

import com.pedidos.domain.entities.EnderecoEntity;
import com.pedidos.domain.repository.EnderecoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.Optional;

public class EnderecoRepositoryJPA implements EnderecoRepository {

    private EntityManager em;

    public EnderecoRepositoryJPA (EntityManager em) { this.em = em; }

    public void salvar(EnderecoEntity enderecoEntity) {
        try {
            em.getTransaction().begin();
            em.persist(enderecoEntity);
            em.getTransaction().commit();
        } catch (Exception e){
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao salvar o usuário", e);
        }
    }

    public void atualizar(EnderecoEntity enderecoEntity) {
        try {
            em.getTransaction().begin();
            em.merge(enderecoEntity);
            em.getTransaction().commit();
        } catch (Exception e){
            em.getTransaction().rollback();
            throw new RuntimeException("Erro em atualizar o usuário", e);
        }
    }

    @Override
    public void remover(String clienteId) {
        try {
            em.getTransaction().begin();
            EnderecoEntity enderecoEntity = em.find(EnderecoEntity.class, clienteId);
            if (enderecoEntity != null) {
                em.remove(enderecoEntity);
            }
            em.getTransaction().commit();
        }
        catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao deletar endereco", e);
        }
    }

    @Override
    public Optional<EnderecoEntity> buscarPorCliente(String clienteId) {
        try {
            return Optional.ofNullable(em.createQuery("select e from Endereco e where e.cliente.id = :cid", EnderecoEntity.class)
                    .setParameter("cid", clienteId)
                    .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
