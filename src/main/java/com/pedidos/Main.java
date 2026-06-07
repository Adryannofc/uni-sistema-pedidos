package com.pedidos;


import com.pedidos.model.service.*;
import com.pedidos.model.infra.config.FlyWayconfig;
import com.pedidos.model.infra.config.JPAUtil;
import com.pedidos.model.infra.repository.impl.*;
import com.pedidos.view.login.LoginFrame;
import com.pedidos.view.util.session.CarrinhoManager;
import jakarta.persistence.EntityManager;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

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
        AdminService adminService = new AdminService(adminRepo, authService, restauranteRepo, categoriaCardapioRepo);
        ClienteService clienteService = new ClienteService(clienteRepo, authService, adminRepo, restauranteRepo, enderecoRepo);
        EnderecoService enderecoService = new EnderecoService(enderecoRepo);
        CategoriaService categoriaService = new CategoriaService(categoriaGlobalRepo, categoriaCardapioRepo, restauranteRepo, produtoRepo);
        ProdutoService produtoService = new ProdutoService(produtoRepo, restauranteRepo);
        RestauranteService restauranteService = new RestauranteService(restauranteRepo, categoriaGlobalRepo, authService);
        PedidoService pedidoService = new PedidoService(pedidoRepo, horarioFuncionamentoRepo);
        HorarioService horarioService = new HorarioService(horarioFuncionamentoRepo);
        AreaEntregaService areaEntregaService = new AreaEntregaService(areaRepo);
        CarrinhoManager carrinhoManager = new CarrinhoManager();

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            LoginFrame frame = new LoginFrame(
                    authService,
                    adminService,
                    clienteService,
                    enderecoService,
                    categoriaService,
                    produtoService,
                    restauranteService,
                    pedidoService,
                    carrinhoManager,
                    restauranteRepo,
                    areaEntregaService,
                    horarioService);
            frame.setVisible(true);
        });
    }
}
