package com.pedidos.view.cliente;
package com.pedidos.view.cliente;

import com.pedidos.application.service.*;
import com.pedidos.domain.entities.Usuario;
import com.pedidos.domain.entities.Restaurante;
import com.pedidos.domain.entities.Cliente;
import com.pedidos.view.util.AppColors;
import com.pedidos.view.util.AppFonts;
import com.pedidos.view.util.base.BaseFrame;
import com.pedidos.view.util.session.CarrinhoManager;
import com.pedidos.view.util.session.SessionManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ClienteFrame extends BaseFrame {

    private final Usuario usuario;
    private final RestauranteService restauranteService;
    private final ProdutoService produtoService;
    private final ClienteService clienteService;
    private final CarrinhoManager carrinho;
    private JTabbedPane tabbedPane;
    private DefaultTableModel tabelaCarrinhoModel;
    private JLabel lblSubtotal;
    private JLabel lblTaxaEntrega;
    private JLabel lblTotal;
    private int itemCountFazerPedido = 0;

    public ClienteFrame(Usuario usuario, RestauranteService restauranteService,
                        ProdutoService produtoService, ClienteService clienteService) {
        super("Sistema Delivery | Cliente", 1000, 700);
        this.usuario = usuario;
        this.restauranteService = restauranteService;
        this.produtoService = produtoService;
        this.clienteService = clienteService;
        this.carrinho = SessionManager.getInstance().getCarrinho();
        construirInterface();
    }

    // ─────────────────────────────────────────────────────────────
    // INTERFACE PRINCIPAL
    // ─────────────────────────────────────────────────────────────
    private void construirInterface() {
        setLayout(new BorderLayout());
        add(criarHeader(),    BorderLayout.NORTH);
        add(criarAbas(),      BorderLayout.CENTER);
        add(criarStatusBar(), BorderLayout.SOUTH);
    }

    // ─────────────────────────────────────────────────────────────
    // HEADER
    // ─────────────────────────────────────────────────────────────
    private JPanel criarHeader() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        menuBar.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));

        for (String nome : new String[]{"Pedido", "Histórico", "Perfil", "Logout"}) {
            JMenu m = new JMenu(nome);
            m.setBackground(Color.WHITE);
            m.setForeground(Color.BLACK);
            m.setFont(AppFonts.STATUS);
            menuBar.add(m);
        }
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

    // ─────────────────────────────────────────────────────────────
    // STATUS BAR (rodapé)
    // ─────────────────────────────────────────────────────────────
    private JPanel criarStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 3));
        bar.setBackground(new Color(240, 240, 240));
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));

        JLabel lblNome    = new JLabel(usuario.getNome() + " | Cliente");
        JLabel sep1       = new JLabel("|");
        JLabel lblPedidos = new JLabel("1 pedido(s) ativo(s)");
        JLabel sep2       = new JLabel("|");
        JLabel lblEnd     = new JLabel("Endereço cadastrado");

        for (JLabel l : new JLabel[]{lblNome, sep1, lblPedidos, sep2, lblEnd}) {
            l.setFont(AppFonts.STATUS.deriveFont(11f));
            l.setForeground(Color.DARK_GRAY);
            bar.add(l);
        }

        return bar;
    }

    // ─────────────────────────────────────────────────────────────
    // ABAS
    // ─────────────────────────────────────────────────────────────
    private JTabbedPane criarAbas() {
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(AppFonts.STATUS);
        tabbedPane.setBackground(Color.WHITE);

        tabbedPane.addTab("Fazer Pedido (" + itemCountFazerPedido + ")", criarPainelFazerPedido());
        tabbedPane.addTab("Checkout",         criarPainelCheckout());
        tabbedPane.addTab("Meus Pedidos",     criarPainelMeusPedidos());
        tabbedPane.addTab("Perfil",           criarPainelPerfil());

        tabbedPane.setSelectedIndex(0);

        return tabbedPane;
    }

    // ─────────────────────────────────────────────────────────────
    // ABA — FAZER PEDIDO
    // ─────────────────────────────────────────────────────────────
    private JPanel criarPainelFazerPedido() {
        JPanel painel = new JPanel(new BorderLayout(12, 0));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        painel.add(criarListaRestaurantes(), BorderLayout.CENTER);
        painel.add(criarPainelCarrinho(),    BorderLayout.EAST);

        return painel;
    }

    private JPanel criarListaRestaurantes() {
        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.setBackground(Color.WHITE);

        JLabel titulo = new JLabel("Restaurantes disponíveis");
        titulo.setFont(AppFonts.STATUS.deriveFont(Font.BOLD, 13f));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        String[] colunas = {"Restaurante", "Categoria", "★", "Ações"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 3; }
        };

        JTable tabela = new JTable(model);
        tabela.setFont(AppFonts.STATUS);
        tabela.setRowHeight(35);
        tabela.setGridColor(new Color(220, 220, 220));
        tabela.setShowGrid(true);
        tabela.setSelectionBackground(new Color(220, 235, 255));

        JTableHeader th = tabela.getTableHeader();
        th.setFont(AppFonts.STATUS.deriveFont(Font.BOLD));
        th.setBackground(new Color(245, 245, 245));
        th.setForeground(Color.DARK_GRAY);
        th.setReorderingAllowed(false);

        tabela.getColumnModel().getColumn(0).setPreferredWidth(250);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(120);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(40);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(200);

        // Renderer para coluna de estrela
        tabela.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean sel, boolean foc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        t, value, sel, foc, row, col);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setForeground("★".equals(value)
                        ? new Color(255, 160, 0) : Color.LIGHT_GRAY);
                return lbl;
            }
        });

        // Renderer para coluna de ações (botões)
        tabela.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean sel, boolean foc, int row, int col) {
                JPanel cellPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 3));
                cellPanel.setBackground(sel ? t.getSelectionBackground() : Color.WHITE);

                JButton btnCardapio = new JButton("Cardápio");
                btnCardapio.setPreferredSize(new Dimension(80, 26));
                btnCardapio.setFont(AppFonts.STATUS.deriveFont(11f));
                btnCardapio.setBackground(AppColors.AZUL_PRIMARIO);
                btnCardapio.setForeground(Color.WHITE);
                btnCardapio.setFocusPainted(false);
                btnCardapio.setOpaque(true);
                btnCardapio.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                final int linhaBtnCardapio = row;
                btnCardapio.addActionListener(e -> acaoVerCardapio(linhaBtnCardapio));

                JButton btnFavoritar = new JButton("★");
                btnFavoritar.setPreferredSize(new Dimension(35, 26));
                btnFavoritar.setFont(AppFonts.STATUS.deriveFont(Font.BOLD, 12f));
                btnFavoritar.setBackground(new Color(240, 200, 80));
                btnFavoritar.setForeground(Color.WHITE);
                btnFavoritar.setFocusPainted(false);
                btnFavoritar.setOpaque(true);
                btnFavoritar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                final int linhaBtnFav = row;
                btnFavoritar.addActionListener(e -> {
                    try {
                        String nomeResto = (String) model.getValueAt(linhaBtnFav, 0);
                        acaoFavoritar(nomeResto);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(ClienteFrame.this,
                                "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                });

                cellPanel.add(btnCardapio);
                cellPanel.add(btnFavoritar);
                return cellPanel;
            }
        });

        // Carregar restaurantes do banco
        try {
            List<Restaurante> restaurantesAtivos = restauranteService
                    .buscarRestaurantesAtivos();

            for (Restaurante r : restaurantesAtivos) {
                String categoria = r.getCategoriaGlobal() != null
                    ? r.getCategoriaGlobal().getNome()
                    : "—";
                model.addRow(new Object[]{
                        r.getNome(),
                        categoria,
                        "", // Será preenchido com ★ se favoritado
                        "" // Botões renderizados na célula
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar restaurantes: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        painel.add(titulo, BorderLayout.NORTH);
        painel.add(scroll, BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarPainelCarrinho() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setPreferredSize(new Dimension(320, 0));
        painel.setBackground(Color.WHITE);
        painel.setBorder(titledBorder("🛒 Meu Carrinho (" + carrinho.contarItens() + ")"));

        String[] colunas = {"Produto", "Qtd", "Subtotal"};
        tabelaCarrinhoModel = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        // Popular tabela com itens do carrinho
        NumberFormat formato = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        for (CarrinhoManager.ItemCarrinho item : carrinho.getItens()) {
            tabelaCarrinhoModel.addRow(new Object[]{
                    item.getProduto().getNome(),
                    item.getQuantidade(),
                    formato.format(item.calcularSubtotal())
            });
        }

        JTable tabela = new JTable(tabelaCarrinhoModel);
        tabela.setFont(AppFonts.STATUS);
        tabela.setRowHeight(28);
        tabela.setGridColor(new Color(220, 220, 220));
        tabela.setShowGrid(true);
        tabela.setSelectionBackground(new Color(220, 235, 255));

        JTableHeader th = tabela.getTableHeader();
        th.setFont(AppFonts.STATUS.deriveFont(Font.BOLD));
        th.setBackground(new Color(245, 245, 245));
        th.setForeground(Color.DARK_GRAY);
        th.setReorderingAllowed(false);

        tabela.getColumnModel().getColumn(0).setPreferredWidth(140);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(35);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(100);

        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        tabela.getColumnModel().getColumn(1).setCellRenderer(centro);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        // Botões de ação
        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 6));
        botoesPanel.setBackground(Color.WHITE);
        botoesPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));

        JButton btnRemover = new JButton("Remover");
        btnRemover.setPreferredSize(new Dimension(80, 28));
        btnRemover.setFont(AppFonts.STATUS.deriveFont(10f));
        btnRemover.setBackground(new Color(220, 100, 100));
        btnRemover.setForeground(Color.WHITE);
        btnRemover.setFocusPainted(false);
        btnRemover.setOpaque(true);
        btnRemover.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRemover.addActionListener(e -> acaoRemoverDoCarrinho(tabela));

        JButton btnEsvaziar = new JButton("Esvaziar");
        btnEsvaziar.setPreferredSize(new Dimension(80, 28));
        btnEsvaziar.setFont(AppFonts.STATUS.deriveFont(10f));
        btnEsvaziar.setBackground(new Color(200, 140, 100));
        btnEsvaziar.setForeground(Color.WHITE);
        btnEsvaziar.setFocusPainted(false);
        btnEsvaziar.setOpaque(true);
        btnEsvaziar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEsvaziar.addActionListener(e -> acaoEsvaziarCarrinho());

        botoesPanel.add(btnRemover);
        botoesPanel.add(btnEsvaziar);

        centerPanel.add(botoesPanel, BorderLayout.SOUTH);
        painel.add(centerPanel, BorderLayout.CENTER);

        return painel;
    }

    // ─────────────────────────────────────────────────────────────
    // ABA — CHECKOUT
    // ─────────────────────────────────────────────────────────────
    private JPanel criarPainelCheckout() {
        JPanel painel = new JPanel(new BorderLayout(0, 12));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        painel.add(criarResumo(),  BorderLayout.CENTER);
        painel.add(criarRodape(), BorderLayout.SOUTH);

        return painel;
    }

    private JPanel criarResumo() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(Color.WHITE);
        painel.setBorder(titledBorder("Resumo do Pedido"));

        String[] colunas = {"Produto", "Qtd", "Preço unit.", "Subtotal"};
        Object[][] dados = {
                {"X-Burguer", 2, "R$ 18,90", "R$ 37,80"},
                {"X-Bacon",   1, "R$ 22,90", "R$ 22,90"},
        };

        DefaultTableModel model = new DefaultTableModel(dados, colunas) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable tabela = new JTable(model);
        tabela.setFont(AppFonts.STATUS);
        tabela.setRowHeight(30);
        tabela.setGridColor(new Color(220, 220, 220));
        tabela.setShowGrid(true);
        tabela.setSelectionBackground(new Color(220, 235, 255));

        JTableHeader th = tabela.getTableHeader();
        th.setFont(AppFonts.STATUS.deriveFont(Font.BOLD));
        th.setBackground(new Color(245, 245, 245));
        th.setForeground(Color.DARK_GRAY);
        th.setReorderingAllowed(false);

        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 1; i <= 3; i++) tabela.getColumnModel().getColumn(i).setCellRenderer(centro);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        painel.add(scroll, BorderLayout.CENTER);
        painel.add(criarTotais(), BorderLayout.SOUTH);

        return painel;
    }

    private JPanel criarTotais() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createEmptyBorder(10, 0, 8, 16));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 12, 3, 12);

        adicionarLinhaTotal(painel, gbc, "Subtotal:",        "R$ 60,70", false, 0);
        adicionarLinhaTotal(painel, gbc, "Taxa de entrega:", "R$ 5,00",  false, 1);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JSeparator sep = new JSeparator();
        sep.setPreferredSize(new Dimension(240, 1));
        painel.add(sep, gbc);
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;

        adicionarLinhaTotal(painel, gbc, "TOTAL:", "R$ 65,70", true, 3);

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        wrapper.setBackground(Color.WHITE);
        wrapper.add(painel);
        return wrapper;
    }

    private void adicionarLinhaTotal(JPanel painel, GridBagConstraints gbc,
                                     String label, String valor,
                                     boolean negrito, int linha) {
        Font fonte = negrito
                ? AppFonts.STATUS.deriveFont(Font.BOLD, 14f)
                : AppFonts.STATUS;

        gbc.gridy = linha;
        gbc.gridx = 0; gbc.anchor = GridBagConstraints.EAST;
        JLabel lbl = new JLabel(label);
        lbl.setFont(fonte);
        painel.add(lbl, gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.EAST;
        JLabel val = new JLabel(valor);
        val.setFont(fonte);
        painel.add(val, gbc);
    }

    private JPanel criarRodape() {
        JPanel rodape = new JPanel(new BorderLayout(0, 10));
        rodape.setBackground(Color.WHITE);

        JPanel enderecoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        enderecoPanel.setBackground(Color.WHITE);
        enderecoPanel.setBorder(titledBorder("Endereço de Entrega"));

        JLabel icone = new JLabel("📍");
        icone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        JLabel endLabel = new JLabel("Rua das Flores, 100 — Centro, Maringá — PR");
        endLabel.setFont(AppFonts.STATUS);

        enderecoPanel.add(icone);
        enderecoPanel.add(endLabel);

        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        botoesPanel.setBackground(Color.WHITE);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(110, 36));
        btnCancelar.setFont(AppFonts.STATUS);
        btnCancelar.setBackground(new Color(220, 220, 220));
        btnCancelar.setForeground(Color.DARK_GRAY);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(e -> tabbedPane.setSelectedIndex(0));

        JButton btnConfirmar = new JButton("Confirmar Pedido");
        btnConfirmar.setPreferredSize(new Dimension(165, 36));
        btnConfirmar.setFont(AppFonts.STATUS.deriveFont(Font.BOLD));
        btnConfirmar.setBackground(AppColors.AZUL_PRIMARIO);
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setFocusPainted(false);
        btnConfirmar.setBorderPainted(false);
        btnConfirmar.setOpaque(true);
        btnConfirmar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnConfirmar.addActionListener(e -> acaoFinalizarPedido());

        botoesPanel.add(btnCancelar);
        botoesPanel.add(btnConfirmar);

        rodape.add(enderecoPanel, BorderLayout.CENTER);
        rodape.add(botoesPanel,   BorderLayout.SOUTH);
        return rodape;
    }

    // ─────────────────────────────────────────────────────────────
    // ABA — MEUS PEDIDOS
    // ─────────────────────────────────────────────────────────────
    private JPanel criarPainelMeusPedidos() {
        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Contador de pedidos
        JLabel contador = new JLabel("1 pedido(s) no histórico");
        contador.setFont(AppFonts.STATUS);
        contador.setForeground(Color.DARK_GRAY);
        contador.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        // Tabela
        String[] colunas = {"ID", "Restaurante", "Data", "Status", "Total"};
        Object[][] dados = {
                {"A1B2C3D4", "Burguer House", "11/05/2026", "AGUARD. CONFIRM.", "R$ 65,70"},
        };

        DefaultTableModel model = new DefaultTableModel(dados, colunas) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable tabela = new JTable(model);
        tabela.setFont(AppFonts.STATUS);
        tabela.setRowHeight(30);
        tabela.setGridColor(new Color(220, 220, 220));
        tabela.setShowGrid(true);
        tabela.setSelectionBackground(new Color(220, 235, 255));

        JTableHeader th = tabela.getTableHeader();
        th.setFont(AppFonts.STATUS.deriveFont(Font.BOLD));
        th.setBackground(new Color(245, 245, 245));
        th.setForeground(Color.DARK_GRAY);
        th.setReorderingAllowed(false);

        // Larguras
        tabela.getColumnModel().getColumn(0).setPreferredWidth(90);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(180);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(160);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(80);

        // Renderer do badge de status
        tabela.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean sel, boolean foc, int row, int col) {
                JPanel cell = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 3));
                cell.setBackground(sel ? t.getSelectionBackground() : Color.WHITE);

                JLabel badge = new JLabel(String.valueOf(value));
                badge.setFont(AppFonts.STATUS.deriveFont(Font.BOLD, 11f));
                badge.setOpaque(true);
                badge.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));

                String status = String.valueOf(value);
                switch (status) {
                    case "AGUARD. CONFIRM." -> {
                        badge.setBackground(new Color(255, 220, 100));
                        badge.setForeground(new Color(120, 80, 0));
                    }
                    case "EM PREPARO" -> {
                        badge.setBackground(new Color(180, 220, 255));
                        badge.setForeground(new Color(0, 60, 140));
                    }
                    case "ENTREGUE" -> {
                        badge.setBackground(new Color(180, 240, 190));
                        badge.setForeground(new Color(0, 100, 30));
                    }
                    case "CANCELADO" -> {
                        badge.setBackground(new Color(255, 190, 190));
                        badge.setForeground(new Color(150, 0, 0));
                    }
                    default -> {
                        badge.setBackground(new Color(220, 220, 220));
                        badge.setForeground(Color.DARK_GRAY);
                    }
                }

                cell.add(badge);
                return cell;
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        painel.add(contador, BorderLayout.NORTH);
        painel.add(scroll,   BorderLayout.CENTER);
        return painel;
    }

    // ─────────────────────────────────────────────────────────────
    // ABA — PERFIL
    // ─────────────────────────────────────────────────────────────
    private JPanel criarPainelPerfil() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Sub-abas
        JTabbedPane subAbas = new JTabbedPane(JTabbedPane.TOP);
        subAbas.setFont(AppFonts.STATUS);
        subAbas.setBackground(Color.WHITE);

        subAbas.addTab("Dados",      criarSubAbaDados());
        subAbas.addTab("Endereço",   criarPainelVazio("Endereço"));
        subAbas.addTab("Favoritos",  criarPainelVazio("Favoritos"));
        subAbas.addTab("Senha",      criarPainelVazio("Senha"));

        subAbas.setSelectedIndex(0);
        painel.add(subAbas, BorderLayout.CENTER);
        return painel;
    }

    // ── Sub-aba Dados Pessoais ────────────────────────────────────
    private JPanel criarSubAbaDados() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(titledBorder("Dados Pessoais"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(6, 10, 6, 10);
        gbc.anchor  = GridBagConstraints.WEST;
        gbc.fill    = GridBagConstraints.HORIZONTAL;

        // Campos
        String[][] campos = {
                {"Nome:",     usuario.getNome()},
                {"E-mail:",   "joao@email.com"},
                {"CPF:",      "123.456.789-00"},
                {"Telefone:", "(44) 9 9876-5432"},
        };

        for (int i = 0; i < campos.length; i++) {
            // Label
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            gbc.fill = GridBagConstraints.NONE;
            JLabel lbl = new JLabel(campos[i][0]);
            lbl.setFont(AppFonts.STATUS);
            lbl.setPreferredSize(new Dimension(70, 24));
            form.add(lbl, gbc);

            // Campo de texto
            gbc.gridx = 1; gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            JTextField field = new JTextField(campos[i][1]);
            field.setFont(AppFonts.STATUS);
            field.setPreferredSize(new Dimension(500, 28));
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(180, 180, 180)),
                    BorderFactory.createEmptyBorder(2, 6, 2, 6)
            ));
            form.add(field, gbc);
        }

        // Botão Salvar
        gbc.gridx = 1; gbc.gridy = campos.length;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        gbc.insets = new Insets(12, 10, 6, 10);

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.setPreferredSize(new Dimension(80, 30));
        btnSalvar.setFont(AppFonts.STATUS.deriveFont(Font.BOLD));
        btnSalvar.setBackground(AppColors.AZUL_PRIMARIO);
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setFocusPainted(false);
        btnSalvar.setBorderPainted(false);
        btnSalvar.setOpaque(true);
        btnSalvar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSalvar.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Dados salvos com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE));
        form.add(btnSalvar, gbc);

        painel.add(form, BorderLayout.NORTH);
        return painel;
    }

    // ─────────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────────
    private JPanel criarPainelVazio(String nome) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        JLabel l = new JLabel(nome + " — em construção", SwingConstants.CENTER);
        l.setFont(AppFonts.STATUS);
        l.setForeground(Color.GRAY);
        p.add(l, BorderLayout.CENTER);
        return p;
    }

    // ─────────────────────────────────────────────────────────────
    // MÉTODOS DE AÇÃO
    // ─────────────────────────────────────────────────────────────
    private void acaoRemoverDoCarrinho(JTable tabelaCarrinho) {
        if (tabelaCarrinho.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um produto para remover.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int linha = tabelaCarrinho.getSelectedRow();
        String nomeProduto = (String) tabelaCarrinhoModel.getValueAt(linha, 0);

        // Encontrar o produto no carrinho
        for (CarrinhoManager.ItemCarrinho item : carrinho.getItens()) {
            if (item.getProduto().getNome().equals(nomeProduto)) {
                carrinho.removerItem(item.getProduto().getId());
                atualizarCarrinho();
                JOptionPane.showMessageDialog(this,
                        "Produto removido do carrinho!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
    }

    private void acaoEsvaziarCarrinho() {
        if (carrinho.estaVazio()) {
            JOptionPane.showMessageDialog(this,
                    "O carrinho já está vazio.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int resposta = JOptionPane.showConfirmDialog(this,
                "Deseja esvaziar todo o carrinho?",
                "Confirmação", JOptionPane.YES_NO_OPTION);

        if (resposta == JOptionPane.YES_OPTION) {
            carrinho.esvaziar();
            atualizarCarrinho();
            JOptionPane.showMessageDialog(this,
                    "Carrinho esvaziado com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void acaoFinalizarPedido() {
        if (carrinho.estaVazio()) {
            JOptionPane.showMessageDialog(this,
                    "Carrinho vazio! Adicione produtos antes de finalizar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int resposta = JOptionPane.showConfirmDialog(this,
                "Deseja confirmar o pedido?\n\n" +
                "Subtotal: " + String.format("R$ %.2f", carrinho.calcularSubtotal()) + "\n" +
                "Taxa: " + String.format("R$ %.2f", carrinho.getTaxaEntrega()) + "\n" +
                "Total: " + String.format("R$ %.2f", carrinho.calcularTotal()),
                "Confirmar Pedido", JOptionPane.YES_NO_OPTION);

        if (resposta == JOptionPane.YES_OPTION) {
            try {
                // Aqui seria feita a persistência do pedido no banco de dados
                JOptionPane.showMessageDialog(this,
                        "Pedido confirmado com sucesso!\nAgradecemos por sua compra.",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carrinho.esvaziar();
                atualizarCarrinho();
                tabbedPane.setSelectedIndex(0); // Volta para "Fazer Pedido"
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Erro ao finalizar pedido: " + e.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
        try {
            DefaultTableModel model = (DefaultTableModel) null;
            // Buscar o restaurante da tabela
            JScrollPane scroll = null;
            for (Component comp : getContentPane().getComponents()) {
                if (comp instanceof JPanel) {
                    for (Component subcomp : ((JPanel) comp).getComponents()) {
                        if (subcomp instanceof JScrollPane) {
                            scroll = (JScrollPane) subcomp;
                        }
                    }
                }
            }

            if (scroll == null) return;

            JTable tabela = (JTable) scroll.getViewport().getView();
            model = (DefaultTableModel) tabela.getModel();

            String nomeRestaurante = (String) model.getValueAt(linhaRestaurante, 0);

            List<Restaurante> restaurantes = restauranteService.buscarRestaurantesAtivos();
            Restaurante selecionado = restaurantes.stream()
                    .filter(r -> r.getNome().equals(nomeRestaurante))
                    .findFirst()
                    .orElse(null);

            if (selecionado != null) {
                carrinho.iniciar(usuario.getId(), selecionado.getId(), BigDecimal.ZERO);
                CardapioDialog dialog = new CardapioDialog(this, produtoService, selecionado, carrinho);
                dialog.setVisible(true);
                atualizarCarrinho();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao abrir cardápio: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void acaoFavoritar(String nomeRestaurante) {
        try {
            List<Restaurante> restaurantes = restauranteService.buscarRestaurantesAtivos();
            Restaurante resto = restaurantes.stream()
                    .filter(r -> r.getNome().equals(nomeRestaurante))
                    .findFirst()
                    .orElse(null);

            if (resto != null) {
                clienteService.favoritar((Cliente) usuario, resto);
                JOptionPane.showMessageDialog(this,
                        "Restaurante favoritado com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao favoritar: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarCarrinho() {
        // Atualizar tabela do carrinho
        tabelaCarrinhoModel.setRowCount(0);

        NumberFormat formato = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        for (CarrinhoManager.ItemCarrinho item : carrinho.getItens()) {
            tabelaCarrinhoModel.addRow(new Object[]{
                    item.getProduto().getNome(),
                    item.getQuantidade(),
                    formato.format(item.calcularSubtotal())
            });
        }

        // Atualizar label do carrinho
        int count = carrinho.contarItens();
        tabbedPane.setTitleAt(0, "Fazer Pedido (" + count + ")");

        // Atualizar totais
        if (lblSubtotal != null) {
            lblSubtotal.setText(formato.format(carrinho.calcularSubtotal()));
        }
        if (lblTotal != null) {
            lblTotal.setText(formato.format(carrinho.calcularTotal()));
        }
    }

    // ─────────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────────
    private TitledBorder titledBorder(String titulo) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                titulo,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                AppFonts.STATUS.deriveFont(Font.BOLD)
        );
    }
}