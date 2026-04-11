package com.pedidos.application.service;

import com.pedidos.domain.entities.RestauranteEntity;
import com.pedidos.domain.entities.UsuarioEntity;
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

    public void editarPerfil(RestauranteEntity restauranteEntity, String novoNome, String novoCnpj, String novoTelefone) {
        try {
            if (novoNome == null || novoNome.isBlank()) {
                throw new IllegalArgumentException("Nome é obrigatório");
            }

            if (novoTelefone == null || novoTelefone.isBlank()) {
                throw new IllegalArgumentException("Telefone é obrigatório");
            }

            String cnpjNormalizado = normalizarCnpj(novoCnpj);

            boolean cnpjExiste = restauranteRepository.listarRestaurantes().stream().filter(r -> !r.getId().equals(restauranteEntity.getId())).anyMatch(r -> r.getCnpj().equals(cnpjNormalizado));
            if (cnpjExiste) {
                throw new IllegalStateException("CNPJ já cadastrado no sistema");
            }

            restauranteEntity.setNome(novoNome);
            restauranteEntity.setTelefone(novoTelefone);
            restauranteEntity.setCnpj(cnpjNormalizado);

            restauranteRepository.salvar(restauranteEntity);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void editarEmail(RestauranteEntity restauranteEntity, String novoEmail) {
        try {
            boolean emailExiste = restauranteRepository.listarTodos().stream().filter(u -> !u.getId().equals(restauranteEntity.getId())).anyMatch(u -> u.getEmail().equalsIgnoreCase(novoEmail));

            if (emailExiste) {
                throw new IllegalStateException("Email já cadastrado");
            }

            restauranteEntity.setEmail(novoEmail);
            restauranteRepository.salvar(restauranteEntity);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void alterarCategoria(RestauranteEntity restauranteEntity, String novaCategoriaGlobalId) {
        try {
            if (novaCategoriaGlobalId == null || novaCategoriaGlobalId.isBlank()) {
                throw new IllegalArgumentException("Categoria é obrigatória");
            }

            if (categoriaGlobalRepository.buscarPorId(novaCategoriaGlobalId).isEmpty()) {
                throw new IllegalArgumentException("Categoria global não encontrada");
            }

            if (novaCategoriaGlobalId.equals(restauranteEntity.getCategoriaGlobalId())) {
                throw new IllegalStateException("Restaurante já pertence a essa categoria");
            }

            restauranteEntity.setCategoriaGlobalId(novaCategoriaGlobalId);
            restauranteRepository.salvar(restauranteEntity);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void alterarSenha(RestauranteEntity restauranteEntity, String senhaAtual, String novaSenha) {
        try {
            if (novaSenha == null || novaSenha.length() < 6) {
                throw new IllegalArgumentException("Senha deve ter no mínimo 6 caracteres");
            }

            if (senhaAtual.equals(novaSenha)) {
                throw new IllegalArgumentException("Nova senha não pode ser igual à senha atual");
            }

            String senhaAtualHash = autenticacaoService.hashSenha(senhaAtual);
            if (!senhaAtualHash.equals(restauranteEntity.getSenhaHash())) {
                throw new IllegalArgumentException("Senha incorreta");
            }

            String novoHash = autenticacaoService.hashSenha(novaSenha);
            restauranteEntity.setSenhaHash(novoHash);
            restauranteRepository.salvar(restauranteEntity);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public RestauranteEntity buscarRestaurantePorId(String id) {
        try {
            UsuarioEntity usuarioEntity = restauranteRepository.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado"));

            if (!(usuarioEntity instanceof RestauranteEntity)) {
                throw new IllegalArgumentException("Usuario não é um restaurante");
            }

            return (RestauranteEntity) usuarioEntity;
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
}
