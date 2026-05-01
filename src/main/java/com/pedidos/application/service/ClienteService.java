package com.pedidos.application.service;

import com.pedidos.domain.entities.Cliente;
import com.pedidos.domain.entities.Endereco;
import com.pedidos.domain.entities.Restaurante;
import com.pedidos.domain.entities.Usuario;
import com.pedidos.domain.repository.AdminRepository;
import com.pedidos.domain.repository.ClienteRepository;
import com.pedidos.domain.repository.RestauranteRepository;

public class ClienteService {
    private final ClienteRepository clienteRepository;
    private final AutenticacaoService autenticacaoService;
    private final AdminRepository adminRepository;
    private final RestauranteRepository restauranteRepository;

    public ClienteService(ClienteRepository clienteRepository, AutenticacaoService autenticacaoService, AdminRepository adminRepository, RestauranteRepository restauranteRepository) {
        this.clienteRepository = clienteRepository;
        this.autenticacaoService = autenticacaoService;
        this.adminRepository = adminRepository;
        this.restauranteRepository = restauranteRepository;
    }

    public void favoritar(Cliente cliente, Restaurante restaurante) {
        if (cliente.getFavoritos().contains(restaurante)) {
            cliente.removerFavorito(restaurante);
        } else {
            cliente.adicionarFavorito(restaurante);
        }
        clienteRepository.salvar(cliente);
    }

    // Cadastro
    public void cadastrarCliente(String nome, String email, String senha, String cpf, String telefone) {
        try {
            validarCpf(cpf);
            validarTelefone(telefone);

            if (emailCadastrado(email)) {
                throw new IllegalArgumentException("E-mail já cadastrado no sistema.");
            }

            String hash = autenticacaoService.hashSenha(senha);
            Cliente cliente = new Cliente(nome, email, hash, cpf, telefone);
            clienteRepository.salvar(cliente);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void editarNome(Cliente cliente, String novoNome) {
        try {
            cliente.setNome(novoNome);
            clienteRepository.salvar(cliente);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void editarEmail(Cliente cliente, String novoEmail) {
        try {
            if (!cliente.getEmail().equalsIgnoreCase(novoEmail) && emailCadastrado(novoEmail)) {
                throw new IllegalArgumentException("E-mail já cadastrado no sistema.");
            }
            cliente.setEmail(novoEmail);
            clienteRepository.salvar(cliente);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void editarCpf(Cliente cliente, String novoCpf) {
        try {
            validarCpf(novoCpf);
            cliente.setCpf(novoCpf);
            clienteRepository.salvar(cliente);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void salvarEndereco(Cliente cliente, String rua, String numero, String bairro, String cidade, String estado, String cep) {
        try {
            cliente.setEnderecoEntrega(new Endereco(rua, numero, bairro, cidade, estado, cep));
            clienteRepository.salvar(cliente);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void editarTelefone(Cliente cliente, String novoTelefone) {
        try {
            cliente.setTelefone(novoTelefone);
            clienteRepository.salvar(cliente);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void alterarSenha(Usuario usuario, String senhaAtual, String novaSenha, String confirmacaoSenha) {
        try {
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
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void validarCpf(String cpf) {
        try {
            if (cpf == null || !cpf.matches("\\d{11}")) {
                throw new IllegalArgumentException("CPF inválido. Informe 11 dígitos numéricos.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private boolean emailCadastrado(String email) {
        try {
            return adminRepository.buscarPorEmail(email).isPresent()
                    || restauranteRepository.buscarPorEmail(email).isPresent()
                    || clienteRepository.buscarPorEmail(email).isPresent();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private String validarTelefone(String telefone) {
        String regexTelefone = "^(\\(\\d{2}\\)\\s?|\\d{2}\\s?)?(9?\\d{4}-?\\d{4})$";

        if (telefone == null || !telefone.matches(regexTelefone)) {
            throw new IllegalArgumentException("Telefone inválido. Use o formato (DDD) 99999-9999.");
        }

        String apenasNumeros = telefone.replaceAll("[^0-9]", "");

        if (apenasNumeros.length() < 10 || apenasNumeros.length() > 11) {
            throw new IllegalArgumentException("O telefone deve conter DDD e o número completo.");
        }

        return apenasNumeros;
    }

}
