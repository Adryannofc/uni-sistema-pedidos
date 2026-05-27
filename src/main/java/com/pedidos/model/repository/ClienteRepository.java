package com.pedidos.model.repository;
import com.pedidos.model.entity.Usuario;

public interface ClienteRepository extends UsuarioRepository{
    Usuario buscarPorEmailSenha(String email, String senha);
}
