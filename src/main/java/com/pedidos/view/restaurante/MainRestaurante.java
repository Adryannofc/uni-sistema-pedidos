package com.pedidos.view.restaurante;

import com.pedidos.application.service.*;
import com.pedidos.domain.entities.Usuario;
import com.pedidos.infra.config.FlyWayconfig;
import com.pedidos.infra.config.JPAUtil;
import com.pedidos.infra.repository.impl.*;
import jakarta.persistence.EntityManager;

import javax.swing.*;

/**
 * Entry point alternativo para desenvolvimento — abre o RestauranteFrame diretamente,
 * sem passar pelo fluxo de autenticação.
 *
 * NÃO usar em produção.
 */
public class MainRestaurante {

    private static final String DEV_RESTAURANTE_ID = "6e5a11fb-f343-41ad-9de6-175817abcc06";

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
        AdminService adminService = new AdminService(adminRepo, authService, restauranteRepo);
        ClienteService clienteService = new ClienteService(clienteRepo, authService, adminRepo, restauranteRepo, enderecoRepo);
        CategoriaService categoriaService = new CategoriaService(categoriaGlobalRepo, categoriaCardapioRepo, restauranteRepo, produtoRepo);
        ProdutoService produtoService = new ProdutoService(produtoRepo, restauranteRepo);
        RestauranteService restauranteService = new RestauranteService(restauranteRepo, categoriaGlobalRepo, authService);
        PedidoService pedidoService = new PedidoService(pedidoRepo, horarioFuncionamentoRepo);
        CarrinhoService carrinhoService = new CarrinhoService();
        HorarioService horarioService = new HorarioService(horarioFuncionamentoRepo);
        AreaEntregaService areaEntregaService = new AreaEntregaService(areaRepo);

        Usuario restaurante = restauranteRepo.buscarPorId(DEV_RESTAURANTE_ID)
                .orElseThrow(() -> new RuntimeException(
                        "Restaurante de dev não encontrado. ID: " + DEV_RESTAURANTE_ID));

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            RestauranteFrame frame = new RestauranteFrame(
                    restaurante,
                    categoriaService,
                    produtoService,
                    restauranteService,
                    areaEntregaService,
                    horarioService,
                    pedidoService
            );
            frame.setVisible(true);
        });
    }
}
