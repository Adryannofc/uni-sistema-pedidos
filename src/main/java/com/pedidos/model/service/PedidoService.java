package com.pedidos.model.service;

import com.pedidos.model.enums.StatusPedido;
import com.pedidos.model.entity.*;
import com.pedidos.model.repository.PedidoRepository;
import com.pedidos.model.repository.HorarioFuncionamentoRepository;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final HorarioFuncionamentoRepository horarioRepository;

    public PedidoService(PedidoRepository pedidoRepository, HorarioFuncionamentoRepository horarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.horarioRepository = horarioRepository;
    }

    /**
     * Cria e persiste um novo pedido.
     *
     * @param cliente         id do cliente que realizou o pedido
     * @param restaurante     id do restaurante do pedido
     * @param carrinho          itens selecionados pelo cliente
     * @param enderecoEntrega   endereço de entrega
     * @param codigoConfirmacao código para confirmar a entrega
     * @return pedido criado
     */
    public Pedido criarPedido(Cliente cliente, Restaurante restaurante, Carrinho carrinho, Endereco enderecoEntrega, String codigoConfirmacao, BigDecimal taxaEntrega) {
        try {
            if (enderecoEntrega == null) {
                throw new IllegalArgumentException("Informe um endereço de entrega antes de finalizar o pedido.");
            }

            isRestauranteAberto(restaurante.getId());

            Pedido pedido = new Pedido();
            pedido.setCliente(cliente);
            pedido.setRestaurante(restaurante);
            pedido.setEnderecoEntrega(enderecoEntrega);
            pedido.setCodigoConfirmacao(codigoConfirmacao);
            pedido.setTaxaEntrega(taxaEntrega != null ? taxaEntrega : BigDecimal.ZERO);
            carrinho.getItens().forEach(pedido::adicionarItem);
            pedido.calcularTotal();
            pedidoRepository.salvar(pedido);
            return pedido;
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
            Pedido pedido = pedidoRepository.buscarPorId(pedidoId)
                    .orElseThrow(() -> new IllegalArgumentException("Pedido nao encontrado."));

            if (pedido.getStatus() != StatusPedido.SAIU_PARA_ENTREGA) {
                throw new IllegalStateException("Pedido nao esta em status de entrega.");
            }
            if (!codigoDigitado.equals(pedido.getCodigoConfirmacao())) {
                throw new IllegalArgumentException("Codigo incorreto.");
            }

            pedido.setStatus(StatusPedido.ENTREGUE);
            pedidoRepository.salvar(pedido);
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
            Pedido pedido = pedidoRepository.buscarPorId(pedidoId)
                    .orElseThrow(() -> new IllegalArgumentException("Pedido nao encontrado."));

            validarTransicao(pedido.getStatus(), novoStatus);
            pedido.setStatus(novoStatus);
            pedidoRepository.salvar(pedido);
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
    public Pedido buscarPorId(String pedidoId) {
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
    public List<Pedido> listarPorCliente(String clienteId) {
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
    public List<Pedido> listarPorRestaurante(String restauranteId) {
        try {
            return pedidoRepository.buscarPorRestaurante(restauranteId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    public List<Pedido> filtrarPorStatus(String restauranteId, StatusPedido status) {
        return pedidoRepository.filtrarPorStatus(restauranteId, status);
    }

    public List<Pedido> obterHistoricoFinalizado(String restauranteId) {
        return pedidoRepository.buscarHistoricoFinalizado(restauranteId);
    }

    /**
     * Verifica se um restaurante está aberto no momento atual.
     *
     * @param restauranteId id do restaurante
     * @return true se o restaurante está aberto, false caso contrário
     */
    private boolean isRestauranteAberto(String restauranteId) {
        try {
            DayOfWeek hoje = LocalDate.now().getDayOfWeek();
            LocalTime agora = LocalTime.now();

            List<HorarioFuncionamento> horarios = horarioRepository.buscarPorRestauranteId(restauranteId);

            // RN-01: Se não houver horário cadastrado, rejeitar pedido com mensagem clara
            if (horarios == null || horarios.isEmpty()) {
                throw new IllegalStateException("Restaurante sem horários de funcionamento cadastrados. Tente novamente mais tarde.");
            }

            // Verificar se há horário disponível para o dia atual
            boolean temHorarioHoje = horarios.stream()
                    .filter(h -> h.getDiaSemana() == hoje)
                    .anyMatch(h -> h.contemHorario(agora));

            if (!temHorarioHoje) {
                throw new IllegalStateException("Restaurante fechado no momento. Consulte os horários de funcionamento.");
            }

            return true;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao verificar horário de funcionamento: " + e.getMessage(), e);
        }
    }
}