package com.pedidos.domain.repository;
import com.pedidos.domain.entities.UsuarioEntity;

public interface ClienteRepository extends UsuarioRepository{
    UsuarioEntity buscarPorEmailSenha(String email, String senha);
}
