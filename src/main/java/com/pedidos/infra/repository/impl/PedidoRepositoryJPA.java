package com.pedidos.infra.repository.impl;

import com.pedidos.domain.entities.Pedido;
import com.pedidos.domain.enums.StatusPedido;
import com.pedidos.domain.repository.PedidoRepository;
import jakarta.persistence.EntityManager;

import java.util.Optional;
import java.util.List;


public class PedidoRepositoryJPA implements PedidoRepository {

    private final EntityManager em;

    public PedidoRepositoryJPA(EntityManager em){this.em = em;}

    @Override
    public void salvar(Pedido pedido) {
        try {
            em.getTransaction().begin();
            em.persist(pedido);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public Optional<Pedido> buscarPorId(String id){
        return Optional.ofNullable(em.find(Pedido.class, id));
    }

    @Override
    public List<Pedido> listarTodos(){
        return em.createQuery("SELECT p FROM Pedido p", Pedido.class).getResultList();
    }

    @Override
    public List<Pedido> buscarPorCliente(String clienteId) {
        return em.createQuery("SELECT p FROM Pedido p WHERE p.cliente.id = :cid", Pedido.class)
                .setParameter("cid", clienteId).getResultList();
    }

    @Override
    public List<Pedido> listarAtivosPorRestaurante(String restauranteId) {
        return em.createQuery(
                        "SELECT p FROM Pedido p WHERE p.restaurante.id = :rid AND p.status NOT IN (:s1, :s2)", Pedido.class)
                .setParameter("rid", restauranteId)
                .setParameter("s1", StatusPedido.ENTREGUE)
                .setParameter("s2", StatusPedido.CANCELADO)
                .getResultList();
    }

    @Override
    public List<Pedido> filtrarPorStatus(String restauranteId, StatusPedido status) {
        return em.createQuery(
                        "SELECT p FROM Pedido p WHERE p.restaurante.id = :rid AND p.status = :status", Pedido.class)
                .setParameter("rid", restauranteId)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public List<Pedido> buscarPorRestaurante(String restauranteId) {
        return em.createQuery("SELECT p FROM Pedido p WHERE p.restaurante.id = :rid", Pedido.class)
                .setParameter("rid", restauranteId)
                .getResultList();
    }

    @Override
    public List<Pedido> buscarPorStatus(StatusPedido status) {
        return em.createQuery("SELECT p FROM Pedido p WHERE p.status = :status", Pedido.class)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public void deletar(String id) {
        try {
            em.getTransaction().begin();
            Pedido pedido = em.find(Pedido.class, id);
            if (pedido != null) {
                em.remove(pedido);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao deletar pedido", e);
        }
    }

}
