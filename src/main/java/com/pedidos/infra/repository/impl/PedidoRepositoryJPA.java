package com.pedidos.infra.repository.impl;

import com.pedidos.domain.entities.PedidoEntity;
import com.pedidos.domain.enums.StatusPedido;
import com.pedidos.domain.repository.PedidoRepository;
import jakarta.persistence.EntityManager;

import java.util.Optional;
import java.util.List;


public class PedidoRepositoryJPA implements PedidoRepository {

    private final EntityManager em;

    public PedidoRepositoryJPA(EntityManager em){this.em = em;}

    @Override
    public void salvar(PedidoEntity pedidoEntity){
        try {
            em.getTransaction().begin();
            em.persist(pedidoEntity);
            em.getTransaction().commit();
        }catch (Exception e){
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao salvar pedido", e);
        }
    }

    @Override
    public Optional<PedidoEntity> buscarPorId(String id){
        return Optional.ofNullable(em.find(PedidoEntity.class, id));
    }

    @Override
    public List<PedidoEntity> listarTodos(){
        return em.createQuery("SELECT p FROM Pedido p", PedidoEntity.class).getResultList();
    }

    @Override
    public List<PedidoEntity> buscarPorCliente(String clienteId) {
        return em.createQuery("SELECT p FROM Pedido p WHERE p.clienteId = :cid", PedidoEntity.class)
                .setParameter("cid", clienteId).getResultList();
    }

    @Override
    public List<PedidoEntity> listarAtivosPorRestaurante(String restauranteId) {
        return em.createQuery(
                        "SELECT p FROM Pedido p WHERE p.restauranteId = :rid AND p.status NOT IN (:s1, :s2)", PedidoEntity.class)
                .setParameter("rid", restauranteId)
                .setParameter("s1", StatusPedido.ENTREGUE)
                .setParameter("s2", StatusPedido.CANCELADO)
                .getResultList();
    }

    @Override
    public List<PedidoEntity> filtrarPorStatus(String restauranteId, StatusPedido status) {
        return em.createQuery(
                        "SELECT p FROM Pedido p WHERE p.restauranteId = :rid AND p.status = :status", PedidoEntity.class)
                .setParameter("rid", restauranteId)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public List<PedidoEntity> buscarPorRestaurante(String restauranteId) {
        return em.createQuery("SELECT p FROM Pedido p WHERE p.restauranteId = :rid", PedidoEntity.class)
                .setParameter("rid", restauranteId)
                .getResultList();
    }

    @Override
    public List<PedidoEntity> buscarPorStatus(StatusPedido status) {
        return em.createQuery("SELECT p FROM Pedido p WHERE p.status = :status", PedidoEntity.class)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public void deletar(String id) {
        try {
            em.getTransaction().begin();
            PedidoEntity pedidoEntity = em.find(PedidoEntity.class, id);
            if (pedidoEntity != null) {
                em.remove(pedidoEntity);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao deletar pedido", e);
        }
    }

}
