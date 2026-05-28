package com.pedidos.view.cliente;

import com.pedidos.model.service.ProdutoService;
import com.pedidos.model.service.RestauranteService;
import com.pedidos.model.entity.Cliente;
import com.pedidos.model.entity.HorarioFuncionamento;
import com.pedidos.model.entity.Restaurante;
import com.pedidos.view.util.AppColors;
import com.pedidos.view.util.AppFonts;
import com.pedidos.view.util.session.CarrinhoManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Painel responsável pela aba "Fazer Pedido".
 * Contém lista de restaurantes, cardápio selecionado e carrinho lateral.
 */
public class PainelFazerPedido extends JPanel {

    private final Cliente cliente;
    private final RestauranteService restauranteService;
    private final ProdutoService produtoService;
    private final CarrinhoManager carrinho;

    private final NumberFormat moedaBR = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Componentes da lista de restaurantes
    private DefaultTableModel modelRestaurantes;
    private Restaurante restauranteSelecionado;

    // CardLayout para alternar entre lista de restaurantes e cardápio
    private CardLayout cardLayoutFazerPedido;
    private JPanel centroPainelFazerPedido;
    private PainelCardapio painelCardapio;

    // Carrinho — referências para atualização dinâmica
    private TitledBorder borderCarrinho;
    private JPanel painelCarrinho;
    private JLabel lblSubtotalValor;
    private JLabel lblTaxaValor;
    private JTable tabelaCarrinho;
    private DefaultTableModel modelCarrinho;

    // Callbacks para coordenação
    private Runnable aoFinalizarPedido;

    public PainelFazerPedido(Cliente cliente,
                             RestauranteService restauranteService,
                             ProdutoService produtoService,
                             CarrinhoManager carrinho,
                             Runnable aoFinalizarPedido) {
        this.cliente = cliente;
        this.restauranteService = restauranteService;
        this.produtoService = produtoService;
        this.carrinho = carrinho;
        this.aoFinalizarPedido = aoFinalizarPedido;

        construir();
    }

    private void construir() {
        setLayout(new BorderLayout(12, 0));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        cardLayoutFazerPedido = new CardLayout();
        centroPainelFazerPedido = new JPanel(cardLayoutFazerPedido);
        centroPainelFazerPedido.setBackground(Color.WHITE);

        painelCardapio = new PainelCardapio(
                produtoService,
                carrinho,
                () -> cardLayoutFazerPedido.show(centroPainelFazerPedido, "RESTAURANTES"),
                () -> { sincronizarCarrinho(); }
        );

        centroPainelFazerPedido.add(criarListaRestaurantes(), "RESTAURANTES");
        centroPainelFazerPedido.add(painelCardapio, "CARDAPIO");

        add(centroPainelFazerPedido, BorderLayout.CENTER);
        add(criarPainelCarrinho(), BorderLayout.EAST);
    }

    // ── Lista de Restaurantes ─────────────────────────────────────
    private JPanel criarListaRestaurantes() {
        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.setBackground(Color.WHITE);

        JLabel titulo = new JLabel("Restaurantes disponíveis");
        titulo.setFont(AppFonts.TITULO);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        String[] colunas = {"Restaurante", "Categoria", "Status", "Horário hoje"};
        modelRestaurantes = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable tabela = new JTable(modelRestaurantes);
        tabela.setFont(AppFonts.LABEL);
        tabela.setRowHeight(30);
        tabela.setGridColor(new Color(220, 220, 220));
        tabela.setShowGrid(true);
        tabela.setSelectionBackground(new Color(220, 235, 255));
        tabela.setSelectionForeground(AppColors.TEXTO_PRIMARIO);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        configurarHeader(tabela);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(280);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(150);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(120);

        // Renderer coluna Status — ●Aberto verde / ●Fechado vermelho
        tabela.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean sel, boolean foc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, value, sel, foc, row, col);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if (sel) {
                    lbl.setBackground(t.getSelectionBackground());
                    lbl.setForeground(AppColors.TEXTO_PRIMARIO);
                } else {
                    lbl.setBackground(t.getBackground());
                    boolean aberto = "● Aberto".equals(value);
                    lbl.setForeground(aberto ? new Color(0, 150, 0) : new Color(200, 0, 0));
                }
                return lbl;
            }
        });

        // Mouse: clique simples na ★ = favoritar | clique simples em outra coluna = selecionar/abrir cardápio
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tabela.rowAtPoint(e.getPoint());
                int col = tabela.columnAtPoint(e.getPoint());
                if (row < 0) return;

                List<Restaurante> restaurantes = restauranteService.buscarRestaurantesAtivos();
                if (row >= restaurantes.size()) return;
                Restaurante r = restaurantes.get(row);

                // Clique simples → seleciona restaurante e abre cardápio
                if (e.getClickCount() == 2) {
                    abrirRestaurante(r);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        // Botões de ação abaixo da lista
        JButton btnVerCardapio = new JButton("Ver Cardápio");
        JButton btnVerHorarios = new JButton("● Ver Horários");
        btnVerCardapio.setFont(AppFonts.BOTAO);
        btnVerHorarios.setFont(AppFonts.BOTAO);

        btnVerCardapio.addActionListener(e -> {
            int row = tabela.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(PainelFazerPedido.this,
                        "Selecione um restaurante para ver o cardápio.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            List<Restaurante> restaurantes = restauranteService.buscarRestaurantesAtivos();
            if (row >= restaurantes.size()) return;
            Restaurante r = restaurantes.get(row);
            abrirRestaurante(r);
        });

        btnVerHorarios.addActionListener(e -> {
            int row = tabela.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(PainelFazerPedido.this,
                        "Selecione um restaurante para ver os horários.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            List<Restaurante> restaurantes = restauranteService.buscarRestaurantesAtivos();
            if (row >= restaurantes.size()) return;
            Restaurante r = restaurantes.get(row);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
            StringBuilder sb = new StringBuilder("Horários de funcionamento — ").append(r.getNome()).append("\n\n");
            r.getHorarios().stream()
                    .sorted(Comparator.comparing(HorarioFuncionamento::getDiaSemana))
                    .forEach(h -> sb.append(String.format("%-16s %s – %s%n",
                            traduzirDia(h.getDiaSemana()),
                            h.getHoraInicio().format(fmt),
                            h.getHoraFim().format(fmt))));
            if (r.getHorarios().isEmpty()) sb.append("Sem horários cadastrados.");
            JOptionPane.showMessageDialog(PainelFazerPedido.this, sb.toString(),
                    "Horários", JOptionPane.INFORMATION_MESSAGE);
        });

        JPanel acoesBaixo = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        acoesBaixo.setBackground(Color.WHITE);
        acoesBaixo.add(btnVerCardapio);
        acoesBaixo.add(btnVerHorarios);

        painel.add(titulo, BorderLayout.NORTH);
        painel.add(scroll, BorderLayout.CENTER);
        painel.add(acoesBaixo, BorderLayout.SOUTH);

        carregarRestaurantes();
        return painel;
    }

    private void abrirRestaurante(Restaurante r) {
        if (!carrinho.estaVazio() && !carrinho.getRestauranteId().equals(r.getId())) {
            int op = JOptionPane.showConfirmDialog(this,
                    "Você já tem itens de outro restaurante no carrinho.\n" +
                            "Deseja esvaziá-lo e selecionar \"" + r.getNome() + "\"?",
                    "Trocar restaurante", JOptionPane.YES_NO_OPTION);
            if (op != JOptionPane.YES_OPTION) return;
            carrinho.esvaziar();
        }

        restauranteSelecionado = r;
        carrinho.iniciar(cliente.getId(), r.getId(), new BigDecimal("5.00"));
        painelCardapio.configurar(restauranteSelecionado);
        cardLayoutFazerPedido.show(centroPainelFazerPedido, "CARDAPIO");
    }

    /** Carrega restaurantes ativos no model com status e horário de hoje. */
    private void carregarRestaurantes() {
        modelRestaurantes.setRowCount(0);
        DayOfWeek hoje = LocalDate.now().getDayOfWeek();
        LocalTime agora = LocalTime.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");

        for (Restaurante r : restauranteService.buscarRestaurantesAtivos()) {
            String categoria = r.getCategoriaGlobal() != null
                    ? r.getCategoriaGlobal().getNome() : "N/A";

            Optional<HorarioFuncionamento> horarioHoje = r.getHorarios().stream()
                    .filter(h -> h.getDiaSemana() == hoje)
                    .findFirst();

            boolean aberto = horarioHoje.map(h -> h.contemHorario(agora)).orElse(false);
            String status = aberto ? "● Aberto" : "● Fechado";
            String horario = horarioHoje.map(h ->
                    h.getHoraInicio().format(fmt) + " – " + h.getHoraFim().format(fmt))
                    .orElse("–");

            modelRestaurantes.addRow(new Object[]{r.getNome(), categoria, status, horario});
        }
    }

    private String traduzirDia(DayOfWeek dia) {
        return switch (dia) {
            case MONDAY -> "Segunda-feira";
            case TUESDAY -> "Terça-feira";
            case WEDNESDAY -> "Quarta-feira";
            case THURSDAY -> "Quinta-feira";
            case FRIDAY -> "Sexta-feira";
            case SATURDAY -> "Sábado";
            case SUNDAY -> "Domingo";
        };
    }

    // ── Painel Carrinho (direita) ─────────────────────────────────
    private JPanel criarPainelCarrinho() {
        painelCarrinho = new JPanel(new BorderLayout(0, 0));
        painelCarrinho.setPreferredSize(new Dimension(280, 0));
        painelCarrinho.setBackground(Color.WHITE);
        borderCarrinho = titledBorder("Meu Carrinho");
        painelCarrinho.setBorder(borderCarrinho);

        String[] colunas = {"Produto", "Qtd", "Subtotal"};
        modelCarrinho = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabelaCarrinho = new JTable(modelCarrinho);
        tabelaCarrinho.setFont(AppFonts.LABEL);
        tabelaCarrinho.setRowHeight(28);
        tabelaCarrinho.setGridColor(new Color(220, 220, 220));
        tabelaCarrinho.setShowGrid(true);
        tabelaCarrinho.setSelectionBackground(new Color(220, 235, 255));

        configurarHeader(tabelaCarrinho);
        tabelaCarrinho.getColumnModel().getColumn(0).setPreferredWidth(120);
        tabelaCarrinho.getColumnModel().getColumn(1).setPreferredWidth(30);
        tabelaCarrinho.getColumnModel().getColumn(2).setPreferredWidth(80);

        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        tabelaCarrinho.getColumnModel().getColumn(1).setCellRenderer(centro);

        // ── Totais (subtotal + taxa) ──────────────────────────────
        JPanel totaisPanel = new JPanel(new GridBagLayout());
        totaisPanel.setBackground(Color.WHITE);
        totaisPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(8, 8, 6, 8)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(2, 4, 2, 4);

        g.gridx = 0; g.gridy = 0; g.anchor = GridBagConstraints.WEST; g.weightx = 1.0; g.fill = GridBagConstraints.HORIZONTAL;
        JLabel lblSubt = new JLabel("Subtotal:");
        lblSubt.setFont(AppFonts.LABEL);
        totaisPanel.add(lblSubt, g);

        g.gridx = 1; g.weightx = 0; g.fill = GridBagConstraints.NONE; g.anchor = GridBagConstraints.EAST;
        lblSubtotalValor = new JLabel("R$ 0,00");
        lblSubtotalValor.setFont(AppFonts.BOTAO);
        totaisPanel.add(lblSubtotalValor, g);

        g.gridx = 0; g.gridy = 1; g.anchor = GridBagConstraints.WEST; g.weightx = 1.0; g.fill = GridBagConstraints.HORIZONTAL;
        JLabel lblTaxa = new JLabel("Taxa entrega:");
        lblTaxa.setFont(AppFonts.LABEL);
        totaisPanel.add(lblTaxa, g);

        g.gridx = 1; g.weightx = 0; g.fill = GridBagConstraints.NONE; g.anchor = GridBagConstraints.EAST;
        lblTaxaValor = new JLabel("R$ 0,00");
        lblTaxaValor.setFont(AppFonts.LABEL);
        lblTaxaValor.setForeground(Color.DARK_GRAY);
        totaisPanel.add(lblTaxaValor, g);

        // ── Botões menores ────────────────────────────────────────
        JButton btnRemover = criarBotaoSecundario("Remover");
        JButton btnEsvaziar = criarBotaoSecundario("Esvaziar");

        btnRemover.addActionListener(e -> {
            int row = tabelaCarrinho.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Selecione um item para remover.",
                        "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String nome = (String) modelCarrinho.getValueAt(row, 0);
            carrinho.getItens().stream()
                    .filter(it -> it.getProduto().getNome().equals(nome))
                    .findFirst()
                    .ifPresent(it -> {
                        carrinho.removerItem(it.getProduto().getId());
                        sincronizarCarrinho();
                    });
        });

        btnEsvaziar.addActionListener(e -> {
            if (carrinho.estaVazio()) return;
            carrinho.esvaziar();
            sincronizarCarrinho();
        });

        JPanel botoesPanel = new JPanel(new GridLayout(1, 2, 6, 0));
        botoesPanel.setBackground(Color.WHITE);
        botoesPanel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        botoesPanel.add(btnRemover);
        botoesPanel.add(btnEsvaziar);

        // ── Finalizar Pedido ──────────────────────────────────────
        JButton btnFinalizar = criarBotaoPrimario("Finalizar Pedido →", 260, 40);
        btnFinalizar.addActionListener(e -> {
            if (aoFinalizarPedido != null) {
                aoFinalizarPedido.run();
            }
        });
        JPanel finalizarPanel = new JPanel(new BorderLayout());
        finalizarPanel.setBackground(Color.WHITE);
        finalizarPanel.setBorder(BorderFactory.createEmptyBorder(4, 8, 8, 8));
        finalizarPanel.add(btnFinalizar, BorderLayout.CENTER);

        JPanel rodape = new JPanel(new BorderLayout(0, 0));
        rodape.setBackground(Color.WHITE);
        rodape.add(totaisPanel, BorderLayout.NORTH);
        rodape.add(botoesPanel, BorderLayout.CENTER);
        rodape.add(finalizarPanel, BorderLayout.SOUTH);

        sincronizarCarrinho();

        painelCarrinho.add(new JScrollPane(tabelaCarrinho), BorderLayout.CENTER);
        painelCarrinho.add(rodape, BorderLayout.SOUTH);
        return painelCarrinho;
    }

    /** Sincroniza modelCarrinho, totais e título do border com o estado atual do CarrinhoManager. */
    public void sincronizarCarrinho() {
        modelCarrinho.setRowCount(0);

        if (!carrinho.estaVazio()) {
            for (CarrinhoManager.ItemCarrinho item : carrinho.getItens()) {
                modelCarrinho.addRow(new Object[]{
                        item.getProduto().getNome(),
                        item.getQuantidade(),
                        moedaBR.format(item.calcularSubtotal())
                });
            }
        }

        // Atualiza totais
        BigDecimal subtotal = carrinho.estaVazio() ? BigDecimal.ZERO : carrinho.calcularSubtotal();
        BigDecimal taxa = carrinho.estaVazio() ? BigDecimal.ZERO : carrinho.getTaxaEntrega();
        if (lblSubtotalValor != null) lblSubtotalValor.setText(moedaBR.format(subtotal));
        if (lblTaxaValor != null) lblTaxaValor.setText(moedaBR.format(taxa));

        // Atualiza título do border com contagem de itens distintos
        if (borderCarrinho != null && painelCarrinho != null) {
            int qtd = carrinho.estaVazio() ? 0 : carrinho.getItens().size();
            borderCarrinho.setTitle("Meu Carrinho" + (qtd > 0 ? " (" + qtd + ")" : ""));
            painelCarrinho.repaint();
        }
    }

    // ── Getters ────────────────────────────────────────────────────
    public Restaurante getRestauranteSelecionado() {
        return restauranteSelecionado;
    }

    // ── Helpers ────────────────────────────────────────────────────
    private void configurarHeader(JTable tabela) {
        JTableHeader th = tabela.getTableHeader();
        th.setFont(AppFonts.TITULO);
        th.setBackground(new Color(245, 245, 245));
        th.setForeground(Color.DARK_GRAY);
        th.setReorderingAllowed(false);
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

    private JButton criarBotaoSecundario(String texto) {
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

