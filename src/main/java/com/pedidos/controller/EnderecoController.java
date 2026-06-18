package com.pedidos.controller;

import com.pedidos.controller.dto.EnderecoResumoDTO;
import com.pedidos.model.entity.Endereco;
import com.pedidos.model.service.EnderecoService;

import java.util.Optional;

public class EnderecoController {

    private final EnderecoService enderecoService;

    public EnderecoController(EnderecoService enderecoService) {
        this.enderecoService = enderecoService;
    }

    public Endereco criarEndereco(String clienteId, String rua, String numero, String bairro, String cidade, String estado, String cep, Boolean isPadrao) {
        return enderecoService.criarEndereco(clienteId, rua, numero, bairro, cidade, estado, cep, isPadrao);
    }

    public Endereco buscarPorCliente(String clienteId) {
        return enderecoService.buscarPorCliente(clienteId);
    }

    public void editarEndereco(String clienteId, String novaRua, String novoBairro) {
        enderecoService.editarEndereco(clienteId, novaRua, novoBairro);
    }

    public void removerEndereco(String clienteId) {
        enderecoService.removerEndereco(clienteId);
    }

    public Optional<EnderecoResumoDTO> buscarPadraoComoDTO(String clienteId) {
        return enderecoService.buscarPadraoDoCliente(clienteId)
                .map(e -> new EnderecoResumoDTO(e.getRua(), e.getNumero(), e.getBairro(), e.getCidade()));
    }
}
