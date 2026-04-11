package com.pedidos.application.service;

import com.pedidos.domain.entities.ClienteEntity;
import com.pedidos.domain.entities.EnderecoEntity;
import com.pedidos.domain.entities.RestauranteEntity;
import com.pedidos.domain.entities.UsuarioEntity;
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

    public void favoritar(ClienteEntity clienteEntity, RestauranteEntity restauranteEntity) {
        if (clienteEntity.getFavoritos().contains(restauranteEntity)) {
            clienteEntity.removerFavorito(restauranteEntity);
        } else {
            clienteEntity.adicionarFavorito(restauranteEntity);
        }
        clienteRepository.salvar(clienteEntity);
    }

    // Cadastro
    public void cadastrarCliente(String nome, String email, String senha, String cpf, String telefone) {
        try {
            validarCpf(cpf);

            if (emailCadastrado(email)) {
                throw new IllegalArgumentException("E-mail já cadastrado no sistema.");
            }

            String hash = autenticacaoService.hashSenha(senha);
            ClienteEntity clienteEntity = new ClienteEntity(nome, email, hash, cpf, telefone);
            clienteRepository.salvar(clienteEntity);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void editarNome(ClienteEntity clienteEntity, String novoNome) {
        try {
            clienteEntity.setNome(novoNome);
            clienteRepository.salvar(clienteEntity);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void editarEmail(ClienteEntity clienteEntity, String novoEmail) {
        try {
            if (!clienteEntity.getEmail().equalsIgnoreCase(novoEmail) && emailCadastrado(novoEmail)) {
                throw new IllegalArgumentException("E-mail já cadastrado no sistema.");
            }
            clienteEntity.setEmail(novoEmail);
            clienteRepository.salvar(clienteEntity);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void editarCpf(ClienteEntity clienteEntity, String novoCpf) {
        try {
            validarCpf(novoCpf);
            clienteEntity.setCpf(novoCpf);
            clienteRepository.salvar(clienteEntity);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void salvarEndereco(ClienteEntity clienteEntity, String rua, String numero, String bairro, String cidade, String estado, String cep) {
        try {
            clienteEntity.setEnderecoEntrega(new EnderecoEntity(rua, numero, bairro, cidade, estado, cep));
            clienteRepository.salvar(clienteEntity);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void editarTelefone(ClienteEntity clienteEntity, String novoTelefone) {
        try {
            clienteEntity.setTelefone(novoTelefone);
            clienteRepository.salvar(clienteEntity);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void alterarSenha(UsuarioEntity usuarioEntity, String senhaAtual, String novaSenha, String confirmacaoSenha) {
        try {
            if (novaSenha.length() < 6) {
                throw new IllegalArgumentException("A nova senha deve ter pelo menos 6 caracteres.");
            }

            if (!novaSenha.equals(confirmacaoSenha)) {
                throw new IllegalArgumentException("Nova senha e confirmação não coincidem.");
            }

            String hashAtual = autenticacaoService.hashSenha(senhaAtual);
            if (!usuarioEntity.verificarSenha(hashAtual)) {
                throw new IllegalArgumentException("Senha atual incorreta.");
            }

            String novoHash = autenticacaoService.hashSenha(novaSenha);
            usuarioEntity.setSenhaHash(novoHash);
            clienteRepository.salvar(usuarioEntity);
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
}
