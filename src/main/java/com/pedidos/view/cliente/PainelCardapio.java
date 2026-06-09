package com.pedidos.view.cliente;

import com.pedidos.model.service.ProdutoService;
import com.pedidos.model.entity.CategoriaCardapio;
import com.pedidos.model.entity.Produto;
import com.pedidos.model.entity.Restaurante;
import com.pedidos.view.util.AppColors;
import com.pedidos.view.util.AppFonts;
import com.pedidos.view.util.session.CarrinhoManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Painel inline de cardápio — substitui o CardapioDialog modal.
 * É embutido no "Fazer Pedido" via CardLayout, sem abrir nova janela.
 */
public class PainelCardapio extends JPanel {

    private static final NumberFormat FMT_MOEDA =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    private final ProdutoService   produtoService;
    private final CarrinhoManager  carrinho;
    private final Runnable         onVoltar;
    private final Runnable         onAdicionado;

    private JLabel                          labelTitulo;
    private DefaultTableModel               modelo;
    private JTable                          tabela;
    private JTextField                      campoQtd;
    private DefaultListModel<CategoriaCardapio> categoriaModel;
    private JList<CategoriaCardapio>        listaCategorias;

    private List<Produto> todosOsProdutos  = new ArrayList<>();
    private List<Produto> produtosVisiveis = new ArrayList<>();

    public PainelCardapio(ProdutoService produtoService, CarrinhoManager carrinho,
                          Runnable onVoltar, Runnable onAdicionado) {
        super(new BorderLayout(0, 0));
        this.produtoService = produtoService;
        this.carrinho       = carrinho;
        this.onVoltar       = onVoltar;
        this.onAdicionado   = onAdicionado;
        setBackground(Color.WHITE);
        construir();
    }

    // ─────────────────────────── build ───────────────────────────────────────

    private void construir() {
        add(criarHeader(),  BorderLayout.NORTH);
        add(criarCentro(),  BorderLayout.CENTER);
        add(criarRodape(),  BorderLayout.SOUTH);
    }

    private JPanel criarHeader() {
        JButton btnVoltar = new JButton("<- Voltar");
        btnVoltar.setFont(AppFonts.BOTAO);
        btnVoltar.setFocusPainted(false);
        btnVoltar.addActionListener(e -> onVoltar.run());

        labelTitulo = new JLabel("Cardapio");
        labelTitulo.setFont(AppFonts.TITULO);
        labelTitulo.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 6));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        header.add(btnVoltar);
        header.add(labelTitulo);
        return header;
    }

    private JSplitPane criarCentro() {
        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, criarPainelCategorias(), criarPainelProdutos());
        split.setDividerLocation(180);
        split.setDividerSize(1);
        split.setBorder(null);
        split.setBackground(Color.WHITE);
        return split;
    }

    private JPanel criarPainelCategorias() {
        JPanel painel = new JPanel(new BorderLayout(0, 6));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 8));

        JLabel titulo = new JLabel("Categorias");
        titulo.setFont(AppFonts.STATUS.deriveFont(Font.BOLD, 12f));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));

        categoriaModel   = new DefaultListModel<>();
        listaCategorias  = new JList<>(categoriaModel);
        listaCategorias.setFont(AppFonts.STATUS);
        listaCategorias.setSelectionBackground(AppColors.AZUL_PRIMARIO);
        listaCategorias.setSelectionForeground(Color.WHITE);
        listaCategorias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaCategorias.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                CategoriaCardapio sel = listaCategorias.getSelectedValue();
                if (sel != null) filtrarPorCategoria(sel);
            }
        });

        JScrollPane scroll = new JScrollPane(listaCategorias);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        painel.add(titulo, BorderLayout.NORTH);
        painel.add(scroll, BorderLayout.CENTER);
        return painel;
    }

    private JScrollPane criarPainelProdutos() {
        modelo = new DefaultTableModel(new String[]{"Nome", "Descricao", "Preco"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabela = new JTable(modelo);
        tabela.setFont(AppFonts.LABEL);
        tabela.getTableHeader().setFont(AppFonts.TITULO);
        tabela.getTableHeader().setBackground(new Color(245, 245, 245));
        tabela.getTableHeader().setForeground(Color.DARK_GRAY);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.setRowHeight(30);
        tabela.setGridColor(new Color(220, 220, 220));
        tabela.setShowGrid(true);
        tabela.setSelectionBackground(new Color(220, 235, 255));
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setFillsViewportHeight(true);

        tabela.getColumnModel().getColumn(0).setPreferredWidth(160);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(400);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(100);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        return scroll;
    }

    private JPanel criarRodape() {
        JButton btnMenos = new JButton("-");
        JButton btnMais  = new JButton("+");
        btnMenos.setFont(AppFonts.BOTAO);
        btnMais.setFont(AppFonts.BOTAO);
        btnMenos.setPreferredSize(new Dimension(32, 28));
        btnMais.setPreferredSize(new Dimension(32, 28));
        btnMenos.setFocusPainted(false);
        btnMais.setFocusPainted(false);

        campoQtd = new JTextField("1", 3);
        campoQtd.setFont(AppFonts.CAMPO);
        campoQtd.setHorizontalAlignment(SwingConstants.CENTER);
        campoQtd.setPreferredSize(new Dimension(40, 28));

        btnMenos.addActionListener(e -> {
            int qtd = lerQuantidade();
            if (qtd > 1) campoQtd.setText(String.valueOf(qtd - 1));
        });
        btnMais.addActionListener(e -> {
            int qtd = lerQuantidade();
            campoQtd.setText(String.valueOf(qtd + 1));
        });

        JLabel lblQtd = new JLabel("Quantidade:");
        lblQtd.setFont(AppFonts.LABEL);

        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        esquerda.setBackground(Color.WHITE);
        esquerda.add(lblQtd);
        esquerda.add(btnMenos);
        esquerda.add(campoQtd);
        esquerda.add(btnMais);

        JButton btnAdicionar = new JButton("+ Adicionar ao Carrinho");
        btnAdicionar.setFont(AppFonts.BOTAO);
        btnAdicionar.setBackground(AppColors.AZUL_PRIMARIO);
        btnAdicionar.setForeground(Color.WHITE);
        btnAdicionar.setOpaque(true);
        btnAdicionar.setBorderPainted(false);
        btnAdicionar.setFocusPainted(false);
        btnAdicionar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAdicionar.setPreferredSize(new Dimension(200, 32));
        btnAdicionar.addActionListener(e -> adicionarAoCarrinho());

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        direita.setBackground(Color.WHITE);
        direita.add(btnAdicionar);

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setBackground(Color.WHITE);
        rodape.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        rodape.add(esquerda, BorderLayout.WEST);
        rodape.add(direita,  BorderLayout.EAST);
        return rodape;
    }

    // ─────────────────────────── API pública ─────────────────────────────────

    /** Carrega o cardápio do restaurante e atualiza categorias e título. */
    public void configurar(Restaurante restaurante) {
        labelTitulo.setText("Cardapio - " + restaurante.getNome());
        campoQtd.setText("1");

        todosOsProdutos = produtoService.listarAtivosPorRestaurante(restaurante.getId());

        categoriaModel.clear();
        List<CategoriaCardapio> cats = restaurante.getCategorias();
        if (cats != null) {
            cats.forEach(categoriaModel::addElement);
        }

        if (categoriaModel.getSize() > 0) {
            listaCategorias.setSelectedIndex(0);
        } else {
            produtosVisiveis = new ArrayList<>(todosOsProdutos);
            popularTabela(produtosVisiveis);
        }
    }

    // ─────────────────────────── helpers ─────────────────────────────────────

    private void filtrarPorCategoria(CategoriaCardapio cat) {
        produtosVisiveis = todosOsProdutos.stream()
                .filter(p -> cat.getId().equals(p.getCategoriaCardapioId()))
                .collect(Collectors.toList());
        popularTabela(produtosVisiveis);
    }

    private void popularTabela(List<Produto> produtos) {
        modelo.setRowCount(0);
        for (Produto p : produtos) {
            modelo.addRow(new Object[]{
                    p.getNome(),
                    p.getDescricao(),
                    FMT_MOEDA.format(p.getPreco())
            });
        }
    }

    private int lerQuantidade() {
        try {
            int qtd = Integer.parseInt(campoQtd.getText().trim());
            return Math.max(1, qtd);
        } catch (NumberFormatException ex) {
            campoQtd.setText("1");
            return 1;
        }
    }

    private void adicionarAoCarrinho() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um produto na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Produto produto = produtosVisiveis.get(row);
        int qtd = lerQuantidade();

        try {
            carrinho.adicionarItem(produto, qtd);
            onAdicionado.run();
            campoQtd.setText("1");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
