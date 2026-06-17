package com.pedidos.controller.dto;

import com.pedidos.model.enums.StatusPedido;

import java.math.BigDecimal;

public record PedidoResumoDTO(
        String id,
        String idCurto,
        String clienteNome,
        String itensResumo,
        BigDecimal total,
        StatusPedido status,
        String dataFormatada,
        StatusPedido proximoStatus,
        boolean podeCancelar
) {}
