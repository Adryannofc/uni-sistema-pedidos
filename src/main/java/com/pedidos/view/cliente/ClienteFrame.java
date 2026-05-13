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

public class ClienteFrame extends BaseFrame {

    private final Usuario usuario;
    private JTabbedPane tabbedPane;

    public ClienteFrame(Usuario usuario) {
        super("Sistema Delivery | Cliente", 1000, 700);
        this.usuario = usuario;
        construirInterface();
    }


    private void construirInterface() {
        setLayout(new BorderLayout());

        add(criarHeader(),    BorderLayout.NORTH);
        add(criarAbas(),      BorderLayout.CENTER);
    }

    private JPanel criarHeader() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        menuBar.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));

        String[] menus = {"Pedido", "Histórico", "Perfil", "Logout"};
        for (String nome : menus) {
            JMenu m = new JMenu(nome);
            m.setBackground(Color.white);
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

    private JTabbedPane criarAbas() {
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(AppFonts.STATUS);
        tabbedPane.setBackground(Color.WHITE);

        tabbedPane.addTab("Fazer Pedido", criarPainelVazio("Fazer Pedido"));
        tabbedPane.addTab("Checkout",         criarPainelCheckout());
        tabbedPane.addTab("Meus Pedidos",     criarPainelVazio("Meus Pedidos"));
        tabbedPane.addTab("Perfil",           criarPainelVazio("Perfil"));

        tabbedPane.setSelectedIndex(1);

        return tabbedPane;
    }

    private JPanel criarPainelCheckout() {
        JPanel painel = new JPanel(new BorderLayout(0, 12));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        painel.add(criarResumo(),   BorderLayout.CENTER);
        painel.add(criarRodape(),   BorderLayout.SOUTH);

        return painel;
    }

    private JPanel criarResumo() {
        JPanel painel = new JPanel(new BorderLayout(0, 0));
        painel.setBackground(Color.WHITE);
        painel.setBorder(titledBorder("Resumo do Pedido"));

        // Tabela
        String[] colunas = {"Produto", "Qtd", "Preço unit.", "Subtotal"};
        Object[][] dados  = {
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

        // Header da tabela
        JTableHeader th = tabela.getTableHeader();
        th.setFont(AppFonts.STATUS.deriveFont(Font.BOLD));
        th.setBackground(new Color(245, 245, 245));
        th.setForeground(Color.DARK_GRAY);
        th.setReorderingAllowed(false);

        // Centralizar Qtd, Preço unit. e Subtotal
        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 1; i <= 3; i++) tabela.getColumnModel().getColumn(i).setCellRenderer(centro);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        painel.add(scroll, BorderLayout.CENTER);

        // Totais à direita
        painel.add(criarTotais(), BorderLayout.SOUTH);

        return painel;
    }

    // Bloco de totais
    private JPanel criarTotais() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createEmptyBorder(10, 0, 8, 16));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 12, 3, 12);

        adicionarLinhaTotal(painel, gbc, "Subtotal:",        "R$ 60,70", false, 0);
        adicionarLinhaTotal(painel, gbc, "Taxa de entrega:", "R$ 5,00",  false, 1);

        // Separador visual antes do total
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JSeparator sep = new JSeparator();
        sep.setPreferredSize(new Dimension(240, 1));
        painel.add(sep, gbc);
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;

        adicionarLinhaTotal(painel, gbc, "TOTAL:", "R$ 65,70", true, 3);

        // Empurrar para a direita
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

        gbc.gridy  = linha;
        gbc.gridx  = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel lbl = new JLabel(label);
        lbl.setFont(fonte);
        painel.add(lbl, gbc);

        gbc.gridx  = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel val = new JLabel(valor);
        val.setFont(fonte);
        painel.add(val, gbc);
    }

    // ── Rodapé: endereço + botões ─────────────────────────────────
    private JPanel criarRodape() {
        JPanel rodape = new JPanel(new BorderLayout(0, 10));
        rodape.setBackground(Color.WHITE);

        // Endereço de entrega
        JPanel enderecoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        enderecoPanel.setBackground(Color.WHITE);
        enderecoPanel.setBorder(titledBorder("Endereço de Entrega"));

        JLabel icone = new JLabel("📍");
        icone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));

        JLabel endLabel = new JLabel("Rua das Flores, 100 — Centro, Maringá — PR");
        endLabel.setFont(AppFonts.STATUS);

        enderecoPanel.add(icone);
        enderecoPanel.add(endLabel);

        // Botões
        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        botoesPanel.setBackground(Color.WHITE);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(110, 36));
        btnCancelar.setFont(AppFonts.STATUS);
        btnCancelar.setBackground(new Color(220, 220, 220));
        btnCancelar.setForeground(Color.DARK_GRAY);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setBorderPainted(true);
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
        btnConfirmar.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Pedido confirmado com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE));

        botoesPanel.add(btnCancelar);
        botoesPanel.add(btnConfirmar);

        rodape.add(enderecoPanel, BorderLayout.CENTER);
        rodape.add(botoesPanel,   BorderLayout.SOUTH);

        return rodape;
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