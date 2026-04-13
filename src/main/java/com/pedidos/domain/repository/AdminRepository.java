package com.pedidos.domain.repository;

import com.pedidos.domain.entities.Usuario;

public interface AdminRepository extends UsuarioRepository{
    Usuario buscarPorEmailSenha(String email, String senha);
}
