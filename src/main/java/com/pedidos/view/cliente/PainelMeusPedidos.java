package com.pedidos.view.cliente;

import com.pedidos.controller.PedidoController;
import com.pedidos.model.entity.Cliente;
import com.pedidos.model.entity.ItemPedido;
import com.pedidos.model.entity.Pedido;
import com.pedidos.model.enums.StatusPedido;
import com.pedidos.view.util.AppColors;
import com.pedidos.view.util.AppFonts;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class PainelMeusPedidos extends JPanel {

    private final Cliente cliente;
    private final PedidoController pedidoController;

    private final NumberFormat moedaBR = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // CardLayout
    private CardLayout cardLayout;
    private static final String CARD_LISTA      = "lista";
    private static final String CARD_ACOMPANHAR = "acompanhar";

    // Lista
    private JLabel lblContagem;
    private DefaultTableModel modelPedidos;
    private JTable tabelaPedidos;
    private List<Pedido> pedidosCarregados;

    // Detalhes
    private JPanel painelDetalhes;
    private JLabel lblDetTitulo;
    private JLabel lblDetStatus;
    private JLabel lblDetData;
    private JLabel lblDetTotal;
    private DefaultTableModel modelDetItens;
    private JButton btnAcompanhar;

    // Acompanhamento
    private PainelAcompanharPedido painelAcompanhar;

    public PainelMeusPedidos(Cliente cliente, PedidoController pedidoController) {
        this.cliente = cliente;
        this.pedidoController = pedidoController;
        construir();
    }

    private void construir() {
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        setBackground(Color.WHITE);

        add(criarCardLista(),      CARD_LISTA);
        add(criarCardAcompanhar(), CARD_ACOMPANHAR);

        carregarMeusPedidos();
    }

    // ── Card: lista ────────────────────────────────────────────────

    private JPanel criarCardLista() {
        JPanel card = new JPanel(new BorderLayout(0, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 0, 16));

        // Topo: contagem
        lblContagem = new JLabel(" ");
        lblContagem.setFont(AppFonts.STATUS);
        lblContagem.setForeground(Color.GRAY);
        lblContagem.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        // Tabela de pedidos
        String[] cols = {"ID", "Restaurante", "Data", "Status", "Total"};
        modelPedidos = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabelaPedidos = new JTable(modelPedidos);
        tabelaPedidos.setFont(AppFonts.LABEL);
        tabelaPedidos.setRowHeight(30);
        tabelaPedidos.setGridColor(new Color(220, 220, 220));
        tabelaPedidos.setShowGrid(true);
        tabelaPedidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaPedidos.setSelectionBackground(new Color(220, 235, 255));

        configurarHeader(tabelaPedidos);
        tabelaPedidos.getColumnModel().getColumn(0).setPreferredWidth(90);
        tabelaPedidos.getColumnModel().getColumn(1).setPreferredWidth(180);
        tabelaPedidos.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabelaPedidos.getColumnModel().getColumn(3).setPreferredWidth(160);
        tabelaPedidos.getColumnModel().getColumn(4).setPreferredWidth(90);
        tabelaPedidos.getColumnModel().getColumn(3).setCellRenderer(badgeRenderer());

        // Seleção → atualiza detalhes
        tabelaPedidos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) atualizarDetalhes();
        });

        // Duplo clique → acompanhar
        tabelaPedidos.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) abrirAcompanhar();
            }
        });

        JScrollPane scroll = new JScrollPane(tabelaPedidos);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        // Painel central (contagem + tabela)
        JPanel centro = new JPanel(new BorderLayout(0, 4));
        centro.setBackground(Color.WHITE);
        centro.add(lblContagem, BorderLayout.NORTH);
        centro.add(scroll, BorderLayout.CENTER);

        card.add(centro, BorderLayout.CENTER);
        card.add(criarPainelDetalhes(), BorderLayout.SOUTH);
        return card;
    }

    private JPanel criarPainelDetalhes() {
        painelDetalhes = new JPanel(new BorderLayout(0, 6));
        painelDetalhes.setBackground(Color.WHITE);
        painelDetalhes.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 0, 8, 0)));
        painelDetalhes.setVisible(false);

        // Título do bloco de detalhes (TitledBorder dinâmico)
        lblDetTitulo = new JLabel("Detalhes");
        lblDetTitulo.setFont(AppFonts.TITULO);
        lblDetTitulo.setForeground(Color.DARK_GRAY);
        lblDetTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));

        // Linha de info rápida
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        infoPanel.setBackground(Color.WHITE);

        lblDetStatus = new JLabel("Status: —");
        lblDetStatus.setFont(AppFonts.LABEL.deriveFont(Font.BOLD));
        lblDetData   = new JLabel("Data: —");
        lblDetData.setFont(AppFonts.LABEL);
        lblDetTotal  = new JLabel("Total: —");
        lblDetTotal.setFont(AppFonts.LABEL);

        infoPanel.add(lblDetStatus);
        infoPanel.add(lblDetData);
        infoPanel.add(lblDetTotal);

        // Mini tabela de itens
        modelDetItens = new DefaultTableModel(new String[]{"Produto", "Qtd", "Preço", "Subtotal"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabelaItens = new JTable(modelDetItens);
        tabelaItens.setFont(AppFonts.STATUS);
        tabelaItens.setRowHeight(22);
        tabelaItens.setGridColor(new Color(230, 230, 230));
        tabelaItens.setShowGrid(true);
        tabelaItens.setBackground(new Color(250, 250, 250));
        tabelaItens.getTableHeader().setFont(AppFonts.STATUS.deriveFont(Font.BOLD));
        tabelaItens.getTableHeader().setBackground(new Color(245, 245, 245));
        tabelaItens.getTableHeader().setReorderingAllowed(false);
        tabelaItens.getColumnModel().getColumn(0).setPreferredWidth(200);

        JScrollPane scrollItens = new JScrollPane(tabelaItens);
        scrollItens.setPreferredSize(new Dimension(0, 80));
        scrollItens.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));

        // Botão acompanhar
        btnAcompanhar = new JButton("Acompanhar Pedido →");
        btnAcompanhar.setFont(AppFonts.BOTAO);
        btnAcompanhar.setBackground(AppColors.AZUL_PRIMARIO);
        btnAcompanhar.setForeground(Color.WHITE);
        btnAcompanhar.setFocusPainted(false);
        btnAcompanhar.setBorderPainted(false);
        btnAcompanhar.setOpaque(true);
        btnAcompanhar.setPreferredSize(new Dimension(180, 30));
        btnAcompanhar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAcompanhar.addActionListener(e -> abrirAcompanhar());

        JPanel topo = new JPanel(new BorderLayout(0, 4));
        topo.setBackground(Color.WHITE);
        topo.add(lblDetTitulo, BorderLayout.NORTH);
        topo.add(infoPanel, BorderLayout.CENTER);

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        direita.setBackground(Color.WHITE);
        direita.add(btnAcompanhar);

        JPanel corpo = new JPanel(new BorderLayout(8, 0));
        corpo.setBackground(Color.WHITE);
        corpo.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        corpo.add(scrollItens, BorderLayout.CENTER);
        corpo.add(direita, BorderLayout.EAST);

        painelDetalhes.add(topo, BorderLayout.NORTH);
        painelDetalhes.add(corpo, BorderLayout.CENTER);
        return painelDetalhes;
    }

    // ── Card: acompanhar ───────────────────────────────────────────

    private JPanel criarCardAcompanhar() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);

        painelAcompanhar = new PainelAcompanharPedido(pedidoController, () -> {
            carregarMeusPedidos();
            cardLayout.show(this, CARD_LISTA);
        });

        card.add(painelAcompanhar, BorderLayout.CENTER);
        return card;
    }

    // ── Lógica ─────────────────────────────────────────────────────

    public void carregarMeusPedidos() {
        pedidosCarregados = pedidoController.listarPorCliente(cliente.getId());
        modelPedidos.setRowCount(0);
        painelDetalhes.setVisible(false);

        for (Pedido p : pedidosCarregados) {
            modelPedidos.addRow(new Object[]{
                    p.getId().substring(0, 8).toUpperCase(),
                    p.getRestaurante() != null ? p.getRestaurante().getNome() : "—",
                    p.getDataPedido() != null ? p.getDataPedido().format(FMT_DATA) : "—",
                    traduzirStatus(p.getStatus()),
                    moedaBR.format(p.getTotal() != null ? p.getTotal() : BigDecimal.ZERO)
            });
        }

        int total = pedidosCarregados.size();
        lblContagem.setText(total + " pedido(s) no histórico — duplo clique para acompanhar");
    }

    private void atualizarDetalhes() {
        int idx = tabelaPedidos.getSelectedRow();
        if (idx < 0 || pedidosCarregados == null || idx >= pedidosCarregados.size()) {
            painelDetalhes.setVisible(false);
            return;
        }

        Pedido p = pedidosCarregados.get(idx);
        String idCurto  = p.getId().substring(0, 8).toUpperCase();
        String nomeRest = p.getRestaurante() != null ? p.getRestaurante().getNome() : "—";

        lblDetTitulo.setText("Detalhes — Pedido " + idCurto + " · " + nomeRest);
        lblDetStatus.setText("Status: " + traduzirStatus(p.getStatus()));
        lblDetData.setText("Data: " + (p.getDataPedido() != null ? p.getDataPedido().format(FMT_DATA) : "—"));
        lblDetTotal.setText("Total: " + moedaBR.format(p.getTotal() != null ? p.getTotal() : BigDecimal.ZERO));

        modelDetItens.setRowCount(0);
        for (ItemPedido item : p.getItens()) {
            BigDecimal sub = item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade()));
            modelDetItens.addRow(new Object[]{
                    item.getNomeProduto(),
                    item.getQuantidade(),
                    moedaBR.format(item.getPrecoUnitario()),
                    moedaBR.format(sub)
            });
        }

        painelDetalhes.setVisible(true);
        revalidate();
        repaint();
    }

    private void abrirAcompanhar() {
        int idx = tabelaPedidos.getSelectedRow();
        if (idx < 0 || pedidosCarregados == null || idx >= pedidosCarregados.size()) return;

        Pedido p = pedidosCarregados.get(idx);
        painelAcompanhar.carregarPedido(p);
        cardLayout.show(this, CARD_ACOMPANHAR);
    }

    // ── Helpers ────────────────────────────────────────────────────

    private String traduzirStatus(StatusPedido s) {
        if (s == null) return "—";
        return switch (s) {
            case AGUARDANDO_CONFIRMACAO -> "AGUARD. CONFIRM.";
            case CONFIRMADO             -> "CONFIRMADO";
            case EM_PREPARO             -> "EM PREPARO";
            case SAIU_PARA_ENTREGA      -> "SAIU P/ ENTREGA";
            case ENTREGUE               -> "ENTREGUE";
            case CANCELADO              -> "CANCELADO";
        };
    }

    private void configurarHeader(JTable t) {
        JTableHeader th = t.getTableHeader();
        th.setFont(AppFonts.TITULO);
        th.setBackground(new Color(245, 245, 245));
        th.setForeground(Color.DARK_GRAY);
        th.setReorderingAllowed(false);
    }

    private DefaultTableCellRenderer badgeRenderer() {
        return new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean sel, boolean foc, int row, int col) {
                JPanel cell = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 3));
                cell.setBackground(sel ? t.getSelectionBackground() : Color.WHITE);
                JLabel badge = new JLabel(String.valueOf(value));
                badge.setFont(AppFonts.STATUS.deriveFont(Font.BOLD, 11f));
                badge.setOpaque(true);
                badge.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
                switch (String.valueOf(value)) {
                    case "AGUARD. CONFIRM." -> { badge.setBackground(new Color(255,220,100)); badge.setForeground(new Color(120,80,0)); }
                    case "CONFIRMADO"       -> { badge.setBackground(new Color(180,220,255)); badge.setForeground(new Color(0,60,140)); }
                    case "EM PREPARO"       -> { badge.setBackground(new Color(255,200,120)); badge.setForeground(new Color(140,60,0)); }
                    case "SAIU P/ ENTREGA"  -> { badge.setBackground(new Color(200,180,255)); badge.setForeground(new Color(60,0,140)); }
                    case "ENTREGUE"         -> { badge.setBackground(new Color(180,240,190)); badge.setForeground(new Color(0,100,30)); }
                    case "CANCELADO"        -> { badge.setBackground(new Color(255,190,190)); badge.setForeground(new Color(150,0,0)); }
                    default                 -> { badge.setBackground(new Color(220,220,220)); badge.setForeground(Color.DARK_GRAY); }
                }
                cell.add(badge);
                return cell;
            }
        };
    }
}
