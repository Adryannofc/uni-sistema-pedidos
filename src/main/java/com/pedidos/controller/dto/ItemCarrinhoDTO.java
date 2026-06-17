package com.pedidos.controller.dto;

import java.math.BigDecimal;

public record ItemCarrinhoDTO(
        String produtoId,
        String nomeProduto,
        int quantidade,
        BigDecimal subtotal
) {}
