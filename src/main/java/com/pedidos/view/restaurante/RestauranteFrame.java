package com.pedidos.view.restaurante;

import com.pedidos.controller.*;
import com.pedidos.model.entity.Usuario;
import com.pedidos.view.util.AppColors;
import com.pedidos.view.util.AppFonts;
import com.pedidos.view.util.base.BaseFrame;
import com.pedidos.view.util.session.SessionManager;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;

public class RestauranteFrame extends BaseFrame {

    private final Usuario usuario;
    private final CategoriaController categoriaController;
    private final ProdutoController produtoController;
    private final RestauranteController restauranteController;
    private final AreaEntregaController areaEntregaController;
    private final HorarioController horarioController;
    private final PedidoController pedidoController;
    private final AutenticacaoController autenticacaoController;
    private final Runnable acaoLogout;

    public RestauranteFrame(Usuario usuario,
                            CategoriaController categoriaController,
                            ProdutoController produtoController,
                            RestauranteController restauranteController,
                            AreaEntregaController areaEntregaController,
                            HorarioController horarioController,
                            PedidoController pedidoController,
                            AutenticacaoController autenticacaoController,
                            Runnable acaoLogout) {
        super("Sistema de Delivery — Painel do Restaurante");
        this.usuario = usuario;
        this.categoriaController = categoriaController;
        this.produtoController = produtoController;
        this.restauranteController = restauranteController;
        this.areaEntregaController = areaEntregaController;
        this.horarioController = horarioController;
        this.pedidoController = pedidoController;
        this.autenticacaoController = autenticacaoController;
        this.acaoLogout = acaoLogout;
        construirInterface();
        criarAbas();
        criarMenu();
    }


    private void construirInterface() {
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppColors.AZUL_PRIMARIO);
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titulo = new JLabel("Painel do Restaurante");
        titulo.setFont(AppFonts.TITULO);
        titulo.setForeground(AppColors.TEXTO_BRANCO);

        JLabel nomeLabel = new JLabel("Olá, " + usuario.getNome());
        nomeLabel.setFont(AppFonts.STATUS);
        nomeLabel.setForeground(AppColors.TEXTO_BRANCO);

        header.add(titulo, BorderLayout.WEST);
        header.add(nomeLabel, BorderLayout.EAST);

        JButton btnSair = new JButton("Sair");
        btnSair.setFont(AppFonts.BOTAO);
        btnSair.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(this,
                    "Tem certeza que deseja sair?",
                    "Sair",
                    JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.OK_OPTION) return;
            SessionManager.getInstance().encerrarSessao();
            dispose();
            System.exit(0);
        });

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rodape.add(btnSair);

        add(header, BorderLayout.NORTH);
        add(rodape, BorderLayout.SOUTH);
    }

    private JMenuBar criarMenu() {

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        menuBar.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));

        JMenu menuLogout = new JMenu("Logout");
        menuLogout.setBackground(Color.WHITE);
        menuLogout.setForeground(Color.BLACK);
        menuLogout.setFont(AppFonts.MENU);
        menuLogout.addMenuListener(new MenuListener() {
            @Override public void menuDeselected(MenuEvent e) {}
            @Override public void menuCanceled(MenuEvent e) {}
            @Override
            public void menuSelected(MenuEvent e) {
                menuLogout.setPopupMenuVisible(false);
                Object[] opcoes = {"Sim", "Não"};
                int r = JOptionPane.showOptionDialog(RestauranteFrame.this,
                        "Deseja sair do sistema?", "Confirmar Logout",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, opcoes, opcoes[0]);
                if (r == JOptionPane.YES_OPTION) {
                    SwingUtilities.invokeLater(() -> acaoLogout.run());
                }
            }
        });

        menuBar.add(menuLogout);
        setJMenuBar(menuBar);

        return menuBar;
    }

    private void criarAbas() {
        JTabbedPane abas = new JTabbedPane();
        abas.setFont(AppFonts.MENU);

        abas.addTab("Produtos",                  new PainelProdutos(usuario, produtoController, categoriaController));
        abas.addTab("Pedidos",                   new PainelPedidos(usuario, pedidoController));
        abas.addTab("Áreas de Entrega",          new PainelAreaEntrega(usuario, areaEntregaController));
        abas.addTab("Horários de funcionamento", new PainelHorarios(usuario, horarioController));
        abas.addTab("Perfil",                    new PainelPerfil(usuario, autenticacaoController, restauranteController));

        add(abas, BorderLayout.CENTER);
    }
}
