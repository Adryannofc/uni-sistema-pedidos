package com.pedidos.view.admin;

import com.pedidos.application.service.AdminService;
import com.pedidos.domain.entities.Restaurante;
import com.pedidos.domain.enums.StatusRestaurante;
import com.pedidos.view.util.AppColors;
import com.pedidos.view.util.AppFonts;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PainelRestaurantes extends JPanel {

    private final AdminService adminService;

    private DefaultTableModel model;
    private JTable tabela;
    private JButton btnAprovar;
    private JButton btnBloquear;
    private JButton btnRemover;
    private JTextField campoBusca;
    private JLabel labelContagem;
    private JLabel labelPendentes;

    private List<Restaurante> restaurantesCarregados = new ArrayList<>();

    public PainelRestaurantes(AdminService adminService) {
        super(new BorderLayout());
        this.adminService = adminService;
        construir();
    }

    // ─── build ────────────────────────────────────────────────────────────────

    private void construir() {
        add(criarCabecalho(),  BorderLayout.NORTH);
        add(criarTabela(),     BorderLayout.CENTER);
        add(criarStatusBar(),  BorderLayout.SOUTH);
        carregar(null);
    }

    private JPanel criarCabecalho() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, AppColors.CINZA_BORDA),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JLabel titulo = new JLabel("Restaurantes");
        titulo.setFont(AppFonts.TITULO);
        titulo.setForeground(AppColors.TEXTO_PRIMARIO);

        painel.add(titulo,       BorderLayout.WEST);
        painel.add(criarToolbar(), BorderLayout.SOUTH);

        return painel;
    }

    private JPanel criarToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        toolbar.setBackground(Color.WHITE);

        btnAprovar = botaoAcao("✓ Aprovar", new Color(212, 237, 218), new Color(21, 87, 36));
        btnBloquear = botaoAcao("✗ Bloquear", new Color(248, 215, 218), new Color(114, 28, 36));
        btnRemover  = botaoAcao("⊘ Remover",  new Color(255, 243, 205), new Color(133, 100, 4));

        btnAprovar.setEnabled(false);
        btnBloquear.setEnabled(false);
        btnRemover.setEnabled(false);

        campoBusca = new JTextField(18);
        campoBusca.setFont(AppFonts.CAMPO);
        campoBusca.putClientProperty("JTextField.placeholderText", "Buscar restaurante...");

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(AppFonts.BOTAO);

        toolbar.add(btnAprovar);
        toolbar.add(btnBloquear);
        toolbar.add(Box.createHorizontalStrut(6));
        toolbar.add(btnRemover);
        toolbar.add(Box.createHorizontalStrut(8));
        toolbar.add(campoBusca);
        toolbar.add(btnBuscar);

        btnAprovar.addActionListener(e -> acao_aprovar());
        btnBloquear.addActionListener(e -> acao_bloquear());
        btnRemover.addActionListener(e -> acao_remover());
        btnBuscar.addActionListener(e -> carregar(campoBusca.getText().trim()));
        campoBusca.addActionListener(e -> carregar(campoBusca.getText().trim()));

        return toolbar;
    }

    private JScrollPane criarTabela() {
        model = new DefaultTableModel(new String[]{"#", "Nome", "CNPJ", "Categoria", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabela = new JTable(model);
        tabela.setFont(AppFonts.LABEL);
        tabela.getTableHeader().setFont(AppFonts.TITULO);
        tabela.setRowHeight(28);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setFillsViewportHeight(true);
        tabela.setGridColor(AppColors.CINZA_BORDA);
        tabela.setShowGrid(true);

        tabela.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabela.getColumnModel().getColumn(0).setMaxWidth(50);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(180);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(130);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(110);
        tabela.getColumnModel().getColumn(4).setCellRenderer(new StatusBadgeRenderer());

        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            atualizarBotoes();
        });

        return new JScrollPane(tabela);
    }

    private JPanel criarStatusBar() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        barra.setBackground(AppColors.CINZA_STATUS);
        barra.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppColors.CINZA_BORDA));

        labelContagem = new JLabel("0 restaurante(s) cadastrado(s)");
        labelContagem.setFont(AppFonts.STATUS);
        labelContagem.setForeground(AppColors.TEXTO_SECUNDARIO);

        labelPendentes = new JLabel();
        labelPendentes.setFont(AppFonts.STATUS);
        labelPendentes.setForeground(new Color(200, 100, 0));

        barra.add(labelContagem);
        barra.add(labelPendentes);
        return barra;
    }

    // ─── data ─────────────────────────────────────────────────────────────────

    private void carregar(String filtro) {
        List<Restaurante> todos = adminService.listarRestaurantes();

        if (filtro != null && !filtro.isEmpty()) {
            String lower = filtro.toLowerCase();
            restaurantesCarregados = todos.stream()
                    .filter(r -> r.getNome().toLowerCase().contains(lower))
                    .collect(Collectors.toList());
        } else {
            restaurantesCarregados = new ArrayList<>(todos);
        }

        model.setRowCount(0);
        for (int i = 0; i < restaurantesCarregados.size(); i++) {
            Restaurante r = restaurantesCarregados.get(i);
            String categoria = r.getCategoriaGlobal() != null
                    ? r.getCategoriaGlobal().getNome() : "—";
            StatusRestaurante status = r.getStatus() != null
                    ? r.getStatus() : StatusRestaurante.PENDENTE;
            model.addRow(new Object[]{i + 1, r.getNome(), r.getCnpj(), categoria, status});
        }

        long pendentes = todos.stream()
                .filter(r -> r.getStatus() == StatusRestaurante.PENDENTE)
                .count();

        labelContagem.setText(todos.size() + " restaurante(s) cadastrado(s)");
        labelPendentes.setText(pendentes > 0 ? pendentes + " pendente(s) de aprovação" : "");

        atualizarBotoes();
    }

    private void atualizarBotoes() {
        int row = tabela.getSelectedRow();
        if (row < 0 || row >= restaurantesCarregados.size()) {
            btnAprovar.setEnabled(false);
            btnBloquear.setEnabled(false);
            btnRemover.setEnabled(false);
            return;
        }
        StatusRestaurante status = restaurantesCarregados.get(row).getStatus();
        btnAprovar.setEnabled(status != StatusRestaurante.ATIVO);
        btnBloquear.setEnabled(status != StatusRestaurante.BLOQUEADO);
        btnRemover.setEnabled(true);
    }

    // ─── ações ────────────────────────────────────────────────────────────────

    private void acao_aprovar() {
        int row = tabela.getSelectedRow();
        if (row < 0) return;
        Restaurante r = restaurantesCarregados.get(row);
        int ok = JOptionPane.showConfirmDialog(this,
                "Aprovar o restaurante \"" + r.getNome() + "\"?",
                "Confirmar aprovação", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;
        try {
            adminService.aprovarRestaurante(r.getId());
            carregar(campoBusca.getText().trim());
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void acao_bloquear() {
        int row = tabela.getSelectedRow();
        if (row < 0) return;
        Restaurante r = restaurantesCarregados.get(row);
        int ok = JOptionPane.showConfirmDialog(this,
                "Bloquear o restaurante \"" + r.getNome() + "\"?",
                "Confirmar bloqueio", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;
        try {
            adminService.bloquearRestaurante(r.getId());
            carregar(campoBusca.getText().trim());
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void acao_remover() {
        int row = tabela.getSelectedRow();
        if (row < 0) return;
        Restaurante r = restaurantesCarregados.get(row);
        int ok = JOptionPane.showConfirmDialog(this,
                "Remover permanentemente \"" + r.getNome() + "\"?\nEsta ação não pode ser desfeita.",
                "Confirmar remoção", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return;
        try {
            adminService.removerRestaurante(r.getId());
            carregar(campoBusca.getText().trim());
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ─── helpers ──────────────────────────────────────────────────────────────

    private static JButton botaoAcao(String texto, Color fundo, Color texto_cor) {
        JButton btn = new JButton(texto);
        btn.setFont(AppFonts.BOTAO);
        btn.setBackground(fundo);
        btn.setForeground(texto_cor);
        btn.setOpaque(true);
        btn.setBorderPainted(true);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ─── renderer ─────────────────────────────────────────────────────────────

    private static class StatusBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int col) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, col);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));

            if (!isSelected && value instanceof StatusRestaurante s) {
                lbl.setOpaque(true);
                Color[] cores = coresStatus(s);
                lbl.setBackground(cores[0]);
                lbl.setForeground(cores[1]);
                lbl.setText(s.name());
            }
            return lbl;
        }

        private static Color[] coresStatus(StatusRestaurante s) {
            return switch (s) {
                case ATIVO     -> new Color[]{new Color(212, 237, 218), new Color(21, 87, 36)};
                case BLOQUEADO -> new Color[]{new Color(248, 215, 218), new Color(114, 28, 36)};
                case PENDENTE  -> new Color[]{new Color(255, 243, 205), new Color(133, 100, 4)};
            };
        }
    }
}
