package com.pedidos.application.service;

import com.pedidos.domain.enums.StatusPedido;
import com.pedidos.domain.entities.*;
import com.pedidos.domain.repository.PedidoRepository;

import java.math.BigDecimal;
import java.util.List;

public class PedidoService {

    private final PedidoRepository pedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    /**
     * Cria e persiste um novo pedido.
     *
     * @param clienteId         id do cliente que realizou o pedido
     * @param restauranteId     id do restaurante do pedido
     * @param carrinhoEntity          itens selecionados pelo cliente
     * @param enderecoEntityEntrega   endereço de entrega
     * @param codigoConfirmacao código para confirmar a entrega
     * @return pedido criado
     */
    public PedidoEntity criarPedido(String clienteId, String restauranteId, CarrinhoEntity carrinhoEntity, EnderecoEntity enderecoEntityEntrega, String codigoConfirmacao) {
        try {
            if (enderecoEntityEntrega == null) {
                throw new IllegalArgumentException("Informe um endereço de entrega antes de finalizar o pedido.");
            }

            PedidoEntity pedidoEntity = new PedidoEntity(null, clienteId, restauranteId, BigDecimal.ZERO);
            pedidoEntity.setEnderecoEntrega(enderecoEntityEntrega);
            pedidoEntity.setCodigoConfirmacao(codigoConfirmacao);
            carrinhoEntity.getItens().forEach(pedidoEntity::adicionarItem);
            pedidoEntity.calcularTotal();
            pedidoRepository.salvar(pedidoEntity);
            return pedidoEntity;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Confirma a entrega do pedido via código informado pelo cliente.
     *
     * @param pedidoId       id do pedido
     * @param codigoDigitado código informado pelo cliente
     * @throws IllegalArgumentException se o pedido não for encontrado ou o código estiver incorreto
     * @throws IllegalStateException    se o pedido não estiver no status SAIU_PARA_ENTREGA
     */
    public void confirmarEntrega(String pedidoId, String codigoDigitado) {
        try {
            PedidoEntity pedidoEntity = pedidoRepository.buscarPorId(pedidoId)
                    .orElseThrow(() -> new IllegalArgumentException("Pedido nao encontrado."));

            if (pedidoEntity.getStatus() != StatusPedido.SAIU_PARA_ENTREGA) {
                throw new IllegalStateException("Pedido nao esta em status de entrega.");
            }
            if (!pedidoEntity.getCodigoConfirmacao().equals(codigoDigitado)) {
                throw new IllegalArgumentException("Codigo incorreto.");
            }

            pedidoEntity.setStatus(StatusPedido.ENTREGUE);
            pedidoRepository.salvar(pedidoEntity);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Atualiza o status do pedido por parte do restaurante.
     *
     * @param pedidoId   id do pedido
     * @param novoStatus novo status a ser aplicado
     * @throws IllegalArgumentException se o pedido não for encontrado
     * @throws IllegalStateException    se a transição de status for inválida
     */
    public void atualizarStatus(String pedidoId, StatusPedido novoStatus) {
        try {
            PedidoEntity pedidoEntity = pedidoRepository.buscarPorId(pedidoId)
                    .orElseThrow(() -> new IllegalArgumentException("Pedido nao encontrado."));

            validarTransicao(pedidoEntity.getStatus(), novoStatus);
            pedidoEntity.setStatus(novoStatus);
            pedidoRepository.salvar(pedidoEntity);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Valida se a transição de status é permitida.
     *
     * @param atual status atual do pedido
     * @param novo  novo status solicitado
     * @throws IllegalStateException se a transição for inválida
     */
    private void validarTransicao(StatusPedido atual, StatusPedido novo) {
        try {
            switch (atual) {
                case AGUARDANDO_CONFIRMACAO:
                    if (novo == StatusPedido.CONFIRMADO || novo == StatusPedido.CANCELADO) return;
                    break;
                case CONFIRMADO:
                    if (novo == StatusPedido.EM_PREPARO || novo == StatusPedido.CANCELADO) return;
                    break;
                case EM_PREPARO:
                    if (novo == StatusPedido.SAIU_PARA_ENTREGA || novo == StatusPedido.CANCELADO) return;
                    break;
                case SAIU_PARA_ENTREGA:
                    if (novo == StatusPedido.ENTREGUE) return;
                    break;
                case ENTREGUE:
                    throw new IllegalStateException("Pedido já entregue — status não pode ser alterado.");
                case CANCELADO:
                    throw new IllegalStateException("Pedido cancelado — status não pode ser alterado.");
            }
            throw new IllegalStateException("Transição de status inválida.");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Busca um pedido pelo ID.
     *
     * @param pedidoId id do pedido
     * @return pedido encontrado
     * @throws IllegalArgumentException se o pedido não for encontrado
     */
    public PedidoEntity buscarPorId(String pedidoId) {
        try {
            return pedidoRepository.buscarPorId(pedidoId)
                    .orElseThrow(() -> new IllegalArgumentException("Pedido nao encontrado."));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Lista todos os pedidos de um cliente.
     *
     * @param clienteId id do cliente
     * @return lista de pedidos do cliente
     */
    public List<PedidoEntity> listarPorCliente(String clienteId) {
        try {
            return pedidoRepository.buscarPorCliente(clienteId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Lista todos os pedidos de um restaurante.
     *
     * @param restauranteId id do restaurante
     * @return lista de pedidos do restaurante
     */
    public List<PedidoEntity> listarPorRestaurante(String restauranteId) {
        try {
            return pedidoRepository.buscarPorRestaurante(restauranteId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}