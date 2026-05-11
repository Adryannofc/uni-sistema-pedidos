package com.pedidos;

import com.pedidos.application.service.*;
import com.pedidos.infra.config.FlyWayconfig;
import com.pedidos.infra.config.JPAUtil;
import com.pedidos.infra.repository.impl.*;
import com.pedidos.presentation.MenuLogin;
import jakarta.persistence.EntityManager;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            FlyWayconfig.migrate();
        } catch (Exception e) {
            System.out.println("Flyway não executado: " + e.getMessage());
        }

        EntityManager em = JPAUtil.getEntityManager();

        Scanner scanner = new Scanner(System.in);

        AdminRepositoryJPA adminRepo = new AdminRepositoryJPA(em);
        RestauranteRepositoryJPA restauranteRepo = new RestauranteRepositoryJPA(em);
        ClienteRepositoryJPA clienteRepo = new ClienteRepositoryJPA(em);
        CategoriaGlobalRepositoryJPA categoriaGlobalRepo = new CategoriaGlobalRepositoryJPA(em);
        CategoriaCardapioRepositoryJPA categoriaCardapioRepo = new CategoriaCardapioRepositoryJPA(em);
        ProdutoRepositoryJPA produtoRepo = new ProdutoRepositoryJPA(em);
        PedidoRepositoryJPA pedidoRepo = new PedidoRepositoryJPA(em);
        HorarioFuncionamentoRepositoryJPA horarioRepo = new HorarioFuncionamentoRepositoryJPA(em);

        AutenticacaoService authService = new AutenticacaoService(adminRepo, restauranteRepo, clienteRepo);
        AdminService adminService = new AdminService(adminRepo, authService, restauranteRepo);
        ClienteService clienteService = new ClienteService(clienteRepo, authService, adminRepo, restauranteRepo);
        CategoriaService categoriaService = new CategoriaService(categoriaGlobalRepo, categoriaCardapioRepo, restauranteRepo, produtoRepo);
        ProdutoService produtoService = new ProdutoService(produtoRepo, restauranteRepo);
        RestauranteService restauranteService = new RestauranteService(restauranteRepo, categoriaGlobalRepo, authService);
        PedidoService pedidoService = new PedidoService(pedidoRepo, horarioRepo);
        CarrinhoService carrinhoService = new CarrinhoService();
        AreaEntregaService areaEntregaService = new AreaEntregaService(areaRepo);
        HorarioService horarioService = new HorarioService(horarioFuncionamentoRepo);

        new MenuLogin(
                authService, adminService, clienteService,
                categoriaService, produtoService, restauranteService,
                pedidoService, carrinhoService, restauranteRepo, areaEntregaService, horarioService
        ).iniciar();

        JPAUtil.close();
    }
}
