package com.pedidos.controller;

import com.pedidos.model.entity.Restaurante;
import com.pedidos.model.service.RestauranteService;

import java.util.List;

public class RestauranteController {

    private final RestauranteService restauranteService;

    public RestauranteController(RestauranteService restauranteService) {
        this.restauranteService = restauranteService;
    }

    public void cadastrarRestaurante(String nome, String email, String senha, String cnpj, String telefone) {
        restauranteService.cadastrarRestaurante(nome, email, senha, cnpj, telefone);
    }

    public Restaurante buscarPorId(String id) {
        return restauranteService.buscarRestaurantePorId(id);
    }

    public List<Restaurante> buscarAtivos() {
        return restauranteService.buscarRestaurantesAtivos();
    }

    public void editarPerfil(Restaurante restaurante, String novoNome, String novoCnpj, String novoTelefone) {
        restauranteService.editarPerfil(restaurante, novoNome, novoCnpj, novoTelefone);
    }

    public void editarEmail(Restaurante restaurante, String novoEmail) {
        restauranteService.editarEmail(restaurante, novoEmail);
    }

    public void alterarCategoria(Restaurante restaurante, String novaCategoriaGlobalId) {
        restauranteService.alterarCategoria(restaurante, novaCategoriaGlobalId);
    }

    public void alterarSenha(Restaurante restaurante, String senhaAtual, String novaSenha) {
        restauranteService.alterarSenha(restaurante, senhaAtual, novaSenha);
    }
}
