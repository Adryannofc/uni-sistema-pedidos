package com.pedidos.domain.repository;

import com.pedidos.domain.model.Restaurante;
import com.pedidos.domain.model.Usuario;
import java.util.List;

public interface RestauranteRepository extends UsuarioRepository {
    Usuario buscarPorEmailSenha(String email, String senha);

    List<Restaurante> listarRestaurantes();
}