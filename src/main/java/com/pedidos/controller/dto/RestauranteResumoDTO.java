package com.pedidos.controller.dto;

import java.math.BigDecimal;

public record RestauranteResumoDTO(
        String id,
        String nome,
        String categoriaNome,
        boolean aberto,
        String horarioHoje,
        BigDecimal taxaEntrega
) {}
