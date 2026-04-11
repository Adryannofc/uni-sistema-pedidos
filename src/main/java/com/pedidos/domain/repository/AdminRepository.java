package com.pedidos.domain.repository;

import com.pedidos.domain.entities.UsuarioEntity;

public interface AdminRepository extends UsuarioRepository{
    UsuarioEntity buscarPorEmailSenha(String email, String senha);
}
