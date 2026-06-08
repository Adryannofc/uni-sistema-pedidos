package com.pedidos.view.cadastro;

import com.pedidos.model.service.ClienteService;
import com.pedidos.view.util.AppColors;
import com.pedidos.view.util.AppFonts;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Etapa 2A do wizard — formulário de cadastro de Cliente.
 * Campos: Nome, E-mail, CPF, Telefone, Senha.
 */
public class PainelCadastroCliente extends JPanel {

    private final CadastroFrame  frame;
    private final ClienteService clienteService;

    private JTextField     campoNome;
    private JTextField     campoEmail;
    private JTextField     campoCPF;
    private JTextField     campoTelefone;
    private JPasswordField campoSenha;

    public PainelCadastroCliente(CadastroFrame frame, ClienteService clienteService) {
        this.frame          = frame;
        this.clienteService = clienteService;

        setLayout(new BorderLayout(0, 10));
        setBackground(AppColors.CINZA_FUNDO);
        setBorder(new EmptyBorder(20, 30, 20, 30));

        add(criarFormulario(), BorderLayout.CENTER);
        add(criarPainelBotoes(), BorderLayout.SOUTH);
    }

    // ── seções ────────────────────────────────────────────────────────────────

    private JPanel criarFormulario() {
        JPanel painel = new JPanel();
        painel.setBackground(AppColors.CINZA_FUNDO);
        painel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Cadastro — Cliente",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                AppFonts.LABEL,
                AppColors.TEXTO_SECUNDARIO));

        campoNome     = new JTextField();
        campoEmail    = new JTextField();
        campoCPF      = new JTextField();
        campoTelefone = new JTextField();
        campoSenha    = new JPasswordField();

        campoNome.setFont(AppFonts.CAMPO);
        campoEmail.setFont(AppFonts.CAMPO);
        campoCPF.setFont(AppFonts.CAMPO);
        campoTelefone.setFont(AppFonts.CAMPO);
        campoSenha.setFont(AppFonts.CAMPO);

        JLabel lNome     = rotulo("Nome:");
        JLabel lEmail    = rotulo("E-mail:");
        JLabel lCPF      = rotulo("CPF:");
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
                        .addComponent(lCPF)
                        .addComponent(lTelefone)
                        .addComponent(lSenha))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(campoNome,     GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                        .addComponent(campoEmail,    GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                        .addComponent(campoCPF,      GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                        .addComponent(campoTelefone, GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                        .addComponent(campoSenha,    GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE))
        );

        gl.setVerticalGroup(gl.createSequentialGroup()
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(lNome).addComponent(campoNome))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(lEmail).addComponent(campoEmail))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(lCPF).addComponent(campoCPF))
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
        btnCadastrar.addActionListener(e -> cadastrarCliente());

        painel.add(btnCancelar);
        painel.add(btnCadastrar);
        return painel;
    }

    // ── ações ─────────────────────────────────────────────────────────────────

    private void cancelar() {
        frame.dispose();
    }

    private void cadastrarCliente() {
        String nome     = campoNome.getText().trim();
        String email    = campoEmail.getText().trim();
        String cpf      = campoCPF.getText().trim();
        String telefone = campoTelefone.getText().trim();
        String senha    = new String(campoSenha.getPassword());

        // Validação de campos obrigatórios
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
        if (cpf.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "O campo CPF é obrigatório.", "Campo obrigatório", JOptionPane.WARNING_MESSAGE);
            campoCPF.requestFocus();
            return;
        }
        if (senha.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "O campo Senha é obrigatório.", "Campo obrigatório", JOptionPane.WARNING_MESSAGE);
            campoSenha.requestFocus();
            return;
        }

        // Validação de formato e-mail
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(frame, "Informe um e-mail válido (ex: nome@dominio.com).", "E-mail inválido", JOptionPane.WARNING_MESSAGE);
            campoEmail.requestFocus();
            return;
        }

        // Validação de CPF (mínimo 11 dígitos numéricos)
        String cpfNumeros = cpf.replaceAll("[^0-9]", "");
        if (cpfNumeros.length() != 11) {
            JOptionPane.showMessageDialog(frame, "CPF inválido. Informe 11 dígitos numéricos.", "CPF inválido", JOptionPane.WARNING_MESSAGE);
            campoCPF.requestFocus();
            return;
        }

        try {
            clienteService.cadastrarCliente(nome, email, senha, cpfNumeros, telefone);
            JOptionPane.showMessageDialog(frame,
                    "Cadastro realizado com sucesso!\nBem-vindo(a), " + nome + "!",
                    "Cadastro realizado", JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();

        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(frame,
                    "Erro ao cadastrar: " + ex.getMessage(),
                    "Erro no cadastro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── helpers ───────────────────────────────────────────────────────────────

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
