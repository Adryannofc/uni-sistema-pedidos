package com.pedidos.infra.repository.impl;

import com.pedidos.domain.model.Usuario;
import com.pedidos.domain.repository.RestauranteRepository;

import java.util.*;
import com.pedidos.domain.model.Restaurante;
import java.util.stream.Collectors;
public class RestauranteRepositoryJPA implements RestauranteRepository {

    private final HashMap<String, Usuario> storage = new HashMap<>();

    @Override
    public void salvar(Usuario usuario) {
        storage.put(usuario.getId().toString(), usuario);
    }

    @Override
    public Optional<Usuario> buscarPorId(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return storage.values().stream()
                .filter(u -> Objects.equals(u.getEmail(), email))
                .findFirst();
    }

    @Override
    public List<Usuario> listarTodos() {
        return Collections.unmodifiableList(new ArrayList<>(storage.values()));
    }

    @Override
    public void deletar(String id) {
        storage.remove(id);
    }

    @Override
    public Usuario buscarPorEmailSenha(String email, String senhaHash) {
        return storage.values().stream()
                .filter(u -> Objects.equals(u.getEmail(), email) && u.verificarSenha(senhaHash))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Restaurante> listarRestaurantes() {
        return storage.values().stream()
                .filter(u -> u instanceof Restaurante)
                .map(u -> (Restaurante) u)
                .collect(Collectors.toList());
    }
}

