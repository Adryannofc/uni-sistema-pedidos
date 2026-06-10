package com.pedidos.view.restaurante;

import com.pedidos.model.entity.Restaurante;
import com.pedidos.model.service.*;
import com.pedidos.model.entity.Usuario;
import com.pedidos.view.util.AppColors;
import com.pedidos.view.util.AppFonts;
import com.pedidos.view.util.base.BaseFrame;
import com.pedidos.view.util.session.SessionManager;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;

public class RestauranteFrame extends BaseFrame {

    private final Usuario usuario;
    private final CategoriaService categoriaService;
    private final ProdutoService produtoService;
    private final RestauranteService restauranteService;
    private final AreaEntregaService areaEntregaService;
    private final HorarioService horarioService;
    private final PedidoService pedidoService;
    private final AutenticacaoService autenticacaoService;
    private final Runnable acaoLogout;

    public RestauranteFrame(Usuario usuario,
                            CategoriaService categoriaService,
                            ProdutoService produtoService,
                            RestauranteService restauranteService,
                            AreaEntregaService areaEntregaService,
                            HorarioService horarioService,
                            PedidoService pedidoService,
                            AutenticacaoService autenticacaoService,
                            Runnable acaoLogout) {
        super("Sistema de Delivery — Painel do Restaurante");
        this.usuario = usuario;
        this.categoriaService = categoriaService;
        this.produtoService = produtoService;
        this.restauranteService = restauranteService;
        this.areaEntregaService = areaEntregaService;
        this.horarioService = horarioService;
        this.pedidoService = pedidoService;
        this.autenticacaoService = autenticacaoService;
        this.acaoLogout = acaoLogout;

        //Busca do restaurante Atualizado (devido a busca direto no BD pelo ID)
        Restaurante restauranteBd = restauranteService.buscarRestaurantePorId(usuario.getId());


        if(restauranteBd != null && !restauranteBd.isStatusAtivo())
        {
            JOptionPane.showMessageDialog(this,"Sua conta está bloqueada ou inativa.\nEntre em contato com o suporte para mais informações.",
                    "Acesso Negado",
                    JOptionPane.ERROR_MESSAGE);

            dispose();

            if (acaoLogout != null)
            {
                SwingUtilities.invokeLater(acaoLogout::run);
            }
            return;

        }

        construirInterface();
        criarAbas();
        criarMenu();

        verificacaoPeriodica();
    }

    private void verificacaoPeriodica(){
           int tempo = 5000;

           Timer timer = new Timer(tempo, e -> {
               // O novo operador que fara uma verificação de 5 em 5 min
               SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                   @Override
                   protected Boolean doInBackground() {
                       if (restauranteService != null) {
                           var restauranteBd = restauranteService.buscarRestaurantePorId(usuario.getId());
                           return restauranteBd != null && restauranteBd.isStatusAtivo();
                       }
                       return true;
                   }

                   @Override
                   protected void done() {
                       // Esse pedaço roda de volta na EDT (operador responsavel pela tela)
                       try {
                           boolean estáAtivo = get(); // Pega o resultado do doInBackground

                           if (!estáAtivo) {
                               // Para o timer para não ficar rodando em loop infinito após fechar
                               ((Timer) e.getSource()).stop();


                               JOptionPane.showMessageDialog(RestauranteFrame.this,
                                       "Sua sessão expirou pois esta conta foi bloqueada pelo administrador.",
                                       "Conta Bloqueada",
                                       JOptionPane.WARNING_MESSAGE);

                               dispose();
                               if (acaoLogout != null) {
                                   SwingUtilities.invokeLater(acaoLogout::run);
                               }
                           }
                       } catch (Exception ex) {
                           ex.printStackTrace(); // Se der erro na busca, não trava o sistema, só avisa o console
                       }
                   }
               };

               // Manda o operário começar a trabalhar de fundo
               worker.execute();
           });

        // Inicializa o cronômetro
        timer.start();
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

        JButton btnSair = new JButton("Sair");
        btnSair.setFont(AppFonts.BOTAO);
        btnSair.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(this,
                    "Tem certeza que deseja sair?",
                    "Sair",
                    JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.OK_OPTION) return;
            SessionManager.getInstance().encerrarSessao();
            dispose();
            System.exit(0);
        });

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rodape.add(btnSair);

        add(header, BorderLayout.NORTH);
        add(rodape, BorderLayout.SOUTH);
    }

    private JMenuBar criarMenu() {

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        menuBar.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));

        JMenu menuLogout = new JMenu("Logout");
        menuLogout.setBackground(Color.WHITE);
        menuLogout.setForeground(Color.BLACK);
        menuLogout.setFont(AppFonts.MENU);
        menuLogout.addMenuListener(new MenuListener() {
            @Override public void menuDeselected(MenuEvent e) {}
            @Override public void menuCanceled(MenuEvent e) {}
            @Override
            public void menuSelected(MenuEvent e) {
                menuLogout.setPopupMenuVisible(false);
                Object[] opcoes = {"Sim", "Não"};
                int r = JOptionPane.showOptionDialog(RestauranteFrame.this,
                        "Deseja sair do sistema?", "Confirmar Logout",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, opcoes, opcoes[0]);
                if (r == JOptionPane.YES_OPTION) {
                    SwingUtilities.invokeLater(() -> acaoLogout.run());
                }
            }
        });

        menuBar.add(menuLogout);
        setJMenuBar(menuBar);

        return menuBar;
    }

    private void criarAbas() {
        JTabbedPane abas = new JTabbedPane();
        abas.setFont(AppFonts.MENU);

        abas.addTab("Produtos",                  new PainelProdutos(usuario, produtoService, categoriaService));
        abas.addTab("Pedidos",                   new PainelPedidos(usuario, pedidoService));
        abas.addTab("Áreas de Entrega",          new PainelAreaEntrega(usuario, areaEntregaService));
        abas.addTab("Horários de funcionamento", new PainelHorarios(usuario, horarioService));
        abas.addTab("Perfil",                    new PainelPerfil(usuario, autenticacaoService, restauranteService));

        add(abas, BorderLayout.CENTER);
    }
}
