package com.pedidos.application.service;

import com.pedidos.domain.model.Cliente;
import com.pedidos.domain.model.Endereco;
import com.pedidos.domain.model.Usuario;
import com.pedidos.domain.repository.AdminRepository;
import com.pedidos.domain.repository.ClienteRepository;
import com.pedidos.domain.repository.RestauranteRepository;

public class ClienteService {
    private final ClienteRepository clienteRepository;
    private final AutenticacaoService autenticacaoService;

    // checar unicidade de e-mail no sistema inteiro.
    private final AdminRepository adminRepository;
    private final RestauranteRepository restauranteRepository;

    public ClienteService(ClienteRepository clienteRepository, AutenticacaoService autenticacaoService, AdminRepository adminRepository, RestauranteRepository restauranteRepository) {
        this.clienteRepository = clienteRepository;
        this.autenticacaoService = autenticacaoService;
        this.adminRepository = adminRepository;
        this.restauranteRepository = restauranteRepository;
    }

    // Cadastro
    public void cadastrarCliente(String nome, String email, String senha, String cpf, String telefone) {
        validarCpf(cpf);

        if (emailCadastrado(email)) {
            throw new IllegalArgumentException("E-mail já cadastrado no sistema.");
        }

        String hash = autenticacaoService.hashSenha(senha);
        Cliente cliente = new Cliente(nome, email, hash, cpf, telefone);
        clienteRepository.salvar(cliente);

    }

    public void editarNome(Cliente cliente, String novoNome) {
        cliente.setNome(novoNome);
        clienteRepository.salvar(cliente);
    }

    public void editarEmail(Cliente cliente, String novoEmail) {
        // Se o novo e-mail é o mesmo que já tem, não faz sentido bloquear.
        // Sem esse cheque, o próprio cliente ficaria bloqueado de "salvar sem mudança".
        if (!cliente.getEmail().equalsIgnoreCase(novoEmail) && emailCadastrado(novoEmail)) {
            throw new IllegalArgumentException("E-mail já cadastrado no sistema.");
        }
        cliente.setEmail(novoEmail);
        clienteRepository.salvar(cliente);
    }

    public void editarCpf(Cliente cliente, String novoCpf) {
        validarCpf(novoCpf);
        cliente.setCpf(novoCpf);
        clienteRepository.salvar(cliente);
    }

    public void salvarEndereco(Cliente cliente, String rua, String numero, String bairro, String cidade, String estado, String cep) {
        cliente.setEnderecoEntrega(new Endereco(rua, numero, bairro, cidade, estado, cep));
        clienteRepository.salvar(cliente);
    }

    public void editarTelefone(Cliente cliente, String novoTelefone) {
        // setTelefone() em Cliente já tem a validação de formato com regex.
        cliente.setTelefone(novoTelefone);
        clienteRepository.salvar(cliente);
    }

    public void alterarSenha(Usuario usuario, String senhaAtual, String novaSenha, String confirmacaoSenha) {
        if (novaSenha.length() < 6) {
            throw new IllegalArgumentException("A nova senha deve ter pelo menos 6 caracteres.");
        }

        if (!novaSenha.equals(confirmacaoSenha)) {
            throw new IllegalArgumentException("Nova senha e confirmação não coincidem.");
        }

        String hashAtual = autenticacaoService.hashSenha(senhaAtual);
        if (!usuario.verificarSenha(hashAtual)) {
            throw new IllegalArgumentException("Senha atual incorreta.");
        }

        String novoHash = autenticacaoService.hashSenha(novaSenha);
        usuario.setSenhaHash(novoHash);
        clienteRepository.salvar(usuario);
    }

    private void validarCpf(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}")) {
            throw new IllegalArgumentException("CPF inválido. Informe 11 dígitos numéricos.");
        }
    }

    private boolean emailCadastrado(String email) {
        return adminRepository.buscarPorEmail(email).isPresent()
                || restauranteRepository.buscarPorEmail(email).isPresent()
                || clienteRepository.buscarPorEmail(email).isPresent();
    }
}
