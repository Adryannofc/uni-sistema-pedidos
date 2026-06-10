package com.pedidos.controller;

import com.pedidos.model.entity.Cliente;
import com.pedidos.model.entity.Endereco;
import com.pedidos.model.entity.Restaurante;
import com.pedidos.model.entity.Usuario;
import com.pedidos.model.service.ClienteService;

public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    public void cadastrarCliente(String nome, String email, String senha, String cpf, String telefone) {
        clienteService.cadastrarCliente(nome, email, senha, cpf, telefone);
    }

    public void favoritar(Cliente cliente, Restaurante restaurante) {
        clienteService.favoritar(cliente, restaurante);
    }

    public void editarNome(Cliente cliente, String novoNome) {
        clienteService.editarNome(cliente, novoNome);
    }

    public void editarEmail(Cliente cliente, String novoEmail) {
        clienteService.editarEmail(cliente, novoEmail);
    }

    public void editarCpf(Cliente cliente, String novoCpf) {
        clienteService.editarCpf(cliente, novoCpf);
    }

    public void editarTelefone(Cliente cliente, String novoTelefone) {
        clienteService.editarTelefone(cliente, novoTelefone);
    }

    public void salvarEndereco(Cliente cliente, String rua, String numero, String bairro, String cidade, String estado, String cep) {
        clienteService.salvarEndereco(cliente, rua, numero, bairro, cidade, estado, cep);
    }

    public void removerEndereco(Cliente cliente, Endereco endereco) {
        clienteService.removerEndereco(cliente, endereco);
    }

    public void definirEnderecoPadrao(Cliente cliente, String enderecoId) {
        clienteService.definirEnderecoPadrao(cliente, enderecoId);
    }

    public void alterarSenha(Usuario usuario, String senhaAtual, String novaSenha, String confirmacaoSenha) {
        clienteService.alterarSenha(usuario, senhaAtual, novaSenha, confirmacaoSenha);
    }
}
