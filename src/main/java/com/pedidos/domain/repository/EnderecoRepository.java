package com.pedidos.domain.repository;

import com.pedidos.domain.entities.Endereco;

import java.util.Optional;

public interface EnderecoRepository {

    void salvar(Endereco endereco);

    Optional<Endereco> buscarPorCliente(String clienteId);

    void remover(String clienteId);
}
