package com.pedidos.infra.repository.impl;

import com.pedidos.domain.model.CategoriaCardapio;
import com.pedidos.domain.repository.CategoriaCardapioRepository;
import java.util.*;

public class CategoriaCardapioRepositoryJPA implements CategoriaCardapioRepository {

    private final Map<String, CategoriaCardapio> storage = new HashMap<>();

    /**
     * Salva ou atualiza uma categoria de cardápio no repositório. Se a categoria já existir (com base no ID), ela será atualizada; caso contrário, uma nova entrada será criada.
     * @param categoria A categoria de cardápio a ser salva ou atualizada.
     */
    @Override
    public void salvar(CategoriaCardapio categoria) {
        storage.put(categoria.getId(), categoria);
    }

    /**
     * Busca uma categoria de cardápio pelo ID.
     * @param id O ID da categoria de cardápio a ser buscada.
     * @return Um Optional contendo a categoria de cardápio encontrada, ou vazio se não for encontrada.
     */
    @Override
    public Optional<CategoriaCardapio> buscarPorId(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    /**
     * Busca todas as categorias de cardápio associadas a um restaurante específico, identificadas pelo ID do restaurante.
     * @param restauranteId O ID do restaurante cujas categorias de cardápio devem ser buscadas.
     * @return Uma lista de categorias de cardápio associadas ao restaurante especificado.
     */
    @Override
    public List<CategoriaCardapio> buscarPorRestauranteId(String restauranteId) {
        return storage.values().stream()
                .filter(categoria -> categoria.getRestauranteId().equals(restauranteId))
                .toList();
    }

    /**
     * Retorna uma lista de todas as categorias de cardápio armazenadas no repositório.
     * @return Uma lista de categorias de cardápio.
     */
    @Override
    public List<CategoriaCardapio> listarTodos() {
        return new ArrayList<>(storage.values());
    }

    /**
     * Remove uma categoria de cardápio do repositório com base no ID fornecido.
     * @param id O ID da categoria de cardápio a ser removida.
     */
    @Override
    public void remover(String id) {
        storage.remove(id);
    }
}