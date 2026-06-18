package com.pedidos.view.cliente;

import com.pedidos.controller.*;
import com.pedidos.model.entity.*;
import com.pedidos.view.util.AppColors;
import com.pedidos.view.util.AppFonts;
import com.pedidos.view.util.base.BaseFrame;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ClienteFrame extends BaseFrame {

    private final Usuario usuario;
    private final Cliente cliente;
    private final ClienteController clienteController;
    private final EnderecoController enderecoController;
    private final CategoriaController categoriaController;
    private final RestauranteController restauranteController;
    private final ProdutoController produtoController;
    private final PedidoController pedidoController;
    private final CarrinhoController carrinhoController;
    private final AreaEntregaController areaEntregaController;
    private final Runnable acaoLogout;

    private JTabbedPane tabbedPane;
    private PainelFazerPedido painelFazerPedido;
    private PainelCheckout painelCheckout;
    private PainelMeusPedidos painelMeusPedidos;
    private PainelPerfil painelPerfil;

    private JLabel lblStatusPedidos;
    private JLabel lblStatusEndereco;

    // track last selected tab to allow cancelling a tab change
    private int lastSelectedIndex = 0;

    public ClienteFrame(Usuario usuario,
                        Cliente cliente,
                        ClienteController clienteController,
                        EnderecoController enderecoController,
                        CategoriaController categoriaController,
                        RestauranteController restauranteController,
                        ProdutoController produtoController,
                        PedidoController pedidoController,
                        CarrinhoController carrinhoController,
                        AreaEntregaController areaEntregaController,
                        Runnable acaoLogout) {
        super("Sistema Delivery — " + usuario.getNome() + " | Cliente");
        this.usuario             = usuario;
        this.cliente             = cliente;
        this.clienteController      = clienteController;
        this.enderecoController     = enderecoController;
        this.categoriaController    = categoriaController;
        this.restauranteController  = restauranteController;
        this.produtoController      = produtoController;
        this.pedidoController       = pedidoController;
        this.carrinhoController  = carrinhoController;
        this.areaEntregaController  = areaEntregaController;
        this.acaoLogout          = acaoLogout;
        construirInterface();

        // Interceptar fechamento da janela para confirmar se há alterações não salvas
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (painelPerfil != null && painelPerfil.isDadosAlterados()) {
                    int r = JOptionPane.showConfirmDialog(ClienteFrame.this,
                            "Alterações não salvas. Deseja sair?",
                            "Confirmar saída",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                    if (r != JOptionPane.YES_OPTION) return; // cancela fechamento
                }
                // sem alterações ou confirmou: encerra app
                dispose();
                System.exit(0);
            }
        });
    }

    private void construirInterface() {
        setLayout(new BorderLayout());
        add(criarHeader(),    BorderLayout.NORTH);
        add(criarAbas(),      BorderLayout.CENTER);
        add(criarStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel criarHeader() {
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
                executarLogout();
            }
        });

        menuBar.add(menuLogout);
        setJMenuBar(menuBar);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppColors.AZUL_PRIMARIO);
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titulo = new JLabel("Sistema Delivery");
        titulo.setFont(AppFonts.TITULO);
        titulo.setForeground(AppColors.TEXTO_BRANCO);

        JLabel nomeLabel = new JLabel("Bem-vindo, " + usuario.getNome() + "!");
        nomeLabel.setFont(AppFonts.STATUS);
        nomeLabel.setForeground(AppColors.TEXTO_BRANCO);

        JButton btnSair = new JButton("Sair");
        btnSair.setFont(AppFonts.BOTAO);
        btnSair.setBackground(new Color(220, 53, 69));
        btnSair.setForeground(AppColors.TEXTO_BRANCO);
        btnSair.setOpaque(true);
        btnSair.setBorderPainted(false);
        btnSair.setFocusPainted(false);
        btnSair.addActionListener(e -> executarLogout());

        JPanel painelDireito = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        painelDireito.setOpaque(false);
        painelDireito.add(nomeLabel);
        painelDireito.add(btnSair);

        header.add(titulo,        BorderLayout.WEST);
        header.add(painelDireito, BorderLayout.EAST);
        return header;
    }

    private void executarLogout() {
        Object[] opcoes = {"Sim", "Não"};
        int r = JOptionPane.showOptionDialog(this,
                "Deseja sair do sistema?", "Confirmar Logout",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, opcoes, opcoes[0]);
        if (r == JOptionPane.YES_OPTION) {
            carrinhoController.esvaziar();
            SwingUtilities.invokeLater(() -> acaoLogout.run());
        }
    }

    private JPanel criarStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 3));
        bar.setBackground(new Color(240, 240, 240));
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));

        int pedidosAtivos = pedidoController.listarPorCliente(cliente.getId()).size();
        String infoEndereco = enderecoController.buscarPadraoComoDTO(cliente.getId())
                .map(e -> e.rua() + ", " + e.numero() + " - " + e.cidade())
                .orElse("Nenhum endereço cadastrado");

        lblStatusPedidos  = new JLabel(pedidosAtivos + " pedido(s) ativo(s)");
        lblStatusEndereco = new JLabel(infoEndereco);

        for (JLabel l : new JLabel[]{
                new JLabel(cliente.getNome() + " | Cliente"),
                new JLabel("|"),
                lblStatusPedidos,
                new JLabel("|"),
                lblStatusEndereco
        }) {
            l.setFont(AppFonts.STATUS.deriveFont(11f));
            l.setForeground(Color.DARK_GRAY);
            bar.add(l);
        }
        return bar;
    }

    public void atualizarStatusBar() {
        int total = pedidoController.listarPorCliente(cliente.getId()).size();
        lblStatusPedidos.setText(total + " pedido(s) ativo(s)");
        lblStatusEndereco.setText(enderecoController.buscarPadraoComoDTO(cliente.getId())
                .map(e -> e.rua() + ", " + e.numero() + " - " + e.cidade())
                .orElse("Nenhum endereço cadastrado"));
    }

    private JTabbedPane criarAbas() {
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(AppFonts.MENU);
        tabbedPane.setBackground(Color.WHITE);

        painelFazerPedido = new PainelFazerPedido(
                cliente.getId(),
                enderecoController,
                categoriaController,
                restauranteController,
                produtoController,
                carrinhoController,
                areaEntregaController,
                () -> {
                    painelCheckout.sincronizar();
                    tabbedPane.setSelectedIndex(1);
                }
        );

        painelCheckout = new PainelCheckout(
                usuario,
                cliente,
                clienteController,
                pedidoController,
                carrinhoController,
                restauranteController,
                painelFazerPedido,
                () -> {
                    painelFazerPedido.sincronizarCarrinho();
                    atualizarTituloFazerPedido();
                    painelMeusPedidos.carregarMeusPedidos();
                    atualizarStatusBar();
                    tabbedPane.setSelectedIndex(2);
                }
        );

        painelMeusPedidos = new PainelMeusPedidos(cliente, pedidoController);

        painelPerfil = new PainelPerfil(usuario, cliente, clienteController, () -> {
            painelCheckout.atualizarEndereco();
            atualizarStatusBar();
        });

        tabbedPane.addTab("Fazer Pedido", painelFazerPedido);
        tabbedPane.addTab("Checkout",     painelCheckout);
        tabbedPane.addTab("Meus Pedidos", painelMeusPedidos);
        tabbedPane.addTab("Perfil",       painelPerfil);

        atualizarTituloFazerPedido();
        tabbedPane.setSelectedIndex(0);

        // Lembrar a aba inicial
        lastSelectedIndex = tabbedPane.getSelectedIndex();

        // Intercepta troca de abas para confirmar saída da aba Perfil se houver alterações
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int sel = tabbedPane.getSelectedIndex();

                if (sel == tabbedPane.indexOfComponent(painelFazerPedido)) {
                    painelFazerPedido.carregarRestaurantes();
                }

                int perfilIndex = tabbedPane.indexOfComponent(painelPerfil);
                if (lastSelectedIndex == perfilIndex && sel != perfilIndex) {
                    if (painelPerfil != null && painelPerfil.isDadosAlterados()) {
                        int r = JOptionPane.showConfirmDialog(ClienteFrame.this,
                                "Alterações não salvas. Deseja sair?",
                                "Confirmar saída",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE);
                        if (r != JOptionPane.YES_OPTION) {
                            // usuário cancelou: volta para a aba anterior
                            SwingUtilities.invokeLater(() -> tabbedPane.setSelectedIndex(lastSelectedIndex));
                            return;
                        } else {
                            // usuário confirmou que quer sair sem salvar: resetar flag
                            painelPerfil.resetDadosAlterados();
                        }
                    }
                }
                lastSelectedIndex = tabbedPane.getSelectedIndex();
            }
        });

        return tabbedPane;
    }

    public void selecionarAba(int indice) {
        tabbedPane.setSelectedIndex(indice);
    }

    public void atualizarTituloFazerPedido() {
        int total = carrinhoController.estaVazio() ? 0
                : carrinhoController.getItensComoDTO().stream()
                .mapToInt(dto -> dto.quantidade()).sum();
        tabbedPane.setTitleAt(0, total > 0 ? "Fazer Pedido (" + total + ")" : "Fazer Pedido");
    }
}