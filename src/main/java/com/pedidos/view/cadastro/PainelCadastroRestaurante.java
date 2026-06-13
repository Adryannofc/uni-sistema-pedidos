package com.pedidos.view.cadastro;

import com.pedidos.controller.RestauranteController;
import com.pedidos.view.util.AppColors;
import com.pedidos.view.util.AppFonts;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class PainelCadastroRestaurante extends JPanel {

    private final CadastroFrame         frame;
    private final RestauranteController restauranteController;

    private JTextField     campoNome;
    private JTextField     campoEmail;
    private JTextField     campoCNPJ;
    private JTextField     campoTelefone;
    private JPasswordField campoSenha;

    public PainelCadastroRestaurante(CadastroFrame frame, RestauranteController restauranteController) {
        this.frame                 = frame;
        this.restauranteController = restauranteController;

        setLayout(new BorderLayout(0, 10));
        setBackground(AppColors.CINZA_FUNDO);
        setBorder(new EmptyBorder(20, 30, 20, 30));

        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setOpaque(false);
        centro.add(criarFormulario());
        centro.add(Box.createVerticalStrut(6));
        centro.add(criarAvisoRestaurante());

        add(centro,              BorderLayout.CENTER);
        add(criarPainelBotoes(), BorderLayout.SOUTH);
    }

    private JPanel criarFormulario() {
        JPanel painel = new JPanel();
        painel.setBackground(AppColors.CINZA_FUNDO);
        painel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Cadastro de Restaurante",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                AppFonts.TITULO,
                AppColors.CINZA_BORDA));

        campoNome     = new JTextField();
        campoEmail    = new JTextField();
        campoCNPJ     = new JTextField();
        campoTelefone = new JTextField();
        campoSenha    = new JPasswordField();

        campoNome.setFont(AppFonts.CAMPO);
        campoEmail.setFont(AppFonts.CAMPO);
        campoCNPJ.setFont(AppFonts.CAMPO);
        campoTelefone.setFont(AppFonts.CAMPO);
        campoSenha.setFont(AppFonts.CAMPO);

        JLabel lNome     = rotulo("Nome:");
        JLabel lEmail    = rotulo("E-mail:");
        JLabel lCNPJ     = rotulo("CNPJ:");
        JLabel lTelefone = rotulo("Telefone:");
        JLabel lSenha    = rotulo("Senha:");

        GroupLayout gl = new GroupLayout(painel);
        painel.setLayout(gl);
        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);

        gl.setHorizontalGroup(gl.createSequentialGroup()
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(lNome)
                        .addComponent(lEmail)
                        .addComponent(lCNPJ)
                        .addComponent(lTelefone)
                        .addComponent(lSenha))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(campoNome,     GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                        .addComponent(campoEmail,    GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                        .addComponent(campoCNPJ,     GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                        .addComponent(campoTelefone, GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                        .addComponent(campoSenha,    GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE))
        );

        gl.setVerticalGroup(gl.createSequentialGroup()
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(lNome).addComponent(campoNome))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(lEmail).addComponent(campoEmail))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(lCNPJ).addComponent(campoCNPJ))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(lTelefone).addComponent(campoTelefone))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(lSenha).addComponent(campoSenha))
        );

        return painel;
    }

    private JPanel criarPainelBotoes() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        painel.setOpaque(false);

        JButton btnCancelar  = botaoSecundario("Cancelar");
        JButton btnCadastrar = botaoPrimario("Cadastrar");

        btnCancelar.setPreferredSize(new Dimension(95, 28));
        btnCadastrar.setPreferredSize(new Dimension(95, 28));

        btnCancelar.addActionListener(e -> cancelar());
        btnCadastrar.addActionListener(e -> cadastrarRestaurante());

        painel.add(btnCancelar);
        painel.add(btnCadastrar);
        return painel;
    }

    private void cancelar() {
        frame.dispose();
    }

    private void cadastrarRestaurante() {
        String nome     = campoNome.getText().trim();
        String email    = campoEmail.getText().trim();
        String cnpj     = campoCNPJ.getText().trim();
        String telefone = campoTelefone.getText().trim();
        String senha    = new String(campoSenha.getPassword());

        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "O campo Nome é obrigatório.", "Campo obrigatório", JOptionPane.WARNING_MESSAGE);
            campoNome.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "O campo E-mail é obrigatório.", "Campo obrigatório", JOptionPane.WARNING_MESSAGE);
            campoEmail.requestFocus();
            return;
        }
        if (cnpj.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "O campo CNPJ é obrigatório.", "Campo obrigatório", JOptionPane.WARNING_MESSAGE);
            campoCNPJ.requestFocus();
            return;
        }
        if (telefone.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "O campo Telefone é obrigatório.", "Campo obrigatório", JOptionPane.WARNING_MESSAGE);
            campoTelefone.requestFocus();
            return;
        }
        if (senha.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "O campo Senha é obrigatório.", "Campo obrigatório", JOptionPane.WARNING_MESSAGE);
            campoSenha.requestFocus();
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(frame, "Informe um e-mail válido (ex: nome@dominio.com).", "E-mail inválido", JOptionPane.WARNING_MESSAGE);
            campoEmail.requestFocus();
            return;
        }

        try {
            restauranteController.cadastrarRestaurante(nome, email, senha, cnpj, telefone);

            JOptionPane.showMessageDialog(frame,
                    "Cadastro enviado com sucesso!\n\n" +
                            "Seu cadastro está aguardando aprovação\ndo administrador do sistema.",
                    "Cadastro em análise", JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();

        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(frame,
                    "Erro ao cadastrar: " + ex.getMessage(),
                    "Erro no cadastro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel criarAvisoRestaurante() {
        JPanel aviso = new JPanel(new BorderLayout());
        aviso.setBackground(new Color(255, 251, 204));
        aviso.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 200, 100), 1),
                new EmptyBorder(6, 10, 6, 10)));
        aviso.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JLabel label = new JLabel("* Sua conta ficará pendente de ativação pelo administrador.");
        label.setFont(AppFonts.HINT);
        aviso.add(label, BorderLayout.CENTER);
        return aviso;
    }

    private JLabel rotulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(AppFonts.LABEL);
        return l;
    }

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