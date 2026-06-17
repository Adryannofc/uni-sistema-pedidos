package com.pedidos.view.cadastro;

import com.pedidos.controller.ClienteController;
import com.pedidos.controller.RestauranteController;
import com.pedidos.view.util.AppColors;
import com.pedidos.view.util.AppFonts;
import com.pedidos.view.util.base.BaseFrame;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class CadastroFrame extends BaseFrame {

    // ── constantes dos cards ──────────────────────────────────────────────────
    public static final String CARD_TIPO        = "TIPO";
    public static final String CARD_CLIENTE     = "CLIENTE";
    public static final String CARD_RESTAURANTE = "RESTAURANTE";

    // ── atributos ─────────────────────────────────────────────────────────────
    private CardLayout cardLayout;
    private JPanel     painelPrincipal;

    private final ClienteController     clienteController;
    private final RestauranteController restauranteController;

    // ── construtor ────────────────────────────────────────────────────────────
    public CadastroFrame(ClienteController clienteController,
                         RestauranteController restauranteController) {
        super("Sistema de Delivery — Novo Cadastro | Cliente", 500, 420);
        this.clienteController     = clienteController;
        this.restauranteController = restauranteController;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setJMenuBar(criarMenuBar());
        inicializarComponentes();
    }

    // ── inicialização ─────────────────────────────────────────────────────────

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        cardLayout      = new CardLayout();
        painelPrincipal = new JPanel(cardLayout);

        painelPrincipal.add(new PainelTipo(this),                                             CARD_TIPO);
        painelPrincipal.add(new PainelCadastroCliente(this, clienteController),              CARD_CLIENTE);
        painelPrincipal.add(new PainelCadastroRestaurante(this, restauranteController),      CARD_RESTAURANTE);

        add(painelPrincipal,  BorderLayout.CENTER);
        add(criarStatusBar(), BorderLayout.SOUTH);
    }

    // ── navegação (chamado pelos painéis) ─────────────────────────────────────

    public void mostrarCard(String nomeCard) {
        cardLayout.show(painelPrincipal, nomeCard);
        if (CARD_RESTAURANTE.equals(nomeCard)) {
            setTitle("Sistema de Delivery — Novo Cadastro | Restaurante");
            setSize(500, 470);
            setLocationRelativeTo(null);
        } else if (CARD_CLIENTE.equals(nomeCard)) {
            setTitle("Sistema de Delivery — Novo Cadastro | Cliente");
            setSize(500, 420);
            setLocationRelativeTo(null);
        }
    }

    // ── status bar ────────────────────────────────────────────────────────────

    private JPanel criarStatusBar() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(AppColors.CINZA_STATUS);
        barra.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, AppColors.CINZA_BORDA),
                new EmptyBorder(3, 8, 3, 8)
        ));

        JLabel labelVersao = new JLabel("Sistema de Delivery v1.0");
        labelVersao.setFont(AppFonts.STATUS);

        JLabel labelConexao = new JLabel("Desconectado");
        labelConexao.setFont(AppFonts.STATUS);
        labelConexao.setForeground(AppColors.TEXTO_SECUNDARIO);

        barra.add(labelVersao,  BorderLayout.WEST);
        barra.add(labelConexao, BorderLayout.EAST);
        return barra;
    }

    // ── menu ──────────────────────────────────────────────────────────────────

    private JMenuBar criarMenuBar() {
        JMenuBar menuBar  = new JMenuBar();
        JMenu menuSistema = new JMenu("Sistema");
        menuSistema.setFont(AppFonts.MENU);

        JMenuItem itemVoltar = new JMenuItem("Voltar ao Login");
        itemVoltar.setFont(AppFonts.MENU);
        itemVoltar.addActionListener(e -> dispose());

        JMenuItem itemSair = new JMenuItem("Sair");
        itemSair.setFont(AppFonts.MENU);
        itemSair.addActionListener(e -> System.exit(0));

        menuSistema.add(itemVoltar);
        menuSistema.addSeparator();
        menuSistema.add(itemSair);
        menuBar.add(menuSistema);
        return menuBar;
    }
}