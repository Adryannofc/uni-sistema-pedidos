package com.pedidos.controller;

import com.pedidos.model.entity.Restaurante;
import com.pedidos.model.entity.Usuario;
import com.pedidos.model.service.AdminService;

import java.util.List;

public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    public void cadastrarAdmin(String nome, String email, String senha) {
        adminService.cadastrarAdmin(nome, email, senha);
    }

    public void alterarSenha(Usuario usuario, String senhaAtual, String novaSenha, String confirmacaoSenha) {
        adminService.alterarSenha(usuario, senhaAtual, novaSenha, confirmacaoSenha);
    }

    public List<Restaurante> listarRestaurantes() {
        return adminService.listarRestaurantes();
    }

    public void aprovarRestaurante(String id) {
        adminService.aprovarRestaurante(id);
    }

    public void bloquearRestaurante(String id) {
        adminService.bloquearRestaurante(id);
    }

    public void removerRestaurante(String id) {
        adminService.removerRestaurante(id);
    }
}
