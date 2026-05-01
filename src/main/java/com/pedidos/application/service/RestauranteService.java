package com.pedidos.application.service;

import com.pedidos.domain.entities.Cliente;
import com.pedidos.domain.entities.Restaurante;
import com.pedidos.domain.entities.Usuario;
import com.pedidos.domain.repository.CategoriaGlobalRepository;
import com.pedidos.domain.repository.RestauranteRepository;

public class RestauranteService {

    private final RestauranteRepository restauranteRepository;
    private final CategoriaGlobalRepository categoriaGlobalRepository;
    private final AutenticacaoService autenticacaoService;

    public RestauranteService(RestauranteRepository restauranteRepository, CategoriaGlobalRepository categoriaGlobalRepository, AutenticacaoService autenticacaoService) {
        this.restauranteRepository = restauranteRepository;
        this.categoriaGlobalRepository = categoriaGlobalRepository;
        this.autenticacaoService = autenticacaoService;
    }

    public void editarPerfil(Restaurante restaurante, String novoNome, String novoCnpj, String novoTelefone) {
        try {
            if (novoNome == null || novoNome.isBlank()) {
                throw new IllegalArgumentException("Nome é obrigatório");
            }

            if (novoTelefone == null || novoTelefone.isBlank()) {
                throw new IllegalArgumentException("Telefone é obrigatório");
            }

            String cnpjNormalizado = normalizarCnpj(novoCnpj);

            boolean cnpjExiste = restauranteRepository.listarRestaurantes().stream().filter(r -> !r.getId().equals(restaurante.getId())).anyMatch(r -> r.getCnpj().equals(cnpjNormalizado));
            if (cnpjExiste) {
                throw new IllegalStateException("CNPJ já cadastrado no sistema");
            }

            restaurante.setNome(novoNome);
            restaurante.setTelefone(novoTelefone);
            restaurante.setCnpj(cnpjNormalizado);

            restauranteRepository.salvar(restaurante);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void editarEmail(Restaurante restaurante, String novoEmail) {
        try {
            boolean emailExiste = restauranteRepository.listarTodos().stream().filter(u -> !u.getId().equals(restaurante.getId())).anyMatch(u -> u.getEmail().equalsIgnoreCase(novoEmail));

            if (emailExiste) {
                throw new IllegalStateException("Email já cadastrado");
            }

            restaurante.setEmail(novoEmail);
            restauranteRepository.salvar(restaurante);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void alterarCategoria(Restaurante restaurante, String novaCategoriaGlobalId) {
        try {
            if (novaCategoriaGlobalId == null || novaCategoriaGlobalId.isBlank()) {
                throw new IllegalArgumentException("Categoria é obrigatória");
            }

            if (categoriaGlobalRepository.buscarPorId(novaCategoriaGlobalId).isEmpty()) {
                throw new IllegalArgumentException("Categoria global não encontrada");
            }

            if (novaCategoriaGlobalId.equals(restaurante.getCategoriaGlobalId())) {
                throw new IllegalStateException("Restaurante já pertence a essa categoria");
            }

            restaurante.setCategoriaGlobalId(novaCategoriaGlobalId);
            restauranteRepository.salvar(restaurante);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void alterarSenha(Restaurante restaurante, String senhaAtual, String novaSenha) {
        try {
            if (novaSenha == null || novaSenha.length() < 6) {
                throw new IllegalArgumentException("Senha deve ter no mínimo 6 caracteres");
            }

            if (senhaAtual.equals(novaSenha)) {
                throw new IllegalArgumentException("Nova senha não pode ser igual à senha atual");
            }

            String senhaAtualHash = autenticacaoService.hashSenha(senhaAtual);
            if (!senhaAtualHash.equals(restaurante.getSenhaHash())) {
                throw new IllegalArgumentException("Senha incorreta");
            }

            String novoHash = autenticacaoService.hashSenha(novaSenha);
            restaurante.setSenhaHash(novoHash);
            restauranteRepository.salvar(restaurante);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Restaurante buscarRestaurantePorId(String id) {
        try {
            Usuario usuario = restauranteRepository.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado"));

            if (!(usuario instanceof Restaurante)) {
                throw new IllegalArgumentException("Usuario não é um restaurante");
            }

            return (Restaurante) usuario;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private String normalizarCnpj(String cnpj) {
        if (cnpj == null) {
            throw new IllegalArgumentException("CNPJ inválido — deve conter 14 dígitos");
        }

        String cnpjNormalizado = cnpj.replaceAll("[^0-9]", "");
        if (cnpjNormalizado.length() != 14 || cnpjNormalizado.matches("(\\d)\\1{13}")) {
            throw new IllegalArgumentException("CNPJ inválido — deve conter 14 dígitos");
        }
        return cnpjNormalizado;
    }

    private void validarCnpj(String cnpj) {

        String regexCnpj = "^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$|^\\d{14}$";

        try {
            if (cnpj == null || !cnpj.matches(regexCnpj)) {
                throw new IllegalArgumentException("CNPJ inválido. Informe 11 dígitos numéricos.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private boolean emailCadastrado(String email) {
        try {
            return restauranteRepository.buscarPorEmail(email).isPresent();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void cadastrarRestaurante(String nomeRestaurante, String emailRestaurante, String senhaRestaurante, String cnpjRestaurante, String telefoneRestaurante) {
        try {
            validarCnpj(cnpjRestaurante);
            validarTelefone(telefoneRestaurante);

            if (emailCadastrado(emailRestaurante)) {
                throw new IllegalArgumentException("E-mail já cadastrado.");
            }

            String hash = autenticacaoService.hashSenha(senhaRestaurante);
            Restaurante restaurante = new Restaurante(nomeRestaurante, emailRestaurante, hash, cnpjRestaurante, telefoneRestaurante);
            restauranteRepository.salvar(restaurante);
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
