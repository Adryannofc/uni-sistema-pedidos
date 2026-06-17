package com.pedidos.model.service;

import com.pedidos.model.entity.Cliente;
import com.pedidos.model.entity.Endereco;
import com.pedidos.model.entity.Restaurante;
import com.pedidos.model.entity.Usuario;
import com.pedidos.model.repository.AdminRepository;
import com.pedidos.model.repository.ClienteRepository;
import com.pedidos.model.repository.RestauranteRepository;
import com.pedidos.model.repository.EnderecoRepository;

import java.util.Optional;

public class ClienteService {
    private final ClienteRepository clienteRepository;
    private final AutenticacaoService autenticacaoService;
    private final AdminRepository adminRepository;
    private final RestauranteRepository restauranteRepository;
    private final EnderecoRepository enderecoRepository;

    public ClienteService(ClienteRepository clienteRepository, AutenticacaoService autenticacaoService, AdminRepository adminRepository, RestauranteRepository restauranteRepository, EnderecoRepository enderecoRepository) {
        this.clienteRepository = clienteRepository;
        this.autenticacaoService = autenticacaoService;
        this.adminRepository = adminRepository;
        this.restauranteRepository = restauranteRepository;
        this.enderecoRepository = enderecoRepository;
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
            Optional<Endereco> padraoExistente = cliente.getEnderecoPadrao();

            if (padraoExistente.isPresent()) {
                // Atualiza os campos do endereço padrão existente em vez de criar um novo
                Endereco existente = padraoExistente.get();
                existente.setRua(rua);
                existente.setNumero(numero);
                existente.setBairro(bairro);
                existente.setCidade(cidade);
                existente.setEstado(estado);
                existente.setCep(cep);
            } else {
                Endereco endereco = new Endereco(rua, numero, bairro, cidade, estado, cep, true);
                cliente.setEndereco(endereco);
                cliente.setClienteAoEndereco(endereco);
            }

            clienteRepository.salvar(cliente);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void removerEndereco(Cliente cliente, Endereco endereco) {

        Endereco removido = cliente.getEnderecos()
                .stream()
                .filter(e -> e.getId().equals(endereco.getId()))
                .findFirst()
                .orElseThrow();

        boolean eraPadrao = removido.isPadrao();

        cliente.getEnderecos().remove(removido);

        if (eraPadrao && !cliente.getEnderecos().isEmpty()) {
            cliente.getEnderecos().get(0).setIsPadrao(true);
        }

        clienteRepository.salvar(cliente);
    }

    public void definirEnderecoPadrao(Cliente cliente, String enderecoId) {

        for (Endereco endereco : cliente.getEnderecos()) {
            endereco.setIsPadrao(false);

            if (endereco.getId().equals(enderecoId)) {
                endereco.setIsPadrao(true);
            }
        }

        clienteRepository.salvar(cliente);
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
