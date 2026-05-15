package com.pedidos.view.cliente;

import com.pedidos.application.service.*;
import com.pedidos.domain.entities.Cliente;
import com.pedidos.domain.entities.Usuario;
import com.pedidos.infra.config.FlyWayconfig;
import com.pedidos.infra.config.JPAUtil;
import com.pedidos.infra.repository.impl.*;
import com.pedidos.view.util.session.CarrinhoManager;
import jakarta.persistence.EntityManager;

import javax.swing.*;

/**
 * Entry point alternativo para desenvolvimento — abre o ClienteFrame diretamente,
 * sem passar pelo fluxo de autenticação.
 *
 */
public class MainCliente {

    private static final String DEV_EMAIL = "adryann@email.com";
    private static final String DEV_SENHA = "123456";

    public static void main(String[] args) {
        try {
            FlyWayconfig.migrate();
        } catch (Exception e) {
            System.out.println("Flyway não executado: " + e.getMessage());
        }

        EntityManager em = JPAUtil.getEntityManager();

        AdminRepositoryJPA           adminRepo           = new AdminRepositoryJPA(em);
        RestauranteRepositoryJPA     restauranteRepo     = new RestauranteRepositoryJPA(em);
        ClienteRepositoryJPA         clienteRepo         = new ClienteRepositoryJPA(em);
        CategoriaGlobalRepositoryJPA categoriaGlobalRepo = new CategoriaGlobalRepositoryJPA(em);
        CategoriaCardapioRepositoryJPA categoriaCardapioRepo = new CategoriaCardapioRepositoryJPA(em);
        ProdutoRepositoryJPA         produtoRepo         = new ProdutoRepositoryJPA(em);
        PedidoRepositoryJPA          pedidoRepo          = new PedidoRepositoryJPA(em);
        AreaEntregaRepositoryJPA     areaRepo            = new AreaEntregaRepositoryJPA(em);
        HorarioFuncionamentoRepositoryJPA horarioRepo     = new HorarioFuncionamentoRepositoryJPA(em);
        EnderecoRepositoryJPA        enderecoRepo        = new EnderecoRepositoryJPA(em);

        AutenticacaoService authService        = new AutenticacaoService(adminRepo, restauranteRepo, clienteRepo);
        ClienteService      clienteService     = new ClienteService(clienteRepo, authService, adminRepo, restauranteRepo, enderecoRepo);
        RestauranteService  restauranteService = new RestauranteService(restauranteRepo, categoriaGlobalRepo, authService);
        ProdutoService      produtoService     = new ProdutoService(produtoRepo, restauranteRepo);
        PedidoService       pedidoService      = new PedidoService(pedidoRepo, horarioRepo);
        EnderecoService     enderecoService    = new EnderecoService(enderecoRepo);
        CarrinhoManager     carrinho           = new CarrinhoManager();

        Usuario usuario = authService.autenticar(DEV_EMAIL, DEV_SENHA);

        if (!(usuario instanceof Cliente cliente)) {
            throw new RuntimeException("Usuário não é um Cliente. Email: " + DEV_EMAIL);
        }

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            ClienteFrame frame = new ClienteFrame(
                    usuario,
                    cliente,
                    clienteService,
                    enderecoService,
                    restauranteService,
                    produtoService,
                    pedidoService,
                    carrinho,
                    () -> { carrinho.esvaziar(); System.exit(0); }
            );
            frame.setVisible(true);
        });
    }
}
