package com.pedidos.view.cliente;

import com.pedidos.model.service.*;
import com.pedidos.model.entity.*;
import com.pedidos.view.util.AppColors;
import com.pedidos.view.util.AppFonts;
import com.pedidos.view.util.base.BaseFrame;
import com.pedidos.view.util.session.CarrinhoManager;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;

public class ClienteFrame extends BaseFrame {

    private final Usuario usuario;
    private final Cliente cliente;
    private final ClienteService clienteService;
    private final EnderecoService enderecoService;
    private final RestauranteService restauranteService;
    private final ProdutoService produtoService;
    private final PedidoService pedidoService;
    private final CarrinhoManager carrinho;
    private final AreaEntregaService areaEntregaService;
    private final Runnable acaoLogout;

    private JTabbedPane tabbedPane;
    private PainelFazerPedido painelFazerPedido;
    private PainelCheckout painelCheckout;
    private PainelMeusPedidos painelMeusPedidos;
    private PainelPerfil painelPerfil;

    private JLabel lblStatusPedidos;
    private JLabel lblStatusEndereco;

    public ClienteFrame(Usuario usuario,
                        Cliente cliente,
                        ClienteService clienteService,
                        EnderecoService enderecoService,
                        RestauranteService restauranteService,
                        ProdutoService produtoService,
                        PedidoService pedidoService,
                        CarrinhoManager carrinho,
                        AreaEntregaService areaEntregaService,
                        Runnable acaoLogout) {
        super("Sistema Delivery — " + usuario.getNome() + " | Cliente");
        this.usuario             = usuario;
        this.cliente             = cliente;
        this.clienteService      = clienteService;
        this.enderecoService     = enderecoService;
        this.restauranteService  = restauranteService;
        this.produtoService      = produtoService;
        this.pedidoService       = pedidoService;
        this.carrinho            = carrinho;
        this.areaEntregaService  = areaEntregaService;
        this.acaoLogout          = acaoLogout;
        construirInterface();
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
                Object[] opcoes = {"Sim", "Não"};
                int r = JOptionPane.showOptionDialog(ClienteFrame.this,
                        "Deseja sair do sistema?", "Confirmar Logout",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, opcoes, opcoes[0]);
                if (r == JOptionPane.YES_OPTION) {
                    carrinho.esvaziar();
                    SwingUtilities.invokeLater(() -> acaoLogout.run());
                }
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

        header.add(titulo,    BorderLayout.WEST);
        header.add(nomeLabel, BorderLayout.EAST);
        return header;
    }

    private JPanel criarStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 3));
        bar.setBackground(new Color(240, 240, 240));
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));

        int pedidosAtivos = pedidoService.listarPorCliente(cliente.getId()).size();
        String infoEndereco = cliente.getEnderecoPadrao()
                .map(e -> e.getRua() + ", " + e.getNumero() + " - " + e.getCidade())
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
        int total = pedidoService.listarPorCliente(cliente.getId()).size();
        lblStatusPedidos.setText(total + " pedido(s) ativo(s)");
        lblStatusEndereco.setText(cliente.getEnderecoPadrao()
                .map(e -> e.getRua() + ", " + e.getNumero() + " - " + e.getCidade())
                .orElse("Nenhum endereço cadastrado"));
    }

    private JTabbedPane criarAbas() {
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(AppFonts.MENU);
        tabbedPane.setBackground(Color.WHITE);

        painelFazerPedido = new PainelFazerPedido(
                cliente,
                restauranteService,
                produtoService,
                carrinho,
                areaEntregaService,
                () -> {
                    painelCheckout.sincronizar();
                    tabbedPane.setSelectedIndex(1);
                }
        );

        painelCheckout = new PainelCheckout(
                usuario,
                cliente,
                clienteService,
                pedidoService,
                carrinho,
                painelFazerPedido,
                () -> {
                    painelFazerPedido.sincronizarCarrinho();
                    atualizarTituloFazerPedido();
                    painelMeusPedidos.carregarMeusPedidos();
                    atualizarStatusBar();
                    tabbedPane.setSelectedIndex(2);
                }
        );

        painelMeusPedidos = new PainelMeusPedidos(cliente, pedidoService);

        painelPerfil = new PainelPerfil(usuario, cliente, clienteService, () -> {
            painelCheckout.atualizarEndereco();
            atualizarStatusBar();
        });

        tabbedPane.addTab("Fazer Pedido", painelFazerPedido);
        tabbedPane.addTab("Checkout",     painelCheckout);
        tabbedPane.addTab("Meus Pedidos", painelMeusPedidos);
        tabbedPane.addTab("Perfil",       painelPerfil);

        atualizarTituloFazerPedido();
        tabbedPane.setSelectedIndex(0);
        return tabbedPane;
    }

    public void selecionarAba(int indice) {
        tabbedPane.setSelectedIndex(indice);
    }

    public void atualizarTituloFazerPedido() {
        int total = carrinho.estaVazio() ? 0
                : carrinho.getItens().stream()
                .mapToInt(CarrinhoManager.ItemCarrinho::getQuantidade).sum();
        tabbedPane.setTitleAt(0, total > 0 ? "Fazer Pedido (" + total + ")" : "Fazer Pedido");
    }
}