package com.pedidos.view.restaurante;

import com.pedidos.application.service.*;
import com.pedidos.domain.entities.CategoriaCardapio;
import com.pedidos.domain.entities.Produto;
import com.pedidos.domain.entities.Usuario;
import com.pedidos.view.util.AppColors;
import com.pedidos.view.util.AppFonts;
import com.pedidos.view.util.base.BaseFrame;
import com.pedidos.view.util.session.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class RestauranteFrame extends BaseFrame {
    private final Usuario usuario;

    private final CategoriaService categoriaService;
    private final ProdutoService produtoService;
    private final RestauranteService restauranteService;
    private final AreaEntregaService areaEntregaService;
    private final HorarioService horarioService;

    public RestauranteFrame( Usuario usuario,
                             CategoriaService categoriaService,
                             ProdutoService produtoService,
                             RestauranteService restauranteService,
                             AreaEntregaService areaEntregaService,
                             HorarioService horarioService) {
        super("Sistema de Delivery — Painel do Restaurante", 700, 500);
        this.usuario = usuario;
        this.categoriaService = categoriaService;
        this.produtoService = produtoService;
        this.restauranteService = restauranteService;
        this.areaEntregaService = areaEntregaService;
        this.horarioService = horarioService;
        construirInterface();
        criarMenu();
        criarAbas();
    }

    private void construirInterface() {
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppColors.AZUL_PRIMARIO);
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titulo = new JLabel("Painel do Restaurante");
        titulo.setFont(AppFonts.TITULO);
        titulo.setForeground(AppColors.TEXTO_BRANCO);

        JLabel nomeLabel = new JLabel("Olá, " + usuario.getNome());
        nomeLabel.setFont(AppFonts.STATUS);
        nomeLabel.setForeground(AppColors.TEXTO_BRANCO);

        header.add(titulo,    BorderLayout.WEST);
        header.add(nomeLabel, BorderLayout.EAST);

        JLabel centro = new JLabel("Área do restaurante — em desenvolvimento",
                SwingConstants.CENTER);
        centro.setFont(AppFonts.LABEL);

        JButton btnSair = new JButton("Sair");
        btnSair.addActionListener(e -> {
            SessionManager.getInstance().encerrarSessao();
            dispose();
            System.exit(0);
        });
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rodape.add(btnSair);

        add(header, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);
    }

    private void criarMenu() {

        JMenuBar menuBar = new JMenuBar();

        JMenu menuCardapio = new JMenu("Cardápio");
        JMenu menuPedidos = new JMenu("Pedidos");
        JMenu menuPerfil = new JMenu("Perfil");
        JMenu menuLogout = new JMenu("Logout");

        menuBar.add(menuCardapio);
        menuBar.add(menuPedidos);
        menuBar.add(menuPerfil);
        menuBar.add(menuLogout);

        setJMenuBar(menuBar);
    }

    private void criarAbas() {

        JTabbedPane abas = new JTabbedPane();

        abas.addTab("Produtos", criarPainelProdutos());
        abas.addTab("Pedidos", new JPanel());
        abas.addTab("Perfil", new JPanel());

        add(abas, BorderLayout.CENTER);
    }

    /** --------------- ABA DE PEDIDOS ------------------- */

    private JPanel criarPainelProdutos() {

        JPanel painel = new JPanel(new BorderLayout());
        JSplitPane splitPane = new JSplitPane();

        splitPane.setDividerLocation(200);
        splitPane.setLeftComponent(criarTabelaDeCategorias());
        splitPane.setRightComponent(criarTabelaDeProdutos());

        painel.add(splitPane, BorderLayout.CENTER);

        return painel;
    }

    private JScrollPane criarTabelaDeCategorias() {

        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> listaCategorias = new JList<>(model);

        categoriaService.listarCategoriasCardapio(usuario.getId())
                .forEach(c ->
                        model.addElement(c.getNome()));

        return new JScrollPane(listaCategorias);
    }

    private JPanel criarTabelaDeProdutos () {

        JPanel painel = new JPanel(new BorderLayout());

        String[] atributos = {
                "#",
                "Nome",
                "Descrição",
                "Preço",
                "Status"
        };

        DefaultTableModel model = new DefaultTableModel(atributos, 0);
        JTable tabelaProdutos = new JTable(model);
        carregarProdutos(model);

        JToolBar toolBarCrud = new JToolBar();

        JButton btnNovoProduto = new JButton("+ Novo Produto");
        JButton btnEditarProduto = new JButton("Editar");
        JButton btnAtivarProduto = new JButton("Ativar/Desativar");
        JButton btnRemoverProduto = new JButton("Remover");

        toolBarCrud.add(btnNovoProduto);
        toolBarCrud.add(btnEditarProduto);
        toolBarCrud.add(btnAtivarProduto);
        toolBarCrud.add(btnRemoverProduto);

        painel.add(toolBarCrud, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabelaProdutos), BorderLayout.CENTER);

        /**------- Eventos ---------*/


        //  -------= Criar novo produto =----------------
        btnNovoProduto.addActionListener (e -> {

            JTextField campoNovoNome = new JTextField();
            JTextField campoNovoDescricao = new JTextField();
            JTextField campoNovoPreco = new JTextField();

            JComboBox<String> selecionadorCategoria = new JComboBox<>();

            selecionadorCategoria.addItem("Sem categoria");

            List<CategoriaCardapio> categoriasCardapio = categoriaService.listarCategoriasCardapio(usuario.getId());

            categoriasCardapio.forEach(c -> selecionadorCategoria.addItem(c.getNome()));

            JPanel painelNovoProduto = new JPanel(new GridLayout(0, 1));

            painelNovoProduto.add(new JLabel("Nome:"));
            painelNovoProduto.add(campoNovoNome);

            painelNovoProduto.add(new JLabel("Descrição:"));
            painelNovoProduto.add(campoNovoDescricao);

            painelNovoProduto.add(new JLabel("Preço:"));
            painelNovoProduto.add(campoNovoPreco);

            painelNovoProduto.add(new JLabel("Categoria:"));
            painelNovoProduto.add(selecionadorCategoria);

            int option = JOptionPane.showConfirmDialog(
                    this,
                    painelNovoProduto,
                    "Novo Produto",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if(option != JOptionPane.OK_OPTION) {
                return;
            }

            String nome = campoNovoNome.getText();
            String descricao = campoNovoDescricao.getText();
            BigDecimal preco = new BigDecimal(campoNovoPreco.getText());
            String categoriaSelecionada = (String) selecionadorCategoria.getSelectedItem();
            String categoriaId = null;

            if(!categoriaSelecionada.equals("Sem categoria")) {

                categoriaId = categoriasCardapio.stream()
                        .filter(c -> c.getNome().equals(categoriaSelecionada))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Categoria não encontrada"))
                        .getId();
            }

            produtoService.criarProduto(nome, descricao, preco ,categoriaId, usuario.getId());
            carregarProdutos(model);
        });


        // ---------------= Editar produto =----------------

        btnEditarProduto.addActionListener (e -> {
            int linhaSelecionadaEditarProduto = tabelaProdutos.getSelectedRow();

            if (linhaSelecionadaEditarProduto == -1) {
                JOptionPane.showMessageDialog(this, "Selecione um produto.");
                return;
            }

            JTextField campoNovoNome = new JTextField();
            JTextField campoNovoDescricao = new JTextField();
            JTextField campoNovoPreco = new JTextField();

            JComboBox<String> selecionadorCategoria = new JComboBox<>();

            selecionadorCategoria.addItem("Sem categoria");

            List<CategoriaCardapio> categoriasCardapio = categoriaService.listarCategoriasCardapio(usuario.getId());

            categoriasCardapio.forEach(c -> selecionadorCategoria.addItem(c.getNome()));

            JPanel painelNovoProduto = new JPanel(new GridLayout(0, 1));

            painelNovoProduto.add(new JLabel("Nome:"));
            painelNovoProduto.add(campoNovoNome);

            painelNovoProduto.add(new JLabel("Descrição:"));
            painelNovoProduto.add(campoNovoDescricao);

            painelNovoProduto.add(new JLabel("Preço:"));
            painelNovoProduto.add(campoNovoPreco);

            painelNovoProduto.add(new JLabel("Categoria:"));
            painelNovoProduto.add(selecionadorCategoria);

            int option = JOptionPane.showConfirmDialog(
                    this,
                    painelNovoProduto,
                    "Novo Produto",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if(option != JOptionPane.OK_OPTION) {
                return;
            }

            String novoNome = campoNovoNome.getText();
            String novoDescricao = campoNovoDescricao.getText();
            BigDecimal novoPreco = new BigDecimal(campoNovoPreco.getText());
            String categoriaSelecionada = (String) selecionadorCategoria.getSelectedItem();
            String novoCategoriaId = null;

            if(!categoriaSelecionada.equals("Sem categoria")) {

                novoCategoriaId = categoriasCardapio.stream()
                        .filter(c -> c.getNome().equals(categoriaSelecionada))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Categoria não encontrada"))
                        .getId();
            }

            produtoService.editarProduto((String) tabelaProdutos.getValueAt(linhaSelecionadaEditarProduto, 0),
                                                    usuario.getId(), novoNome, novoDescricao, novoPreco, novoCategoriaId);

            carregarProdutos(model);

        });


        // ---------= Ativo ou Inativo --------------

        btnAtivarProduto.addActionListener (e -> {
            int linhaSelecionadaAtivarProduto = tabelaProdutos.getSelectedRow();

            if(linhaSelecionadaAtivarProduto == -1) {
                JOptionPane.showMessageDialog(this, "Selecione um produto.");
                return;
            }

            String produtoId = (String) tabelaProdutos.getValueAt(linhaSelecionadaAtivarProduto, 0);

            produtoService.ativarInativar(produtoId, usuario.getId());
            carregarProdutos(model);

        });

        btnRemoverProduto.addActionListener ( e -> {

            int linhaSelecionadaRemoverProduto = tabelaProdutos.getSelectedRow();

            if (linhaSelecionadaRemoverProduto == -1) {
                JOptionPane.showMessageDialog(this, "Selecione um produto.");
                return;
            }

            produtoService.removerProduto(
                    (String) tabelaProdutos.getValueAt(linhaSelecionadaRemoverProduto, 0),
                    usuario.getId()
            );;

            carregarProdutos(model);

        });

        return painel;
    }

    // Metodo para carregar produtos

    private void carregarProdutos (DefaultTableModel model) {

        model.setRowCount(0);

        produtoService.listarPorRestaurante (usuario.getId())
                .forEach(p -> model.addRow(new Object[]{
                        p.getId(),
                        p.getNome(),
                        p.getDescricao(),
                        p.getPreco(),
                        p.isStatusAtivo() ? "Ativo" : "Inativo"
                }));

    }

    private JScrollPane criarTabela() {

        JTable tabela = new JTable();

        return new JScrollPane(tabela);
    }

    private void criarRodape() {

    }


}
