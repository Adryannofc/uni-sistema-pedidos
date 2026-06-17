package com.pedidos.view.cliente;

import com.pedidos.controller.ClienteController;
import com.pedidos.model.entity.Cliente;
import com.pedidos.model.entity.Endereco;
import com.pedidos.model.entity.Restaurante;
import com.pedidos.model.entity.Usuario;
import com.pedidos.view.util.AppColors;
import com.pedidos.view.util.AppFonts;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
 * Contém sub-abas: Dados, Endereço, Senha.
 */
public class PainelPerfil extends JPanel {

    private final Usuario usuario;
    private final Cliente cliente;
    private final ClienteController clienteController;
    private final Runnable aoAtualizarEndereco;

    // Flag de dirty-tracking
    private boolean dadosAlterados = false;

    public PainelPerfil(Usuario usuario, Cliente cliente, ClienteController clienteController, Runnable aoAtualizarEndereco) {
        this.usuario = usuario;
        this.cliente = cliente;
        this.clienteController = clienteController;
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
        subAbas.addTab("Senha", criarSubAbaSenha());

        subAbas.setSelectedIndex(0);
        add(subAbas, BorderLayout.CENTER);
    }

    // Método para marcar alteração
    private void marcarAlterado() {
        this.dadosAlterados = true;
    }

    // Método público para checar se há modificações
    public boolean isDadosAlterados() {
        return dadosAlterados;
    }

    // Método público para resetar flag após salvar
    public void resetDadosAlterados() {
        this.dadosAlterados = false;
    }

    // DocumentListener reutilizável que marca alterações
    private DocumentListener criarDocumentListenerQueMarca() {
        return new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { marcarAlterado(); }
            @Override public void removeUpdate(DocumentEvent e) { marcarAlterado(); }
            @Override public void changedUpdate(DocumentEvent e) { marcarAlterado(); }
        };
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
            // adiciona listener de documento para marcar alterações
            fields[i].getDocument().addDocumentListener(criarDocumentListenerQueMarca());
            form.add(fields[i], gbc);
        }

        gbc.gridx = 1; gbc.gridy = labels.length;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(12, 10, 6, 10);

        JButton btnSalvar = criarBotaoPrimario("Salvar", 80, 30);
        btnSalvar.addActionListener(e -> {
            try {
                clienteController.editarNome(cliente, fields[0].getText().trim());
                clienteController.editarEmail(cliente, fields[1].getText().trim());
                clienteController.editarCpf(cliente, fields[2].getText().trim());
                clienteController.editarTelefone(cliente, fields[3].getText().trim());
                JOptionPane.showMessageDialog(this, "Dados salvos com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                // resetar flag após salvar
                resetDadosAlterados();
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
            // adicionar listener para marcar alterações
            fields[i].getDocument().addDocumentListener(criarDocumentListenerQueMarca());
            form.add(fields[i], gbc);
        }

        gbc.gridx = 1; gbc.gridy = labels.length;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(14, 10, 6, 10);

        JButton btnSalvar = criarBotaoPrimario("Salvar Endereço", 150, 30);
        btnSalvar.addActionListener(e -> {

            String rua = fields[0].getText().trim(); // rua
            String numero = fields[1].getText().trim(); // numero
            String bairro =fields[2].getText().trim(); // bairro
            String cidade =fields[3].getText().trim(); // cidade
            String estado =fields[4].getText().trim(); // estado
            String cep =fields[5].getText().trim(); // cep

            if (rua.isEmpty() || numero.isEmpty()  || bairro.isEmpty()  || cidade.isEmpty()  || estado.isEmpty()  || cep.isEmpty() ) {

                JOptionPane.showMessageDialog(this, "Preencha todos os campos!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                return;
            }

            if (rua.equals(endAtual.map(Endereco::getRua).orElse("")) &&
                numero.equals(endAtual.map(Endereco::getNumero).orElse("")) &&
                bairro.equals(endAtual.map(Endereco::getBairro).orElse("")) &&
                cidade.equals(endAtual.map(Endereco::getCidade).orElse("")) &&
                estado.equals(endAtual.map(Endereco::getEstado).orElse("")) &&
                cep.equals(endAtual.map(Endereco::getCep).orElse(""))) {


                JOptionPane.showMessageDialog(this, "Nenhuma alteração detectada!",
                        "Atenção", JOptionPane.INFORMATION_MESSAGE);

                return;
            }

            try {
                clienteController.salvarEndereco(
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

                // resetar flag após salvar
                resetDadosAlterados();

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
            // adicionar listener para marcar alterações em senha
            fields[i].getDocument().addDocumentListener(criarDocumentListenerQueMarca());
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
                clienteController.alterarSenha(usuario, atual, nova, confirma);
                for (JPasswordField f : fields) f.setText("");
                JOptionPane.showMessageDialog(this, "Senha alterada com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                // resetar flag após alterar senha
                resetDadosAlterados();
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
