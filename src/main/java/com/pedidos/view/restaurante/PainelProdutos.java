package com.pedidos.view.restaurante;

import com.pedidos.application.service.CategoriaService;
import com.pedidos.application.service.ProdutoService;
import com.pedidos.domain.entities.CategoriaCardapio;
import com.pedidos.domain.entities.Produto;
import com.pedidos.domain.entities.Usuario;
import com.pedidos.view.util.AppColors;
import com.pedidos.view.util.AppFonts;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PainelProdutos extends JPanel {

    private static final NumberFormat FMT_MOEDA =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private static final Color COR_ATIVO   = new Color(40, 167, 69);
    private static final Color COR_INATIVO = new Color(108, 117, 125);

    private final Usuario usuario;
    private final ProdutoService produtoService;
    private final CategoriaService categoriaService;

    private DefaultTableModel modelProdutos;
    private JTable tabelaProdutos;
    private JLabel labelSubtitulo;
    private List<Produto> produtosCarregados = new ArrayList<>();
    private CategoriaCardapio categoriaSelecionada;

    // left panel refs for refresh
    private DefaultListModel<CategoriaCardapio> modelCategorias;
    private JList<CategoriaCardapio> listaCategorias;

    public PainelProdutos(Usuario usuario, ProdutoService produtoService, CategoriaService categoriaService) {
        super(new BorderLayout());
        this.usuario = usuario;
        this.produtoService = produtoService;
        this.categoriaService = categoriaService;
        construir();
    }

    // ─────────────────────────── build ───────────────────────────────────────

    private void construir() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                criarPainelCategorias(), criarPainelProdutos());
        split.setDividerLocation(200);
        split.setDividerSize(4);
        add(split, BorderLayout.CENTER);
    }

    // ─────────────────────────── left: categorias ────────────────────────────

    private JPanel criarPainelCategorias() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(AppColors.CINZA_FUNDO);

        // header
        JLabel titulo = new JLabel("Categorias");
        titulo.setFont(AppFonts.TITULO);
        titulo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, AppColors.CINZA_BORDA),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        titulo.setOpaque(true);
        titulo.setBackground(AppColors.CINZA_FUNDO);

        // list
        modelCategorias = new DefaultListModel<>();
        listaCategorias = new JList<>(modelCategorias);
        listaCategorias.setFont(AppFonts.LABEL);
        listaCategorias.setSelectionBackground(AppColors.AZUL_PRIMARIO);
        listaCategorias.setSelectionForeground(AppColors.TEXTO_BRANCO);
        listaCategorias.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                if (value instanceof CategoriaCardapio c) lbl.setText(c.getNome());
                lbl.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                return lbl;
            }
        });
        recarregarListaCategorias();

        // buttons
        JButton btnAdd = new JButton("+");
        JButton btnDel = new JButton("−");
        btnAdd.setFont(AppFonts.BOTAO);
        btnDel.setFont(AppFonts.BOTAO);
        btnAdd.setToolTipText("Nova categoria");
        btnDel.setToolTipText("Remover categoria selecionada");

        JPanel painelBotoes = new JPanel(new GridLayout(1, 2, 4, 0));
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        painelBotoes.setBackground(AppColors.CINZA_FUNDO);
        painelBotoes.add(btnAdd);
        painelBotoes.add(btnDel);

        btnAdd.addActionListener(e -> {
            String nome = JOptionPane.showInputDialog(this, "Nome da categoria:");
            if (nome == null || nome.isBlank()) return;
            String descricao = JOptionPane.showInputDialog(this, "Descrição:");
            try {
                categoriaService.criarCategoriaCardapio(nome, descricao, usuario.getId());
                recarregarListaCategorias();
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnDel.addActionListener(e -> {
            CategoriaCardapio cat = listaCategorias.getSelectedValue();
            if (cat == null) {
                JOptionPane.showMessageDialog(this, "Selecione uma categoria.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int ok = JOptionPane.showConfirmDialog(this,
                    "Remover categoria \"" + cat.getNome() + "\"?",
                    "Confirmar", JOptionPane.OK_CANCEL_OPTION);
            if (ok != JOptionPane.OK_OPTION) return;
            categoriaService.removerCategoriaCardapio(cat.getId());
            recarregarListaCategorias();
            categoriaSelecionada = null;
            carregarProdutos(null);
        });

        listaCategorias.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            categoriaSelecionada = listaCategorias.getSelectedValue();
            if (categoriaSelecionada != null) carregarProdutos(categoriaSelecionada);
        });

        painel.add(titulo,                      BorderLayout.NORTH);
        painel.add(new JScrollPane(listaCategorias), BorderLayout.CENTER);
        painel.add(painelBotoes,                BorderLayout.SOUTH);
        return painel;
    }

    private void recarregarListaCategorias() {
        modelCategorias.clear();
        categoriaService.listarCategoriasCardapio(usuario.getId()).forEach(modelCategorias::addElement);
    }

    // ─────────────────────────── right: produtos ─────────────────────────────

    private JPanel criarPainelProdutos() {
        JPanel painel = new JPanel(new BorderLayout());

        // toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppColors.CINZA_BORDA));

        JButton btnNovo   = criarBotaoPrimario("+ Novo Produto");
        JButton btnEditar = new JButton("Editar");
        JButton btnAtivar = new JButton("Ativar");
        JButton btnRemover = new JButton("Remover");
        btnEditar.setFont(AppFonts.BOTAO);
        btnAtivar.setFont(AppFonts.BOTAO);
        btnRemover.setFont(AppFonts.BOTAO);

        toolbar.add(btnNovo);
        toolbar.add(btnEditar);
        toolbar.add(btnAtivar);
        toolbar.add(btnRemover);

        // subtitle
        labelSubtitulo = new JLabel(" ");
        labelSubtitulo.setFont(AppFonts.STATUS);
        labelSubtitulo.setForeground(AppColors.TEXTO_SECUNDARIO);
        labelSubtitulo.setBorder(BorderFactory.createEmptyBorder(4, 8, 2, 8));

        // table
        modelProdutos = new DefaultTableModel(
                new String[]{"#", "Nome", "Descrição", "Preço", "Status"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        tabelaProdutos = new JTable(modelProdutos);
        tabelaProdutos.setFont(AppFonts.LABEL);
        tabelaProdutos.getTableHeader().setFont(AppFonts.TITULO);
        tabelaProdutos.setRowHeight(26);
        tabelaProdutos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaProdutos.setFillsViewportHeight(true);
        tabelaProdutos.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabelaProdutos.getColumnModel().getColumn(0).setMaxWidth(60);
        tabelaProdutos.getColumnModel().getColumn(1).setPreferredWidth(180);
        tabelaProdutos.getColumnModel().getColumn(2).setPreferredWidth(400);
        tabelaProdutos.getColumnModel().getColumn(3).setPreferredWidth(100);
        tabelaProdutos.getColumnModel().getColumn(4).setPreferredWidth(90);
        tabelaProdutos.getColumnModel().getColumn(4).setCellRenderer(new StatusBadgeRenderer());

        JPanel topo = new JPanel(new BorderLayout());
        topo.add(toolbar,        BorderLayout.NORTH);
        topo.add(labelSubtitulo, BorderLayout.SOUTH);

        painel.add(topo,                           BorderLayout.NORTH);
        painel.add(new JScrollPane(tabelaProdutos), BorderLayout.CENTER);

        carregarProdutos(null);

        // actions
        btnNovo.addActionListener(e -> {
            FormularioProduto form = criarFormulario(null);
            int opt = JOptionPane.showConfirmDialog(this, form.painel, "Novo Produto",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (opt != JOptionPane.OK_OPTION) return;
            if (form.campoNome.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Nome é obrigatório.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                BigDecimal preco = new BigDecimal(form.campoPreco.getText().trim().replace(",", "."));
                String catId = resolverCategoriaId(form);
                produtoService.criarProduto(form.campoNome.getText().trim(),
                        form.campoDescricao.getText().trim(), preco, catId, usuario.getId());
                carregarProdutos(categoriaSelecionada);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Preço inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnEditar.addActionListener(e -> {
            int row = selecionado();
            if (row < 0) return;
            Produto p = produtosCarregados.get(row);
            FormularioProduto form = criarFormulario(p);
            int opt = JOptionPane.showConfirmDialog(this, form.painel, "Editar Produto",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (opt != JOptionPane.OK_OPTION) return;
            try {
                BigDecimal preco = new BigDecimal(form.campoPreco.getText().trim().replace(",", "."));
                String catId = resolverCategoriaId(form);
                produtoService.editarProduto(p.getId(), usuario.getId(),
                        form.campoNome.getText().trim(), form.campoDescricao.getText().trim(),
                        preco, catId);
                carregarProdutos(categoriaSelecionada);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Preço inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnAtivar.addActionListener(e -> {
            int row = selecionado();
            if (row < 0) return;
            produtoService.ativarInativar(produtosCarregados.get(row).getId(), usuario.getId());
            carregarProdutos(categoriaSelecionada);

            JOptionPane.showMessageDialog(painel, "Produto criado!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

        });

        btnRemover.addActionListener(e -> {
            int row = selecionado();
            if (row < 0) return;
            Produto p = produtosCarregados.get(row);
            int ok = JOptionPane.showConfirmDialog(this,
                    "Remover produto \"" + p.getNome() + "\"?",
                    "Confirmar", JOptionPane.OK_CANCEL_OPTION);
            if (ok != JOptionPane.OK_OPTION) return;
            try {
                produtoService.removerProduto(p.getId(), usuario.getId());
                carregarProdutos(categoriaSelecionada);
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        return painel;
    }

    // ─────────────────────────── data ────────────────────────────────────────

    private void carregarProdutos(CategoriaCardapio categoria) {
        produtosCarregados = produtoService.listarPorRestaurante(usuario.getId()).stream()
                .filter(p -> categoria == null
                        || (p.getCategoriaCardapioId() != null
                            && p.getCategoriaCardapioId().equals(categoria.getId())))
                .toList();

        modelProdutos.setRowCount(0);
        for (int i = 0; i < produtosCarregados.size(); i++) {
            Produto p = produtosCarregados.get(i);
            modelProdutos.addRow(new Object[]{
                i + 1,
                p.getNome(),
                p.getDescricao(),
                FMT_MOEDA.format(p.getPreco()),
                p.isStatusAtivo() ? "ATIVO" : "INATIVO"
            });
        }

        if (categoria != null) {
            labelSubtitulo.setText(categoria.getNome() + " — " + produtosCarregados.size() + " produto(s)");
        } else {
            labelSubtitulo.setText(produtosCarregados.size() + " produto(s) no total");
        }
    }

    // ─────────────────────────── helpers ─────────────────────────────────────

    private int selecionado() {
        int row = tabelaProdutos.getSelectedRow();
        if (row < 0) JOptionPane.showMessageDialog(this, "Selecione um produto.", "Aviso", JOptionPane.WARNING_MESSAGE);
        return row;
    }

    private JButton criarBotaoPrimario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(AppFonts.BOTAO);
        btn.setBackground(AppColors.AZUL_PRIMARIO);
        btn.setForeground(AppColors.TEXTO_BRANCO);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        return btn;
    }

    private FormularioProduto criarFormulario(Produto existente) {
        FormularioProduto form = new FormularioProduto();

        List<CategoriaCardapio> cats = categoriaService.listarCategoriasCardapio(usuario.getId());
        form.selecionadorCategoria.addItem("Sem categoria");
        cats.forEach(c -> form.selecionadorCategoria.addItem(c.getNome()));

        if (existente != null) {
            form.campoNome.setText(existente.getNome());
            form.campoDescricao.setText(existente.getDescricao());
            form.campoPreco.setText(existente.getPreco().toPlainString());
            cats.stream()
                    .filter(c -> c.getId().equals(existente.getCategoriaCardapioId()))
                    .findFirst()
                    .ifPresent(c -> form.selecionadorCategoria.setSelectedItem(c.getNome()));
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        adicionarCampoForm(form.painel, gbc, 0, "Nome:",      form.campoNome);
        adicionarCampoForm(form.painel, gbc, 1, "Descrição:", form.campoDescricao);
        adicionarCampoForm(form.painel, gbc, 2, "Preço:",     form.campoPreco);
        adicionarCampoForm(form.painel, gbc, 3, "Categoria:", form.selecionadorCategoria);

        return form;
    }

    private void adicionarCampoForm(JPanel painel, GridBagConstraints gbc,
                                     int row, String rotulo, JComponent campo) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        JLabel lbl = new JLabel(rotulo);
        lbl.setFont(AppFonts.LABEL);
        painel.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        painel.add(campo, gbc);
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

    // ─────────────────────────── renderers ───────────────────────────────────

    private static class StatusBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);

            boolean ativo = "ATIVO".equals(value);
            if (!isSelected) {
                lbl.setOpaque(true);
                lbl.setBackground(ativo ? new Color(212, 237, 218) : new Color(230, 230, 230));
                lbl.setForeground(ativo ? COR_ATIVO : COR_INATIVO);
            }
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
            return lbl;
        }
    }

    // ─────────────────────────── inner class ─────────────────────────────────

    private static class FormularioProduto {
        JTextField campoNome        = new JTextField(20);
        JTextField campoDescricao   = new JTextField(20);
        JTextField campoPreco       = new JTextField(10);
        JComboBox<String> selecionadorCategoria = new JComboBox<>();
        JPanel painel = new JPanel(new GridBagLayout());
    }
}
