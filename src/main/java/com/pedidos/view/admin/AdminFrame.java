package com.pedidos.view.admin;

import com.pedidos.application.service.AutenticacaoService; // Import correto do serviço
import com.pedidos.domain.entities.Usuario;
import com.pedidos.view.login.LoginFrame;
import com.pedidos.view.util.AppColors;
import com.pedidos.view.util.AppFonts;
import com.pedidos.view.util.base.BaseFrame;

import javax.swing.*;
import java.awt.*;

import static com.pedidos.view.util.AppColors.*;
import static com.pedidos.view.util.AppFonts.LABEL;

public class AdminFrame extends BaseFrame {
    private final Usuario usuario;
    private AutenticacaoService authService; // O tipo correto é o Serviço

    public AdminFrame(Usuario usuario,AutenticacaoService authService) {
        super("Sistema de Delivery — Painel Administrativo", 700, 500);
        this.usuario = usuario;
        this.authService = authService;

        construirInterface();
        menuBarAdmin();

    }

    private void menuBarAdmin() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuArquivo = new JMenu("Arquivo");
        JMenu menuRestaurantes = new JMenu("Restaurantes");
        JMenu menuCategoria = new JMenu("Categoria");
        JMenu menuContas = new JMenu("Contas");

        JMenuItem itemLogout = new JMenuItem("Logout");
        JMenuItem itemSair = new JMenuItem("Sair");

        // Ação de Sair do Sistema
        itemSair.addActionListener(e -> System.exit(0));

        // Ação de Logout (Retornar para o Login)
        itemLogout.addActionListener(e -> {
            System.out.println("DEBUG: O serviço no Admin é nulo? " + (this.authService == null));
            this.dispose(); //Fecha/Descarta a tela
            new LoginFrame(this.authService).setVisible(true); //Reabre com a logica inicial de login

        });

        // Organiza o Menu Arquivo
        menuArquivo.add(itemLogout);
        menuArquivo.addSeparator();
        menuArquivo.add(itemSair);

        // Itens de Restaurantes
        JMenuItem itemListar = new JMenuItem("Listar Todos");
        JMenuItem itemPendentes = new JMenuItem("Aprovações Pendentes");
        menuRestaurantes.add(itemListar);
        menuRestaurantes.addSeparator();
        menuRestaurantes.add(itemPendentes);

        // Adiciona tudo na Barra
        menuBar.add(menuArquivo);
        menuBar.add(menuRestaurantes);
        menuBar.add(menuCategoria);
        menuBar.add(menuContas);

        this.setJMenuBar(menuBar);
    }


    private JPanel sideBar(){
        JPanel sideBar = new JPanel();
        sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.Y_AXIS));
        sideBar.setBackground(CINZA_FUNDO);
        sideBar.setPreferredSize(new Dimension(220, 0));
        sideBar.setBorder(BorderFactory.createMatteBorder(0,0,0,1, CINZA_BORDA));

        // --- Seção do Perfil (Foto e Nome) ---
        JPanel perfilPanel = new JPanel();
        perfilPanel.setLayout(new BoxLayout(perfilPanel, BoxLayout.Y_AXIS));
        perfilPanel.setOpaque(false);
        perfilPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel fotoLabel = new JLabel("AD", SwingConstants.CENTER);
        fotoLabel.setOpaque(true);
        fotoLabel.setBackground(AZUL_PRIMARIO);
        fotoLabel.setForeground(TEXTO_BRANCO);
        fotoLabel.setFont(LABEL);
        fotoLabel.setPreferredSize(new Dimension(80, 80));
        fotoLabel.setMaximumSize(new Dimension(80, 80));
        fotoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);


        JLabel nomeAdmin = new JLabel("Administrador");
        nomeAdmin.setAlignmentX(Component.CENTER_ALIGNMENT);
        nomeAdmin.setFont(LABEL);

        JLabel badgeAdmin = new JLabel(" ADMIN ");
        badgeAdmin.setAlignmentX(Component.CENTER_ALIGNMENT);
        badgeAdmin.setForeground(AZUL_HOVER);
        badgeAdmin.setBorder(BorderFactory.createLineBorder(AZUL_HOVER));

        perfilPanel.add(fotoLabel);
        perfilPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Espaço
        perfilPanel.add(nomeAdmin);
        perfilPanel.add(badgeAdmin);

        sideBar.add(Box.createVerticalGlue());



        return sideBar;
    };

    private void construirInterface() {
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppColors.AZUL_PRIMARIO);
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));


        JLabel nomeLabel = new JLabel("Olá, " + usuario.getNome());
        nomeLabel.setFont(AppFonts.STATUS);
        nomeLabel.setForeground(AppColors.TEXTO_BRANCO);

        // BARRA ACIMA NO MENU
        menuBarAdmin();

        sideBar();
        add(sideBar(), BorderLayout.WEST);


        header.add(nomeLabel, BorderLayout.EAST);

        // Conteúdo Central
        JLabel centro = new JLabel("Área administrativa — em desenvolvimento", SwingConstants.CENTER);
        centro.setFont(LABEL);




        // Rodapé
        JButton btnSairRodape = new JButton("Sair");
        btnSairRodape.addActionListener(e -> System.exit(0));

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rodape.add(btnSairRodape);

        add(header, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);
    }
}