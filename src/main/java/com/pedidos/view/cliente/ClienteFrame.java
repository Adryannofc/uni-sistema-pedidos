package com.pedidos.view.cliente;

import com.pedidos.application.service.*;
import com.pedidos.domain.entities.*;
import com.pedidos.domain.enums.StatusPedido;
import com.pedidos.view.util.AppColors;
import com.pedidos.view.util.AppFonts;
import com.pedidos.view.util.base.BaseFrame;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public class ClienteFrame extends BaseFrame {

    // ─── Entidades ────────────────────────────────────────────────
    private final Usuario usuario;
    private final Cliente cliente;

    // ─── Services ─────────────────────────────────────────────────
    private final ClienteService     clienteService;
    private final EnderecoService    enderecoService;
    private final RestauranteService restauranteService;
    private final ProdutoService     produtoService;
    private final PedidoService      pedidoService;

    // ─── Sessão de carrinho ───────────────────────────────────────
    private final CarrinhoManager carrinho;

    // ─── Formatação ───────────────────────────────────────────────
    private final NumberFormat       moedaBR = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ─── Componentes dinâmicos ────────────────────────────────────
    private JTabbedPane      tabbedPane;
    private DefaultTableModel modelCarrinho;
    private DefaultTableModel modelRestaurantes;
    private DefaultTableModel modelMeusPedidos;
    private Restaurante       restauranteSelecionado;

    // Status bar — atualizada após ações
    private JLabel lblStatusPedidos;
    private JLabel lblStatusEndereco;

    // ═════════════════════════════════════════════════════════════
    // CONSTRUTOR
    // ═════════════════════════════════════════════════════════════
    public ClienteFrame(Usuario usuario,
                        Cliente cliente,
                        ClienteService clienteService,
                        EnderecoService enderecoService,
                        RestauranteService restauranteService,
                        ProdutoService produtoService,
                        PedidoService pedidoService,
                        CarrinhoManager carrinho) {
        super("Sistema Delivery | Cliente", 1200, 750);
        this.usuario            = usuario;
        this.cliente            = cliente;
        this.clienteService     = clienteService;
        this.enderecoService    = enderecoService;
        this.restauranteService = restauranteService;
        this.produtoService     = produtoService;
        this.pedidoService      = pedidoService;
        this.carrinho           = carrinho;
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

        int pedidosAtivos = pedidoService.listarPorCliente(cliente.getId()).size();

        String infoEndereco = cliente.getEnderecoPadrao()
                .map(e -> e.getRua() + ", " + e.getNumero() + " - " + e.getCidade())
                .orElse("Nenhum endereço cadastrado");

        lblStatusPedidos  = new JLabel(pedidosAtivos + " pedido(s) ativo(s)");
        lblStatusEndereco = new JLabel("📍 " + infoEndereco);

        for (JLabel l : new JLabel[]{
                new JLabel(cliente.getNome() + " | Cliente"),
                new JLabel("|"),
                lblStatusPedidos,
                new JLabel("|"),
                lblStatusEndereco
        }) {
            l.setFont(AppFonts.STATUS.deriveFont(11f));
            l.setForeground(Color.DARK_GRAY);
            bar.add(l);
        }
        return bar;
    }

    /** Atualiza os labels da status bar sem recriar o painel. */
    private void atualizarStatusBar() {
        int total = pedidoService.listarPorCliente(cliente.getId()).size();
        lblStatusPedidos.setText(total + " pedido(s) ativo(s)");
        lblStatusEndereco.setText("📍 " + cliente.getEnderecoPadrao()
                .map(e -> e.getRua() + ", " + e.getNumero() + " - " + e.getCidade())
                .orElse("Nenhum endereço cadastrado"));
    }

    // ═════════════════════════════════════════════════════════════
    // ABAS PRINCIPAIS
    // ═════════════════════════════════════════════════════════════
    private JTabbedPane criarAbas() {
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(AppFonts.STATUS);
        tabbedPane.setBackground(Color.WHITE);

        tabbedPane.addTab("Fazer Pedido", criarPainelFazerPedido());
        tabbedPane.addTab("Checkout",     criarPainelCheckout());
        tabbedPane.addTab("Meus Pedidos", criarPainelMeusPedidos());
        tabbedPane.addTab("Perfil",       criarPainelPerfil());

        atualizarTituloFazerPedido();
        tabbedPane.setSelectedIndex(0);
        return tabbedPane;
    }

    private void atualizarTituloFazerPedido() {
        int qtd = !carrinho.estaVazio() ? carrinho.getItens().size() : 0;
        tabbedPane.setTitleAt(0, qtd > 0 ? "Fazer Pedido (" + qtd + ")" : "Fazer Pedido");
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

    // ── Lista de Restaurantes ─────────────────────────────────────
    private JPanel criarListaRestaurantes() {
        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.setBackground(Color.WHITE);

        JLabel titulo = new JLabel("Restaurantes disponíveis");
        titulo.setFont(AppFonts.STATUS.deriveFont(Font.BOLD, 13f));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        String[] colunas = {"Restaurante", "Categoria", "★"};
        modelRestaurantes = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable tabela = new JTable(modelRestaurantes);
        tabela.setFont(AppFonts.STATUS);
        tabela.setRowHeight(30);
        tabela.setGridColor(new Color(220, 220, 220));
        tabela.setShowGrid(true);
        tabela.setSelectionBackground(new Color(220, 235, 255));
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        configurarHeader(tabela);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(340);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(40);

        // Renderer estrela favorito
        tabela.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean sel, boolean foc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, value, sel, foc, row, col);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setForeground("★".equals(value) ? new Color(255, 160, 0) : Color.LIGHT_GRAY);
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

                if (col == 2) {
                    // Toggle favorito — igual ao MenuCliente.acaoEscolherRestaurante opção 2
                    clienteService.favoritar(cliente, r);
                    boolean agora = cliente.getFavoritos().contains(r);
                    modelRestaurantes.setValueAt(agora ? "★" : " ", row, 2);
                    return;
                }

                // Clique simples em qualquer outra coluna → seleciona restaurante e abre cardápio
                // (equivalente ao duplo-clique anterior, simplificado conforme MenuCliente)
                if (e.getClickCount() >= 1 && col != 2) {
                    if (!carrinho.estaVazio() &&
                            !carrinho.getRestauranteId().equals(r.getId())) {
                        int op = JOptionPane.showConfirmDialog(ClienteFrame.this,
                                "Você já tem itens de outro restaurante no carrinho.\n" +
                                        "Deseja esvaziá-lo e selecionar \"" + r.getNome() + "\"?",
                                "Trocar restaurante", JOptionPane.YES_NO_OPTION);
                        if (op != JOptionPane.YES_OPTION) return;
                        carrinho.esvaziar();
                    }

                    restauranteSelecionado = r;
                    carrinho.iniciar(cliente.getId(), r.getId(), new BigDecimal("5.00"));

                    // Abre o CardapioDialog — equivalente ao acaoNavegarCardapio do MenuCliente
                    CardapioDialog dialog = new CardapioDialog(
                            ClienteFrame.this, produtoService, restauranteSelecionado, carrinho);
                    dialog.setVisible(true);

                    // Após fechar o cardápio, atualiza carrinho e aba
                    sincronizarCarrinho();
                    atualizarTituloFazerPedido();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JLabel dica = new JLabel("💡 Clique para ver o cardápio · Clique em ★ para favoritar/desfavoritar");
        dica.setFont(AppFonts.STATUS.deriveFont(Font.ITALIC, 11f));
        dica.setForeground(Color.GRAY);
        dica.setBorder(BorderFactory.createEmptyBorder(4, 2, 0, 0));

        painel.add(titulo, BorderLayout.NORTH);
        painel.add(scroll,  BorderLayout.CENTER);
        painel.add(dica,    BorderLayout.SOUTH);

        carregarRestaurantes();
        return painel;
    }

    /** Carrega restaurantes ativos no model — equivalente ao acaoEscolherRestaurante do MenuCliente. */
    private void carregarRestaurantes() {
        modelRestaurantes.setRowCount(0);
        for (Restaurante r : restauranteService.buscarRestaurantesAtivos()) {
            // getCategoriaGlobal().getNome() conforme MenuCliente
            String categoria = r.getCategoriaGlobal() != null
                    ? r.getCategoriaGlobal().getNome() : "N/A";
            String estrela = cliente.getFavoritos().contains(r) ? "★" : " ";
            modelRestaurantes.addRow(new Object[]{r.getNome(), categoria, estrela});
        }
    }

    // ── Painel Carrinho (direita) ─────────────────────────────────
    private JPanel criarPainelCarrinho() {
        JPanel painel = new JPanel(new BorderLayout(0, 6));
        painel.setPreferredSize(new Dimension(320, 0));
        painel.setBackground(Color.WHITE);
        painel.setBorder(titledBorder("🛒 Meu Carrinho"));

        String[] colunas = {"Produto", "Qtd", "Subtotal"};
        modelCarrinho = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable tabela = new JTable(modelCarrinho);
        tabela.setFont(AppFonts.STATUS);
        tabela.setRowHeight(28);
        tabela.setGridColor(new Color(220, 220, 220));
        tabela.setShowGrid(true);
        tabela.setSelectionBackground(new Color(220, 235, 255));

        configurarHeader(tabela);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(130);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(35);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(90);

        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        tabela.getColumnModel().getColumn(1).setCellRenderer(centro);

        // Duplo-clique remove item — equivalente ao acaoVerCarrinho opção 1 do MenuCliente
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tabela.rowAtPoint(e.getPoint());
                    if (row < 0 || carrinho.estaVazio()) return;
                    String nome = (String) modelCarrinho.getValueAt(row, 0);
                    carrinho.getItens().stream()
                            .filter(it -> it.getProduto().getNome().equals(nome))
                            .findFirst()
                            .ifPresent(it -> {
                                carrinho.removerItem(it.getProduto().getId());
                                sincronizarCarrinho();
                                atualizarTituloFazerPedido();
                            });
                }
            }
        });

        // Botões — equivalentes às opções do acaoVerCarrinho do MenuCliente
        JButton btnRemover = criarBotaoSecundario("Remover item");
        btnRemover.addActionListener(e -> {
            int row = tabela.getSelectedRow();
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
                        atualizarTituloFazerPedido();
                    });
        });

        JButton btnLimpar = criarBotaoSecundario("Limpar");
        btnLimpar.addActionListener(e -> {
            if (carrinho.estaVazio()) return;
            carrinho.esvaziar(); // equivalente ao acaoVerCarrinho opção 2
            sincronizarCarrinho();
            atualizarTituloFazerPedido();
        });

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 6, 0));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        btnPanel.add(btnRemover);
        btnPanel.add(btnLimpar);

        sincronizarCarrinho();

        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);
        painel.add(btnPanel,                BorderLayout.SOUTH);
        return painel;
    }

    /** Sincroniza modelCarrinho com CarrinhoManager — equivalente ao acaoVerCarrinho do MenuCliente. */
    private void sincronizarCarrinho() {
        modelCarrinho.setRowCount(0);
        if (carrinho.estaVazio()) return;
        for (CarrinhoManager.ItemCarrinho item : carrinho.getItens()) {
            modelCarrinho.addRow(new Object[]{
                    item.getProduto().getNome(),
                    item.getQuantidade(),
                    moedaBR.format(item.calcularSubtotal())
            });
        }
    }

    // ═════════════════════════════════════════════════════════════
    // ABA — CHECKOUT
    // ═════════════════════════════════════════════════════════════
    private JPanel criarPainelCheckout() {
        JPanel painel = new JPanel(new BorderLayout(0, 12));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        painel.add(criarResumoCheckout(), BorderLayout.CENTER);
        painel.add(criarRodapeCheckout(), BorderLayout.SOUTH);
        return painel;
    }

    private JPanel criarResumoCheckout() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(Color.WHITE);
        painel.setBorder(titledBorder("Resumo do Pedido"));

        String[] colunas = {"Produto", "Qtd", "Preço unit.", "Subtotal"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        // Itens do CarrinhoManager — mesmo conteúdo exibido no acaoCheckout do MenuCliente
        if (!carrinho.estaVazio()) {
            for (CarrinhoManager.ItemCarrinho item : carrinho.getItens()) {
                model.addRow(new Object[]{
                        item.getProduto().getNome(),
                        item.getQuantidade(),
                        moedaBR.format(item.getProduto().getPreco()),
                        moedaBR.format(item.calcularSubtotal())
                });
            }
        }

        JTable tabela = new JTable(model);
        tabela.setFont(AppFonts.STATUS);
        tabela.setRowHeight(30);
        tabela.setGridColor(new Color(220, 220, 220));
        tabela.setShowGrid(true);
        tabela.setSelectionBackground(new Color(220, 235, 255));

        configurarHeader(tabela);

        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 1; i <= 3; i++) tabela.getColumnModel().getColumn(i).setCellRenderer(centro);

        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);
        painel.add(criarTotaisCheckout(),    BorderLayout.SOUTH);
        return painel;
    }

    private JPanel criarTotaisCheckout() {
        // Usa os métodos do CarrinhoManager — equivalente ao acaoCheckout do MenuCliente
        BigDecimal subtotal  = carrinho.calcularSubtotal();
        BigDecimal taxa      = carrinho.getTaxaEntrega();
        BigDecimal total     = carrinho.calcularTotal();

        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createEmptyBorder(10, 0, 8, 16));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 12, 3, 12);

        adicionarLinhaTotal(painel, gbc, "Subtotal:",        moedaBR.format(subtotal), false, 0);
        adicionarLinhaTotal(painel, gbc, "Taxa de entrega:", moedaBR.format(taxa),     false, 1);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JSeparator sep = new JSeparator();
        sep.setPreferredSize(new Dimension(240, 1));
        painel.add(sep, gbc);
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;

        adicionarLinhaTotal(painel, gbc, "TOTAL:", moedaBR.format(total), true, 3);

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        wrapper.setBackground(Color.WHITE);
        wrapper.add(painel);
        return wrapper;
    }

    private void adicionarLinhaTotal(JPanel painel, GridBagConstraints gbc,
                                     String label, String valor,
                                     boolean negrito, int linha) {
        Font fonte = negrito ? AppFonts.STATUS.deriveFont(Font.BOLD, 14f) : AppFonts.STATUS;

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

    private JPanel criarRodapeCheckout() {
        JPanel rodape = new JPanel(new BorderLayout(0, 10));
        rodape.setBackground(Color.WHITE);

        // Endereço padrão — mesmo que o MenuCliente usa em acaoCheckout
        String enderecoTexto = cliente.getEnderecoPadrao()
                .map(e -> e.getRua() + ", " + e.getNumero() + " - " +
                        e.getBairro() + ", " + e.getCidade() + " - " + e.getEstado())
                .orElse("⚠ Nenhum endereço cadastrado. Acesse Perfil > Endereço.");

        JPanel enderecoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        enderecoPanel.setBackground(Color.WHITE);
        enderecoPanel.setBorder(titledBorder("Endereço de Entrega"));

        JLabel icone = new JLabel("📍");
        icone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        JLabel endLabel = new JLabel(enderecoTexto);
        endLabel.setFont(AppFonts.STATUS);

        enderecoPanel.add(icone);
        enderecoPanel.add(endLabel);

        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        botoesPanel.setBackground(Color.WHITE);

        JButton btnCancelar = criarBotaoSecundario("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(110, 36));
        btnCancelar.addActionListener(e -> tabbedPane.setSelectedIndex(0));

        JButton btnConfirmar = criarBotaoPrimario("Confirmar Pedido", 165, 36);
        btnConfirmar.addActionListener(e -> confirmarPedido());

        botoesPanel.add(btnCancelar);
        botoesPanel.add(btnConfirmar);

        rodape.add(enderecoPanel, BorderLayout.CENTER);
        rodape.add(botoesPanel,   BorderLayout.SOUTH);
        return rodape;
    }

    /**
     * Finaliza o pedido — lógica espelhada do acaoCheckout do MenuCliente:
     * valida carrinho → valida endereço → cria Pedido → esvazia carrinho → atualiza UI.
     */
    private void confirmarPedido() {
        if (carrinho.estaVazio()) {
            JOptionPane.showMessageDialog(this,
                    "Carrinho vazio! Adicione itens antes de confirmar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (restauranteSelecionado == null) {
            JOptionPane.showMessageDialog(this,
                    "Nenhum restaurante selecionado. Volte à aba Fazer Pedido.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            tabbedPane.setSelectedIndex(0);
            return;
        }

        Optional<Endereco> enderecoPadrao = cliente.getEnderecoPadrao();
        if (enderecoPadrao.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Configure um endereço de entrega antes de confirmar.\n" +
                            "Acesse: Perfil > Endereço.",
                    "Endereço obrigatório", JOptionPane.WARNING_MESSAGE);
            tabbedPane.setSelectedIndex(3);
            return;
        }

        try {
            // Converte CarrinhoManager → Carrinho (entidade de domínio)
            // que é o que PedidoService.criarPedido espera
            Carrinho carrinhoDominio = new Carrinho(cliente.getId(), restauranteSelecionado.getId());
            for (CarrinhoManager.ItemCarrinho item : carrinho.getItens()) {
                carrinhoDominio.adicionarItem(
                        item.getProduto().getId(),
                        item.getProduto().getNome(),
                        item.getQuantidade(),
                        item.getProduto().getPreco()
                );
            }

            // Código de confirmação de entrega — mesmo padrão do MenuCliente.acaoCheckout
            String codigoConfirmacao = cliente.getCpf() != null
                    ? cliente.getCpf().replaceAll("[^0-9]", "").substring(0, 4)
                    : UUID.randomUUID().toString().substring(0, 6).toUpperCase();

            Pedido pedido = pedidoService.criarPedido(
                    cliente,
                    restauranteSelecionado,
                    carrinhoDominio,
                    enderecoPadrao.get(),
                    codigoConfirmacao
            );

            // Limpa sessão local
            carrinho.esvaziar();
            restauranteSelecionado = null;

            // Atualiza UI
            sincronizarCarrinho();
            atualizarTituloFazerPedido();
            carregarMeusPedidos();
            atualizarStatusBar();

            JOptionPane.showMessageDialog(this,
                    "✅ Pedido confirmado com sucesso!\n" +
                            "ID: " + pedido.getId().toUpperCase() + "\n" +
                            "Total: " + moedaBR.format(pedido.getTotal()) + "\n" +
                            "Código de confirmação de entrega: [ " + codigoConfirmacao + " ]",
                    "Pedido realizado", JOptionPane.INFORMATION_MESSAGE);

            tabbedPane.setSelectedIndex(2); // vai para Meus Pedidos

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao confirmar pedido:\n" + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    // ═════════════════════════════════════════════════════════════
    // ABA — MEUS PEDIDOS
    // ═════════════════════════════════════════════════════════════
    private JPanel criarPainelMeusPedidos() {
        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        String[] colunas = {"ID", "Restaurante", "Data", "Status", "Total"};
        modelMeusPedidos = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable tabela = new JTable(modelMeusPedidos);
        tabela.setFont(AppFonts.STATUS);
        tabela.setRowHeight(30);
        tabela.setGridColor(new Color(220, 220, 220));
        tabela.setShowGrid(true);
        tabela.setSelectionBackground(new Color(220, 235, 255));

        configurarHeader(tabela);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(90);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(180);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(130);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(160);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(90);

        // Badge de status — cores espelhadas dos status do MenuCliente.exibirListaPedidos
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
                    case "AGUARD. CONFIRM." -> { badge.setBackground(new Color(255, 220, 100)); badge.setForeground(new Color(120, 80,  0));   }
                    case "CONFIRMADO"       -> { badge.setBackground(new Color(180, 220, 255)); badge.setForeground(new Color(0,   60, 140));  }
                    case "EM PREPARO"       -> { badge.setBackground(new Color(255, 200, 120)); badge.setForeground(new Color(140, 60,  0));   }
                    case "SAIU P/ ENTREGA"  -> { badge.setBackground(new Color(200, 180, 255)); badge.setForeground(new Color(60,  0,  140));  }
                    case "ENTREGUE"         -> { badge.setBackground(new Color(180, 240, 190)); badge.setForeground(new Color(0,  100,  30));  }
                    case "CANCELADO"        -> { badge.setBackground(new Color(255, 190, 190)); badge.setForeground(new Color(150,  0,   0));  }
                    default                 -> { badge.setBackground(new Color(220, 220, 220)); badge.setForeground(Color.DARK_GRAY);          }
                }
                cell.add(badge);
                return cell;
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        painel.add(scroll, BorderLayout.CENTER);

        carregarMeusPedidos();
        return painel;
    }

    /** Recarrega a tabela de Meus Pedidos — equivalente ao exibirListaPedidos do MenuCliente. */
    private void carregarMeusPedidos() {
        modelMeusPedidos.setRowCount(0);
        for (Pedido p : pedidoService.listarPorCliente(cliente.getId())) {
            modelMeusPedidos.addRow(new Object[]{
                    p.getId(),
                    p.getRestaurante() != null ? p.getRestaurante().getNome() : "—",
                    p.getDataPedido()  != null ? p.getDataPedido().format(FMT_DATA) : "—",
                    traduzirStatus(p.getStatus()),
                    moedaBR.format(p.getTotal() != null ? p.getTotal() : BigDecimal.ZERO)
            });
        }
    }

    /** Traduz o enum StatusPedido para o texto exibido no badge — espelha MenuCliente.exibirListaPedidos. */
    private String traduzirStatus(StatusPedido status) {
        if (status == null) return "—";
        return switch (status) {
            case AGUARDANDO_CONFIRMACAO -> "AGUARD. CONFIRM.";
            case CONFIRMADO             -> "CONFIRMADO";
            case EM_PREPARO             -> "EM PREPARO";
            case SAIU_PARA_ENTREGA      -> "SAIU P/ ENTREGA";
            case ENTREGUE               -> "ENTREGUE";
            case CANCELADO              -> "CANCELADO";
        };
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
    // SUB-ABA — DADOS  (espelha acaoEditarNome/Email/Cpf/Telefone do MenuCliente)
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

        String[] labels  = {"Nome:", "E-mail:", "CPF:", "Telefone:"};
        String[] valores = {
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getCpf()      != null ? cliente.getCpf()      : "",
                cliente.getTelefone() != null ? cliente.getTelefone() : ""
        };
        JTextField[] fields = new JTextField[labels.length];

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(AppFonts.STATUS);
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
                // Cada campo chama seu service — igual ao menuPerfil do MenuCliente
                clienteService.editarNome(cliente,     fields[0].getText().trim());
                clienteService.editarEmail(cliente,    fields[1].getText().trim());
                clienteService.editarCpf(cliente,      fields[2].getText().trim());
                clienteService.editarTelefone(cliente, fields[3].getText().trim());
                atualizarStatusBar();
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
    // SUB-ABA — ENDEREÇO  (espelha menuGerenciarEnderecos do MenuCliente)
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

        String[] labels  = {"Rua / Logradouro:", "Número:", "Bairro:", "Cidade:", "Estado (UF):", "CEP:"};
        String[] valores = {
                endAtual.map(Endereco::getRua)    .orElse(""),
                endAtual.map(Endereco::getNumero) .orElse(""),
                endAtual.map(Endereco::getBairro) .orElse(""),
                endAtual.map(Endereco::getCidade) .orElse(""),
                endAtual.map(Endereco::getEstado) .orElse(""),
                endAtual.map(Endereco::getCep)    .orElse("")
        };
        JTextField[] fields = new JTextField[labels.length];

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(AppFonts.STATUS);
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
                // Usa clienteService.salvarEndereco — igual ao case "1" do menuGerenciarEnderecos
                clienteService.salvarEndereco(
                        cliente,
                        fields[0].getText().trim(), // rua
                        fields[1].getText().trim(), // numero
                        fields[2].getText().trim(), // bairro
                        fields[3].getText().trim(), // cidade
                        fields[4].getText().trim(), // estado
                        fields[5].getText().trim()  // cep
                );
                atualizarStatusBar();
                JOptionPane.showMessageDialog(this, "Endereço salvo com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
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
    // SUB-ABA — FAVORITOS  (espelha menuFavoritos do MenuCliente)
    // ─────────────────────────────────────────────────────────────
    private JPanel criarSubAbaFavoritos() {
        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel titulo = new JLabel("Restaurantes favoritos");
        titulo.setFont(AppFonts.STATUS.deriveFont(Font.BOLD, 13f));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        String[] colunas = {"Restaurante", "Categoria", "Status", "★ Remover"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        // Carrega favoritos — mesmo dado de cliente.getFavoritos() do MenuCliente
        List<Restaurante> favs = cliente.getFavoritos();
        for (Restaurante r : favs) {
            String categoria = r.getCategoriaGlobal() != null
                    ? r.getCategoriaGlobal().getNome() : "N/A";
            model.addRow(new Object[]{r.getNome(), categoria,
                    r.isStatusAtivo() ? "Ativo" : "Inativo", "★"});
        }

        JTable tabela = new JTable(model);
        tabela.setFont(AppFonts.STATUS);
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

        // Clique na ★ remove favorito — equivalente ao menuFavoritos do MenuCliente
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = tabela.columnAtPoint(e.getPoint());
                int row = tabela.rowAtPoint(e.getPoint());
                if (col == 3 && row >= 0 && row < favs.size()) {
                    Restaurante r = favs.get(row);
                    int confirm = JOptionPane.showConfirmDialog(ClienteFrame.this,
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
        info.setFont(AppFonts.STATUS.deriveFont(Font.ITALIC, 11f));
        info.setForeground(Color.GRAY);
        info.setBorder(BorderFactory.createEmptyBorder(6, 2, 0, 0));

        painel.add(titulo, BorderLayout.NORTH);
        painel.add(scroll,  BorderLayout.CENTER);
        painel.add(info,    BorderLayout.SOUTH);
        return painel;
    }

    // ─────────────────────────────────────────────────────────────
    // SUB-ABA — SENHA  (espelha acaoAlterarSenha do MenuCliente)
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

        gbc.gridx = 1; gbc.gridy = labels.length + 1;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.insets = new Insets(4, 10, 8, 10);
        JLabel dica = new JLabel("💡 Use letras, números e caracteres especiais para maior segurança.");
        dica.setFont(AppFonts.STATUS.deriveFont(Font.ITALIC, 11f));
        dica.setForeground(Color.GRAY);
        form.add(dica, gbc);

        gbc.gridx = 1; gbc.gridy = labels.length + 2;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 6, 10);

        JButton btnSalvar = criarBotaoPrimario("Alterar Senha", 130, 30);
        btnSalvar.addActionListener(e -> {
            String atual    = new String(fields[0].getPassword());
            String nova     = new String(fields[1].getPassword());
            String confirma = new String(fields[2].getPassword());
            try {
                // Delega toda validação ao ClienteService.alterarSenha —
                // mesmo que acaoAlterarSenha do MenuCliente
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

    // ═════════════════════════════════════════════════════════════
    // HELPERS
    // ═════════════════════════════════════════════════════════════

    private void configurarHeader(JTable tabela) {
        JTableHeader th = tabela.getTableHeader();
        th.setFont(AppFonts.STATUS.deriveFont(Font.BOLD));
        th.setBackground(new Color(245, 245, 245));
        th.setForeground(Color.DARK_GRAY);
        th.setReorderingAllowed(false);
    }

    private JTextField criarCampoTexto(String valor) {
        JTextField f = new JTextField(valor);
        f.setFont(AppFonts.STATUS);
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
        btn.setFont(AppFonts.STATUS.deriveFont(Font.BOLD));
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
        btn.setFont(AppFonts.STATUS);
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
                AppFonts.STATUS.deriveFont(Font.BOLD)
        );
    }
}