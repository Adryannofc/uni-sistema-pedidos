package com.pedidos.controller;

import com.pedidos.controller.dto.RestauranteResumoDTO;
import com.pedidos.model.entity.HorarioFuncionamento;
import com.pedidos.model.entity.Restaurante;
import com.pedidos.model.service.RestauranteService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

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

    public List<RestauranteResumoDTO> buscarAtivosComoDTO() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        DayOfWeek hoje = LocalDate.now().getDayOfWeek();
        LocalTime agora = LocalTime.now();
        return restauranteService.buscarRestaurantesAtivos().stream()
                .map(r -> {
                    Optional<HorarioFuncionamento> h = r.getHorarios().stream()
                            .filter(hr -> hr.getDiaSemana() == hoje)
                            .findFirst();
                    boolean aberto = h.map(hr -> hr.contemHorario(agora)).orElse(false);
                    String horario = h.map(hr -> hr.getHoraInicio().format(fmt) + " – " + hr.getHoraFim().format(fmt))
                            .orElse("–");
                    String categoria = r.getCategoriaGlobal() != null ? r.getCategoriaGlobal().getNome() : "N/A";
                    return new RestauranteResumoDTO(r.getId(), r.getNome(), categoria, aberto, horario, null);
                })
                .toList();
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
