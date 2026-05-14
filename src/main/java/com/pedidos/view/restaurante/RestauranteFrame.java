package com.pedidos.view.restaurante;

import com.pedidos.application.service.*;
import com.pedidos.domain.entities.CategoriaCardapio;
import com.pedidos.domain.entities.Pedido;
import com.pedidos.domain.entities.Usuario;
import com.pedidos.domain.enums.StatusPedido;
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
    private final PedidoService pedidoService;

    private DefaultTableModel modelPedidos;
    private DefaultTableModel modelProdutos;
    private CategoriaCardapio categoriaSelecionada;

    public RestauranteFrame(Usuario usuario,
                            CategoriaService categoriaService,
                            ProdutoService produtoService,
                            RestauranteService restauranteService,
                            AreaEntregaService areaEntregaService,
                            HorarioService horarioService, PedidoService pedidoService) {
        super("Sistema de Delivery — Painel do Restaurante", 700, 500);
        this.usuario = usuario;
        this.categoriaService = categoriaService;
        this.produtoService = produtoService;
        this.restauranteService = restauranteService;
        this.areaEntregaService = areaEntregaService;
        this.horarioService = horarioService;
        this.pedidoService = pedidoService;
        construirInterface();
        criarAbasMenu();
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

        header.add(titulo, BorderLayout.WEST);
        header.add(nomeLabel, BorderLayout.EAST);

        JLabel centro = new JLabel("Área do restaurante — em desenvolvimento",
                SwingConstants.CENTER);
        centro.setFont(AppFonts.LABEL);

        JButton btnSair = new JButton("Sair");
        btnSair.addActionListener(e -> {

            JPanel painelConfirmarSaida = new JPanel(new BorderLayout());
            JLabel labelConfirmarSaida = new JLabel("Tem certeza que deseja sair?");
            labelConfirmarSaida.setFont(AppFonts.LABEL);
            painelConfirmarSaida.add(labelConfirmarSaida, BorderLayout.CENTER);

            int option = JOptionPane.showConfirmDialog(
                    this,
                    painelConfirmarSaida,
                    "Sair",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (option != JOptionPane.OK_OPTION) {
                return;
            }

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

    private void criarAbasMenu() {

        JTabbedPane abas = new JTabbedPane();

        abas.addTab("Produtos", criarPainelProdutos());
        abas.addTab("Pedidos", criarPainelPedidos());
        abas.addTab("Áreas de Entrega", new JPanel());
        abas.addTab("Horários de funcionamento", new JPanel());
        abas.addTab("Perfil", new JPanel());

        add(abas, BorderLayout.CENTER);
    }

    /**
     * --------------- ABA DE PRODUTOS -------------------
     */

    private JPanel criarPainelProdutos() {

        JPanel painel = new JPanel(new BorderLayout());
        JSplitPane splitPane = new JSplitPane();

        splitPane.setDividerLocation(200);
        splitPane.setLeftComponent(criarTabelaDeCategorias());
        splitPane.setRightComponent(criarTabelaDeProdutos());

        painel.add(splitPane, BorderLayout.CENTER);

        return painel;
    }

    private static class FormularioProduto {

        JTextField campoNome = new JTextField();
        JTextField campoDescricao = new JTextField();
        JTextField campoPreco = new JTextField();

        JComboBox<String> selecionadorCategoria = new JComboBox<>();

        JPanel painel = new JPanel(new GridLayout(0, 1));
    }


    private FormularioProduto criarFormularioProduto() {

        FormularioProduto form = new FormularioProduto();

        form.selecionadorCategoria.addItem("Sem categoria");

        categoriaService
                .listarCategoriasCardapio(usuario.getId())
                .forEach(c ->
                        form.selecionadorCategoria.addItem(c.getNome())
                );

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

    private JPanel criarTabelaDeCategorias() {

        JPanel painel = new JPanel(new BorderLayout());

        DefaultListModel<CategoriaCardapio> model = new DefaultListModel<>();
        JList<CategoriaCardapio> listaCategorias = new JList<>(model);

        categoriaService.listarCategoriasCardapio(usuario.getId()).forEach(model::addElement);

        JButton btnNovaCategoria = new JButton("+");
        JButton btnListarTodos = new JButton("All");
        JButton btnRemoverCategoria = new JButton("-");

        JPanel painelBotoes = new JPanel(new GridLayout(1, 2, 5, 5));

        painelBotoes.add(btnListarTodos);
        painelBotoes.add(btnNovaCategoria);
        painelBotoes.add(btnRemoverCategoria);

        /**------------= Categoria eventos =---------------*/

        // -----------= Criar nova categoria =--------------- //
        btnNovaCategoria.addActionListener(e -> {

            String nome = JOptionPane.showInputDialog(
                    this,
                    "Nome da categoria:"
            );

            if (nome.isBlank()) {
                return;
            }

            String descricao = JOptionPane.showInputDialog(
                    this,
                    "Descrição da Categoria:"
            );

            categoriaService.criarCategoriaCardapio(nome, descricao,  usuario.getId());

            model.clear();

            categoriaService
                    .listarCategoriasCardapio(usuario.getId())
                    .forEach(model::addElement);
        });


        // ----------= Listar todas categorias =----------- //
        btnListarTodos.addActionListener( e -> {

            categoriaSelecionada = null;

            carregarProdutos(categoriaSelecionada);

        });

        // ------------= Remover categoria =-------------- //
        btnRemoverCategoria.addActionListener(e -> {

            CategoriaCardapio categoria = listaCategorias.getSelectedValue();

            if (categoria == null) {
                JOptionPane.showMessageDialog(this, "Selecione uma categoria para remover.");
                return;
            }

            JPanel painelConfirmarRemoverCategoria = new JPanel(new BorderLayout());
            JLabel labelConfirmarRemoverCategoria = new JLabel("Tem certeza que deseja remover esta categoria?");
            painelConfirmarRemoverCategoria.add(labelConfirmarRemoverCategoria, BorderLayout.CENTER);

            int option = DialogJOptionPane("confirm", null,"Remover Categoria",
                    painelConfirmarRemoverCategoria,
                    DialogOptions.OK_CANCEL);

            if (option != JOptionPane.OK_OPTION) {
                return;
            }

            categoriaService.removerCategoriaCardapio(categoria.getId());

            model.clear();

            categoriaService
                    .listarCategoriasCardapio(usuario.getId())
                    .forEach(model::addElement);

            categoriaSelecionada = null;

            carregarProdutos(categoriaSelecionada);
        });

        // ------------= Selecionar categoria =-------------- //
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

    private JPanel criarTabelaDeProdutos() {

        JPanel painel = new JPanel(new BorderLayout());

        String[] atributos = {
                "#",
                "Nome",
                "Descrição",
                "Preço",
                "Status"
        };

        modelProdutos = new DefaultTableModel(atributos, 0);
        JTable tabelaProdutos = new JTable(modelProdutos);

        JToolBar toolBarCrud = new JToolBar();

        JButton btnNovoProduto = new JButton("+ Novo Produto");
        JButton btnEditarProduto = new JButton("Editar");
        JButton btnAtivarProduto = new JButton("Ativar/Desativar");
        JButton btnRemoverProduto = new JButton("Remover");

        carregarProdutos(null);

        toolBarCrud.add(btnNovoProduto);
        toolBarCrud.add(btnEditarProduto);
        toolBarCrud.add(btnAtivarProduto);
        toolBarCrud.add(btnRemoverProduto);

        painel.add(toolBarCrud, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabelaProdutos), BorderLayout.CENTER);

        /**-------------= Produtos Eventos =-----------------*/


        //  ----------= Criar novo produto =-------------- //
        btnNovoProduto.addActionListener(e -> {

            FormularioProduto form = criarFormularioProduto();

            int option = DialogJOptionPane("confirm", null,"Novo Produto", form.painel, DialogOptions.OK_CANCEL);

            if (option != JOptionPane.OK_OPTION) {
                return;
            }

            if (form.campoNome.getText().isBlank()) {

                DialogJOptionPane("message", "Nome obrigatório", "AVISO", null, DialogOptions.WARNING_MESSAGE);

                return;
            }

            if (form.campoPreco.getText().isBlank()) {

                DialogJOptionPane("message", "Preço obrigatório", "AVISO", null, DialogOptions.WARNING_MESSAGE);

                return;
            }

            String categoriaEscolhida = (String) form.selecionadorCategoria.getSelectedItem();
            String categoriaId = null;

            if (!categoriaEscolhida.equals("Sem categoria")) {

                categoriaId = categoriaService.listarCategoriasCardapio(usuario.getId())
                        .stream()
                        .filter(c -> c.getNome().equals(categoriaEscolhida))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Categoria não encontrada"))
                        .getId();
            }

            produtoService.criarProduto(form.campoNome.getText(), form.campoDescricao.getText(),
                    new BigDecimal(form.campoPreco.getText()), categoriaId, usuario.getId());

            carregarProdutos(categoriaSelecionada);
        });


        // -------------= Editar produto =--------------- //

        btnEditarProduto.addActionListener(e -> {

            int ed = lerLinhaSelecionada(tabelaProdutos);

            if (ed == -1) {
                return;
            }

            FormularioProduto form = criarFormularioProduto();

            List<CategoriaCardapio> categoriasCardapio = categoriaService.listarCategoriasCardapio(usuario.getId());

            int option = DialogJOptionPane("confirm", null,"Editar Produto", form.painel, DialogOptions.OK_CANCEL);

            if (option != JOptionPane.OK_OPTION) {
                return;
            }

            if (form.campoNome.getText().isBlank()) {
                form.campoNome.setText((String) tabelaProdutos.getValueAt(ed, 1));
            }

            if (form.campoDescricao.getText().isBlank()) {
                form.campoDescricao.setText((String) tabelaProdutos.getValueAt(ed, 2));
            }

            if (form.campoPreco.getText().isBlank()) {
                form.campoPreco.setText(tabelaProdutos.getValueAt(ed, 3).toString());
            }

            String categoriaEscolhida = (String) form.selecionadorCategoria.getSelectedItem();
            String novoCategoriaId = null;

            if (!categoriaEscolhida.equals("Sem categoria")) {

                novoCategoriaId = categoriasCardapio.stream()
                        .filter(c -> c.getNome().equals(categoriaEscolhida))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Categoria não encontrada"))
                        .getId();
            }

            produtoService.editarProduto((String) tabelaProdutos.getValueAt(ed, 0),
                    usuario.getId(), form.campoNome.getText(), form.campoDescricao.getText(),
                    new BigDecimal(form.campoPreco.getText()), novoCategoriaId);

            carregarProdutos(categoriaSelecionada);
        });


        // -----------= Ativo ou Inativo -------------- //

        btnAtivarProduto.addActionListener(e -> {

            int a = lerLinhaSelecionada(tabelaProdutos);

            if (a == -1) { return; }

            String produtoId = (String) tabelaProdutos.getValueAt(a, 0);

            produtoService.ativarInativar(produtoId, usuario.getId());
            carregarProdutos(categoriaSelecionada);

        });


        // -------------= Remover produto =--------------- //

        btnRemoverProduto.addActionListener(e -> {

            JPanel painelConfirmarRemoverProduto = new JPanel(new BorderLayout());
            JLabel labelConfirmarRemoverProduto = new JLabel("Tem certeza que deseja remover este produto?");
            painelConfirmarRemoverProduto.add(labelConfirmarRemoverProduto, BorderLayout.CENTER);

            int r = lerLinhaSelecionada(tabelaProdutos);

            if (r == -1) { return; }

            int option = DialogJOptionPane("confirm", null,"Remover Produto",
                                            painelConfirmarRemoverProduto,
                                            DialogOptions.OK_CANCEL);

            if (option != JOptionPane.OK_OPTION) {
                return;
            }

            produtoService.removerProduto(
                    (String) tabelaProdutos.getValueAt(r, 0),
                    usuario.getId()
            );

            carregarProdutos(categoriaSelecionada);

        });

        return painel;
    }

    // Metodo para carregar produtos

    private void carregarProdutos(CategoriaCardapio categoria) {

        modelProdutos.setRowCount(0);

        if (categoria != null) {
            produtoService.listarPorRestaurante(usuario.getId())
                    .stream()
                    .filter(p -> p.getCategoriaCardapioId() != null)
                    .filter(p -> p.getCategoriaCardapioId().equals(categoria.getId()))
                    .forEach(p -> modelProdutos.addRow(new Object[]{
                            p.getId(),
                            p.getNome(),
                            p.getDescricao(),
                            p.getPreco(),
                            p.isStatusAtivo() ? "Ativo" : "Inativo"
                    }));
        } else {
            produtoService.listarPorRestaurante(usuario.getId())
                    .forEach(p -> modelProdutos.addRow(new Object[]{
                            p.getId(),
                            p.getNome(),
                            p.getDescricao(),
                            p.getPreco(),
                            p.isStatusAtivo() ? "Ativo" : "Inativo"
                    }));

        }
    }

     //Metodo para ler coluna selecionada

    private int lerLinhaSelecionada(JTable tabela){

        int linhaSelecionada = tabela.getSelectedRow();

        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto.");
        }

        return linhaSelecionada;

    }

    public enum DialogOptions {
        OK_CANCEL,
        YES_NO,
        WARNING_MESSAGE
    }

    private int DialogJOptionPane(String typeShow,
                                         String message,
                                         String title,
                                         JPanel painel,
                                         DialogOptions option) {

        int typeOption = switch (option) {

            case YES_NO -> JOptionPane.YES_NO_OPTION;
            case OK_CANCEL -> JOptionPane.OK_CANCEL_OPTION;
            case WARNING_MESSAGE -> JOptionPane.WARNING_MESSAGE;

        };

        if (typeShow == "confirm") {
            return JOptionPane.showConfirmDialog(
                    this,
                    painel,
                    title,
                    typeOption
            );
        }

        if (typeShow == "message") {
            JOptionPane.showMessageDialog(
                    this,
                    message,
                    title,
                    typeOption
            );
        }

        return 0;
    }

    public JPanel criarPainelPedidos() {

        JPanel painelPedidos = new JPanel(new BorderLayout());
        JPanel painelAcoesPedidos = new JPanel();

        JComboBox<String> filtroStatus = new JComboBox<>();
        filtroStatus.addItem("Todos");
        filtroStatus.addItem("PENDENTE");
        filtroStatus.addItem("PREPARANDO");
        filtroStatus.addItem("SAIU_PARA_ENTREGA");
        filtroStatus.addItem("ENTREGUE");

        JButton btnFiltrarPedidos = new JButton("Filtrar");

        JComboBox<String> novoStatus = new JComboBox<>();
        novoStatus.addItem("PENDENTE");
        novoStatus.addItem("EM_PREPARO");
        novoStatus.addItem("SAIU_PARA_ENTREGA");
        novoStatus.addItem("ENTREGUE");

        JButton btnAtualizarStatus = new JButton("Atualizar Status");

        painelAcoesPedidos.add(new JLabel("Filtro:"));
        painelAcoesPedidos.add(filtroStatus);
        painelAcoesPedidos.add(btnFiltrarPedidos);

        painelAcoesPedidos.add(new JLabel("Novo status:"));
        painelAcoesPedidos.add(novoStatus);
        painelAcoesPedidos.add(btnAtualizarStatus);

        // --- Construindo tabela de pedidos --- //
        String[] atributos = { "ID", "Cliente", "Status", "Total", "Itens" };
         modelPedidos = new DefaultTableModel(atributos, 0);
        JTable tabelaPedidos = new JTable(modelPedidos);
        JScrollPane scrollPedidos = new JScrollPane(tabelaPedidos);

        painelPedidos.add(painelAcoesPedidos, BorderLayout.NORTH);
        painelPedidos.add(scrollPedidos, BorderLayout.CENTER);

        List<Pedido> pedidos = pedidoService.listarPorRestaurante(usuario.getId());
        carregarPedidos(pedidos);

        btnFiltrarPedidos.addActionListener(e -> {

            String statusSelecionado = (String) filtroStatus.getSelectedItem();

            if (statusSelecionado.equals("Todos")) {
                carregarPedidos(pedidoService.listarPorRestaurante(usuario.getId()));
            } else {
                carregarPedidos(pedidoService.listarPorRestaurante(usuario.getId())
                        .stream()
                        .filter(p -> p.getStatus().name().equals(statusSelecionado))
                        .toList());
            }
        });

        btnAtualizarStatus.addActionListener(e -> {

            int linhaSelecionada = tabelaPedidos.getSelectedRow();

            if (linhaSelecionada == -1) {
                JOptionPane.showMessageDialog(this, "Selecione um pedido para atualizar.");
                return;
            }

            String pedidoId = (String) tabelaPedidos.getValueAt(linhaSelecionada, 0);
            StatusPedido statusNovo = StatusPedido.valueOf((String) novoStatus.getSelectedItem());

            try {

                int op = JOptionPane.showConfirmDialog(
                        this,
                        "Atualizarstatus do pedido para" + statusNovo + "?",
                        "Confirmar atualização de status",
                        JOptionPane.OK_CANCEL_OPTION
                );

                if (op != JOptionPane.OK_OPTION) {
                    return;
                }

                pedidoService.atualizarStatus(pedidoId, statusNovo);
                carregarPedidos(pedidoService.listarPorRestaurante(usuario.getId()));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar status: " + ex.getMessage());
            }
        });

        return painelPedidos;
    }

    public void carregarPedidos (List<Pedido> pedidos) {

        modelPedidos.setRowCount(0);

        for (Pedido p : pedidos) {
            modelPedidos.addRow(new Object[]{
                    p.getId(),
                    p.getCliente().getNome(),
                    p.getStatus(),
                    String.format("R$ %.2f", p.getTotal()),
                    p.getItens()
            });

        }
    }
}

