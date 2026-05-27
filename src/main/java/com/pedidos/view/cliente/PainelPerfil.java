package com.pedidos.view.cliente;

import com.pedidos.model.service.ClienteService;
import com.pedidos.model.entity.Cliente;
import com.pedidos.model.entity.Endereco;
import com.pedidos.model.entity.Restaurante;
import com.pedidos.model.entity.Usuario;
import com.pedidos.view.util.AppColors;
import com.pedidos.view.util.AppFonts;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;

/**
 * Painel responsável pela aba "Perfil".
 * Contém sub-abas: Dados, Endereço, Favoritos, Senha.
 */
public class PainelPerfil extends JPanel {

    private final Usuario usuario;
    private final Cliente cliente;
    private final ClienteService clienteService;
    private final Runnable aoAtualizarEndereco;

    public PainelPerfil(Usuario usuario, Cliente cliente, ClienteService clienteService, Runnable aoAtualizarEndereco) {
        this.usuario = usuario;
        this.cliente = cliente;
        this.clienteService = clienteService;
        this.aoAtualizarEndereco = aoAtualizarEndereco;

        construir();
    }

    private void construir() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JTabbedPane subAbas = new JTabbedPane(JTabbedPane.TOP);
        subAbas.setFont(AppFonts.MENU);
        subAbas.setBackground(Color.WHITE);

        subAbas.addTab("Dados", criarSubAbaDados());
        subAbas.addTab("Endereço", criarSubAbaEndereco());
        subAbas.addTab("Favoritos", criarSubAbaFavoritos());
        subAbas.addTab("Senha", criarSubAbaSenha());

        subAbas.setSelectedIndex(0);
        add(subAbas, BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────────────────────
    // SUB-ABA — DADOS
    // ─────────────────────────────────────────────────────────────
    private JPanel criarSubAbaDados() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(titledBorder("Dados Pessoais"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.anchor = GridBagConstraints.WEST;

        String[] labels = {"Nome:", "E-mail:", "CPF:", "Telefone:"};
        String[] valores = {
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getCpf() != null ? cliente.getCpf() : "",
                cliente.getTelefone() != null ? cliente.getTelefone() : ""
        };
        JTextField[] fields = new JTextField[labels.length];

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(AppFonts.LABEL);
            lbl.setPreferredSize(new Dimension(75, 24));
            form.add(lbl, gbc);

            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
            fields[i] = criarCampoTexto(valores[i]);
            form.add(fields[i], gbc);
        }

        gbc.gridx = 1; gbc.gridy = labels.length;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(12, 10, 6, 10);

        JButton btnSalvar = criarBotaoPrimario("Salvar", 80, 30);
        btnSalvar.addActionListener(e -> {
            try {
                clienteService.editarNome(cliente, fields[0].getText().trim());
                clienteService.editarEmail(cliente, fields[1].getText().trim());
                clienteService.editarCpf(cliente, fields[2].getText().trim());
                clienteService.editarTelefone(cliente, fields[3].getText().trim());
                JOptionPane.showMessageDialog(this, "Dados salvos com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Erro ao salvar dados:\n" + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        form.add(btnSalvar, gbc);

        painel.add(form, BorderLayout.NORTH);
        return painel;
    }

    // ─────────────────────────────────────────────────────────────
    // SUB-ABA — ENDEREÇO
    // ─────────────────────────────────────────────────────────────
    private JPanel criarSubAbaEndereco() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(titledBorder("Endereço de Entrega"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Pré-preenche com endereço padrão existente
        Optional<Endereco> endAtual = cliente.getEnderecoPadrao();

        String[] labels = {"Rua / Logradouro:", "Número:", "Bairro:", "Cidade:", "Estado (UF):", "CEP:"};
        String[] valores = {
                endAtual.map(Endereco::getRua).orElse(""),
                endAtual.map(Endereco::getNumero).orElse(""),
                endAtual.map(Endereco::getBairro).orElse(""),
                endAtual.map(Endereco::getCidade).orElse(""),
                endAtual.map(Endereco::getEstado).orElse(""),
                endAtual.map(Endereco::getCep).orElse("")
        };
        JTextField[] fields = new JTextField[labels.length];

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(AppFonts.LABEL);
            lbl.setPreferredSize(new Dimension(140, 24));
            form.add(lbl, gbc);

            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
            fields[i] = criarCampoTexto(valores[i]);
            form.add(fields[i], gbc);
        }

        gbc.gridx = 1; gbc.gridy = labels.length;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(14, 10, 6, 10);

        JButton btnSalvar = criarBotaoPrimario("Salvar Endereço", 150, 30);
        btnSalvar.addActionListener(e -> {
            try {
                clienteService.salvarEndereco(
                        cliente,
                        fields[0].getText().trim(), // rua
                        fields[1].getText().trim(), // numero
                        fields[2].getText().trim(), // bairro
                        fields[3].getText().trim(), // cidade
                        fields[4].getText().trim(), // estado
                        fields[5].getText().trim()  // cep
                );
                JOptionPane.showMessageDialog(this, "Endereço salvo com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                if (aoAtualizarEndereco != null) aoAtualizarEndereco.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Erro ao salvar endereço:\n" + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        form.add(btnSalvar, gbc);

        painel.add(form, BorderLayout.NORTH);
        return painel;
    }

    // ─────────────────────────────────────────────────────────────
    // SUB-ABA — FAVORITOS
    // ─────────────────────────────────────────────────────────────
    private JPanel criarSubAbaFavoritos() {
        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel titulo = new JLabel("Restaurantes favoritos");
        titulo.setFont(AppFonts.TITULO);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        String[] colunas = {"Restaurante", "Categoria", "Status", "★ Remover"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        // Carrega favoritos
        List<Restaurante> favs = cliente.getFavoritos();
        for (Restaurante r : favs) {
            String categoria = r.getCategoriaGlobal() != null
                    ? r.getCategoriaGlobal().getNome() : "N/A";
            model.addRow(new Object[]{r.getNome(), categoria,
                    r.isStatusAtivo() ? "Ativo" : "Inativo", "★"});
        }

        JTable tabela = new JTable(model);
        tabela.setFont(AppFonts.LABEL);
        tabela.setRowHeight(30);
        tabela.setGridColor(new Color(220, 220, 220));
        tabela.setShowGrid(true);
        tabela.setSelectionBackground(new Color(220, 235, 255));

        configurarHeader(tabela);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(220);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(160);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(80);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(70);

        // Badge de status ativo/inativo
        tabela.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean sel, boolean foc, int row, int col) {
                JPanel cell = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 3));
                cell.setBackground(sel ? t.getSelectionBackground() : Color.WHITE);
                JLabel badge = new JLabel(String.valueOf(value));
                badge.setFont(AppFonts.STATUS.deriveFont(Font.BOLD, 11f));
                badge.setOpaque(true);
                badge.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
                if ("Ativo".equals(value)) {
                    badge.setBackground(new Color(180, 240, 190));
                    badge.setForeground(new Color(0, 100, 30));
                } else {
                    badge.setBackground(new Color(255, 190, 190));
                    badge.setForeground(new Color(150, 0, 0));
                }
                cell.add(badge);
                return cell;
            }
        });

        // Estrela laranja para remover
        tabela.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean sel, boolean foc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, value, sel, foc, row, col);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setForeground(new Color(255, 160, 0));
                lbl.setFont(lbl.getFont().deriveFont(14f));
                return lbl;
            }
        });

        // Clique na ★ remove favorito
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = tabela.columnAtPoint(e.getPoint());
                int row = tabela.rowAtPoint(e.getPoint());
                if (col == 3 && row >= 0 && row < favs.size()) {
                    Restaurante r = favs.get(row);
                    int confirm = JOptionPane.showConfirmDialog(PainelPerfil.this,
                            "Remover \"" + r.getNome() + "\" dos favoritos?",
                            "Confirmar", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        clienteService.favoritar(cliente, r); // toggle → remove
                        favs.remove(r);
                        model.removeRow(row);
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JLabel info = new JLabel("💡 Clique em ★ para remover um restaurante dos favoritos.");
        info.setFont(AppFonts.HINT);
        info.setForeground(Color.GRAY);
        info.setBorder(BorderFactory.createEmptyBorder(6, 2, 0, 0));

        painel.add(titulo, BorderLayout.NORTH);
        painel.add(scroll, BorderLayout.CENTER);
        painel.add(info, BorderLayout.SOUTH);
        return painel;
    }

    // ─────────────────────────────────────────────────────────────
    // SUB-ABA — SENHA
    // ─────────────────────────────────────────────────────────────
    private JPanel criarSubAbaSenha() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(titledBorder("Alterar Senha"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.anchor = GridBagConstraints.WEST;

        String[] labels = {"Senha atual:", "Nova senha:", "Confirmar nova senha:"};
        JPasswordField[] fields = new JPasswordField[labels.length];

        for (int i = 0; i < labels.length; i++) {
            if (i == 1) {
                gbc.gridx = 0; gbc.gridy = i; gbc.gridwidth = 2;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets = new Insets(14, 10, 2, 10);
                form.add(new JSeparator(), gbc);
                gbc.gridwidth = 1;
                gbc.fill = GridBagConstraints.NONE;
                gbc.insets = new Insets(6, 10, 6, 10);
            }

            int linha = i + (i >= 1 ? 1 : 0);
            gbc.gridx = 0; gbc.gridy = linha;
            gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(AppFonts.LABEL);
            lbl.setPreferredSize(new Dimension(185, 24));
            form.add(lbl, gbc);

            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
            fields[i] = new JPasswordField();
            fields[i].setFont(AppFonts.CAMPO);
            fields[i].setPreferredSize(new Dimension(300, 28));
            fields[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(180, 180, 180)),
                    BorderFactory.createEmptyBorder(2, 6, 2, 6)
            ));
            form.add(fields[i], gbc);
        }

        gbc.gridx = 1; gbc.gridy = labels.length + 1;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.insets = new Insets(4, 10, 8, 10);
        JLabel dica = new JLabel("💡 Use letras, números e caracteres especiais para maior segurança.");
        dica.setFont(AppFonts.HINT);
        dica.setForeground(Color.GRAY);
        form.add(dica, gbc);

        gbc.gridx = 1; gbc.gridy = labels.length + 2;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 6, 10);

        JButton btnSalvar = criarBotaoPrimario("Alterar Senha", 130, 30);
        btnSalvar.addActionListener(e -> {
            String atual = new String(fields[0].getPassword());
            String nova = new String(fields[1].getPassword());
            String confirma = new String(fields[2].getPassword());
            try {
                clienteService.alterarSenha(usuario, atual, nova, confirma);
                for (JPasswordField f : fields) f.setText("");
                JOptionPane.showMessageDialog(this, "Senha alterada com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Erro ao alterar senha:\n" + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
                fields[1].setText(""); fields[2].setText("");
                fields[1].requestFocus();
            }
        });
        form.add(btnSalvar, gbc);

        painel.add(form, BorderLayout.NORTH);
        return painel;
    }

    // ── Helpers ────────────────────────────────────────────────────
    private void configurarHeader(JTable tabela) {
        JTableHeader th = tabela.getTableHeader();
        th.setFont(AppFonts.TITULO);
        th.setBackground(new Color(245, 245, 245));
        th.setForeground(Color.DARK_GRAY);
        th.setReorderingAllowed(false);
    }

    private JTextField criarCampoTexto(String valor) {
        JTextField f = new JTextField(valor);
        f.setFont(AppFonts.CAMPO);
        f.setPreferredSize(new Dimension(450, 28));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        return f;
    }

    private JButton criarBotaoPrimario(String texto, int largura, int altura) {
        JButton btn = new JButton(texto);
        btn.setPreferredSize(new Dimension(largura, altura));
        btn.setFont(AppFonts.BOTAO);
        btn.setBackground(AppColors.AZUL_PRIMARIO);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private TitledBorder titledBorder(String titulo) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                titulo,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                AppFonts.TITULO
        );
    }
}

