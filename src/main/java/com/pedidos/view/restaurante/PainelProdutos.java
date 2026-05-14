package com.pedidos.view.restaurante;

import com.pedidos.application.service.CategoriaService;
import com.pedidos.application.service.ProdutoService;
import com.pedidos.domain.entities.CategoriaCardapio;
import com.pedidos.domain.entities.Produto;
import com.pedidos.domain.entities.Usuario;
import com.pedidos.view.util.AppFonts;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class PainelProdutos extends JPanel {

    private final Usuario usuario;
    private final ProdutoService produtoService;
    private final CategoriaService categoriaService;

    private DefaultTableModel modelProdutos;
    private CategoriaCardapio categoriaSelecionada;

    public PainelProdutos(Usuario usuario, ProdutoService produtoService, CategoriaService categoriaService) {
        super(new BorderLayout());
        this.usuario = usuario;
        this.produtoService = produtoService;
        this.categoriaService = categoriaService;
        construir();
    }

    private void construir() {
        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(200);
        splitPane.setLeftComponent(criarPainelCategorias());
        splitPane.setRightComponent(criarPainelProdutos());
        add(splitPane, BorderLayout.CENTER);
    }

    // -------------------- Categorias --------------------

    private JPanel criarPainelCategorias() {
        JPanel painel = new JPanel(new BorderLayout());

        DefaultListModel<CategoriaCardapio> model = new DefaultListModel<>();
        JList<CategoriaCardapio> listaCategorias = new JList<>(model);
        listaCategorias.setFont(AppFonts.LABEL);

        categoriaService.listarCategoriasCardapio(usuario.getId()).forEach(model::addElement);

        JButton btnNovaCategoria = new JButton("+");
        JButton btnListarTodos = new JButton("All");
        JButton btnRemoverCategoria = new JButton("-");

        JPanel painelBotoes = new JPanel(new GridLayout(1, 2, 5, 5));
        painelBotoes.add(btnListarTodos);
        painelBotoes.add(btnNovaCategoria);
        painelBotoes.add(btnRemoverCategoria);

        btnNovaCategoria.addActionListener(e -> {
            String nome = JOptionPane.showInputDialog(this, "Nome da categoria:");
            if (nome == null || nome.isBlank()) return;

            String descricao = JOptionPane.showInputDialog(this, "Descrição da Categoria:");
            categoriaService.criarCategoriaCardapio(nome, descricao, usuario.getId());

            model.clear();
            categoriaService.listarCategoriasCardapio(usuario.getId()).forEach(model::addElement);
        });

        btnListarTodos.addActionListener(e -> {
            categoriaSelecionada = null;
            carregarProdutos(null);
        });

        btnRemoverCategoria.addActionListener(e -> {
            CategoriaCardapio categoria = listaCategorias.getSelectedValue();
            if (categoria == null) {
                JOptionPane.showMessageDialog(this, "Selecione uma categoria para remover.");
                return;
            }

            int option = JOptionPane.showConfirmDialog(this,
                    "Tem certeza que deseja remover esta categoria?",
                    "Remover Categoria",
                    JOptionPane.OK_CANCEL_OPTION);

            if (option != JOptionPane.OK_OPTION) return;

            categoriaService.removerCategoriaCardapio(categoria.getId());
            model.clear();
            categoriaService.listarCategoriasCardapio(usuario.getId()).forEach(model::addElement);
            categoriaSelecionada = null;
            carregarProdutos(null);
        });

        listaCategorias.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                categoriaSelecionada = listaCategorias.getSelectedValue();
                if (categoriaSelecionada != null) {
                    carregarProdutos(categoriaSelecionada);
                }
            }
        });

        painel.add(painelBotoes, BorderLayout.NORTH);
        painel.add(new JScrollPane(listaCategorias), BorderLayout.CENTER);
        return painel;
    }

    // -------------------- Produtos --------------------

    private JPanel criarPainelProdutos() {
        JPanel painel = new JPanel(new BorderLayout());

        String[] atributos = { "#", "Nome", "Descrição", "Preço", "Status" };
        modelProdutos = new DefaultTableModel(atributos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable tabelaProdutos = new JTable(modelProdutos);
        tabelaProdutos.setFont(AppFonts.LABEL);
        tabelaProdutos.getTableHeader().setFont(AppFonts.LABEL);
        tabelaProdutos.setRowHeight(24);

        JButton btnNovoProduto = new JButton("+ Novo Produto");
        JButton btnEditarProduto = new JButton("Editar");
        JButton btnAtivarProduto = new JButton("Ativar/Desativar");
        JButton btnRemoverProduto = new JButton("Remover");
        btnNovoProduto.setFont(AppFonts.BOTAO);
        btnEditarProduto.setFont(AppFonts.BOTAO);
        btnAtivarProduto.setFont(AppFonts.BOTAO);
        btnRemoverProduto.setFont(AppFonts.BOTAO);

        JToolBar toolBar = new JToolBar();
        toolBar.add(btnNovoProduto);
        toolBar.add(btnEditarProduto);
        toolBar.add(btnAtivarProduto);
        toolBar.add(btnRemoverProduto);

        carregarProdutos(null);

        painel.add(toolBar, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabelaProdutos), BorderLayout.CENTER);

        // Criar novo produto
        btnNovoProduto.addActionListener(e -> {
            FormularioProduto form = criarFormulario();

            int option = JOptionPane.showConfirmDialog(this, form.painel, "Novo Produto", JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.OK_OPTION) return;

            if (form.campoNome.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Nome obrigatório", "AVISO", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (form.campoPreco.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Preço obrigatório", "AVISO", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String categoriaId = resolverCategoriaId(form);
            produtoService.criarProduto(form.campoNome.getText(), form.campoDescricao.getText(),
                    new BigDecimal(form.campoPreco.getText()), categoriaId, usuario.getId());
            carregarProdutos(categoriaSelecionada);
        });

        // Editar produto
        btnEditarProduto.addActionListener(e -> {
            int linha = lerLinhaSelecionada(tabelaProdutos);
            if (linha == -1) return;

            String produtoId = (String) tabelaProdutos.getValueAt(linha, 0);
            Produto produto = produtoService.buscarPorId(produtoId);
            FormularioProduto form = criarFormulario();

            form.campoNome.setText(produto.getNome());
            form.campoDescricao.setText(produto.getDescricao());
            form.campoPreco.setText(produto.getPreco().toString());

            List<CategoriaCardapio> categorias = categoriaService.listarCategoriasCardapio(usuario.getId());
            String categoriaAtual = categorias.stream()
                    .filter(c -> c.getId().equals(produto.getCategoriaCardapioId()))
                    .findFirst()
                    .map(CategoriaCardapio::getNome)
                    .orElse("Sem categoria");
            form.selecionadorCategoria.setSelectedItem(categoriaAtual);

            int option = JOptionPane.showConfirmDialog(this, form.painel, "Editar Produto", JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.OK_OPTION) return;

            int confirmacao = JOptionPane.showConfirmDialog(this, "Salvar alterações do produto?",
                    "Confirmar edição", JOptionPane.OK_CANCEL_OPTION);
            if (confirmacao != JOptionPane.OK_OPTION) return;

            String novaCategoriaId = resolverCategoriaId(form);
            produtoService.editarProduto(produtoId, usuario.getId(), form.campoNome.getText(),
                    form.campoDescricao.getText(), new BigDecimal(form.campoPreco.getText()), novaCategoriaId);
            carregarProdutos(categoriaSelecionada);
        });

        // Ativar/Desativar produto
        btnAtivarProduto.addActionListener(e -> {
            int linha = lerLinhaSelecionada(tabelaProdutos);
            if (linha == -1) return;

            produtoService.ativarInativar((String) tabelaProdutos.getValueAt(linha, 0), usuario.getId());
            carregarProdutos(categoriaSelecionada);
        });

        // Remover produto
        btnRemoverProduto.addActionListener(e -> {
            int linha = lerLinhaSelecionada(tabelaProdutos);
            if (linha == -1) return;

            int option = JOptionPane.showConfirmDialog(this,
                    "Tem certeza que deseja remover este produto?",
                    "Remover Produto", JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.OK_OPTION) return;

            produtoService.removerProduto((String) tabelaProdutos.getValueAt(linha, 0), usuario.getId());
            carregarProdutos(categoriaSelecionada);
        });

        return painel;
    }

    // -------------------- Helpers --------------------

    private FormularioProduto criarFormulario() {
        FormularioProduto form = new FormularioProduto();
        form.selecionadorCategoria.addItem("Sem categoria");
        categoriaService.listarCategoriasCardapio(usuario.getId())
                .forEach(c -> form.selecionadorCategoria.addItem(c.getNome()));

        form.painel.add(new JLabel("Nome:"));
        form.painel.add(form.campoNome);
        form.painel.add(new JLabel("Descrição:"));
        form.painel.add(form.campoDescricao);
        form.painel.add(new JLabel("Preço:"));
        form.painel.add(form.campoPreco);
        form.painel.add(new JLabel("Categoria:"));
        form.painel.add(form.selecionadorCategoria);
        return form;
    }

    private String resolverCategoriaId(FormularioProduto form) {
        String escolhida = (String) form.selecionadorCategoria.getSelectedItem();
        if (escolhida == null || escolhida.equals("Sem categoria")) return null;

        return categoriaService.listarCategoriasCardapio(usuario.getId()).stream()
                .filter(c -> c.getNome().equals(escolhida))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"))
                .getId();
    }

    private void carregarProdutos(CategoriaCardapio categoria) {
        modelProdutos.setRowCount(0);
        produtoService.listarPorRestaurante(usuario.getId()).stream()
                .filter(p -> categoria == null
                        || (p.getCategoriaCardapioId() != null && p.getCategoriaCardapioId().equals(categoria.getId())))
                .forEach(p -> modelProdutos.addRow(new Object[]{
                        p.getId(),
                        p.getNome(),
                        p.getDescricao(),
                        p.getPreco(),
                        p.isStatusAtivo() ? "Ativo" : "Inativo"
                }));
    }

    private int lerLinhaSelecionada(JTable tabela) {
        int linha = tabela.getSelectedRow();
        if (linha == -1) JOptionPane.showMessageDialog(this, "Selecione um produto.");
        return linha;
    }

    // -------------------- Inner class --------------------

    private static class FormularioProduto {
        JTextField campoNome = new JTextField();
        JTextField campoDescricao = new JTextField();
        JTextField campoPreco = new JTextField();
        JComboBox<String> selecionadorCategoria = new JComboBox<>();
        JPanel painel = new JPanel(new GridLayout(0, 1));
    }
}
