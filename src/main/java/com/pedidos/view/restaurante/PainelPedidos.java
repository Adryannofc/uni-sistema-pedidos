package com.pedidos.view.restaurante;

import com.pedidos.controller.PedidoController;
import com.pedidos.model.service.PedidoService;
import com.pedidos.model.entity.Pedido;
import com.pedidos.model.entity.Usuario;
import com.pedidos.model.enums.StatusPedido;
import com.pedidos.view.util.AppColors;
import com.pedidos.view.util.AppFonts;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class PainelPedidos extends JPanel {

    private static final NumberFormat    FMT_MOEDA = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Usuario usuario;
    private final PedidoController pedidoController;

    private DefaultTableModel modelPedidos;
    private JTable tabelaPedidos;
    private JLabel labelContagem;
    private JButton btnAvancar;
    private JButton btnCancelar;
    private List<Pedido> pedidosCarregados = new ArrayList<>();

    public PainelPedidos(Usuario usuario, PedidoController pedidoController) {
        super(new BorderLayout());
        this.usuario = usuario;
        this.pedidoController = pedidoController;
        construir();
    }

    // ─────────────────────────── build ───────────────────────────────────────

    private void construir() {
        add(criarToolbar(), BorderLayout.NORTH);
        add(criarTabela(),  BorderLayout.CENTER);

        carregarPedidos(null);
    }

    private JPanel criarToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 5));
        toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppColors.CINZA_BORDA));

        btnAvancar  = new JButton("Avançar Status");
        btnCancelar = new JButton("Cancelar Pedido");
        btnAvancar.setFont(AppFonts.BOTAO);
        btnCancelar.setFont(AppFonts.BOTAO);
        btnAvancar.setEnabled(false);
        btnCancelar.setEnabled(false);

        JLabel lblFiltro = new JLabel("Filtrar:");
        lblFiltro.setFont(AppFonts.LABEL);

        JComboBox<String> filtro = new JComboBox<>(new String[]{
            "Todos os status",
            "AGUARDANDO_CONFIRMACAO",
            "CONFIRMADO",
            "EM_PREPARO",
            "SAIU_PARA_ENTREGA",
            "ENTREGUE",
            "CANCELADO"
        });
        filtro.setFont(AppFonts.LABEL);
        filtro.setRenderer(new FiltroRenderer());

        labelContagem = new JLabel("0 pedido(s)");
        labelContagem.setFont(AppFonts.STATUS);
        labelContagem.setForeground(AppColors.TEXTO_SECUNDARIO);

        toolbar.add(btnAvancar);
        toolbar.add(btnCancelar);
        toolbar.add(Box.createHorizontalStrut(8));
        toolbar.add(lblFiltro);
        toolbar.add(filtro);
        toolbar.add(labelContagem);

        // actions
        filtro.addActionListener(e -> {
            String sel = (String) filtro.getSelectedItem();
            if ("Todos os status".equals(sel)) {
                carregarPedidos(null);
            } else {
                carregarPedidos(StatusPedido.valueOf(sel));
            }
        });

        btnAvancar.addActionListener(e -> {
            int row = tabelaPedidos.getSelectedRow();
            if (row < 0) return;
            Pedido p = pedidosCarregados.get(row);
            StatusPedido proximo = proximoStatus(p.getStatus());
            if (proximo == null) return;
            int ok = JOptionPane.showConfirmDialog(this,
                    "Avançar pedido de " + labelStatus(p.getStatus())
                    + " para " + labelStatus(proximo) + "?",
                    "Confirmar", JOptionPane.OK_CANCEL_OPTION);
            if (ok != JOptionPane.OK_OPTION) return;
            try {
                pedidoController.atualizarStatus(p.getId(), proximo);
                carregarPedidos(filtroAtual(filtro));
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> {
            int row = tabelaPedidos.getSelectedRow();
            if (row < 0) return;
            Pedido p = pedidosCarregados.get(row);
            int ok = JOptionPane.showConfirmDialog(this,
                    "Cancelar o pedido de " + p.getCliente().getNome() + "?",
                    "Confirmar cancelamento", JOptionPane.OK_CANCEL_OPTION);
            if (ok != JOptionPane.OK_OPTION) return;
            try {
                pedidoController.atualizarStatus(p.getId(), StatusPedido.CANCELADO);
                carregarPedidos(filtroAtual(filtro));
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        return toolbar;
    }

    private JScrollPane criarTabela() {
        modelPedidos = new DefaultTableModel(
                new String[]{"ID", "Cliente", "Itens", "Total", "Status", "Data"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        tabelaPedidos = new JTable(modelPedidos);
        tabelaPedidos.setFont(AppFonts.LABEL);
        tabelaPedidos.getTableHeader().setFont(AppFonts.TITULO);
        tabelaPedidos.setRowHeight(26);
        tabelaPedidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaPedidos.setFillsViewportHeight(true);

        tabelaPedidos.getColumnModel().getColumn(0).setPreferredWidth(100);
        tabelaPedidos.getColumnModel().getColumn(0).setMaxWidth(120);
        tabelaPedidos.getColumnModel().getColumn(1).setPreferredWidth(160);
        tabelaPedidos.getColumnModel().getColumn(2).setPreferredWidth(380);
        tabelaPedidos.getColumnModel().getColumn(3).setPreferredWidth(100);
        tabelaPedidos.getColumnModel().getColumn(4).setPreferredWidth(160);
        tabelaPedidos.getColumnModel().getColumn(5).setPreferredWidth(100);
        tabelaPedidos.getColumnModel().getColumn(4).setCellRenderer(new StatusBadgeRenderer());

        tabelaPedidos.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = tabelaPedidos.getSelectedRow();
            boolean temSel = row >= 0;
            if (temSel) {
                StatusPedido s = pedidosCarregados.get(row).getStatus();
                btnAvancar.setEnabled(proximoStatus(s) != null);
                btnCancelar.setEnabled(s != StatusPedido.ENTREGUE && s != StatusPedido.CANCELADO);
            } else {
                btnAvancar.setEnabled(false);
                btnCancelar.setEnabled(false);
            }
        });

        return new JScrollPane(tabelaPedidos);
    }

    // ─────────────────────────── data ────────────────────────────────────────

    private void carregarPedidos(StatusPedido filtro) {
        List<Pedido> todos = pedidoController.listarPorRestaurante(usuario.getId());
        pedidosCarregados = filtro == null ? todos
                : todos.stream().filter(p -> p.getStatus() == filtro).collect(Collectors.toList());

        modelPedidos.setRowCount(0);
        for (Pedido p : pedidosCarregados) {
            modelPedidos.addRow(new Object[]{
                idCurto(p.getId()),
                p.getCliente().getNome(),
                resumoItens(p),
                FMT_MOEDA.format(p.getTotal()),
                p.getStatus(),
                p.getDataPedido() != null ? p.getDataPedido().format(FMT_DATA) : "—"
            });
        }

        labelContagem.setText(pedidosCarregados.size() + " pedido(s)");
        btnAvancar.setEnabled(false);
        btnCancelar.setEnabled(false);
    }

    // ─────────────────────────── helpers ─────────────────────────────────────

    private static String idCurto(String id) {
        if (id == null) return "—";
        return id.length() > 8 ? id.substring(0, 8).toUpperCase() : id.toUpperCase();
    }

    private static String resumoItens(Pedido p) {
        return p.getItens().stream()
                .map(i -> i.getQuantidade() + "× " + i.getNomeProduto())
                .collect(Collectors.joining(", "));
    }

    private static StatusPedido proximoStatus(StatusPedido atual) {
        return switch (atual) {
            case AGUARDANDO_CONFIRMACAO -> StatusPedido.CONFIRMADO;
            case CONFIRMADO             -> StatusPedido.EM_PREPARO;
            case EM_PREPARO             -> StatusPedido.SAIU_PARA_ENTREGA;
            case SAIU_PARA_ENTREGA      -> StatusPedido.ENTREGUE;
            default                     -> null;
        };
    }

    private static String labelStatus(StatusPedido s) {
        return switch (s) {
            case AGUARDANDO_CONFIRMACAO -> "AGUARD. CONFIRM.";
            case CONFIRMADO             -> "CONFIRMADO";
            case EM_PREPARO             -> "EM PREPARO";
            case SAIU_PARA_ENTREGA      -> "SAIU P/ ENTREGA";
            case ENTREGUE               -> "ENTREGUE";
            case CANCELADO              -> "CANCELADO";
        };
    }

    private static StatusPedido filtroAtual(JComboBox<String> combo) {
        String val = (String) combo.getSelectedItem();
        if ("Todos os status".equals(val)) return null;
        try { return StatusPedido.valueOf(val); } catch (Exception e) { return null; }
    }

    // ─────────────────────────── renderers ───────────────────────────────────

    private static class StatusBadgeRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int col) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, col);

            StatusPedido s = value instanceof StatusPedido sp ? sp : null;
            lbl.setText(s != null ? labelStatus(s) : (value != null ? value.toString() : ""));
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));

            if (!isSelected && s != null) {
                lbl.setOpaque(true);
                Color[] cores = coresStatus(s);
                lbl.setBackground(cores[0]);
                lbl.setForeground(cores[1]);
            }
            lbl.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
            return lbl;
        }

        private static Color[] coresStatus(StatusPedido s) {
            return switch (s) {
                case AGUARDANDO_CONFIRMACAO -> new Color[]{new Color(255, 243, 205), new Color(133, 100, 4)};
                case CONFIRMADO             -> new Color[]{new Color(207, 226, 255), new Color(13, 71, 161)};
                case EM_PREPARO             -> new Color[]{new Color(212, 237, 218), new Color(21, 87, 36)};
                case SAIU_PARA_ENTREGA      -> new Color[]{new Color(255, 224, 178), new Color(130, 60, 0)};
                case ENTREGUE               -> new Color[]{new Color(168, 230, 207), new Color(10, 75, 45)};
                case CANCELADO              -> new Color[]{new Color(248, 215, 218), new Color(114, 28, 36)};
            };
        }
    }

    private static class FiltroRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof String s && !s.equals("Todos os status")) {
                try { setText(labelStatus(StatusPedido.valueOf(s))); } catch (Exception ignored) {}
            }
            return this;
        }
    }
}
