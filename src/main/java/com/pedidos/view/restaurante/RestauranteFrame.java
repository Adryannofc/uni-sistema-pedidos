package com.pedidos.view.restaurante;

import com.pedidos.application.service.*;
import com.pedidos.domain.entities.Usuario;
import com.pedidos.view.util.AppColors;
import com.pedidos.view.util.AppFonts;
import com.pedidos.view.util.base.BaseFrame;
import com.pedidos.view.util.session.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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

    private JPanel criarPainelProdutos() {

        JPanel painel = new JPanel(new BorderLayout());
        JSplitPane splitPane = new JSplitPane();

        splitPane.setDividerLocation(200);
        splitPane.setLeftComponent(criarPainelCategorias());
        splitPane.setRightComponent(criarPainelDeProdutos());

        painel.add(splitPane, BorderLayout.CENTER);

        return painel;
    }

    private JScrollPane criarPainelCategorias() {

        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> listaCategorias = new JList<>(model);

        categoriaService.listarCategoriasCardapio(usuario.getId())
                .forEach(c ->
                        model.addElement(c.getNome()));

        return new JScrollPane(listaCategorias);
    }

    private JPanel criarPainelDeProdutos() {

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

        produtoService.listarPorRestaurante(usuario.getId())
                .forEach(p -> model.addRow(new Object[]{
                        p.getId(),
                        p.getNome(),
                        p.getDescricao(),
                        p.getPreco(),
                        p.isStatusAtivo() ? "Ativo" : "Inativo"
                }));

        painel.add(criarToolbarCrud(), BorderLayout.NORTH);
        painel.add(new JScrollPane(tabelaProdutos), BorderLayout.CENTER);

        return painel;
    }

    private JToolBar criarToolbarCrud() {

        JToolBar toolBar = new JToolBar();

        JButton btnNovoProduto = new JButton("+ Novo Produto");
        JButton btnEditarProduto = new JButton("Editar");
        JButton btnAtivarProduto = new JButton("Ativar");
        JButton btnRemoverProduto = new JButton("Remover");

        toolBar.add(btnNovoProduto);
        toolBar.add(btnEditarProduto);
        toolBar.add(btnAtivarProduto);
        toolBar.add(btnRemoverProduto);

        return toolBar;
    }

    private JScrollPane criarTabela() {

        JTable tabela = new JTable();

        return new JScrollPane(tabela);
    }

    private void criarRodape() {

    }


}
