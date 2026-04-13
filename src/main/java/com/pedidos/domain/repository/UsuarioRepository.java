package com.pedidos.domain.repository;
import com.pedidos.domain.entities.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {

    void salvar(Usuario usuario);

    Optional<Usuario> buscarPorId(String id);

    Optional<Usuario> buscarPorEmail(String email);

    List<Usuario> listarTodos();

    void deletar(String id);
}