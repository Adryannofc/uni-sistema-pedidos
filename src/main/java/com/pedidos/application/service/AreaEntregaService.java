package com.pedidos.application.service;


import com.pedidos.domain.entities.AreaEntrega;
import com.pedidos.domain.entities.Restaurante;
import com.pedidos.domain.repository.AreaEntregaRepository;

import java.math.BigDecimal;
import java.util.List;

public class AreaEntregaService {

    private final AreaEntregaRepository areaEntregaRepository;

    public AreaEntregaService(AreaEntregaRepository areaEntregaRepository) {
        this.areaEntregaRepository = areaEntregaRepository;
    }


    public AreaEntrega criarAreaEntrega(Restaurante restaurante,
                                        String bairro,
                                        BigDecimal distanciaMaximaKm,
                                        BigDecimal taxaEntrega,
                                        int previsaoEntregaMinutos) {

        if (bairro == null || bairro.isBlank()) {
            throw new IllegalArgumentException("Bairro é obrigatório");
        }

        if (taxaEntrega.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Taxa de entrega não pode ser negativa");
        }

        if (distanciaMaximaKm.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Distância máxim deve ser maior que zero");
        }

        if (previsaoEntregaMinutos <= 0) {
            throw new IllegalArgumentException("Previsão de entrega deve ser maior que zero");
        }


        boolean jaExiste = areaEntregaRepository
                .buscarPorRestauranteId(restaurante.getId())
                .stream()
                .anyMatch(a -> a.getBairro().equalsIgnoreCase(bairro));

        if (jaExiste) {
            throw new IllegalStateException("Bairro já cadastrado para esse restaurante");
        }

        AreaEntrega areaEntrega = new AreaEntrega(
                restaurante,
                bairro,
                distanciaMaximaKm,
                taxaEntrega,
                previsaoEntregaMinutos
        );

        areaEntregaRepository.salvar(areaEntrega);

        return areaEntrega;
    }

    public List<AreaEntrega> listarAreasPorRestaurante(String restauranteId) {
        return areaEntregaRepository.buscarPorRestauranteId(restauranteId);

    }

   AreaEntrega buscarPorId(String id) {
        return areaEntregaRepository
                .buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Área de entrega não encontrada"));
    }


    public void editarAreaEntrega(String id,
                                  String novoBairro,
                                  BigDecimal novaDistancia,
                                  BigDecimal novoTaxa,
                                  int novoPrevisao) {

        AreaEntrega areaEntrega = buscarPorId(id);

        if (novoBairro == null || novoBairro.isBlank()) {
            throw new IllegalArgumentException("Bairro é obrigatório");
        }

        if (novoTaxa.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Taxa de entrega não pode ser negativa");
        }

        if (novaDistancia.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Previsão de entrega deve ser maior que zero");
        }

        if (novoPrevisao <= 0) {
            throw new IllegalArgumentException("Previsão de entrega deve ser maior que zero");
        }

        areaEntrega.setBairro(novoBairro);
        areaEntrega.setDistanciaKm(novaDistancia);
        areaEntrega.setTaxaEntrega(novoTaxa);
        areaEntrega.setPrevisaoMinutos(novoPrevisao);

        areaEntregaRepository.salvar(areaEntrega);

    }

    public void deletarAreaEntrega(String id) {

        AreaEntrega areaEntrega = buscarPorId(id);

        areaEntregaRepository.deletar(areaEntrega.getId());
    }


    public BigDecimal buscarTaxaPorBairro(String restauranteId,
                                          String bairro) {

        return areaEntregaRepository
                .buscarPorRestauranteId(restauranteId)
                .stream()
                .filter(a -> a.getBairro().equalsIgnoreCase(bairro))
                .findFirst()
                .map(AreaEntrega::getTaxaEntrega)
                .orElseThrow(() ->
                        new RuntimeException("Bairro '" + bairro + "' não é atendido por este restaurante"));

    }

    public AreaEntrega buscarAreaPorBairro(String restauranteId,
                                           String bairro) {

        return areaEntregaRepository
                .buscarPorRestauranteId(restauranteId)
                .stream()
                .filter(a -> a.getBairro().equalsIgnoreCase(bairro))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Bairro '" + bairro + "' não é atendido por este restaurante"));

    }
}
