package com.pedidos.view.cliente;

import com.pedidos.domain.entities.Usuario;
import com.pedidos.view.util.AppColors;
import com.pedidos.view.util.AppFonts;
import com.pedidos.view.util.base.BaseFrame;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ClienteFrame extends BaseFrame {

    private final Usuario usuario;
    private JTabbedPane tabbedPane;

    public ClienteFrame(Usuario usuario) {
        super("Sistema Delivery | Cliente", 1000, 700);
        this.usuario = usuario;
        construirInterface();
    }

    // ═════════════════════════════════════════════════════════════
    // INTERFACE PRINCIPAL
    // ═════════════════════════════════════════════════════════════
    private void construirInterface() {
        setLayout(new BorderLayout());
        add(criarHeader(),    BorderLayout.NORTH);
        add(criarAbas(),      BorderLayout.CENTER);
        add(criarStatusBar(), BorderLayout.SOUTH);
    }

    // ═════════════════════════════════════════════════════════════
    // HEADER
    // ═════════════════════════════════════════════════════════════
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

    // ═════════════════════════════════════════════════════════════
    // STATUS BAR
    // ═════════════════════════════════════════════════════════════
    private JPanel criarStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 3));
        bar.setBackground(new Color(240, 240, 240));
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));

        for (JLabel l : new JLabel[]{
                new JLabel(usuario.getNome() + " | Cliente"),
                new JLabel("|"),
                new JLabel("1 pedido(s) ativo(s)"),
                new JLabel("|"),
                new JLabel("Endereço cadastrado")
        }) {
            l.setFont(AppFonts.STATUS.deriveFont(11f));
            l.setForeground(Color.DARK_GRAY);
            bar.add(l);
        }
        return bar;
    }

    // ═════════════════════════════════════════════════════════════
    // ABAS PRINCIPAIS
    // ═════════════════════════════════════════════════════════════
    private JTabbedPane criarAbas() {
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(AppFonts.STATUS);
        tabbedPane.setBackground(Color.WHITE);

        tabbedPane.addTab("Fazer Pedido (3)", criarPainelFazerPedido());
        tabbedPane.addTab("Checkout",         criarPainelCheckout());
        tabbedPane.addTab("Meus Pedidos",     criarPainelMeusPedidos());
        tabbedPane.addTab("Perfil",           criarPainelPerfil());

        tabbedPane.setSelectedIndex(0);
        return tabbedPane;
    }

    // ═════════════════════════════════════════════════════════════
    // ABA — FAZER PEDIDO
    // ═════════════════════════════════════════════════════════════
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

        String[] colunas = {"Restaurante", "Categoria", "★"};
        Object[][] dados = {
                {"Burguer House", "Lanches", "★"},
                {"Pizzaria Bella", "Pizza",  " "},
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

        tabela.getColumnModel().getColumn(0).setPreferredWidth(340);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(40);

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

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        painel.add(titulo, BorderLayout.NORTH);
        painel.add(scroll,  BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarPainelCarrinho() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setPreferredSize(new Dimension(270, 0));
        painel.setBackground(Color.WHITE);
        painel.setBorder(titledBorder("🛒 Meu Carrinho (2)"));

        String[] colunas = {"Produto", "Qtd", "Subtotal"};
        Object[][] dados = {
                {"X-Burguer", 2, "R$ 37,80"},
                {"X-Bacon",   1, "R$ 22,90"},
        };

        DefaultTableModel model = new DefaultTableModel(dados, colunas) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable tabela = new JTable(model);
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

        tabela.getColumnModel().getColumn(0).setPreferredWidth(110);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(35);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(80);

        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        tabela.getColumnModel().getColumn(1).setCellRenderer(centro);

        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);
        return painel;
    }

    // ═════════════════════════════════════════════════════════════
    // ABA — CHECKOUT
    // ═════════════════════════════════════════════════════════════
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

        JButton btnConfirmar = criarBotaoPrimario("Confirmar Pedido", 165, 36);
        btnConfirmar.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Pedido confirmado com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE));

        botoesPanel.add(btnCancelar);
        botoesPanel.add(btnConfirmar);

        rodape.add(enderecoPanel, BorderLayout.CENTER);
        rodape.add(botoesPanel,   BorderLayout.SOUTH);
        return rodape;
    }

    // ═════════════════════════════════════════════════════════════
    // ABA — MEUS PEDIDOS
    // ═════════════════════════════════════════════════════════════
    private JPanel criarPainelMeusPedidos() {
        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel contador = new JLabel("1 pedido(s) no histórico");
        contador.setFont(AppFonts.STATUS);
        contador.setForeground(Color.DARK_GRAY);
        contador.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

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

        tabela.getColumnModel().getColumn(0).setPreferredWidth(90);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(180);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(160);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(80);

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
                    case "AGUARD. CONFIRM." -> { badge.setBackground(new Color(255, 220, 100));  badge.setForeground(new Color(120, 80,  0));   }
                    case "EM PREPARO"       -> { badge.setBackground(new Color(180, 220, 255));  badge.setForeground(new Color(0,   60,  140)); }
                    case "ENTREGUE"         -> { badge.setBackground(new Color(180, 240, 190));  badge.setForeground(new Color(0,   100, 30));  }
                    case "CANCELADO"        -> { badge.setBackground(new Color(255, 190, 190));  badge.setForeground(new Color(150, 0,   0));   }
                    default                 -> { badge.setBackground(new Color(220, 220, 220));  badge.setForeground(Color.DARK_GRAY);          }
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

    // ═════════════════════════════════════════════════════════════
    // ABA — PERFIL
    // ═════════════════════════════════════════════════════════════
    private JPanel criarPainelPerfil() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JTabbedPane subAbas = new JTabbedPane(JTabbedPane.TOP);
        subAbas.setFont(AppFonts.STATUS);
        subAbas.setBackground(Color.WHITE);

        subAbas.addTab("Dados",     criarSubAbaDados());
        subAbas.addTab("Endereço",  criarSubAbaEndereco());
        subAbas.addTab("Favoritos", criarSubAbaFavoritos());
        subAbas.addTab("Senha",     criarSubAbaSenha());

        subAbas.setSelectedIndex(0);
        painel.add(subAbas, BorderLayout.CENTER);
        return painel;
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

        String[][] campos = {
                {"Nome:",     usuario.getNome()},
                {"E-mail:",   "joao@email.com"},
                {"CPF:",      "123.456.789-00"},
                {"Telefone:", "(44) 9 9876-5432"},
        };

        for (int i = 0; i < campos.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            JLabel lbl = new JLabel(campos[i][0]);
            lbl.setFont(AppFonts.STATUS);
            lbl.setPreferredSize(new Dimension(70, 24));
            form.add(lbl, gbc);

            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
            JTextField field = new JTextField(campos[i][1]);
            field.setFont(AppFonts.STATUS);
            field.setPreferredSize(new Dimension(500, 28));
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(180, 180, 180)),
                    BorderFactory.createEmptyBorder(2, 6, 2, 6)
            ));
            form.add(field, gbc);
        }

        gbc.gridx = 1; gbc.gridy = campos.length;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(12, 10, 6, 10);

        JButton btnSalvar = criarBotaoPrimario("Salvar", 80, 30);
        btnSalvar.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Dados salvos com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE));
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

        String[][] campos = {
                {"Rua / Logradouro:", "Rua das Flores"},
                {"Número:",           "100"},
                {"Complemento:",      "Apto 12"},
                {"Bairro:",           "Centro"},
                {"Cidade:",           "Maringá"},
                {"Estado (UF):",      "PR"},
                {"CEP:",              "87010-000"},
        };

        for (int i = 0; i < campos.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            JLabel lbl = new JLabel(campos[i][0]);
            lbl.setFont(AppFonts.STATUS);
            lbl.setPreferredSize(new Dimension(140, 24));
            form.add(lbl, gbc);

            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
            JTextField field = new JTextField(campos[i][1]);
            field.setFont(AppFonts.STATUS);
            field.setPreferredSize(new Dimension(420, 28));
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(180, 180, 180)),
                    BorderFactory.createEmptyBorder(2, 6, 2, 6)
            ));
            form.add(field, gbc);
        }

        gbc.gridx = 1; gbc.gridy = campos.length;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(14, 10, 6, 10);

        JButton btnSalvar = criarBotaoPrimario("Salvar Endereço", 150, 30);
        btnSalvar.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Endereço salvo com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE));
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
        titulo.setFont(AppFonts.STATUS.deriveFont(Font.BOLD, 13f));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        String[] colunas = {"Restaurante", "Categoria", "Status", "★ Remover"};
        Object[][] dados = {
                {"Burguer House", "Lanches", "Ativo", "★"},
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

        tabela.getColumnModel().getColumn(0).setPreferredWidth(220);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(160);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(80);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(70);

        // Badge verde para status
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
                badge.setBackground(new Color(180, 240, 190));
                badge.setForeground(new Color(0, 100, 30));
                cell.add(badge);
                return cell;
            }
        });

        // Estrela laranja clicável
        tabela.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean sel, boolean foc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        t, value, sel, foc, row, col);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setForeground(new Color(255, 160, 0));
                lbl.setFont(lbl.getFont().deriveFont(14f));
                return lbl;
            }
        });

        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = tabela.columnAtPoint(e.getPoint());
                int row = tabela.rowAtPoint(e.getPoint());
                if (col == 3 && row >= 0) {
                    String nome = (String) model.getValueAt(row, 0);
                    int confirm = JOptionPane.showConfirmDialog(
                            ClienteFrame.this,
                            "Remover \"" + nome + "\" dos favoritos?",
                            "Confirmar", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) model.removeRow(row);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JLabel info = new JLabel("💡 Clique em ★ para remover um restaurante dos favoritos.");
        info.setFont(AppFonts.STATUS.deriveFont(Font.ITALIC, 11f));
        info.setForeground(Color.GRAY);
        info.setBorder(BorderFactory.createEmptyBorder(6, 2, 0, 0));

        painel.add(titulo, BorderLayout.NORTH);
        painel.add(scroll,  BorderLayout.CENTER);
        painel.add(info,    BorderLayout.SOUTH);
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
            // Separador visual antes de "Nova senha"
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
            lbl.setFont(AppFonts.STATUS);
            lbl.setPreferredSize(new Dimension(185, 24));
            form.add(lbl, gbc);

            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
            fields[i] = new JPasswordField();
            fields[i].setFont(AppFonts.STATUS);
            fields[i].setPreferredSize(new Dimension(300, 28));
            fields[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(180, 180, 180)),
                    BorderFactory.createEmptyBorder(2, 6, 2, 6)
            ));
            form.add(fields[i], gbc);
        }

        // Dica de segurança
        gbc.gridx = 1; gbc.gridy = labels.length + 1;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.insets = new Insets(4, 10, 8, 10);
        JLabel dica = new JLabel("💡 Use letras, números e caracteres especiais para maior segurança.");
        dica.setFont(AppFonts.STATUS.deriveFont(Font.ITALIC, 11f));
        dica.setForeground(Color.GRAY);
        form.add(dica, gbc);

        // Botão
        gbc.gridx = 1; gbc.gridy = labels.length + 2;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 6, 10);

        JButton btnSalvar = criarBotaoPrimario("Alterar Senha", 130, 30);
        btnSalvar.addActionListener(e -> {
            String atual    = new String(fields[0].getPassword());
            String nova     = new String(fields[1].getPassword());
            String confirma = new String(fields[2].getPassword());

            if (atual.isBlank() || nova.isBlank() || confirma.isBlank()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos.",
                        "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!nova.equals(confirma)) {
                JOptionPane.showMessageDialog(this,
                        "A nova senha e a confirmação não coincidem.",
                        "Atenção", JOptionPane.WARNING_MESSAGE);
                fields[1].setText(""); fields[2].setText("");
                fields[1].requestFocus();
                return;
            }
            if (nova.length() < 6) {
                JOptionPane.showMessageDialog(this,
                        "A nova senha deve ter pelo menos 6 caracteres.",
                        "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Aqui: clienteService.alterarSenha(usuario, atual, nova)
            for (JPasswordField f : fields) f.setText("");
            JOptionPane.showMessageDialog(this, "Senha alterada com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        });
        form.add(btnSalvar, gbc);

        painel.add(form, BorderLayout.NORTH);
        return painel;
    }

    // ═════════════════════════════════════════════════════════════
    // HELPERS
    // ═════════════════════════════════════════════════════════════
    private JButton criarBotaoPrimario(String texto, int largura, int altura) {
        JButton btn = new JButton(texto);
        btn.setPreferredSize(new Dimension(largura, altura));
        btn.setFont(AppFonts.STATUS.deriveFont(Font.BOLD));
        btn.setBackground(AppColors.AZUL_PRIMARIO);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel criarPainelVazio(String nome) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        JLabel l = new JLabel(nome + " — em construção", SwingConstants.CENTER);
        l.setFont(AppFonts.STATUS);
        l.setForeground(Color.GRAY);
        p.add(l, BorderLayout.CENTER);
        return p;
    }

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