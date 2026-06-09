package com.pedidos.view.restaurante;

import com.pedidos.model.service.AreaEntregaService;
import com.pedidos.model.entity.AreaEntrega;
import com.pedidos.model.entity.Restaurante;
import com.pedidos.model.entity.Usuario;
import com.pedidos.view.util.AppColors;
import com.pedidos.view.util.AppFonts;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PainelAreaEntrega extends JPanel {

    private static final NumberFormat FMT_MOEDA =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    private final Restaurante restaurante;
    private final AreaEntregaService areaEntregaService;

    private DefaultTableModel modelo;
    private JTable tabela;
    private JLabel labelStatus;
    private List<AreaEntrega> areas;

    public PainelAreaEntrega(Usuario usuario, AreaEntregaService areaEntregaService) {
        super(new BorderLayout());
        this.restaurante = (Restaurante) usuario;
        this.areaEntregaService = areaEntregaService;
        construir();
        carregarAreas();
    }

    // ─────────────────────────── build UI ────────────────────────────────────

    private void construir() {
        add(criarToolbar(),  BorderLayout.NORTH);
        add(criarTabela(),   BorderLayout.CENTER);
        add(criarRodape(),   BorderLayout.SOUTH);
    }

    private JPanel criarToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        JButton btnNova   = criarBotaoPrimario("+ Nova Área");
        JButton btnEditar = new JButton("Editar");
        JButton btnRemover = new JButton("Remover");

        btnEditar.setFont(AppFonts.BOTAO);
        btnRemover.setFont(AppFonts.BOTAO);

        btnNova.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> editarSelecionada());
        btnRemover.addActionListener(e -> removerSelecionada());

        toolbar.add(btnNova);
        toolbar.add(btnEditar);
        toolbar.add(btnRemover);
        return toolbar;
    }

    private JScrollPane criarTabela() {
        modelo = new DefaultTableModel(
                new String[]{"#", "Bairro", "Taxa (R$)", "Tempo Estimado"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        tabela = new JTable(modelo);
        tabela.setFont(AppFonts.LABEL);
        tabela.getTableHeader().setFont(AppFonts.TITULO);
        tabela.setRowHeight(24);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setFillsViewportHeight(true);

        tabela.getColumnModel().getColumn(0).setPreferredWidth(60);
        tabela.getColumnModel().getColumn(0).setMaxWidth(80);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(400);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(200);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(200);

        return new JScrollPane(tabela);
    }

    private JPanel criarRodape() {
        labelStatus = new JLabel("0 área(s) de entrega cadastrada(s)");
        labelStatus.setFont(AppFonts.STATUS);
        labelStatus.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rodape.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        rodape.add(labelStatus);
        return rodape;
    }

    // ─────────────────────────── data ────────────────────────────────────────

    private void carregarAreas() {
        areas = areaEntregaService.listarAreasPorRestaurante(restaurante.getId());
        modelo.setRowCount(0);
        for (int i = 0; i < areas.size(); i++) {
            AreaEntrega a = areas.get(i);
            modelo.addRow(new Object[]{
                i + 1,
                a.getBairro(),
                FMT_MOEDA.format(a.getTaxaEntrega()),
                a.getPrevisaoMinutos() + " min"
            });
        }
        labelStatus.setText(areas.size() + " área(s) de entrega cadastrada(s)");
    }

    // ─────────────────────────── actions ─────────────────────────────────────

    private void editarSelecionada() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma área para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        abrirFormulario(areas.get(row));
    }

    private void removerSelecionada() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma área para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        AreaEntrega area = areas.get(row);
        int opcao = JOptionPane.showConfirmDialog(this,
                "Remover área \"" + area.getBairro() + "\"?",
                "Confirmar remoção", JOptionPane.OK_CANCEL_OPTION);
        if (opcao != JOptionPane.OK_OPTION) return;

        try {
            areaEntregaService.deletarAreaEntrega(area.getId());
            carregarAreas();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao remover área:\n" + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirFormulario(AreaEntrega existente) {
        boolean editando = existente != null;
        String titulo = editando ? "Editar Área de Entrega" : "Nova Área de Entrega";

        JTextField campoBairro   = new JTextField(20);
        JTextField campoTaxa     = new JTextField(10);
        JTextField campoPrevisao = new JTextField(10);
        JTextField campoDistancia = new JTextField(10);

        campoBairro.setFont(AppFonts.CAMPO);
        campoTaxa.setFont(AppFonts.CAMPO);
        campoPrevisao.setFont(AppFonts.CAMPO);
        campoDistancia.setFont(AppFonts.CAMPO);

        if (editando) {
            campoBairro.setText(existente.getBairro());
            campoTaxa.setText(existente.getTaxaEntrega().toPlainString());
            campoPrevisao.setText(String.valueOf(existente.getPrevisaoMinutos()));
            campoDistancia.setText(existente.getDistanciaKm() != null
                    ? existente.getDistanciaKm().toPlainString() : "");
        }

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        adicionarCampoForm(form, gbc, 0, "Bairro:",              campoBairro);
        adicionarCampoForm(form, gbc, 1, "Taxa de entrega (R$):", campoTaxa);
        adicionarCampoForm(form, gbc, 2, "Previsão (minutos):",   campoPrevisao);
        adicionarCampoForm(form, gbc, 3, "Distância máx. (km):",  campoDistancia);

        int resultado = JOptionPane.showConfirmDialog(this, form, titulo,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (resultado != JOptionPane.OK_OPTION) return;

        String bairro = campoBairro.getText().trim();
        BigDecimal taxa, distancia;
        int previsao;

        try {
            taxa = new BigDecimal(campoTaxa.getText().trim().replace(",", "."));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Taxa inválida. Use números (ex: 5.50).",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            previsao = Integer.parseInt(campoPrevisao.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Previsão inválida. Use um número inteiro (ex: 30).",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            distancia = new BigDecimal(campoDistancia.getText().trim().replace(",", "."));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Distância inválida. Use números (ex: 5.0).",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (editando) {
                areaEntregaService.editarAreaEntrega(existente.getId(), bairro, distancia, taxa, previsao);
            } else {
                areaEntregaService.criarAreaEntrega(restaurante, bairro, distancia, taxa, previsao);
            }
            carregarAreas();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar área:\n" + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ─────────────────────────── helpers ─────────────────────────────────────

    private JButton criarBotaoPrimario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(AppFonts.BOTAO);
        btn.setBackground(AppColors.AZUL_PRIMARIO);
        btn.setForeground(AppColors.TEXTO_BRANCO);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        return btn;
    }

    private void adicionarCampoForm(JPanel form, GridBagConstraints gbc,
                                     int row, String rotulo, JTextField campo) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel lbl = new JLabel(rotulo);
        lbl.setFont(AppFonts.LABEL);
        form.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(campo, gbc);
        gbc.fill = GridBagConstraints.NONE;
    }
}
