package com.pedidos.presentation;

import com.pedidos.application.service.*;
import com.pedidos.infra.config.FlyWayconfig;
import com.pedidos.infra.config.JPAUtil;
import com.pedidos.infra.repository.impl.*;
import com.pedidos.infra.seed.DataSeeder;
import com.pedidos.presentation.menu.MenuLogin;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {


        // --- Iniciando Flyway ---
        try {
            FlyWayconfig.migrate();
        } catch (Exception e) {
            System.out.println("Flyway não executado: " + e.getMessage());
        }

        Scanner scanner = new Scanner(System.in);

        // --- Repositórios ---
        AdminRepositoryMemoria adminRepo = new AdminRepositoryMemoria();
        RestauranteRepositoryMemoria restauranteRepo = new RestauranteRepositoryMemoria();
        ClienteRepositoryMemoria clienteRepo = new ClienteRepositoryMemoria();
        CategoriaGlobalRepositoryMemoria categoriaGlobalRepo = new CategoriaGlobalRepositoryMemoria();
        CategoriaCardapioRepositoryMemoria categoriaCardapioRepo = new CategoriaCardapioRepositoryMemoria();
        ProdutoRepositoryMemoria produtoRepo = new ProdutoRepositoryMemoria();
        PedidoRepositoryMemoria pedidoRepo = new PedidoRepositoryMemoria();

        // --- Services ---
        AutenticacaoService authService = new AutenticacaoService(adminRepo, restauranteRepo, clienteRepo);
        AdminService adminService = new AdminService(adminRepo, authService, restauranteRepo);
        ClienteService clienteService = new ClienteService(clienteRepo, authService, adminRepo, restauranteRepo);
        CategoriaService categoriaService = new CategoriaService(categoriaGlobalRepo, categoriaCardapioRepo, restauranteRepo, produtoRepo);
        ProdutoService produtoService = new ProdutoService(produtoRepo);
        RestauranteService restauranteService = new RestauranteService(restauranteRepo, categoriaGlobalRepo, authService);
        PedidoService pedidoService = new PedidoService(pedidoRepo);
        CarrinhoService carrinhoService = new CarrinhoService();

        // --- Seed ---
        DataSeeder seeder = new DataSeeder(
                adminRepo, clienteRepo, restauranteRepo, authService,
                produtoRepo, categoriaGlobalRepo, categoriaCardapioRepo
        );
        seeder.popular();

        // --- Inicia aplicação ---
        new MenuLogin(
                authService, adminService, clienteService,
                categoriaService, produtoService, restauranteService,
                pedidoService, carrinhoService, restauranteRepo
        ).iniciar();

        // --- Fecha recursos JPA ---
        JPAUtil.close();
    }
}
