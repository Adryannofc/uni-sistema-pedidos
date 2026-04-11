package com.pedidos.domain.repository;
import com.pedidos.domain.entities.UsuarioEntity;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {

    void salvar(UsuarioEntity usuarioEntity);

    Optional<UsuarioEntity> buscarPorId(String id);

    Optional<UsuarioEntity> buscarPorEmail(String email);

    List<UsuarioEntity> listarTodos();

    void deletar(String id);
}