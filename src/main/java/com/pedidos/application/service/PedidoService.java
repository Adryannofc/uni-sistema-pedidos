package com.pedidos.application.service;

import com.pedidos.domain.enums.StatusPedido;
import com.pedidos.domain.model.*;
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
     * @param clienteId id do cliente que realizou o pedido
     * @param restauranteId id do restaurante do pedido
     * @param carrinho itens selecionados pelo cliente
     * @param enderecoEntrega endereço de entrega
     * @param codigoConfirmacao código para confirmar a entrega
     * @return pedido criado
     */
    public Pedido criarPedido(String clienteId, String restauranteId,
                              Carrinho carrinho, Endereco enderecoEntrega,
                              String codigoConfirmacao) {
        if (enderecoEntrega == null) {
            throw new IllegalArgumentException("Informe um endereço de entrega antes de finalizar o pedido.");
        }

        Pedido pedido = new Pedido(null, clienteId, restauranteId, BigDecimal.ZERO);
        pedido.setEnderecoEntrega(enderecoEntrega);
        pedido.setCodigoConfirmacao(codigoConfirmacao);
        carrinho.getItens().forEach(pedido::adicionarItem);
        pedido.calcularTotal();
        pedidoRepository.salvar(pedido);
        return pedido;
    }

    /**
     * Confirma a entrega do pedido via código informado pelo cliente.
     * @param pedidoId id do pedido
     * @param codigoDigitado código informado pelo cliente
     * @throws IllegalArgumentException se o pedido não for encontrado ou o código estiver incorreto
     * @throws IllegalStateException se o pedido não estiver no status SAIU_PARA_ENTREGA
     */
    public void confirmarEntrega(String pedidoId, String codigoDigitado) {
        Pedido pedido = pedidoRepository.buscarPorId(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido nao encontrado."));

        if (pedido.getStatus() != StatusPedido.SAIU_PARA_ENTREGA) {
            throw new IllegalStateException("Pedido nao esta em status de entrega.");
        }
        if (!pedido.getCodigoConfirmacao().equals(codigoDigitado)) {
            throw new IllegalArgumentException("Codigo incorreto.");
        }

        pedido.setStatus(StatusPedido.ENTREGUE);
        pedidoRepository.salvar(pedido);
    }

    /**
     * Atualiza o status do pedido por parte do restaurante.
     * @param pedidoId id do pedido
     * @param novoStatus novo status a ser aplicado
     * @throws IllegalArgumentException se o pedido não for encontrado
     * @throws IllegalStateException se a transição de status for inválida
     */
    public void atualizarStatus(String pedidoId, StatusPedido novoStatus) {
        Pedido pedido = pedidoRepository.buscarPorId(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido nao encontrado."));

        validarTransicao(pedido.getStatus(), novoStatus);
        pedido.setStatus(novoStatus);
        pedidoRepository.salvar(pedido);
    }

    /**
     * Valida se a transição de status é permitida.
     * @param atual status atual do pedido
     * @param novo novo status solicitado
     * @throws IllegalStateException se a transição for inválida
     */
    private void validarTransicao(StatusPedido atual, StatusPedido novo) {
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
    }

    /**
     * Busca um pedido pelo ID.
     * @param pedidoId id do pedido
     * @return pedido encontrado
     * @throws IllegalArgumentException se o pedido não for encontrado
     */
    public Pedido buscarPorId(String pedidoId) {
        return pedidoRepository.buscarPorId(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido nao encontrado."));
    }

    /**
     * Lista todos os pedidos de um cliente.
     * @param clienteId id do cliente
     * @return lista de pedidos do cliente
     */
    public List<Pedido> listarPorCliente(String clienteId) {
        return pedidoRepository.buscarPorCliente(clienteId);
    }

    /**
     * Lista todos os pedidos de um restaurante.
     * @param restauranteId id do restaurante
     * @return lista de pedidos do restaurante
     */
    public List<Pedido> listarPorRestaurante(String restauranteId) {
        return pedidoRepository.buscarPorRestaurante(restauranteId);
    }
}