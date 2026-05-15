package com.pedidos.view.cliente;

import com.pedidos.application.service.PedidoService;
import com.pedidos.domain.entities.Cliente;
import com.pedidos.domain.entities.Pedido;
import com.pedidos.domain.enums.StatusPedido;
import com.pedidos.view.util.AppFonts;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Painel responsável pela aba "Meus Pedidos".
 * Exibe tabela com histórico de pedidos do cliente com status em badges.
 */
public class PainelMeusPedidos extends JPanel {

    private final Cliente cliente;
    private final PedidoService pedidoService;

    private final NumberFormat moedaBR = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private DefaultTableModel modelMeusPedidos;

    public PainelMeusPedidos(Cliente cliente, PedidoService pedidoService) {
        this.cliente = cliente;
        this.pedidoService = pedidoService;

        construir();
    }

    private void construir() {
        setLayout(new BorderLayout(0, 8));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        String[] colunas = {"ID", "Restaurante", "Data", "Status", "Total"};
        modelMeusPedidos = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable tabela = new JTable(modelMeusPedidos);
        tabela.setFont(AppFonts.LABEL);
        tabela.setRowHeight(30);
        tabela.setGridColor(new Color(220, 220, 220));
        tabela.setShowGrid(true);
        tabela.setSelectionBackground(new Color(220, 235, 255));

        configurarHeader(tabela);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(90);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(180);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(130);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(160);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(90);

        // Badge de status — cores espelhadas dos status
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
                switch (String.valueOf(value)) {
                    case "AGUARD. CONFIRM." -> { badge.setBackground(new Color(255, 220, 100)); badge.setForeground(new Color(120, 80,  0));   }
                    case "CONFIRMADO"       -> { badge.setBackground(new Color(180, 220, 255)); badge.setForeground(new Color(0,   60, 140));  }
                    case "EM PREPARO"       -> { badge.setBackground(new Color(255, 200, 120)); badge.setForeground(new Color(140, 60,  0));   }
                    case "SAIU P/ ENTREGA"  -> { badge.setBackground(new Color(200, 180, 255)); badge.setForeground(new Color(60,  0,  140));  }
                    case "ENTREGUE"         -> { badge.setBackground(new Color(180, 240, 190)); badge.setForeground(new Color(0,  100,  30));  }
                    case "CANCELADO"        -> { badge.setBackground(new Color(255, 190, 190)); badge.setForeground(new Color(150,  0,   0));  }
                    default                 -> { badge.setBackground(new Color(220, 220, 220)); badge.setForeground(Color.DARK_GRAY);          }
                }
                cell.add(badge);
                return cell;
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        add(scroll, BorderLayout.CENTER);

        carregarMeusPedidos();
    }

    /** Recarrega a tabela de Meus Pedidos. */
    public void carregarMeusPedidos() {
        modelMeusPedidos.setRowCount(0);
        for (Pedido p : pedidoService.listarPorCliente(cliente.getId())) {
            modelMeusPedidos.addRow(new Object[]{
                    p.getId(),
                    p.getRestaurante() != null ? p.getRestaurante().getNome() : "—",
                    p.getDataPedido() != null ? p.getDataPedido().format(FMT_DATA) : "—",
                    traduzirStatus(p.getStatus()),
                    moedaBR.format(p.getTotal() != null ? p.getTotal() : BigDecimal.ZERO)
            });
        }
    }

    /** Traduz o enum StatusPedido para o texto exibido no badge. */
    private String traduzirStatus(StatusPedido status) {
        if (status == null) return "—";
        return switch (status) {
            case AGUARDANDO_CONFIRMACAO -> "AGUARD. CONFIRM.";
            case CONFIRMADO             -> "CONFIRMADO";
            case EM_PREPARO             -> "EM PREPARO";
            case SAIU_PARA_ENTREGA      -> "SAIU P/ ENTREGA";
            case ENTREGUE               -> "ENTREGUE";
            case CANCELADO              -> "CANCELADO";
        };
    }

    // ── Helpers ────────────────────────────────────────────────────
    private void configurarHeader(JTable tabela) {
        JTableHeader th = tabela.getTableHeader();
        th.setFont(AppFonts.TITULO);
        th.setBackground(new Color(245, 245, 245));
        th.setForeground(Color.DARK_GRAY);
        th.setReorderingAllowed(false);
    }
}

