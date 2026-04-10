package com.pedidos.presentation.menu;

import com.pedidos.application.service.*;
import com.pedidos.domain.model.*;
import com.pedidos.domain.repository.RestauranteRepository;
import com.pedidos.presentation.admin.MenuCategorias;
import com.pedidos.presentation.restaurante.*;
import com.pedidos.presentation.util.EntradaSegura;
import com.pedidos.presentation.util.TerminalUtils;

import java.util.Scanner;

public class MenuLogin {
    private final AutenticacaoService autenticacaoService;
    private final AdminService adminService;
    private final ClienteService clienteService;
    private final CategoriaService categoriaService;
    private final ProdutoService produtoService;
    private final RestauranteService restauranteService;
    private final PedidoService pedidoService;
    private final CarrinhoService carrinhoService;
    private final RestauranteRepository restauranteRepo;

    private final Scanner scan = new Scanner(System.in);

    public MenuLogin(AutenticacaoService autenticacaoService,
                     AdminService adminService,
                     ClienteService clienteService,
                     CategoriaService categoriaService,
                     ProdutoService produtoService,
                     RestauranteService restauranteService,
                     PedidoService pedidoService,
                     CarrinhoService carrinhoService,
                     RestauranteRepository restauranteRepo) {
        this.autenticacaoService = autenticacaoService;
        this.adminService = adminService;
        this.clienteService = clienteService;
        this.categoriaService = categoriaService;
        this.produtoService = produtoService;
        this.restauranteService = restauranteService;
        this.pedidoService = pedidoService;
        this.carrinhoService = carrinhoService;
        this.restauranteRepo = restauranteRepo;
    }

    public void iniciar() {
        while (true) {
            TerminalUtils.limparTela();
            System.out.println("╔══════════════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                                    LOGIN                                     ║");
            System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝");

            String email = EntradaSegura.lerString(scan, "E-MAIL: ");
            String senha = EntradaSegura.lerString(scan, "SENHA : ");

            try {
                Usuario usuario = autenticacaoService.autenticar(email, senha);
                System.out.println("Bem-vindo, " + usuario.getNome() + "!");

                switch (usuario.getTipoUsuario()) {
                    case ADMIN -> {
                        Admin adminLogado = (Admin) usuario;
                        MenuCategorias menuCategorias = new MenuCategorias(adminService, categoriaService, scan);
                        new com.pedidos.presentation.admin.MenuAdmin(adminService, scan, menuCategorias)
                                .exibir(adminLogado);
                    }
                    case RESTAURANTE -> {
                        Restaurante restauranteLogado = (Restaurante) usuario;
                        MenuProdutos menuProdutos = new MenuProdutos(produtoService, categoriaService, scan);
                        MenuCategoriasCardapio menuCats = new MenuCategoriasCardapio(categoriaService, scan);
                        new MenuRestaurante(
                                menuProdutos, menuCats,
                                restauranteService, categoriaService, pedidoService, scan
                        ).exibir(restauranteLogado);
                    }
                    case CLIENTE -> {
                        Cliente clienteLogado = (Cliente) usuario;
                        new MenuCliente(
                                clienteLogado,
                                clienteService,
                                pedidoService,
                                carrinhoService,
                                produtoService,
                                restauranteRepo,
                                scan
                        ).iniciar();
                    }
                }
            } catch (RuntimeException e) {
                System.out.println("[ERRO] " + e.getMessage());
                TerminalUtils.pausar();
            }
        }
    }
}
