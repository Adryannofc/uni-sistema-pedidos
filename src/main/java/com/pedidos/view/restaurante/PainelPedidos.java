package com.pedidos.view.restaurante;

import com.pedidos.application.service.PedidoService;
import com.pedidos.domain.entities.ItemPedido;
import com.pedidos.domain.entities.Pedido;
import com.pedidos.domain.entities.Usuario;
import com.pedidos.domain.enums.StatusPedido;
import com.pedidos.view.util.AppFonts;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.List;

public class PainelPedidos extends JPanel {

    private final Usuario usuario;
    private final PedidoService pedidoService;

    private DefaultTableModel modelPedidos;

    public PainelPedidos(Usuario usuario, PedidoService pedidoService) {
        super(new BorderLayout());
        this.usuario = usuario;
        this.pedidoService = pedidoService;
        construir();
    }

    private void construir() {
        JPanel painelAcoes = new JPanel();
        JPanel painelDetalhes = new JPanel(new BorderLayout());

        // Filtro de status
        JComboBox<String> filtroStatus = new JComboBox<>();
        filtroStatus.addItem("Todos");
        filtroStatus.addItem("PENDENTE");
        filtroStatus.addItem("EM_PREPARO");
        filtroStatus.addItem("SAIU_PARA_ENTREGA");
        filtroStatus.addItem("ENTREGUE");
        filtroStatus.setFont(AppFonts.LABEL);

        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.setFont(AppFonts.BOTAO);

        // Atualização de status
        JComboBox<String> novoStatus = new JComboBox<>();
        novoStatus.addItem("EM_PREPARO");
        novoStatus.addItem("SAIU_PARA_ENTREGA");
        novoStatus.addItem("ENTREGUE");
        novoStatus.setFont(AppFonts.LABEL);

        JButton btnAtualizarStatus = new JButton("Atualizar Status");
        btnAtualizarStatus.setFont(AppFonts.BOTAO);

        JLabel labelFiltro = new JLabel("Filtro:");
        labelFiltro.setFont(AppFonts.LABEL);
        JLabel labelNovoStatus = new JLabel("Novo status:");
        labelNovoStatus.setFont(AppFonts.LABEL);

        painelAcoes.add(labelFiltro);
        painelAcoes.add(filtroStatus);
        painelAcoes.add(btnFiltrar);
        painelAcoes.add(labelNovoStatus);
        painelAcoes.add(novoStatus);
        painelAcoes.add(btnAtualizarStatus);

        // Tabela de pedidos
        String[] atributos = { "ID", "Cliente", "Status" };
        modelPedidos = new DefaultTableModel(atributos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable tabelaPedidos = new JTable(modelPedidos);
        tabelaPedidos.setFont(AppFonts.LABEL);
        tabelaPedidos.getTableHeader().setFont(AppFonts.LABEL);
        tabelaPedidos.setRowHeight(24);

        add(painelAcoes, BorderLayout.NORTH);
        add(new JScrollPane(tabelaPedidos), BorderLayout.CENTER);
        add(painelDetalhes, BorderLayout.EAST);

        carregarPedidos(pedidoService.listarPorRestaurante(usuario.getId()));

        // Filtrar pedidos
        btnFiltrar.addActionListener(e -> {
            String status = (String) filtroStatus.getSelectedItem();
            if ("Todos".equals(status)) {
                carregarPedidos(pedidoService.listarPorRestaurante(usuario.getId()));
            } else {
                carregarPedidos(pedidoService.listarPorRestaurante(usuario.getId()).stream()
                        .filter(p -> p.getStatus().name().equals(status))
                        .toList());
            }
        });

        // Duplo clique → detalhes do pedido
        tabelaPedidos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int linha = tabelaPedidos.getSelectedRow();
                    if (linha == -1) return;

                    Pedido pedido = pedidoService.buscarPorId((String) tabelaPedidos.getValueAt(linha, 0));

                    JButton btnVoltar = new JButton("Voltar");
                    btnVoltar.setFont(AppFonts.BOTAO);
                    btnVoltar.addActionListener(ev -> {
                        painelDetalhes.removeAll();
                        painelDetalhes.revalidate();
                        painelDetalhes.repaint();
                    });

                    painelDetalhes.removeAll();
                    painelDetalhes.add(new JScrollPane(criarTabelaDetalhes(pedido)), BorderLayout.CENTER);
                    painelDetalhes.add(btnVoltar, BorderLayout.SOUTH);
                    painelDetalhes.revalidate();
                    painelDetalhes.repaint();
                }
            }
        });

        // Atualizar status
        btnAtualizarStatus.addActionListener(e -> {
            int linha = tabelaPedidos.getSelectedRow();
            if (linha == -1) {
                JOptionPane.showMessageDialog(this, "Selecione um pedido para atualizar.");
                return;
            }

            String pedidoId = (String) tabelaPedidos.getValueAt(linha, 0);
            StatusPedido statusNovo = StatusPedido.valueOf((String) novoStatus.getSelectedItem());

            try {
                int op = JOptionPane.showConfirmDialog(this,
                        "Atualizar status do pedido para " + statusNovo + "?",
                        "Confirmar atualização de status",
                        JOptionPane.OK_CANCEL_OPTION);

                if (op != JOptionPane.OK_OPTION) return;

                pedidoService.atualizarStatus(pedidoId, statusNovo);
                carregarPedidos(pedidoService.listarPorRestaurante(usuario.getId()));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar status: " + ex.getMessage());
            }
        });
    }

    // -------------------- Helpers --------------------

    private void carregarPedidos(List<Pedido> pedidos) {
        modelPedidos.setRowCount(0);
        for (Pedido p : pedidos) {
            modelPedidos.addRow(new Object[]{
                    p.getId(),
                    p.getCliente().getNome(),
                    p.getStatus()
            });
        }
    }

    private JTable criarTabelaDetalhes(Pedido pedido) {
        String[] atributos = { "Produto", "Quantidade", "Preço Unitário", "Subtotal" };
        DefaultTableModel model = new DefaultTableModel(atributos, 0);
        JTable tabela = new JTable(model);
        tabela.setFont(AppFonts.LABEL);
        tabela.getTableHeader().setFont(AppFonts.LABEL);
        tabela.setRowHeight(24);

        for (ItemPedido item : pedido.getItens()) {
            model.addRow(new Object[]{
                    item.getNomeProduto(),
                    item.getQuantidade(),
                    item.getPrecoUnitario(),
                    item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade()))
            });
        }
        return tabela;
    }
}
