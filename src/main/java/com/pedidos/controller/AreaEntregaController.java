package com.pedidos.controller;

import com.pedidos.model.entity.AreaEntrega;
import com.pedidos.model.entity.Restaurante;
import com.pedidos.model.service.AreaEntregaService;

import java.math.BigDecimal;
import java.util.List;

public class AreaEntregaController {

    private final AreaEntregaService areaEntregaService;

    public AreaEntregaController(AreaEntregaService areaEntregaService) {
        this.areaEntregaService = areaEntregaService;
    }

    public AreaEntrega criarAreaEntrega(Restaurante restaurante,
                                        String bairro,
                                        BigDecimal distanciaMaximaKm,
                                        BigDecimal taxaEntrega,
                                        int previsaoEntregaMinutos) {
        return areaEntregaService.criarAreaEntrega(restaurante, bairro, distanciaMaximaKm, taxaEntrega, previsaoEntregaMinutos);
    }

    public List<AreaEntrega> listarAreasPorRestaurante(String restauranteId) {
        return areaEntregaService.listarAreasPorRestaurante(restauranteId);
    }

    public void editarAreaEntrega(String id,
                                  String novoBairro,
                                  BigDecimal novaDistancia,
                                  BigDecimal novoTaxa,
                                  int novoPrevisao) {
        areaEntregaService.editarAreaEntrega(id, novoBairro, novaDistancia, novoTaxa, novoPrevisao);
    }

    public void deletarAreaEntrega(String id) {
        areaEntregaService.deletarAreaEntrega(id);
    }

    public BigDecimal buscarTaxaPorBairro(String restauranteId, String bairro) {
        return areaEntregaService.buscarTaxaPorBairro(restauranteId, bairro);
    }

    public AreaEntrega buscarAreaPorBairro(String restauranteId, String bairro) {
        return areaEntregaService.buscarAreaPorBairro(restauranteId, bairro);
    }
}
