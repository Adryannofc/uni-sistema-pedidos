package com.pedidos.view.cadastro;

import com.pedidos.view.util.AppColors;
import com.pedidos.view.util.AppFonts;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Etapa 1 do wizard de cadastro.
 * Permite ao usuário selecionar entre "Cliente" e "Restaurante".
 */
public class PainelTipo extends JPanel {

    private final CadastroFrame frame;

    private JRadioButton rbCliente;
    private JRadioButton rbRestaurante;

    public PainelTipo(CadastroFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout(0, 10));
        setBackground(AppColors.CINZA_FUNDO);
        setBorder(new EmptyBorder(20, 30, 20, 30));

        add(criarPainelSelecao(), BorderLayout.CENTER);
        add(criarPainelBotoes(),  BorderLayout.SOUTH);
    }

    // ── seções ────────────────────────────────────────────────────────────────

    private JPanel criarPainelSelecao() {
        JPanel painel = new JPanel();
        painel.setBackground(AppColors.CINZA_FUNDO);
        painel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Tipo de Cadastro",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                AppFonts.LABEL,
                AppColors.TEXTO_SECUNDARIO));

        rbCliente     = new JRadioButton("Cliente");
        rbRestaurante = new JRadioButton("Restaurante");

        rbCliente.setFont(AppFonts.LABEL);
        rbRestaurante.setFont(AppFonts.LABEL);
        rbCliente.setOpaque(false);
        rbRestaurante.setOpaque(false);
        rbCliente.setSelected(true); // padrão

        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rbCliente);
        grupo.add(rbRestaurante);

        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.add(Box.createVerticalStrut(15));
        painel.add(rbCliente);
        painel.add(Box.createVerticalStrut(10));
        painel.add(rbRestaurante);
        painel.add(Box.createVerticalStrut(15));

        return painel;
    }

    private JPanel criarPainelBotoes() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        painel.setOpaque(false);

        JButton btnCancelar = botaoSecundario("Cancelar");
        JButton btnProximo  = botaoPrimario("Próximo");

        btnCancelar.setPreferredSize(new Dimension(95, 28));
        btnProximo.setPreferredSize(new Dimension(95, 28));

        btnCancelar.addActionListener(e -> cancelar());
        btnProximo.addActionListener(e -> avancarEtapa());

        painel.add(btnCancelar);
        painel.add(btnProximo);
        return painel;
    }

    // ── ações ─────────────────────────────────────────────────────────────────

    private void cancelar() {
        frame.dispose();
    }

    private void avancarEtapa() {
        if (rbCliente.isSelected()) {
            frame.mostrarCard(CadastroFrame.CARD_CLIENTE);
        } else {
            frame.mostrarCard(CadastroFrame.CARD_RESTAURANTE);
        }
    }

    // ── helpers de botões ─────────────────────────────────────────────────────

    private JButton botaoPrimario(String texto) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color cor = getModel().isPressed()  ? AppColors.AZUL_PRESSIONADO
                        : getModel().isRollover() ? AppColors.AZUL_HOVER
                        :                           AppColors.AZUL_PRIMARIO;
                g2.setColor(cor);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(AppFonts.BOTAO);
        btn.setForeground(AppColors.TEXTO_BRANCO);
        btn.setBackground(AppColors.AZUL_PRIMARIO);
        btn.setOpaque(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton botaoSecundario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(AppFonts.BOTAO);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
