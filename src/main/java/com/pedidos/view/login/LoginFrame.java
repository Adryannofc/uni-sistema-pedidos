package com.pedidos.view.login;

import com.pedidos.controller.AutenticacaoController;
import com.pedidos.controller.ClienteController;
import com.pedidos.controller.RestauranteController;
import com.pedidos.controller.dto.UsuarioSessaoDTO;
import com.pedidos.model.entity.*;
import com.pedidos.model.enums.TipoUsuario;
import com.pedidos.controller.*;
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

    private final AutenticacaoController autenticacaoController;
    private final ClienteController      clienteController;
    private final RestauranteController  restauranteController;
    private final AdminController          adminController;
    private final EnderecoController    enderecoController;
    private final CategoriaController   categoriaController;
    private final ProdutoController   produtoController;
    private final PedidoController    pedidoController;
    private final CarrinhoController  carrinhoController;
    private final AreaEntregaController  areaEntregaController;
    private final HorarioController     horarioController;

    private JTextField     campoEmail;
    private JPasswordField campoSenha;
    private JButton        botaoCancelar;
    private JButton        botaoEntrar;
    private JLabel         labelConexao;
    private JPanel         painelNovoCadastro;

    public LoginFrame(AutenticacaoController autenticacaoController,
                      ClienteController clienteController,
                      RestauranteController restauranteController,
                      AdminController adminController,
                      EnderecoController enderecoController,
                      CategoriaController categoriaController,
                      ProdutoController produtoController,
                      PedidoController pedidoController,
                      CarrinhoController carrinhoController,
                      AreaEntregaController areaEntregaController,
                      HorarioController horarioController) {
        super("Sistema de Delivery - Login", 500, 310);
        this.autenticacaoController = autenticacaoController;
        this.clienteController      = clienteController;
        this.restauranteController  = restauranteController;
        this.adminController          = adminController;
        this.enderecoController    = enderecoController;
        this.categoriaController   = categoriaController;
        this.produtoController     = produtoController;
        this.pedidoController      = pedidoController;
        this.carrinhoController  = carrinhoController;
        this.areaEntregaController  = areaEntregaController;
        this.horarioController      = horarioController;
        construirInterface();
    }

    // CONSTRUÇÃO

    private void construirInterface() {
        setLayout(new BorderLayout());
        add(criarPainelCentral(), BorderLayout.CENTER);
        add(criarStatusBar(),     BorderLayout.SOUTH);
        configurarMenuBar();
    }

    private JPanel criarPainelCentral() {
        JPanel externo = new JPanel(new BorderLayout(0, 6));
        externo.setBackground(AppColors.CINZA_FUNDO);
        externo.setBorder(new EmptyBorder(10, 15, 8, 15));
        externo.add(criarPainelFormulario(), BorderLayout.CENTER);
        externo.add(criarLabelHint(),        BorderLayout.SOUTH);
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

        painelNovoCadastro = criarLinksNovoCadastro();

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
                        .addComponent(campoEmail,    GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                        .addComponent(campoSenha,    GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                        .addComponent(painelNovoCadastro)
                        .addComponent(painelBotoes,  GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE))
        );

        gl.setVerticalGroup(gl.createSequentialGroup()
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelEmail).addComponent(campoEmail))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelSenha).addComponent(campoSenha))
                .addComponent(painelNovoCadastro)
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

    // AÇÕES

    private JPanel criarLinksNovoCadastro() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        painel.setOpaque(false);

        JLabel prefixo = new JLabel("Novo cadastro: ");
        prefixo.setFont(AppFonts.LABEL);

        JLabel linkCliente     = criarLinkCadastro("Cliente",     () -> abrirCadastroCliente());
        JLabel separador       = new JLabel(" | ");
        separador.setFont(AppFonts.LABEL);
        JLabel linkRestaurante = criarLinkCadastro("Restaurante", () -> abrirCadastroRestaurante());

        painel.add(prefixo);
        painel.add(linkCliente);
        painel.add(separador);
        painel.add(linkRestaurante);
        return painel;
    }

    private JLabel criarLinkCadastro(String texto, Runnable acao) {
        JLabel link = new JLabel("<html><a href='#'>" + texto + "</a></html>");
        link.setFont(AppFonts.LABEL);
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                acao.run();
            }
        });
        return link;
    }

    private void abrirCadastroCliente() {
        CadastroFrame f = new CadastroFrame(clienteController, restauranteController);
        f.mostrarCard(CadastroFrame.CARD_CLIENTE);
        f.setVisible(true);
    }

    private void abrirCadastroRestaurante() {
        CadastroFrame f = new CadastroFrame(clienteController, restauranteController);
        f.mostrarCard(CadastroFrame.CARD_RESTAURANTE);
        f.setVisible(true);
    }

    private void autenticarUsuario() {
        String email = campoEmail.getText().trim();
        String senha = new String(campoSenha.getPassword());

        if (!validarCampos(email, senha)) return;

        try {
            Usuario usuario = autenticacaoController.autenticar(email, senha);
            SessionManager.getInstance().iniciarSessao(
                    new UsuarioSessaoDTO(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getTipoUsuario()),
                    this);
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
            case ADMIN -> proximo = new AdminFrame(usuario, adminController, categoriaController);
            case RESTAURANTE -> proximo = new RestauranteFrame(
                    usuario,
                    categoriaController,
                    produtoController,
                    restauranteController,
                    areaEntregaController,
                    horarioController,
                    pedidoController,
                    autenticacaoController,
                    this::abrirTelaLogin);
            case CLIENTE -> {
                if (!(usuario instanceof Cliente)) {
                    JOptionPane.showMessageDialog(this,
                            "Usuário autenticado não é um Cliente válido.",
                            "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                proximo = new ClienteFrame(
                        usuario,
                        (Cliente) usuario,
                        clienteController,
                        enderecoController,
                        categoriaController,
                        restauranteController,
                        produtoController,
                        pedidoController,
                        carrinhoController,
                        areaEntregaController,
                        this::abrirTelaLogin);
            }
            default -> {
                JOptionPane.showMessageDialog(this,
                        "Tipo de usuário desconhecido: " + tipo,
                        "Erro de Configuração", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        SessionManager.getInstance().trocarFrame(proximo);
    }

    private void abrirTelaLogin() {
        LoginFrame novoLogin = new LoginFrame(
                autenticacaoController, clienteController, restauranteController,
                adminController, enderecoController,
                categoriaController, produtoController,
                pedidoController, carrinhoController,
                areaEntregaController, horarioController);
        SessionManager.getInstance().trocarFrame(novoLogin);
        SessionManager.getInstance().encerrarSessao();
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
