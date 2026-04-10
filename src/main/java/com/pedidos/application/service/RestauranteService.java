package com.pedidos.application.service;

import com.pedidos.domain.model.Restaurante;
import com.pedidos.domain.model.Usuario;
import com.pedidos.domain.repository.CategoriaGlobalRepository;
import com.pedidos.domain.repository.RestauranteRepository;

import java.util.Optional;

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
    }

    public void editarEmail(Restaurante restaurante, String novoEmail) {
        boolean emailExiste = restauranteRepository.listarTodos().stream().filter(u -> !u.getId().equals(restaurante.getId())).anyMatch(u -> u.getEmail().equalsIgnoreCase(novoEmail));

        if (emailExiste) {
            throw new IllegalStateException("Email já cadastrado");
        }

        restaurante.setEmail(novoEmail); // dispara a validação de formato
        restauranteRepository.salvar(restaurante);
    }

    public void alterarCategoria(Restaurante restaurante, String novaCategoriaGlobalId) {

        if (novaCategoriaGlobalId == null || novaCategoriaGlobalId.isBlank()) {
            throw new IllegalArgumentException("Categoria é obrigatória");
        }

        Optional<?> categoriaOptional = categoriaGlobalRepository.buscarPorId(novaCategoriaGlobalId);

        if (categoriaOptional.isEmpty()) {
            throw new IllegalArgumentException("Categoria global não encontrada");
        }

        if (novaCategoriaGlobalId.equals(restaurante.getCategoriaGlobalId())) {
            throw new IllegalStateException("Restaurante já pertence a essa categoria");
        }

        restaurante.setCategoriaGlobalId(novaCategoriaGlobalId);

        restauranteRepository.salvar(restaurante);
    }

    public void alterarSenha(Restaurante restaurante, String senhaAtual, String novaSenha) {
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
    }

    public Restaurante buscarRestaurantePorId(String id) {

        Usuario usuario = restauranteRepository.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado"));

        if (!(usuario instanceof Restaurante)) {
            throw new IllegalArgumentException("Usuario não é um restaurante");
        }

        return (Restaurante) usuario;
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
}
