package com.pedidos.dto;

import java.math.BigDecimal;

public record ItemCarrinhoDTO(
        String produtoId,
        String nomeProduto,
        int quantidade,
        BigDecimal subtotal
) {}
