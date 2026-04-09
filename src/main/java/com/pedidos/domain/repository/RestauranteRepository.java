package com.pedidos.domain.repository;

import com.pedidos.domain.model.Restaurante;
import com.pedidos.domain.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface RestauranteRepository extends UsuarioRepository {
    Optional<Usuario> buscarPorEmailSenha(String email, String senha);

    public List<Restaurante> listarRestaurantes();

}
