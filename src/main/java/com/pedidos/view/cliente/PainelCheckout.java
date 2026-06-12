package com.pedidos.view.cliente;

import com.pedidos.controller.ClienteController;
import com.pedidos.controller.PedidoController;
import com.pedidos.model.entity.*;
import com.pedidos.view.util.AppColors;
import com.pedidos.view.util.AppFonts;
import com.pedidos.view.util.session.CarrinhoManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

/**
 * Painel responsável pela aba "Checkout".
 * Exibe resumo do pedido, totais, endereço e botões de confirmação.
 */
public class PainelCheckout extends JPanel {

    private final Usuario usuario;
    private final Cliente cliente;
    private final ClienteController clienteController;
    private final PedidoController pedidoController;
    private final CarrinhoManager carrinho;
    private PainelFazerPedido painelFazerPedido;

    private final NumberFormat moedaBR = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    // Model da tabela de resumo
    private DefaultTableModel modelCheckout;
    private JLabel lblCheckoutSubtotal;
    private JLabel lblCheckoutTaxa;
    private JLabel lblCheckoutTotal;

    // Callback após confirmar pedido
    private Runnable aoConfirmarPedido;

    // Label do endereço — atualizado externamente quando o cliente muda o endereço
    private JLabel endLabel;

    public PainelCheckout(Usuario usuario,
                          Cliente cliente,
                          ClienteController clienteController,
                          PedidoController pedidoController,
                          CarrinhoManager carrinho,
                          PainelFazerPedido painelFazerPedido,
                          Runnable aoConfirmarPedido) {
        this.usuario = usuario;
        this.cliente = cliente;
        this.clienteController = clienteController;
        this.pedidoController = pedidoController;
        this.carrinho = carrinho;
        this.painelFazerPedido = painelFazerPedido;
        this.aoConfirmarPedido = aoConfirmarPedido;

        construir();
    }

    private void construir() {
        setLayout(new BorderLayout(0, 12));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(criarResumoCheckout(), BorderLayout.CENTER);
        add(criarRodapeCheckout(), BorderLayout.SOUTH);
    }

    private JPanel criarResumoCheckout() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(Color.WHITE);
        painel.setBorder(titledBorder("Resumo do Pedido"));

        String[] colunas = {"Produto", "Qtd", "Preço unit.", "Subtotal"};
        modelCheckout = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable tabela = new JTable(modelCheckout);
        tabela.setFont(AppFonts.LABEL);
        tabela.setRowHeight(30);
        tabela.setGridColor(new Color(220, 220, 220));
        tabela.setShowGrid(true);
        tabela.setSelectionBackground(new Color(220, 235, 255));

        configurarHeader(tabela);

        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 1; i <= 3; i++) tabela.getColumnModel().getColumn(i).setCellRenderer(centro);

        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);
        painel.add(criarTotaisCheckout(), BorderLayout.SOUTH);
        return painel;
    }

    private JPanel criarTotaisCheckout() {
        String zero = moedaBR.format(BigDecimal.ZERO);

        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createEmptyBorder(10, 0, 8, 16));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 12, 3, 12);

        lblCheckoutSubtotal = adicionarLinhaTotal(painel, gbc, "Subtotal:", zero, false, 0);
        lblCheckoutTaxa = adicionarLinhaTotal(painel, gbc, "Taxa de entrega:", zero, false, 1);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JSeparator sep = new JSeparator();
        sep.setPreferredSize(new Dimension(240, 1));
        painel.add(sep, gbc);
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;

        lblCheckoutTotal = adicionarLinhaTotal(painel, gbc, "TOTAL:", zero, true, 3);

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        wrapper.setBackground(Color.WHITE);
        wrapper.add(painel);
        return wrapper;
    }

    private JLabel adicionarLinhaTotal(JPanel painel, GridBagConstraints gbc,
                                       String label, String valor,
                                       boolean negrito, int linha) {
        Font fonte = negrito ? AppFonts.TITULO : AppFonts.LABEL;

        gbc.gridy = linha;
        gbc.gridx = 0; gbc.anchor = GridBagConstraints.EAST;
        JLabel lbl = new JLabel(label);
        lbl.setFont(fonte);
        painel.add(lbl, gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.EAST;
        JLabel val = new JLabel(valor);
        val.setFont(fonte);
        painel.add(val, gbc);
        return val;
    }

    private JPanel criarRodapeCheckout() {
        JPanel rodape = new JPanel(new BorderLayout(0, 10));
        rodape.setBackground(Color.WHITE);

        JPanel enderecoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        enderecoPanel.setBackground(Color.WHITE);
        enderecoPanel.setBorder(titledBorder("Endereço de Entrega"));

        JLabel icone = new JLabel("📍");
        icone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        endLabel = new JLabel(textoEndereco());
        endLabel.setFont(AppFonts.LABEL);

        enderecoPanel.add(icone);
        enderecoPanel.add(endLabel);

        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        botoesPanel.setBackground(Color.WHITE);

        JButton btnCancelar = criarBotaoSecundario("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(110, 36));
        btnCancelar.addActionListener(e -> voltar());

        JButton btnConfirmar = criarBotaoPrimario("Confirmar Pedido", 165, 36);
        btnConfirmar.addActionListener(e -> confirmarPedido());

        botoesPanel.add(btnCancelar);
        botoesPanel.add(btnConfirmar);

        rodape.add(enderecoPanel, BorderLayout.CENTER);
        rodape.add(botoesPanel, BorderLayout.SOUTH);
        return rodape;
    }

    private void voltar() {
        // Dispara evento para ClienteFrame voltar à aba "Fazer Pedido"
        if (aoConfirmarPedido != null) {
            SwingUtilities.invokeLater(() -> {
                Component comp = this;
                while (comp != null && !(comp instanceof ClienteFrame)) {
                    comp = comp.getParent();
                }
                if (comp instanceof ClienteFrame frame) {
                    frame.selecionarAba(0);
                }
            });
        }
    }

    /**
     * Finaliza o pedido — valida carrinho → valida endereço → cria Pedido →
     * esvazia carrinho → atualiza UI.
     */
    public void confirmarPedido() {
        if (carrinho.estaVazio()) {
            JOptionPane.showMessageDialog(this,
                    "Carrinho vazio! Adicione itens antes de confirmar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Restaurante restauranteSelecionado = painelFazerPedido.getRestauranteSelecionado();
        if (restauranteSelecionado == null) {
            JOptionPane.showMessageDialog(this,
                    "Nenhum restaurante selecionado. Volte à aba Fazer Pedido.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            voltar();
            return;
        }

        Optional<Endereco> enderecoPadrao = cliente.getEnderecoPadrao();
        if (enderecoPadrao.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Configure um endereço de entrega antes de confirmar.\n" +
                            "Acesse: Perfil > Endereço.",
                    "Endereço obrigatório", JOptionPane.WARNING_MESSAGE);
            // Redireciona para aba Perfil
            Component comp = this;
            while (comp != null && !(comp instanceof ClienteFrame)) {
                comp = comp.getParent();
            }
            if (comp instanceof ClienteFrame frame) {
                frame.selecionarAba(3);
            }
            return;
        }

        try {
            // Converte CarrinhoManager → Carrinho (entidade de domínio)
            Carrinho carrinhoDominio = new Carrinho(cliente.getId(), restauranteSelecionado.getId());
            for (CarrinhoManager.ItemCarrinho item : carrinho.getItens()) {
                carrinhoDominio.adicionarItem(
                        item.getProduto().getId(),
                        item.getProduto().getNome(),
                        item.getQuantidade(),
                        item.getProduto().getPreco()
                );
            }

            // Código de confirmação de entrega — últimos 4 dígitos do CPF
            String cpfDigitos = cliente.getCpf().replaceAll("[^0-9]", "");
            String codigoConfirmacao = cpfDigitos.substring(cpfDigitos.length() - 4);

            Pedido pedido = pedidoController.criarPedido(
                    cliente,
                    restauranteSelecionado,
                    carrinhoDominio,
                    enderecoPadrao.get(),
                    codigoConfirmacao,
                    carrinho.getTaxaEntrega()
            );

            // Limpa sessão local
            carrinho.esvaziar();

            JOptionPane.showMessageDialog(this,
                    "✅ Pedido confirmado com sucesso!\n" +
                            "ID: " + pedido.getId().toUpperCase() + "\n" +
                            "Total: " + moedaBR.format(pedido.getTotal()) + "\n" +
                            "Código de confirmação de entrega: [ " + codigoConfirmacao + " ]",
                    "Pedido realizado", JOptionPane.INFORMATION_MESSAGE);

            // Notifica ClienteFrame para atualizar UI e ir para "Meus Pedidos"
            if (aoConfirmarPedido != null) {
                aoConfirmarPedido.run();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao confirmar pedido:\n" + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Sincroniza modelCheckout e totais do checkout com o estado atual do CarrinhoManager. */
    public void sincronizar() {
        modelCheckout.setRowCount(0);
        if (!carrinho.estaVazio()) {
            for (CarrinhoManager.ItemCarrinho item : carrinho.getItens()) {
                modelCheckout.addRow(new Object[]{
                        item.getProduto().getNome(),
                        item.getQuantidade(),
                        moedaBR.format(item.getProduto().getPreco()),
                        moedaBR.format(item.calcularSubtotal())
                });
            }
        }
        BigDecimal sub = carrinho.estaVazio() ? BigDecimal.ZERO : carrinho.calcularSubtotal();
        BigDecimal taxa = carrinho.estaVazio() ? BigDecimal.ZERO : carrinho.getTaxaEntrega();
        BigDecimal total = carrinho.estaVazio() ? BigDecimal.ZERO : carrinho.calcularTotal();
        if (lblCheckoutSubtotal != null) lblCheckoutSubtotal.setText(moedaBR.format(sub));
        if (lblCheckoutTaxa != null) lblCheckoutTaxa.setText(moedaBR.format(taxa));
        if (lblCheckoutTotal != null) lblCheckoutTotal.setText(moedaBR.format(total));
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

    /** Atualiza o label do endereço quando o cliente altera seu endereço padrão. */
    public void atualizarEndereco() {
        if (endLabel != null) endLabel.setText(textoEndereco());
    }

    private String textoEndereco() {
        return cliente.getEnderecoPadrao()
                .map(e -> e.getRua() + ", " + e.getNumero() + " - " +
                        e.getBairro() + ", " + e.getCidade() + " - " + e.getEstado())
                .orElse("⚠ Nenhum endereço cadastrado. Acesse Perfil > Endereço.");
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

