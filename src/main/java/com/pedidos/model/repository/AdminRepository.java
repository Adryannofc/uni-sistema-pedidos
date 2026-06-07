package com.pedidos.model.repository;

import com.pedidos.model.entity.Usuario;

public interface AdminRepository extends UsuarioRepository{
    Usuario buscarPorEmailSenha(String email, String senha);
}
