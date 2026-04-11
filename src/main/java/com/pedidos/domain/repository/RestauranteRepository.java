package com.pedidos.domain.repository;

import com.pedidos.domain.entities.RestauranteEntity;
import com.pedidos.domain.entities.UsuarioEntity;
import java.util.List;

public interface RestauranteRepository extends UsuarioRepository {
    UsuarioEntity buscarPorEmailSenha(String email, String senha);

    List<RestauranteEntity> listarRestaurantes();
}