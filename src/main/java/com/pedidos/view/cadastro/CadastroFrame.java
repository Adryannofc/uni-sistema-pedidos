package com.pedidos.view.cadastro;

import com.pedidos.application.service.ClienteService;
import com.pedidos.application.service.RestauranteService;
import com.pedidos.view.util.AppFonts;

import javax.swing.*;
import java.awt.*;

public class CadastroFrame extends JFrame {

    // ── constantes dos cards ──────────────────────────────────────────────────
    public static final String CARD_TIPO        = "TIPO";
    public static final String CARD_CLIENTE     = "CLIENTE";
    public static final String CARD_RESTAURANTE = "RESTAURANTE";

    // ── atributos ─────────────────────────────────────────────────────────────
    private CardLayout cardLayout;
    private JPanel     painelPrincipal;

    private final ClienteService     clienteService;
    private final RestauranteService restauranteService;

    // ── construtor ────────────────────────────────────────────────────────────
    public CadastroFrame(ClienteService clienteService,
                         RestauranteService restauranteService) {
        this.clienteService     = clienteService;
        this.restauranteService = restauranteService;

        configurarJanela();
        inicializarComponentes();
    }

    // ── inicialização ─────────────────────────────────────────────────────────

    private void configurarJanela() {
        setTitle("Sistema de Delivery — Cadastro");
        setSize(480, 380);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setJMenuBar(criarMenuBar());
    }

    private void inicializarComponentes() {
        cardLayout      = new CardLayout();
        painelPrincipal = new JPanel(cardLayout);

        painelPrincipal.add(new PainelTipo(this),                              CARD_TIPO);
        painelPrincipal.add(new PainelCadastroCliente(this, clienteService),   CARD_CLIENTE);
        painelPrincipal.add(new PainelCadastroRestaurante(this, restauranteService), CARD_RESTAURANTE);

        add(painelPrincipal);
        setVisible(true);
    }

    // ── navegação (chamado pelos painéis) ─────────────────────────────────────

    public void mostrarCard(String nomeCard) {
        cardLayout.show(painelPrincipal, nomeCard);
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
