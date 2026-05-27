package com.pedidos.cli;

import com.pedidos.model.service.*;
import com.pedidos.model.infra.config.FlyWayconfig;
import com.pedidos.model.infra.config.JPAUtil;
import com.pedidos.model.infra.repository.impl.*;
import jakarta.persistence.EntityManager;

import java.util.Scanner;

public class MainTerminal {
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
        AreaEntregaRepositoryJPA areaRepo = new AreaEntregaRepositoryJPA(em);
        HorarioFuncionamentoRepositoryJPA horarioFuncionamentoRepo = new HorarioFuncionamentoRepositoryJPA(em);
        EnderecoRepositoryJPA enderecoRepo = new EnderecoRepositoryJPA(em);

        AutenticacaoService authService = new AutenticacaoService(adminRepo, restauranteRepo, clienteRepo);
        AdminService adminService = new AdminService(adminRepo, authService, restauranteRepo, categoriaCardapioRepo);
        ClienteService clienteService = new ClienteService(clienteRepo, authService, adminRepo, restauranteRepo, enderecoRepo);
        CategoriaService categoriaService = new CategoriaService(categoriaGlobalRepo, categoriaCardapioRepo, restauranteRepo, produtoRepo);
        ProdutoService produtoService = new ProdutoService(produtoRepo, restauranteRepo);
        RestauranteService restauranteService = new RestauranteService(restauranteRepo, categoriaGlobalRepo, authService);
        PedidoService pedidoService = new PedidoService(pedidoRepo, horarioFuncionamentoRepo);
        CarrinhoService carrinhoService = new CarrinhoService();
        HorarioService horarioService = new HorarioService(horarioFuncionamentoRepo);
        AreaEntregaService areaEntregaService = new AreaEntregaService(areaRepo);


        new MenuLogin(
                authService, adminService, clienteService,
                categoriaService, produtoService, restauranteService,
                pedidoService, carrinhoService, restauranteRepo, areaEntregaService, horarioService
        ).iniciar();

        JPAUtil.close();
    }
}
