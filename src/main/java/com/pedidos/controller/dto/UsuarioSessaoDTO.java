package com.pedidos.controller.dto;

import com.pedidos.model.enums.TipoUsuario;

public record UsuarioSessaoDTO(
        String id,
        String nome,
        String email,
        TipoUsuario tipo
) {}
