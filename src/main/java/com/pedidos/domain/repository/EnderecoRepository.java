package com.pedidos.domain.repository;

import com.pedidos.domain.entities.EnderecoEntity;

import java.util.Optional;

public interface EnderecoRepository {

    void salvar(EnderecoEntity enderecoEntity);

    Optional<EnderecoEntity> buscarPorCliente(String clienteId);

    void remover(String clienteId);
}
