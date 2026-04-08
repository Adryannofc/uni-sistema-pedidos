package com.pedidos.infra.repository.impl;

import com.pedidos.domain.model.CategoriaGlobal;
import com.pedidos.domain.repository.CategoriaGlobalRepository;
import java.util.*;

public class CategoriaGlobalRepositoryJPA implements CategoriaGlobalRepository {
    private final Map<String, CategoriaGlobal> storage = new HashMap<>();

    /**
     * Busca uma categoria global pelo nome, ignorando maiúsculas e minúsculas.
     * @param nome O nome da categoria global a ser buscada.
     * @return Um Optional contendo a categoria global encontrada, ou vazio se não for encontrada.
     */
    @Override
    public Optional<CategoriaGlobal> buscarPorNome(String nome) {
        return storage.values().stream()
                .filter(c -> c.getNome().equalsIgnoreCase(nome))
                .findFirst();
    }

    /**
     * Salva ou atualiza uma categoria global no repositório. Se a categoria global já existir (com base no ID), ela será atualizada; caso contrário, uma nova entrada será criada.
     * @param categoriaGlobal A categoria global a ser salva ou atualizada.
     */
    @Override
    public void salvar(CategoriaGlobal categoriaGlobal) {
        storage.put(categoriaGlobal.getId(), categoriaGlobal);
    }

    /**
     * Busca uma categoria global pelo ID.
     * @param id O ID da categoria global a ser buscada.
     * @return Um Optional contendo a categoria global encontrada, ou vazio se não for encontrada.
     */
    @Override
    public Optional<CategoriaGlobal> buscarPorId(String id) { return Optional.ofNullable(storage.get(id)); }

    /**
     * Retorna uma lista imutável de todas as categorias globais armazenadas no repositório.
     * @return Uma lista imutável de categorias globais.
     */
    @Override
    public List<CategoriaGlobal> listarTodos() {
        return Collections.unmodifiableList(new ArrayList<>(storage.values()));
    }

    /**
     * Remove uma categoria global do repositório com base no ID fornecido.
     * @param id O ID da categoria global a ser removida.
     */
    @Override
    public void remover(String id) {
        storage.remove(id);
    }
}