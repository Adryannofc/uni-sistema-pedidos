package com.pedidos.infra.repository.impl;

import com.pedidos.domain.model.Usuario;
import com.pedidos.domain.repository.ClienteRepository;

import java.util.*;

public class ClienteRepositoryJPA implements ClienteRepository {

    private final HashMap<String, Usuario> storage = new HashMap<>();

    /**
     * Salva um usuário no repositório de memória, utilizando o ID do usuário como chave.
     * @param usuario usuário a ser salvo no repositório
     */
    @Override
    public void salvar(Usuario usuario) {
        storage.put(usuario.getId().toString(), usuario);
    }
    /**
     * Busca um usuário pelo ID, retornando um Optional para lidar com a possibilidade de não encontrar o usuário.
     * @param id ID do usuário a ser buscado
     * @return Optional contendo o usuário encontrado ou vazio se não encontrado
     */
    @Override
    public Optional<Usuario> buscarPorId(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    /**
     * Busca um usuário pelo email, retornando um Optional para lidar com a possibilidade de não encontrar o usuário.
     * @param email email do usuário a ser buscado
     * @return Optional contendo o usuário encontrado ou vazio se não encontrado
     */
    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return storage.values().stream()
                .filter(u -> Objects.equals(u.getEmail(), email))
                .findFirst();
    }

    /**
     * Lista todos os usuários armazenados no repositório de memória, retornando uma lista imutável para evitar modificações externas.
     * @return lista de todos os usuários armazenados no repositório de memória
     */
    @Override
    public List<Usuario> listarTodos() {
        return Collections.unmodifiableList(new ArrayList<>(storage.values()));
    }
    /**
     * Remove um usuário do repositório de memória com base no ID fornecido.
     * @param id ID do usuário a ser removido
     */
    @Override
    public void deletar(String id) {
        storage.remove(id);
    }

    /**
     * Busca um usuário pelo email e senha hash, retornando o usuário correspondente ou null se não encontrado.
     * @param email email do usuário a ser buscado
     * @param senhaHash senha hash do usuário a ser buscado
     * @return o usuário correspondente ao email e senha hash fornecidos, ou null se não encontrado
     */
    @Override
    public Usuario buscarPorEmailSenha(String email, String senhaHash) {
        return storage.values().stream()
                .filter(u -> Objects.equals(u.getEmail(), email) && u.verificarSenha(senhaHash))
                .findFirst()
                .orElse(null);
    }
}

