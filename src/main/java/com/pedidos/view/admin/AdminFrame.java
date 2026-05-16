package com.pedidos.view.admin;

import com.pedidos.application.service.AdminService;
import com.pedidos.application.service.CategoriaService;
import com.pedidos.domain.entities.Usuario;
import com.pedidos.view.util.AppColors;
import com.pedidos.view.util.AppFonts;
import com.pedidos.view.util.base.BaseFrame;
import com.pedidos.view.util.session.SessionManager;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class AdminFrame extends BaseFrame {

    private static final String CARD_RESTAURANTES  = "restaurantes";
    private static final String CARD_CATEGORIAS    = "categorias";
    private static final String CARD_PERFIL        = "perfil";

    private final Usuario         usuario;
    private final AdminService    adminService;
    private final CategoriaService categoriaService;

    private CardLayout cardLayout;
    private JPanel     painelConteudo;

    private final List<NavItem> navItems = new ArrayList<>();
    private NavItem itemSelecionado;

    public AdminFrame(Usuario usuario, AdminService adminService, CategoriaService categoriaService) {
        super("Sistema Delivery — Painel Administrativo | " + usuario.getEmail(), 1100, 700);
        this.usuario          = usuario;
        this.adminService     = adminService;
        this.categoriaService = categoriaService;
        construirInterface();
    }

    // ─── layout principal ─────────────────────────────────────────────────────

    private void construirInterface() {
        setLayout(new BorderLayout());
        setJMenuBar(criarMenuBar());
        JPanel conteudo = criarConteudo();   // cardLayout precisa existir antes do sidebar chamar selecionar()
        add(criarSidebar(),   BorderLayout.WEST);
        add(conteudo,         BorderLayout.CENTER);
        add(criarStatusBar(), BorderLayout.SOUTH);
    }

    // ─── menu bar ─────────────────────────────────────────────────────────────

    private JMenuBar criarMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu mArquivo = menu("Arquivo");
        JMenuItem iSair = menuItem("Sair");
        iSair.addActionListener(e -> confirmarSaida());
        mArquivo.add(iSair);

        JMenu mRestaurantes = menu("Restaurantes");
        JMenuItem iListar = menuItem("Listar Restaurantes");
        iListar.addActionListener(e -> navegarPara(CARD_RESTAURANTES));
        mRestaurantes.add(iListar);

        JMenu mCategorias = menu("Categorias");
        JMenuItem iCategorias = menuItem("Categorias Globais");
        iCategorias.addActionListener(e -> navegarPara(CARD_CATEGORIAS));
        mCategorias.add(iCategorias);

        JMenu mConta = menu("Conta");
        JMenuItem iPerfil = menuItem("Meu Perfil");
        iPerfil.addActionListener(e -> navegarPara(CARD_PERFIL));
        JMenuItem iSairConta = menuItem("Sair");
        iSairConta.addActionListener(e -> confirmarSaida());
        mConta.add(iPerfil);
        mConta.addSeparator();
        mConta.add(iSairConta);

        bar.add(mArquivo);
        bar.add(mRestaurantes);
        bar.add(mCategorias);
        bar.add(mConta);
        return bar;
    }

    // ─── sidebar ──────────────────────────────────────────────────────────────

    private JPanel criarSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(new MatteBorder(0, 0, 0, 1, AppColors.CINZA_BORDA));

        sidebar.add(criarPerfilTopo(),  BorderLayout.NORTH);
        sidebar.add(criarNavegacao(),   BorderLayout.CENTER);

        return sidebar;
    }

    private JPanel criarPerfilTopo() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(Color.WHITE);
        painel.setBorder(new EmptyBorder(20, 0, 16, 0));

        // avatar circle
        JLabel avatar = new JLabel(iniciais(usuario.getNome()), SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppColors.AZUL_PRIMARIO);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 22));
        avatar.setForeground(Color.WHITE);
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(64, 64));
        avatar.setMaximumSize(new Dimension(64, 64));
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nome = new JLabel(usuario.getNome(), SwingConstants.CENTER);
        nome.setFont(AppFonts.TITULO);
        nome.setForeground(AppColors.TEXTO_PRIMARIO);
        nome.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel badge = criarBadge("ADMIN");
        badge.setAlignmentX(Component.CENTER_ALIGNMENT);

        painel.add(avatar);
        painel.add(Box.createVerticalStrut(10));
        painel.add(nome);
        painel.add(Box.createVerticalStrut(6));
        painel.add(badge);

        return painel;
    }

    private JPanel criarNavegacao() {
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(Color.WHITE);
        nav.setBorder(new MatteBorder(1, 0, 0, 0, AppColors.CINZA_BORDA));

        NavItem iRestaurantes = new NavItem("    Restaurantes",    CARD_RESTAURANTES);
        NavItem iCategorias   = new NavItem("    Categorias Globais", CARD_CATEGORIAS);
        NavItem iPerfil       = new NavItem("    Meu Perfil",      CARD_PERFIL);

        navItems.add(iRestaurantes);
        navItems.add(iCategorias);
        navItems.add(iPerfil);

        nav.add(iRestaurantes);
        nav.add(iCategorias);
        nav.add(iPerfil);
        nav.add(Box.createVerticalGlue());

        selecionar(iRestaurantes);
        return nav;
    }

    // ─── conteúdo (CardLayout) ────────────────────────────────────────────────

    private JPanel criarConteudo() {
        cardLayout    = new CardLayout();
        painelConteudo = new JPanel(cardLayout);
        painelConteudo.setBackground(AppColors.CINZA_FUNDO);

        painelConteudo.add(new PainelRestaurantes(adminService),           CARD_RESTAURANTES);
        painelConteudo.add(new PainelCategoriasGlobais(categoriaService),  CARD_CATEGORIAS);
        painelConteudo.add(new PainelPerfil(usuario, adminService),        CARD_PERFIL);

        return painelConteudo;
    }

    // ─── status bar ───────────────────────────────────────────────────────────

    private JPanel criarStatusBar() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 3));
        barra.setBackground(AppColors.CINZA_STATUS);
        barra.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, AppColors.CINZA_BORDA),
                new EmptyBorder(2, 6, 2, 6)));

        JLabel logado = new JLabel("Logado como: " + usuario.getNome());
        logado.setFont(AppFonts.STATUS);
        logado.setForeground(AppColors.TEXTO_SECUNDARIO);

        long total = 0;
        try { total = adminService.listarRestaurantes().size(); } catch (Exception ignored) {}
        JLabel contagem = new JLabel(total + " restaurante(s) cadastrado(s)");
        contagem.setFont(AppFonts.STATUS);
        contagem.setForeground(AppColors.TEXTO_SECUNDARIO);

        JLabel online = new JLabel("Sistema Online");
        online.setFont(AppFonts.STATUS);
        online.setForeground(new Color(21, 120, 50));

        barra.add(logado);
        barra.add(separador());
        barra.add(contagem);
        barra.add(separador());
        barra.add(online);

        return barra;
    }

    // ─── navegação ────────────────────────────────────────────────────────────

    private void navegarPara(String card) {
        navItems.stream()
                .filter(n -> n.card.equals(card))
                .findFirst()
                .ifPresent(this::selecionar);
    }

    private void selecionar(NavItem item) {
        if (itemSelecionado != null) itemSelecionado.desselecionar();
        itemSelecionado = item;
        item.selecionar();
        cardLayout.show(painelConteudo, item.card);
    }

    // ─── ação sair ────────────────────────────────────────────────────────────

    private void confirmarSaida() {
        int r = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja sair?", "Sair",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (r != JOptionPane.YES_OPTION) return;
        SessionManager.getInstance().encerrarSessao();
        dispose();
        System.exit(0);
    }

    // ─── componentes utilitários ──────────────────────────────────────────────

    private static JLabel criarBadge(String texto) {
        JLabel badge = new JLabel(texto, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppColors.AZUL_PRIMARIO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setForeground(Color.WHITE);
        badge.setOpaque(false);
        badge.setPreferredSize(new Dimension(60, 18));
        badge.setMaximumSize(new Dimension(60, 18));
        return badge;
    }

    private static JLabel separador() {
        JLabel sep = new JLabel("|");
        sep.setForeground(AppColors.CINZA_BORDA);
        sep.setFont(AppFonts.STATUS);
        return sep;
    }

    private static JMenu menu(String texto) {
        JMenu m = new JMenu(texto);
        m.setFont(AppFonts.MENU);
        return m;
    }

    private static JMenuItem menuItem(String texto) {
        JMenuItem i = new JMenuItem(texto);
        i.setFont(AppFonts.MENU);
        return i;
    }

    private static String iniciais(String nome) {
        if (nome == null || nome.isBlank()) return "?";
        String[] partes = nome.trim().split("\\s+");
        if (partes.length == 1) return partes[0].substring(0, Math.min(2, partes[0].length())).toUpperCase();
        return ("" + partes[0].charAt(0) + partes[1].charAt(0)).toUpperCase();
    }

    // ─── inner class: NavItem ─────────────────────────────────────────────────

    private class NavItem extends JPanel {
        final String card;
        private final JLabel label;

        NavItem(String texto, String card) {
            super(new BorderLayout());
            this.card = card;
            setBackground(Color.WHITE);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new MatteBorder(0, 0, 1, 0, AppColors.CINZA_BORDA));

            label = new JLabel(texto);
            label.setFont(AppFonts.MENU);
            label.setForeground(AppColors.TEXTO_PRIMARIO);
            label.setBorder(new EmptyBorder(12, 16, 12, 16));
            add(label, BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { AdminFrame.this.selecionar(NavItem.this); }
                @Override public void mouseEntered(MouseEvent e) { if (itemSelecionado != NavItem.this) hover(true); }
                @Override public void mouseExited(MouseEvent e)  { if (itemSelecionado != NavItem.this) hover(false); }
            });
        }

        void selecionar() {
            setBackground(AppColors.AZUL_PRIMARIO);
            label.setForeground(Color.WHITE);
            repaint();
        }

        void desselecionar() {
            setBackground(Color.WHITE);
            label.setForeground(AppColors.TEXTO_PRIMARIO);
            repaint();
        }

        private void hover(boolean ativo) {
            setBackground(ativo ? AppColors.CINZA_STATUS : Color.WHITE);
            repaint();
        }


    }
}
