package com.pedidos.view.restaurante;

import com.pedidos.application.service.*;
import com.pedidos.domain.entities.Restaurante;
import com.pedidos.domain.entities.Usuario;
import com.pedidos.view.util.AppColors;
import com.pedidos.view.util.AppFonts;
import com.pedidos.view.util.base.BaseFrame;
import com.pedidos.view.util.session.SessionManager;

import javax.swing.*;
import java.awt.*;

public class RestauranteFrame extends BaseFrame {

    private final Usuario usuario;
    private final CategoriaService categoriaService;
    private final ProdutoService produtoService;
    private final RestauranteService restauranteService;
    private final AreaEntregaService areaEntregaService;
    private final HorarioService horarioService;
    private final PedidoService pedidoService;
    private final AutenticacaoService autenticacaoService;

    public RestauranteFrame(Usuario usuario,
                            CategoriaService categoriaService,
                            ProdutoService produtoService,
                            RestauranteService restauranteService,
                            AreaEntregaService areaEntregaService,
                            HorarioService horarioService,
                            PedidoService pedidoService,
                            AutenticacaoService autenticacaoService) {
        super("Sistema de Delivery — Painel do Restaurante", 1000, 800);
        this.usuario = usuario;
        this.categoriaService = categoriaService;
        this.produtoService = produtoService;
        this.restauranteService = restauranteService;
        this.areaEntregaService = areaEntregaService;
        this.horarioService = horarioService;
        this.pedidoService = pedidoService;
        this.autenticacaoService = autenticacaoService;
        construirInterface();
        criarAbas();
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

    private void criarAbas() {
        JTabbedPane abas = new JTabbedPane();
        abas.setFont(AppFonts.MENU);

        abas.addTab("Produtos",                  new PainelProdutos(usuario, produtoService, categoriaService));
        abas.addTab("Pedidos",                   new PainelPedidos(usuario, pedidoService));
        abas.addTab("Áreas de Entrega",          new JPanel());
        abas.addTab("Horários de funcionamento", new JPanel());
        abas.addTab("Perfil",                    new PainelPerfil(usuario, autenticacaoService, restauranteService));

        add(abas, BorderLayout.CENTER);
    }
}
