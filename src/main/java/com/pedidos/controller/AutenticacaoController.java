package com.pedidos.controller;

import com.pedidos.model.entity.Usuario;
import com.pedidos.model.service.AutenticacaoService;

public class AutenticacaoController {

    private final AutenticacaoService autenticacaoService;

    public AutenticacaoController(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    public Usuario autenticar(String email, String senha) {
        return autenticacaoService.autenticar(email, senha);
    }

    public String hashSenha(String senha) {
        return autenticacaoService.hashSenha(senha);
    }
}
