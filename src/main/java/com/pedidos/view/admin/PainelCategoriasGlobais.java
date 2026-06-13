package com.pedidos.view.admin;

import com.pedidos.controller.CategoriaController;
import com.pedidos.model.entity.CategoriaGlobal;
import com.pedidos.view.util.AppColors;
import com.pedidos.view.util.AppFonts;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PainelCategoriasGlobais extends JPanel {

    private final CategoriaController categoriaController;

    private DefaultTableModel model;
    private JTable tabela;
    private JButton btnNova;
    private JButton btnEditar;
    private JButton btnRemover;
    private JLabel labelContagem;

    private List<CategoriaGlobal> categoriasCarregadas = new ArrayList<>();

    public PainelCategoriasGlobais(CategoriaController categoriaController) {
        super(new BorderLayout());
        this.categoriaController = categoriaController;
        construir();
    }

    // ─── build ────────────────────────────────────────────────────────────────

    private void construir() {
        add(criarCabecalho(), BorderLayout.NORTH);
        add(criarTabela(),    BorderLayout.CENTER);
        add(criarStatusBar(), BorderLayout.SOUTH);
        carregar();
    }

    private JPanel criarCabecalho() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, AppColors.CINZA_BORDA),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JLabel titulo = new JLabel("Categorias Globais");
        titulo.setFont(AppFonts.TITULO);
        titulo.setForeground(AppColors.TEXTO_PRIMARIO);

        painel.add(titulo,         BorderLayout.WEST);
        painel.add(criarToolbar(), BorderLayout.SOUTH);
        return painel;
    }

    private JPanel criarToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        toolbar.setBackground(Color.WHITE);

        btnNova    = new JButton(" Nova Categoria");
        btnEditar  = new JButton(" Editar");
        btnRemover = new JButton(" Remover");
        btnNova.setFont(AppFonts.BOTAO);
        btnNova.setIcon(carregarIcone("/img/criar.png"));
        btnEditar.setFont(AppFonts.BOTAO);
        btnEditar.setIcon(carregarIcone("/img/editar.png"));
        btnRemover.setFont(AppFonts.BOTAO);
        btnRemover.setIcon(carregarIcone("/img/remover.png"));
        btnEditar.setEnabled(false);
        btnRemover.setEnabled(false);

        toolbar.add(btnNova);
        toolbar.add(btnEditar);
        toolbar.add(btnRemover);

        btnNova.addActionListener(e    -> acao_nova());
        btnEditar.addActionListener(e  -> acao_editar());
        btnRemover.addActionListener(e -> acao_remover());

        return toolbar;
    }

    private JScrollPane criarTabela() {
        model = new DefaultTableModel(new String[]{"#", "Nome", "Descrição"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabela = new JTable(model);
        tabela.setFont(AppFonts.LABEL);
        tabela.getTableHeader().setFont(AppFonts.TITULO);
        tabela.setRowHeight(28);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setFillsViewportHeight(true);
        tabela.setGridColor(AppColors.CINZA_BORDA);

        tabela.getColumnModel().getColumn(0).setMaxWidth(50);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(180);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(400);

        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            boolean sel = tabela.getSelectedRow() >= 0;
            btnEditar.setEnabled(sel);
            btnRemover.setEnabled(sel);
        });

        return new JScrollPane(tabela);
    }

    private JPanel criarStatusBar() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        barra.setBackground(AppColors.CINZA_STATUS);
        barra.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppColors.CINZA_BORDA));

        labelContagem = new JLabel("0 categoria(s)");
        labelContagem.setFont(AppFonts.STATUS);
        labelContagem.setForeground(AppColors.TEXTO_SECUNDARIO);

        barra.add(labelContagem);
        return barra;
    }

    // ─── data ─────────────────────────────────────────────────────────────────

    private void carregar() {
        categoriasCarregadas = new ArrayList<>(categoriaController.listarCategoriasGlobais());
        model.setRowCount(0);
        for (int i = 0; i < categoriasCarregadas.size(); i++) {
            CategoriaGlobal c = categoriasCarregadas.get(i);
            model.addRow(new Object[]{i + 1, c.getNome(), c.getDescricao()});
        }
        labelContagem.setText(categoriasCarregadas.size() + " categoria(s)");
        btnEditar.setEnabled(false);
        btnRemover.setEnabled(false);
    }

    // ─── ações ────────────────────────────────────────────────────────────────

    private void acao_nova() {
        JTextField campoNome = new JTextField(24);
        JTextField campoDesc = new JTextField(24);
        campoNome.setFont(AppFonts.CAMPO);
        campoDesc.setFont(AppFonts.CAMPO);

        JPanel form = new JPanel(new GridLayout(4, 1, 4, 4));
        form.add(labelForm("Nome:"));
        form.add(campoNome);
        form.add(labelForm("Descrição:"));
        form.add(campoDesc);

        int ok = JOptionPane.showConfirmDialog(this, form,
                "Nova Categoria Global", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return;

        String nome = campoNome.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O nome é obrigatório.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            categoriaController.criarCategoriaGlobal(nome, campoDesc.getText().trim());
            carregar();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void acao_editar() {
        int row = tabela.getSelectedRow();
        if (row < 0) return;
        CategoriaGlobal cat = categoriasCarregadas.get(row);

        JTextField campoNome = new JTextField(cat.getNome(), 24);
        JTextField campoDesc = new JTextField(cat.getDescricao() != null ? cat.getDescricao() : "", 24);
        campoNome.setFont(AppFonts.CAMPO);
        campoDesc.setFont(AppFonts.CAMPO);

        JPanel form = new JPanel(new GridLayout(4, 1, 4, 4));
        form.add(labelForm("Nome:"));
        form.add(campoNome);
        form.add(labelForm("Descrição:"));
        form.add(campoDesc);

        int ok = JOptionPane.showConfirmDialog(this, form,
                "Editar Categoria", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return;

        String novoNome = campoNome.getText().trim();
        if (novoNome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O nome é obrigatório.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            categoriaController.editarCategoriaGlobal(cat.getId(), novoNome, campoDesc.getText().trim());
            carregar();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void acao_remover() {
        int row = tabela.getSelectedRow();
        if (row < 0) return;
        CategoriaGlobal cat = categoriasCarregadas.get(row);
        int ok = JOptionPane.showConfirmDialog(this,
                "Remover a categoria \"" + cat.getNome() + "\"?\nRestaurantes vinculados perderão a categoria.",
                "Confirmar remoção", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return;
        try {
            categoriaController.removerCategoriaGlobal(cat.getId());
            carregar();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private ImageIcon carregarIcone(String caminho) {
        return new ImageIcon(
                getClass().getResource(caminho)
        );
    }

    // ─── helpers ──────────────────────────────────────────────────────────────

    private static JLabel labelForm(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(AppFonts.LABEL);
        return l;
    }
}
