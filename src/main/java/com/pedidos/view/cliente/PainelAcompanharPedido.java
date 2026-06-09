package com.pedidos.view.cliente;

import com.pedidos.model.service.PedidoService;
import com.pedidos.model.entity.Endereco;
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
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class PainelAcompanharPedido extends JPanel {

    private final PedidoService pedidoService;
    private final Runnable aoVoltar;
    private Pedido pedido;

    private final NumberFormat moedaBR = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private static final String[] STEP_NAMES = {"Recebido", "Em Preparo", "Saiu para Entrega", "Entregue"};

    // Header
    private JLabel lblTitulo;
    private JLabel lblHora;
    private JLabel lblStatusBadge;

    // Timeline
    private JPanel[] stepDots;
    private JLabel[] lblStepTime;
    private JProgressBar progressBar;
    private JLabel lblMensagem;

    // Resumo
    private DefaultTableModel modelItens;
    private JLabel lblSubtotal;
    private JLabel lblTaxa;
    private JLabel lblTotal;
    private JLabel lblEndereco;
    private JLabel lblCodigo;

    // Botões
    private JButton btnCancelar;

    public PainelAcompanharPedido(PedidoService pedidoService, Runnable aoVoltar) {
        this.pedidoService = pedidoService;
        this.aoVoltar = aoVoltar;
        construir();
    }

    private void construir() {
        setLayout(new BorderLayout(0, 0));
        setBackground(Color.WHITE);
        add(criarHeader(), BorderLayout.NORTH);
        add(criarCorpo(), BorderLayout.CENTER);
        add(criarRodape(), BorderLayout.SOUTH);
    }

    // ── Header ─────────────────────────────────────────────────────

    private JPanel criarHeader() {
        JPanel header = new JPanel(new BorderLayout(8, 0));
        header.setBackground(new Color(248, 249, 250));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));

        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        esquerda.setOpaque(false);

        lblTitulo = new JLabel("Pedido —");
        lblTitulo.setFont(AppFonts.TITULO.deriveFont(Font.BOLD, 14f));

        lblHora = new JLabel("");
        lblHora.setFont(AppFonts.LABEL);
        lblHora.setForeground(Color.GRAY);

        esquerda.add(lblTitulo);
        esquerda.add(lblHora);

        lblStatusBadge = new JLabel("");
        lblStatusBadge.setFont(AppFonts.LABEL.deriveFont(Font.BOLD, 11f));
        lblStatusBadge.setOpaque(true);
        lblStatusBadge.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));

        header.add(esquerda, BorderLayout.WEST);
        header.add(lblStatusBadge, BorderLayout.EAST);
        return header;
    }

    // ── Corpo ──────────────────────────────────────────────────────

    private JPanel criarCorpo() {
        JPanel corpo = new JPanel(new GridLayout(1, 2, 12, 0));
        corpo.setBackground(Color.WHITE);
        corpo.setBorder(BorderFactory.createEmptyBorder(12, 16, 8, 16));
        corpo.add(criarPainelTimeline());
        corpo.add(criarPainelResumo());
        return corpo;
    }

    private JPanel criarPainelTimeline() {
        JPanel painel = new JPanel(new BorderLayout(0, 0));
        painel.setBackground(Color.WHITE);
        painel.setBorder(titledBorder("Status do Pedido"));

        JPanel steps = new JPanel(new GridLayout(STEP_NAMES.length, 1, 0, 2));
        steps.setBackground(Color.WHITE);
        steps.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        stepDots = new JPanel[STEP_NAMES.length];
        lblStepTime = new JLabel[STEP_NAMES.length];

        for (int i = 0; i < STEP_NAMES.length; i++) {
            JPanel row = new JPanel(new BorderLayout(8, 0));
            row.setBackground(Color.WHITE);
            row.setPreferredSize(new Dimension(0, 34));

            JPanel dot = criarDot(new Color(180, 180, 180));
            stepDots[i] = dot;

            JLabel lblNome = new JLabel(STEP_NAMES[i]);
            lblNome.setFont(AppFonts.LABEL);
            lblNome.setForeground(Color.DARK_GRAY);

            lblStepTime[i] = new JLabel("--:--");
            lblStepTime[i].setFont(AppFonts.STATUS);
            lblStepTime[i].setForeground(new Color(180, 180, 180));

            JPanel esq = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
            esq.setBackground(Color.WHITE);
            esq.add(dot);
            esq.add(lblNome);

            row.add(esq, BorderLayout.CENTER);
            row.add(lblStepTime[i], BorderLayout.EAST);
            steps.add(row);
        }

        progressBar = new JProgressBar(0, STEP_NAMES.length - 1);
        progressBar.setBorder(BorderFactory.createEmptyBorder(0, 12, 4, 12));
        progressBar.setForeground(AppColors.AZUL_PRIMARIO);
        progressBar.setBackground(new Color(220, 220, 220));

        lblMensagem = new JLabel(" ");
        lblMensagem.setFont(AppFonts.LABEL.deriveFont(Font.BOLD));
        lblMensagem.setForeground(AppColors.AZUL_PRIMARIO);
        lblMensagem.setBorder(BorderFactory.createEmptyBorder(2, 16, 10, 16));

        JPanel sul = new JPanel(new BorderLayout(0, 2));
        sul.setBackground(Color.WHITE);
        sul.add(progressBar, BorderLayout.NORTH);
        sul.add(lblMensagem, BorderLayout.CENTER);

        painel.add(steps, BorderLayout.CENTER);
        painel.add(sul, BorderLayout.SOUTH);
        return painel;
    }

    private JPanel criarPainelResumo() {
        JPanel painel = new JPanel(new BorderLayout(0, 6));
        painel.setBackground(Color.WHITE);
        painel.setBorder(titledBorder("Resumo do Pedido"));

        modelItens = new DefaultTableModel(new String[]{"Produto", "Qtd", "Total"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable tabela = new JTable(modelItens);
        tabela.setFont(AppFonts.LABEL);
        tabela.setRowHeight(26);
        tabela.setGridColor(new Color(220, 220, 220));
        tabela.setShowGrid(true);

        JTableHeader th = tabela.getTableHeader();
        th.setFont(AppFonts.TITULO);
        th.setBackground(new Color(245, 245, 245));
        th.setReorderingAllowed(false);

        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        tabela.getColumnModel().getColumn(1).setCellRenderer(centro);
        tabela.getColumnModel().getColumn(2).setCellRenderer(centro);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(200);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(40);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(90);

        JPanel totais = new JPanel(new GridBagLayout());
        totais.setBackground(Color.WHITE);
        totais.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(2, 4, 2, 8);

        lblSubtotal = linhaTotal(totais, g, "Subtotal:", false, 0);
        lblTaxa     = linhaTotal(totais, g, "Taxa de entrega:", false, 1);

        g.gridx = 0; g.gridy = 2; g.gridwidth = 2; g.fill = GridBagConstraints.HORIZONTAL;
        totais.add(new JSeparator(), g);
        g.gridwidth = 1; g.fill = GridBagConstraints.NONE;

        lblTotal = linhaTotal(totais, g, "TOTAL:", true, 3);

        JPanel endPanel = new JPanel(new BorderLayout(0, 2));
        endPanel.setBackground(Color.WHITE);
        endPanel.setBorder(BorderFactory.createEmptyBorder(6, 8, 8, 8));

        JLabel lblEndTitulo = new JLabel("Endereço de entrega:");
        lblEndTitulo.setFont(AppFonts.LABEL.deriveFont(Font.BOLD));

        lblEndereco = new JLabel("—");
        lblEndereco.setFont(AppFonts.LABEL);
        lblEndereco.setForeground(Color.DARK_GRAY);

        // Código de confirmação de entrega
        JPanel codigoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        codigoPanel.setBackground(new Color(255, 248, 220));
        codigoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 190, 80)),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)));

        JLabel lblCodigoTitulo = new JLabel("Código de confirmação de entrega:");
        lblCodigoTitulo.setFont(AppFonts.LABEL.deriveFont(Font.BOLD));
        lblCodigoTitulo.setForeground(new Color(100, 70, 0));

        lblCodigo = new JLabel("—");
        lblCodigo.setFont(AppFonts.TITULO.deriveFont(Font.BOLD, 16f));
        lblCodigo.setForeground(new Color(80, 50, 0));

        codigoPanel.add(lblCodigoTitulo);
        codigoPanel.add(lblCodigo);

        endPanel.add(lblEndTitulo, BorderLayout.NORTH);
        endPanel.add(lblEndereco, BorderLayout.CENTER);
        endPanel.add(codigoPanel, BorderLayout.SOUTH);

        JPanel sul = new JPanel(new BorderLayout());
        sul.setBackground(Color.WHITE);
        sul.add(totais, BorderLayout.NORTH);
        sul.add(endPanel, BorderLayout.CENTER);

        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);
        painel.add(sul, BorderLayout.SOUTH);
        return painel;
    }

    // ── Rodapé ─────────────────────────────────────────────────────

    private JPanel criarRodape() {
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setBackground(Color.WHITE);
        rodape.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)));

        btnCancelar = new JButton("Cancelar Pedido");
        btnCancelar.setFont(AppFonts.BOTAO);
        btnCancelar.setForeground(new Color(180, 0, 0));
        btnCancelar.setBackground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(e -> cancelarPedido());

        JButton btnAtualizar = botaoSecundario("Atualizar Status ↺");
        btnAtualizar.addActionListener(e -> atualizarDoBanco());

        JButton btnVoltar = botaoPrimario("← Voltar");
        btnVoltar.addActionListener(e -> aoVoltar.run());

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        direita.setBackground(Color.WHITE);
        direita.add(btnAtualizar);
        direita.add(btnVoltar);

        rodape.add(btnCancelar, BorderLayout.WEST);
        rodape.add(direita, BorderLayout.EAST);
        return rodape;
    }

    // ── API pública ────────────────────────────────────────────────

    public void carregarPedido(Pedido p) {
        this.pedido = p;
        atualizar();
    }

    // ── Ações ──────────────────────────────────────────────────────

    private void atualizarDoBanco() {
        try {
            pedido = pedidoService.buscarPorId(pedido.getId());
            atualizar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelarPedido() {
        if (pedido == null) return;
        int r = JOptionPane.showConfirmDialog(this,
                "Deseja cancelar este pedido?", "Cancelar Pedido", JOptionPane.YES_NO_OPTION);
        if (r != JOptionPane.YES_OPTION) return;
        try {
            pedidoService.atualizarStatus(pedido.getId(), StatusPedido.CANCELADO);
            pedido = pedidoService.buscarPorId(pedido.getId());
            atualizar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizar() {
        if (pedido == null) return;

        // Header
        String idCurto = pedido.getId().length() > 8
                ? pedido.getId().substring(0, 8).toUpperCase() : pedido.getId().toUpperCase();
        String restaurante = pedido.getRestaurante() != null ? pedido.getRestaurante().getNome() : "—";
        lblTitulo.setText("Pedido #" + idCurto + " • " + restaurante);
        lblHora.setText(pedido.getDataPedido() != null
                ? "Realizado às " + pedido.getDataPedido().format(FMT_HORA) : "");

        atualizarBadge(pedido.getStatus());
        atualizarTimeline(pedido.getStatus());

        // Itens
        modelItens.setRowCount(0);
        for (ItemPedido item : pedido.getItens()) {
            BigDecimal sub = item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade()));
            modelItens.addRow(new Object[]{item.getNomeProduto(), item.getQuantidade(), moedaBR.format(sub)});
        }

        BigDecimal taxa  = pedido.getTaxaEntrega() != null ? pedido.getTaxaEntrega() : BigDecimal.ZERO;
        BigDecimal total = pedido.getTotal() != null ? pedido.getTotal() : BigDecimal.ZERO;
        BigDecimal sub   = total.subtract(taxa);

        lblSubtotal.setText(moedaBR.format(sub));
        lblTaxa.setText(moedaBR.format(taxa));
        lblTotal.setText(moedaBR.format(total));

        Endereco end = pedido.getEnderecoEntrega();
        lblEndereco.setText(end != null
                ? end.getRua() + ", " + end.getNumero() + " — " + end.getBairro() : "—");

        String codigo = pedido.getCodigoConfirmacao();
        lblCodigo.setText(codigo != null ? "[ " + codigo + " ]" : "—");

        boolean podeCancelar = pedido.getStatus() == StatusPedido.AGUARDANDO_CONFIRMACAO
                || pedido.getStatus() == StatusPedido.CONFIRMADO;
        btnCancelar.setVisible(podeCancelar);

        revalidate();
        repaint();
    }

    private void atualizarBadge(StatusPedido status) {
        String texto; Color bg, fg;
        switch (status) {
            case AGUARDANDO_CONFIRMACAO -> { texto = "AGUARD. CONFIRM."; bg = new Color(255,220,100); fg = new Color(120,80,0); }
            case CONFIRMADO             -> { texto = "CONFIRMADO";       bg = new Color(180,220,255); fg = new Color(0,60,140); }
            case EM_PREPARO             -> { texto = "EM PREPARO";       bg = new Color(255,200,120); fg = new Color(140,60,0); }
            case SAIU_PARA_ENTREGA      -> { texto = "SAIU P/ ENTREGA";  bg = new Color(200,180,255); fg = new Color(60,0,140); }
            case ENTREGUE               -> { texto = "ENTREGUE";         bg = new Color(180,240,190); fg = new Color(0,100,30); }
            case CANCELADO              -> { texto = "CANCELADO";        bg = new Color(255,190,190); fg = new Color(150,0,0); }
            default                     -> { texto = status.name();      bg = new Color(220,220,220); fg = Color.DARK_GRAY; }
        }
        lblStatusBadge.setText(texto);
        lblStatusBadge.setBackground(bg);
        lblStatusBadge.setForeground(fg);
    }

    private void atualizarTimeline(StatusPedido status) {
        int stepAtivo = stepIndex(status);
        Color corAtivo    = AppColors.AZUL_PRIMARIO;
        Color corFeito    = new Color(80, 160, 80);
        Color corPendente = new Color(190, 190, 190);

        for (int i = 0; i < STEP_NAMES.length; i++) {
            Color cor = i < stepAtivo ? corFeito : (i == stepAtivo ? corAtivo : corPendente);
            stepDots[i].putClientProperty("cor", cor);
            stepDots[i].repaint();

            if (i == 0 && pedido.getDataPedido() != null) {
                lblStepTime[i].setText(pedido.getDataPedido().format(FMT_HORA));
                lblStepTime[i].setForeground(i == 0 && stepAtivo == 0 ? corAtivo : corFeito);
            } else if (i < stepAtivo) {
                lblStepTime[i].setText("ok");
                lblStepTime[i].setForeground(corFeito);
            } else {
                lblStepTime[i].setText("--:--");
                lblStepTime[i].setForeground(corPendente);
            }
        }

        if (status == StatusPedido.CANCELADO) {
            progressBar.setForeground(new Color(200, 80, 80));
            lblMensagem.setForeground(new Color(150, 0, 0));
        } else {
            progressBar.setForeground(AppColors.AZUL_PRIMARIO);
            lblMensagem.setForeground(AppColors.AZUL_PRIMARIO);
        }
        progressBar.setValue(Math.min(stepAtivo, STEP_NAMES.length - 1));
        lblMensagem.setText(mensagemStatus(status));
    }

    private int stepIndex(StatusPedido status) {
        return switch (status) {
            case AGUARDANDO_CONFIRMACAO, CONFIRMADO -> 0;
            case EM_PREPARO                         -> 1;
            case SAIU_PARA_ENTREGA                  -> 2;
            case ENTREGUE, CANCELADO                -> 3;
        };
    }

    private String mensagemStatus(StatusPedido status) {
        return switch (status) {
            case AGUARDANDO_CONFIRMACAO -> "Recebido — Aguardando confirmação do restaurante...";
            case CONFIRMADO             -> "Confirmado — Seu pedido foi aceito!";
            case EM_PREPARO             -> "Em Preparo — Seu pedido está sendo preparado...";
            case SAIU_PARA_ENTREGA      -> "Saiu para Entrega — Seu pedido está a caminho!";
            case ENTREGUE               -> "Entregue — Pedido entregue com sucesso!";
            case CANCELADO              -> "Cancelado — Este pedido foi cancelado.";
        };
    }

    // ── Helpers ────────────────────────────────────────────────────

    private JPanel criarDot(Color cor) {
        JPanel dot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = (Color) getClientProperty("cor");
                if (c == null) c = new Color(180, 180, 180);
                g2.setColor(c);
                g2.fillOval(1, 1, getWidth() - 2, getHeight() - 2);
            }
        };
        dot.setPreferredSize(new Dimension(16, 16));
        dot.setBackground(Color.WHITE);
        dot.putClientProperty("cor", cor);
        return dot;
    }

    private JLabel linhaTotal(JPanel p, GridBagConstraints g, String label, boolean negrito, int linha) {
        Font f = negrito ? AppFonts.TITULO : AppFonts.LABEL;
        g.gridy = linha;
        g.gridx = 0; g.anchor = GridBagConstraints.EAST;
        JLabel lbl = new JLabel(label); lbl.setFont(f); p.add(lbl, g);
        g.gridx = 1;
        JLabel val = new JLabel("—"); val.setFont(f); p.add(val, g);
        return val;
    }

    private JButton botaoPrimario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(AppFonts.BOTAO);
        btn.setBackground(AppColors.AZUL_PRIMARIO);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton botaoSecundario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(AppFonts.BOTAO);
        btn.setBackground(new Color(220, 220, 220));
        btn.setForeground(Color.DARK_GRAY);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private javax.swing.border.TitledBorder titledBorder(String titulo) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                titulo, 0, 0, AppFonts.TITULO);
    }
}
