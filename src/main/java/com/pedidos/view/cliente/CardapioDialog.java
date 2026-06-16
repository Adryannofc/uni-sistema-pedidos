package com.pedidos.view.cliente;

import com.pedidos.controller.CarrinhoController;
import com.pedidos.controller.ProdutoController;
import com.pedidos.model.entity.CategoriaCardapio;
import com.pedidos.model.entity.Produto;
import com.pedidos.model.entity.Restaurante;
import com.pedidos.view.util.AppColors;
import com.pedidos.view.util.AppFonts;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * JDialog que exibe o cardápio de um restaurante.
 * O usuário pode selecionar um produto e adicionar ao carrinho.
 */
public class CardapioDialog extends JDialog {

    private final ProdutoController produtoController;
    private final Restaurante restaurante;
    private final CarrinhoController carrinhoController;
    private final DefaultTableModel produtosModel;
    private JTable tabelaProdutos;

    public CardapioDialog(JFrame owner, ProdutoController produtoController,
                          Restaurante restaurante, CarrinhoController carrinhoController) {
        super(owner, "Cardápio — " + restaurante.getNome(), true);
        this.produtoController = produtoController;
        this.restaurante = restaurante;
        this.carrinhoController = carrinhoController;

        setSize(700, 500);
        setLocationRelativeTo(owner);
        setResizable(false);

        this.produtosModel = new DefaultTableModel(
                new String[]{"Produto", "Descrição", "Preço"},
                0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        construirInterface();
        carregarCategorias();
    }

    private void construirInterface() {
        setLayout(new BorderLayout(12, 12));
        setBackground(Color.WHITE);

        add(criarPainelCategorias(), BorderLayout.WEST);
        add(criarPainelProdutos(), BorderLayout.CENTER);
        add(criarRodape(), BorderLayout.SOUTH);
    }

    private JPanel criarPainelCategorias() {
        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 6));
        painel.setPreferredSize(new Dimension(180, 0));

        JLabel titulo = new JLabel("Categorias");
        titulo.setFont(AppFonts.STATUS.deriveFont(Font.BOLD, 12f));

        DefaultListModel<CategoriaCardapio> categoriaModel = new DefaultListModel<>();

        List<CategoriaCardapio> categorias = restaurante.getCategorias();
        if (categorias != null) {
            categorias.forEach(categoriaModel::addElement);
        }

        JList<CategoriaCardapio> listaCategorias = new JList<>(categoriaModel);
        listaCategorias.setFont(AppFonts.STATUS);
        listaCategorias.setSelectionBackground(AppColors.AZUL_PRIMARIO);
        listaCategorias.setSelectionForeground(Color.WHITE);
        listaCategorias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaCategorias.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                CategoriaCardapio selecionada = listaCategorias.getSelectedValue();
                if (selecionada != null) {
                    carregarProdutosDaCategoria(selecionada);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(listaCategorias);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        painel.add(titulo, BorderLayout.NORTH);
        painel.add(scroll, BorderLayout.CENTER);

        // Selecionar primeira categoria por padrão
        if (categoriaModel.getSize() > 0) {
            listaCategorias.setSelectedIndex(0);
        }

        return painel;
    }

    private JPanel criarPainelProdutos() {
        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createEmptyBorder(12, 6, 12, 12));

        JLabel titulo = new JLabel("Produtos");
        titulo.setFont(AppFonts.STATUS.deriveFont(Font.BOLD, 12f));

        tabelaProdutos = new JTable(produtosModel);
        tabelaProdutos.setFont(AppFonts.STATUS);
        tabelaProdutos.setRowHeight(30);
        tabelaProdutos.setGridColor(new Color(220, 220, 220));
        tabelaProdutos.setShowGrid(true);
        tabelaProdutos.setSelectionBackground(new Color(220, 235, 255));

        JTableHeader th = tabelaProdutos.getTableHeader();
        th.setFont(AppFonts.STATUS.deriveFont(Font.BOLD));
        th.setBackground(new Color(245, 245, 245));
        th.setForeground(Color.DARK_GRAY);
        th.setReorderingAllowed(false);

        tabelaProdutos.getColumnModel().getColumn(0).setPreferredWidth(150);
        tabelaProdutos.getColumnModel().getColumn(1).setPreferredWidth(280);
        tabelaProdutos.getColumnModel().getColumn(2).setPreferredWidth(80);

        JScrollPane scroll = new JScrollPane(tabelaProdutos);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        painel.add(titulo, BorderLayout.NORTH);
        painel.add(scroll, BorderLayout.CENTER);

        return painel;
    }

    private JPanel criarRodape() {
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        rodape.setBackground(Color.WHITE);
        rodape.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));

        JButton btnAdicionar = new JButton("Adicionar ao Carrinho");
        btnAdicionar.setPreferredSize(new Dimension(180, 36));
        btnAdicionar.setFont(AppFonts.STATUS.deriveFont(Font.BOLD));
        btnAdicionar.setBackground(AppColors.AZUL_PRIMARIO);
        btnAdicionar.setForeground(Color.WHITE);
        btnAdicionar.setFocusPainted(false);
        btnAdicionar.setBorderPainted(false);
        btnAdicionar.setOpaque(true);
        btnAdicionar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAdicionar.addActionListener(e -> acaoAdicionarAoCarrinho());

        JButton btnFechar = new JButton("Fechar");
        btnFechar.setPreferredSize(new Dimension(110, 36));
        btnFechar.setFont(AppFonts.STATUS);
        btnFechar.setBackground(new Color(220, 220, 220));
        btnFechar.setForeground(Color.DARK_GRAY);
        btnFechar.setFocusPainted(false);
        btnFechar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnFechar.addActionListener(e -> dispose());

        rodape.add(btnAdicionar);
        rodape.add(btnFechar);

        return rodape;
    }

    private void carregarCategorias() {
        // Já carregadas na construção
    }

    private void carregarProdutosDaCategoria(CategoriaCardapio categoria) {
        produtosModel.setRowCount(0);

        List<Produto> produtos = produtoController.listarAtivosPorRestaurante(restaurante.getId());
        NumberFormat formato = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        produtos.stream()
                .filter(p -> p.getCategoriaCardapioId().equals(categoria.getId()))
                .forEach(p -> produtosModel.addRow(new Object[]{
                        p.getNome(),
                        p.getDescricao(),
                        formato.format(p.getPreco())
                }));
    }

    private void acaoAdicionarAoCarrinho() {
        if (tabelaProdutos.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um produto na tabela.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int linhaSelecionada = tabelaProdutos.getSelectedRow();
        String nomeProduto = (String) produtosModel.getValueAt(linhaSelecionada, 0);

        // Procurar o produto por nome
        List<Produto> produtos = produtoController.listarAtivosPorRestaurante(restaurante.getId());
        Produto selecionado = produtos.stream()
                .filter(p -> p.getNome().equals(nomeProduto))
                .findFirst()
                .orElse(null);

        if (selecionado == null) {
            JOptionPane.showMessageDialog(this,
                    "Produto não encontrado.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Solicitar quantidade
        String quantidadeStr = JOptionPane.showInputDialog(this,
                "Digite a quantidade desejada:",
                "1");

        if (quantidadeStr == null || quantidadeStr.trim().isEmpty()) {
            return; // Cancelado
        }

        try {
            int quantidade = Integer.parseInt(quantidadeStr);
            if (quantidade <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Quantidade deve ser maior que 0.",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            carrinhoController.adicionarItem(selecionado, quantidade);
            JOptionPane.showMessageDialog(this,
                    quantidade + "x " + selecionado.getNome() + " adicionado(s) ao carrinho!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Digite um número válido.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}

