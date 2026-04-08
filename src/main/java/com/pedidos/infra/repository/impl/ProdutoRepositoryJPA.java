package com.pedidos.infra.repository.impl;

import com.pedidos.domain.model.Produto;
import com.pedidos.domain.repository.ProdutoRepository;
import java.util.*;

public class ProdutoRepositoryJPA implements ProdutoRepository {
    private final Map<String, Produto> storage = new HashMap<>();

    @Override
    public void salvar(Produto produto) {
        storage.put(produto.getId(), produto);
    }


    public Optional<Produto> buscarPorId(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Produto> listarTodos() {
        return Collections.unmodifiableList(new ArrayList<>(storage.values()));
    }

    @Override
    public void deletar(String id) {
        storage.remove(id);
    }

}
