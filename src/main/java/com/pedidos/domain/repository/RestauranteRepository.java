package com.pedidos.domain.repository;

import com.pedidos.domain.entities.Restaurante;
import com.pedidos.domain.entities.Usuario;
import java.util.List;

public interface RestauranteRepository extends UsuarioRepository {
    Usuario buscarPorEmailSenha(String email, String senha);

    void salvarCadastro(Usuario usuario);

    List<Restaurante> listarRestaurantes();
}