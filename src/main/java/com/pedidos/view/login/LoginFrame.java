package com.pedidos.view.login;

import com.pedidos.application.service.*;
import com.pedidos.domain.entities.Usuario;
import com.pedidos.domain.enums.TipoUsuario;
import com.pedidos.domain.repository.RestauranteRepository;
import com.pedidos.view.admin.AdminFrame;
import com.pedidos.view.cadastro.CadastroFrame;
import com.pedidos.view.cliente.ClienteFrame;
import com.pedidos.view.restaurante.RestauranteFrame;
import com.pedidos.view.util.base.BaseFrame;
import com.pedidos.view.util.session.SessionManager;
import com.pedidos.view.util.AppColors;
import com.pedidos.view.util.AppFonts;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class LoginFrame extends BaseFrame {
    private final AutenticacaoService autenticacaoService;
    private final AdminService adminService;
    private final ClienteService clienteService;
    private final CategoriaService categoriaService;
    private final ProdutoService produtoService;
    private final RestauranteService restauranteService;
    private final PedidoService pedidoService;
    private final CarrinhoService carrinhoService;
    private final RestauranteRepository restauranteRepo;
    private final AreaEntregaService areaEntregaService;
    private final HorarioService horarioService;

    private JTextField campoEmail;
    private JPasswordField campoSenha;
    private JCheckBox checkLembrar;
    private JButton botaoCancelar;
    private JButton botaoEntrar;
    private JLabel labelConexao;
    private JLabel linkCadastrar;  // ← linha que faltava

    public LoginFrame(AutenticacaoService autenticacaoService,
                      AdminService adminService,
                      ClienteService clienteService,
                      CategoriaService categoriaService,
                      ProdutoService produtoService,
                      RestauranteService restauranteService,
                      PedidoService pedidoService,
                      CarrinhoService carrinhoService,
                      RestauranteRepository restauranteRepo,
                      AreaEntregaService areaEntregaService,
                      HorarioService horarioService) {
        super("Sistema de Delivery - Login", 500, 310);
        this.autenticacaoService = autenticacaoService;
        this.adminService = adminService;
        this.clienteService = clienteService;
        this.categoriaService = categoriaService;
        this.produtoService = produtoService;
        this.restauranteService = restauranteService;
        this.carrinhoService = carrinhoService;
        this.restauranteRepo = restauranteRepo;
        this.areaEntregaService = areaEntregaService;
        this.horarioService = horarioService;
        this.pedidoService = pedidoService;
        construirInterface();
    }

    // CONSTRUCAO

    private void construirInterface() {
        setLayout(new BorderLayout());
        add(criarPainelCentral(), BorderLayout.CENTER);
        add(criarStatusBar(), BorderLayout.SOUTH);
        configurarMenuBar();
    }

    private JPanel criarPainelCentral() {
        JPanel externo = new JPanel(new BorderLayout(0, 6));
        externo.setBackground(AppColors.CINZA_FUNDO);
        externo.setBorder(new EmptyBorder(10, 15, 8, 15));
        externo.add(criarPainelFormulario(), BorderLayout.CENTER);
        externo.add(criarLabelHint(), BorderLayout.SOUTH);
        return externo;
    }

    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel();
        painel.setBackground(AppColors.CINZA_FUNDO);
        painel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "Acesso ao Sistema",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                AppFonts.LABEL,
                AppColors.TEXTO_SECUNDARIO));

        JLabel labelEmail = rotulo("E-mail:");
        JLabel labelSenha = rotulo("Senha:");

        campoEmail = new JTextField(22);
        campoEmail.setFont(AppFonts.CAMPO);

        campoSenha = new JPasswordField(22);
        campoSenha.setFont(AppFonts.CAMPO);

        checkLembrar = new JCheckBox("Lembrar acesso");
        checkLembrar.setFont(AppFonts.LABEL);
        checkLembrar.setOpaque(false);

        linkCadastrar = new JLabel("<html><a href='#'>Não tem conta? Cadastre-se</a></html>");
        linkCadastrar.setFont(AppFonts.LABEL);
        linkCadastrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        linkCadastrar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                abrirCadastro();
            }
        });

        botaoCancelar = botaoSecundario("Cancelar");
        botaoCancelar.addActionListener(e -> cancelar());

        botaoEntrar = botaoPrimario("Entrar");
        botaoEntrar.addActionListener(e -> autenticarUsuario());

        getRootPane().setDefaultButton(botaoEntrar);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        painelBotoes.setOpaque(false);
        botaoCancelar.setPreferredSize(new Dimension(95, 28));
        botaoEntrar.setPreferredSize(new Dimension(95, 28));
        painelBotoes.add(botaoCancelar);
        painelBotoes.add(botaoEntrar);

        GroupLayout gl = new GroupLayout(painel);
        painel.setLayout(gl);
        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);

        gl.setHorizontalGroup(gl.createSequentialGroup()
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(labelEmail)
                        .addComponent(labelSenha))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(campoEmail,   GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                        .addComponent(campoSenha,   GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                        .addComponent(checkLembrar)
                        .addComponent(linkCadastrar)
                        .addComponent(painelBotoes, GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE))
        );

        gl.setVerticalGroup(gl.createSequentialGroup()
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelEmail)
                        .addComponent(campoEmail))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelSenha)
                        .addComponent(campoSenha))
                .addGap(4)
                .addComponent(checkLembrar)
                .addComponent(linkCadastrar)
                .addGap(8)
                .addComponent(painelBotoes)
        );

        return painel;
    }

    private JLabel criarLabelHint() {
        JLabel hint = new JLabel(
                "<html><b>Usuários de teste:</b> " +
                        "admin@delivery.com / admin123 &nbsp;|&nbsp; " +
                        "burguer@delivery.com / 123456 &nbsp;|&nbsp; " +
                        "joao@email.com / 123456</html>"
        );
        hint.setFont(AppFonts.HINT);
        hint.setForeground(AppColors.TEXTO_SECUNDARIO);
        hint.setBorder(new EmptyBorder(4, 2, 0, 0));
        return hint;
    }

    private JPanel criarStatusBar() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(AppColors.CINZA_STATUS);
        barra.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, AppColors.CINZA_BORDA),
                new EmptyBorder(3, 8, 3, 8)
        ));

        JLabel labelVersao = new JLabel("Sistema de Delivery v1.0");
        labelVersao.setFont(AppFonts.STATUS);

        labelConexao = new JLabel("Desconectado");
        labelConexao.setFont(AppFonts.STATUS);
        labelConexao.setForeground(AppColors.TEXTO_SECUNDARIO);

        barra.add(labelVersao,  BorderLayout.WEST);
        barra.add(labelConexao, BorderLayout.EAST);
        return barra;
    }

    private void configurarMenuBar() {
        JMenuBar menuBar  = new JMenuBar();
        JMenu menuSistema = new JMenu("Sistema");
        menuSistema.setFont(AppFonts.MENU);

        JMenuItem itemSair = new JMenuItem("Sair");
        itemSair.setFont(AppFonts.MENU);
        itemSair.addActionListener(e -> cancelar());

        menuSistema.add(itemSair);
        menuBar.add(menuSistema);
        setJMenuBar(menuBar);
    }

    // ACOES

    private void abrirCadastro() {
        new CadastroFrame(clienteService, restauranteService).setVisible(true);
    }

    private void autenticarUsuario() {
        String email = campoEmail.getText().trim();
        String senha = new String(campoSenha.getPassword());

        if (!validarCampos(email, senha)) return;

        try {
            Usuario usuario = autenticacaoService.autenticar(email, senha);
            SessionManager.getInstance().iniciarSessao(usuario, this);
            labelConexao.setText("Conectado: " + usuario.getNome());
            redirecionarConformalPapel(usuario);

        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if (msg != null && msg.toLowerCase().contains("inválido")) {
                JOptionPane.showMessageDialog(this,
                        "E-mail ou senha inválidos.\nVerifique seus dados e tente novamente.",
                        "Falha no Login", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erro ao conectar:\n" + msg,
                        "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
            }
            campoSenha.setText("");
            campoSenha.requestFocus();
        }
    }

    private boolean validarCampos(String email, String senha) {
        if (email.isEmpty() && senha.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Preencha o e-mail e a senha para continuar.",
                    "Campos obrigatórios", JOptionPane.WARNING_MESSAGE);
            campoEmail.requestFocus();
            return false;
        }
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "O campo E-mail é obrigatório.",
                    "Campo obrigatório", JOptionPane.WARNING_MESSAGE);
            campoEmail.requestFocus();
            return false;
        }
        if (senha.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "O campo Senha é obrigatório.",
                    "Campo obrigatório", JOptionPane.WARNING_MESSAGE);
            campoSenha.requestFocus();
            return false;
        }
        return true;
    }

    private void redirecionarConformalPapel(Usuario usuario) {
        TipoUsuario tipo = usuario.getTipoUsuario();
        JFrame proximo;

        switch (tipo) {
            case ADMIN       -> proximo = new AdminFrame(usuario);
            case RESTAURANTE -> proximo = new RestauranteFrame(
                    usuario,
                    categoriaService,
                    produtoService,
                    restauranteService,
                    areaEntregaService,
                    horarioService,
                    pedidoService);
            case CLIENTE     -> proximo = new ClienteFrame(usuario);
            default -> {
                JOptionPane.showMessageDialog(this,
                        "Tipo de usuário desconhecido: " + tipo,
                        "Erro de Configuração", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        SessionManager.getInstance().trocarFrame(proximo);
    }

    private void cancelar() {
        int r = JOptionPane.showConfirmDialog(this,
                "Deseja sair do sistema?", "Confirmar Saída",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (r == JOptionPane.YES_OPTION) System.exit(0);
    }

    // Helpers de componentes

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