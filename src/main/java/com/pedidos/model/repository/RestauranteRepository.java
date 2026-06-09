package com.pedidos.model.repository;

import com.pedidos.model.entity.Restaurante;
import com.pedidos.model.entity.Usuario;
import java.util.List;

public interface RestauranteRepository extends UsuarioRepository {


    Usuario buscarPorEmailSenha(String email, String senha);

    void salvarCadastro(Usuario usuario);

    List<Restaurante> listarRestaurantes();
}