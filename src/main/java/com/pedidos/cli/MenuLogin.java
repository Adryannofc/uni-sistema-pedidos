package com.pedidos.cli;

import com.pedidos.model.service.*;
import com.pedidos.model.entity.*;
import com.pedidos.model.repository.RestauranteRepository;
import com.pedidos.cli.admin.MenuCategorias;
import com.pedidos.cli.cliente.MenuCliente;
import com.pedidos.cli.restaurante.*;
import com.pedidos.cli.util.EntradaSegura;
import com.pedidos.cli.util.TerminalUtils;

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
    private final AreaEntregaService areaEntregaService;
    private final HorarioService horarioService;

    private final Scanner scan = new Scanner(System.in);

    public MenuLogin(AutenticacaoService autenticacaoService,
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
        this.autenticacaoService = autenticacaoService;
        this.adminService = adminService;
        this.clienteService = clienteService;
        this.categoriaService = categoriaService;
        this.produtoService = produtoService;
        this.restauranteService = restauranteService;
        this.pedidoService = pedidoService;
        this.carrinhoService = carrinhoService;
        this.restauranteRepo = restauranteRepo;
        this.areaEntregaService = areaEntregaService;
        this.horarioService = horarioService;
    }

    public void iniciar() {
        while (true) {
            TerminalUtils.limparTela();
            System.out.println("╔══════════════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                            SISTEMA DE DELIVERY                               ║");
            System.out.println("╠══════════════════════════════════════════════════════════════════════════════╣");
            System.out.println("║                                                                              ║");
            System.out.println("║  [1] LOGIN                                                                   ║");
            System.out.println("║  [2] CADASTRAR-SE                                                            ║");
            System.out.println("║                                                                              ║");
            System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝");

            System.out.println("ESCOLHA UMA OPÇÃO: ");
            int opcao = EntradaSegura.lerOpcao(scan, 0, 2);

            switch (opcao) {
                case 1:
                    TerminalUtils.limparTela();

                    String email = EntradaSegura.lerString(scan, "E-MAIL: ");
                    String senha = EntradaSegura.lerString(scan, "SENHA : ");

                    try {
                        Usuario usuario = autenticacaoService.autenticar(email, senha);
                        System.out.println("Bem-vindo, " + usuario.getNome() + "!");

                        switch (usuario.getTipoUsuario()) {
                            case ADMIN -> {
                                Admin adminLogado = (Admin) usuario;
                                MenuCategorias menuCategorias = new MenuCategorias(adminService, categoriaService, scan);
                                new com.pedidos.cli.admin.MenuAdmin(adminService, scan, menuCategorias)
                                        .exibir(adminLogado);
                            }
                            case RESTAURANTE -> {
                                Restaurante restauranteLogado = (Restaurante) usuario;
                                MenuProdutos menuProdutos = new MenuProdutos(produtoService, categoriaService, scan);
                                MenuCategoriasCardapio menuCats = new MenuCategoriasCardapio(categoriaService, scan);
                                MenuAreaEntrega menuAreaEntrega = new MenuAreaEntrega(areaEntregaService, scan);
                                MenuHorarios menuHorarios = new MenuHorarios(horarioService, scan);
                                new MenuRestaurante(
                                        menuProdutos, menuCats, menuAreaEntrega, menuHorarios,
                                        restauranteService, categoriaService, pedidoService, areaEntregaService, horarioService, scan
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
                    }
                    break;
                case 2:
                    TerminalUtils.limparTela();

                    System.out.println("╔══════════════════════════════════════════════════════════════════════════════╗");
                    System.out.println("║                            CADASTRO                                          ║");
                    System.out.println("╠══════════════════════════════════════════════════════════════════════════════╣");
                    System.out.println("║  [1] CLIENTE                                                                   ║");
                    System.out.println("║  [2] RESTAURANTE                                                            ║");
                    System.out.println("║  [0] SAIR                                                                    ║");
                    System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝");

                    int opcaoCadastro = EntradaSegura.lerOpcao(scan, 0, 2);
                    switch (opcaoCadastro) {
                        case 1:
                            String nomeCadastroCliente = EntradaSegura.lerString(scan, "NOME : ");
                            String emailCadastroCliente = EntradaSegura.lerString(scan, "EMAIL : ");
                            String cpfCadastroCliente = EntradaSegura.lerString(scan, "CPF : ");
                            String telefoneCadastroCliente = EntradaSegura.lerString(scan, "TELEFONE : ");
                            String senhaCadastroCliente = EntradaSegura.lerString(scan, "SENHA: ");

                            try {
                                clienteService.cadastrarCliente(nomeCadastroCliente, emailCadastroCliente, senhaCadastroCliente,
                                        cpfCadastroCliente, telefoneCadastroCliente);
                                System.out.println("\n[✔] Bem-vindo, " + nomeCadastroCliente + "! Cadastro realizado.");
                            } catch (RuntimeException e) {
                                System.out.println("\n[✘] ERRO AO CADASTRAR: " + e.getMessage());
                            }
                            break;
                        case 2:
                            String nomeCadastroRestaurante = EntradaSegura.lerString(scan, "NOME : ");
                            String emailCadastroRestaurante = EntradaSegura.lerString(scan, "EMAIL : ");
                            String cnpjCadastroRestaurante = EntradaSegura.lerString(scan, "CNPJ : ");
                            String telefoneCadastroRestaurante = EntradaSegura.lerString(scan, "TELEFONE : ");
                            String senhaCadastroRestaurante = EntradaSegura.lerString(scan, "SENHA: ");
                            try {
                                restauranteService.cadastrarRestaurante(nomeCadastroRestaurante, emailCadastroRestaurante, senhaCadastroRestaurante,
                                        cnpjCadastroRestaurante, telefoneCadastroRestaurante);
                                System.out.println("\n[✔] Bem-vindo, " + nomeCadastroRestaurante + "! Cadastro realizado.");
                            } catch (RuntimeException e) {
                                System.out.println("\n[✘] ERRO AO CADASTRAR: " + e.getMessage());
                            }
                            break;
                    };

            }

        }
    }

}

