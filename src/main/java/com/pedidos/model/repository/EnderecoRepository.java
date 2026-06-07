package com.pedidos.model.repository;

import com.pedidos.model.entity.Endereco;

import java.util.Optional;

public interface EnderecoRepository {

    void salvar(Endereco endereco);

    Optional<Endereco> buscarPorCliente(String clienteId);

    Optional<Endereco> buscarPadraoDoCliente(String clienteId);

    void remover(String clienteId);
}
