package com.pedidos.infra.repository.impl;

import com.pedidos.domain.model.CategoriaGlobal;
import com.pedidos.domain.model.Endereco;
import com.pedidos.domain.repository.EnderecoRepository;
import jakarta.persistence.EntityManager;

import java.util.Optional;

public class EnderecoRepositoryJPA implements EnderecoRepository {

    private EntityManager em;

    public EnderecoRepositoryJPA (EntityManager em) { this.em = em; }

    public void salvar(Endereco endereco) {
        try {
            em.getTransaction().begin();
            em.persist(endereco);
            em.getTransaction().commit();
        } catch (Exception e){
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao salvar o usuário", e);
        }
    }

    public void atualizar(Endereco endereco) {
        try {
            em.getTransaction().begin();
            em.merge(endereco);
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
            Endereco endereco = em.find(Endereco.class, clienteId);
            if (clienteId != null) {
                em.remove(clienteId);
            }
            em.getTransaction().commit();
        }
        catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao deletar endereco", e);
        }
    }

    @Override
    public Optional<Endereco> buscarPorCliente(String clienteId) {
        return Optional.ofNullable(em.find(Endereco.class, clienteId));
    }
}
