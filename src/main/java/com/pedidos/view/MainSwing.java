package com.pedidos.view;

import com.pedidos.application.service.*;
import com.pedidos.domain.entities.CategoriaGlobal;
import com.pedidos.infra.config.FlyWayconfig;
import com.pedidos.infra.config.JPAUtil;
import com.pedidos.infra.repository.impl.*;
import com.pedidos.view.login.LoginFrame;
import jakarta.persistence.EntityManager;

import javax.swing.*;

public class MainSwing {
    public static void main(String[] args) {

        // 1. Infraestrutura fora da EDT (operação pesada — conecta ao banco)
        try {
            FlyWayconfig.migrate();
        } catch (Exception e) {
            System.out.println("Flyway não executado: " + e.getMessage());
        }

        EntityManager em = JPAUtil.getEntityManager();

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
        AdminService adminService = new AdminService(adminRepo, authService, restauranteRepo);
        ClienteService clienteService = new ClienteService(clienteRepo, authService, adminRepo, restauranteRepo, enderecoRepo);
        CategoriaService categoriaService = new CategoriaService(categoriaGlobalRepo, categoriaCardapioRepo, restauranteRepo, produtoRepo);
        ProdutoService produtoService = new ProdutoService(produtoRepo, restauranteRepo);
        RestauranteService restauranteService = new RestauranteService(restauranteRepo, categoriaGlobalRepo, authService);
        PedidoService pedidoService = new PedidoService(pedidoRepo, horarioFuncionamentoRepo);
        CarrinhoService carrinhoService = new CarrinhoService();
        HorarioService horarioService = new HorarioService(horarioFuncionamentoRepo);
        AreaEntregaService areaEntregaService = new AreaEntregaService(areaRepo);

        // 2. UI na EDT
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            LoginFrame frame = new LoginFrame(authService, adminService, clienteService,
                    categoriaService, produtoService, restauranteService,
                    pedidoService, carrinhoService, restauranteRepo, areaEntregaService, horarioService);
            frame.setVisible(true);
        });
    }
}
