package com.pedidos.presentation.menu;

import com.pedidos.application.service.*;
import com.pedidos.domain.enums.StatusPedido;
import com.pedidos.domain.model.*;
import com.pedidos.domain.repository.RestauranteRepository;
import com.pedidos.presentation.util.EntradaSegura;
import com.pedidos.presentation.util.TerminalUtils;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MenuCliente {

    private final Cliente clienteLogado;
    private final ClienteService clienteService;
    private final PedidoService pedidoService;
    private final CarrinhoService carrinhoService;
    private final ProdutoService produtoService;
    private final RestauranteRepository restauranteRepo;
    private final Scanner scanner;

    public MenuCliente(Cliente clienteLogado,
                       ClienteService clienteService,
                       PedidoService pedidoService,
                       CarrinhoService carrinhoService,
                       ProdutoService produtoService,
                       RestauranteRepository restauranteRepo,
                       Scanner scanner) {
        this.clienteLogado = clienteLogado;
        this.clienteService = clienteService;
        this.pedidoService = pedidoService;
        this.carrinhoService = carrinhoService;
        this.produtoService = produtoService;
        this.restauranteRepo = restauranteRepo;
        this.scanner = scanner;
    }

    public void iniciar() {
        while (true) {
            TerminalUtils.limparTela();
            TerminalUtils.cabecalho("MENU CLIENTE", "Ola, " + clienteLogado.getNome());

            System.out.println(TerminalUtils.TOPO);
            System.out.println(TerminalUtils.linha("  1  \u00bb  Meu Perfil"));
            System.out.println(TerminalUtils.linha("  2  \u00bb  Fazer Pedido"));
            System.out.println(TerminalUtils.linha("  3  \u00bb  Meus Pedidos"));
            System.out.println(TerminalUtils.SEPARADOR);
            System.out.println(TerminalUtils.linha("  0  \u00bb  Sair (Logout)"));
            System.out.println(TerminalUtils.BASE);
            System.out.print("\n  Escolha uma opcao: ");

            int opcao = EntradaSegura.lerOpcao(scanner, 0, 3);

            switch (opcao) {
                case 1 -> menuPerfil();
                case 2 -> menuFazerPedido();
                case 3 -> menuMeusPedidos();
                case 0 -> {
                    carrinhoService.encerrarCarrinho();
                    return;
                }
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Perfil
    // ─────────────────────────────────────────────────────────────────────────

    private void menuPerfil() {
        while (true) {
            TerminalUtils.limparTela();
            TerminalUtils.cabecalho("PERFIL", clienteLogado.getNome());

            System.out.println(TerminalUtils.TOPO);
            System.out.println(TerminalUtils.linha("  1  \u00bb  Visualizar Perfil"));
            System.out.println(TerminalUtils.linha("  2  \u00bb  Editar Nome"));
            System.out.println(TerminalUtils.linha("  3  \u00bb  Editar E-mail"));
            System.out.println(TerminalUtils.linha("  4  \u00bb  Editar CPF"));
            System.out.println(TerminalUtils.linha("  5  \u00bb  Editar Telefone"));
            System.out.println(TerminalUtils.linha("  6  \u00bb  Alterar Senha"));
            System.out.println(TerminalUtils.linha("  7  \u00bb  Meu Endereco"));
            System.out.println(TerminalUtils.SEPARADOR);
            System.out.println(TerminalUtils.linha("  0  \u00bb  Voltar"));
            System.out.println(TerminalUtils.BASE);
            System.out.print("\n  Escolha uma opcao: ");

            int opcao = EntradaSegura.lerOpcao(scanner, 0, 7);

            switch (opcao) {
                case 1 -> acaoVisualizarPerfil();
                case 2 -> acaoEditarNome();
                case 3 -> acaoEditarEmail();
                case 4 -> acaoEditarCpf();
                case 5 -> acaoEditarTelefone();
                case 6 -> acaoAlterarSenha();
                case 7 -> acaoEditarEndereco();
                case 0 -> {
                    return;
                }
            }
        }
    }

    private void acaoVisualizarPerfil() {
        TerminalUtils.limparTela();
        TerminalUtils.cabecalho("VISUALIZAR PERFIL");
        System.out.println("  ID       : " + clienteLogado.getId());
        System.out.println("  Nome     : " + clienteLogado.getNome());
        System.out.println("  E-mail   : " + clienteLogado.getEmail());
        System.out.println("  CPF      : " + clienteLogado.getCpf());
        System.out.println("  Telefone : " + clienteLogado.getTelefone());
        Endereco end = clienteLogado.getEnderecoEntrega();
        System.out.println("  Endereco : " + (end != null ? end.toString() : "Nao cadastrado"));
        TerminalUtils.pausar();
    }

    private void acaoEditarNome() {
        TerminalUtils.limparTela();
        TerminalUtils.cabecalho("EDITAR NOME");
        try {
            System.out.println("  Atual : " + clienteLogado.getNome());
            System.out.print("  Novo  : ");
            String novo = scanner.nextLine().trim();
            if (novo.isBlank()) {
                TerminalUtils.aviso("Nenhuma alteracao realizada.");
            } else {
                clienteService.editarNome(clienteLogado, novo);
                TerminalUtils.sucesso("Nome atualizado para: " + clienteLogado.getNome());
            }
        } catch (Exception e) {
            TerminalUtils.erro(e.getMessage());
        }
        TerminalUtils.pausar();
    }

    private void acaoEditarEmail() {
        TerminalUtils.limparTela();
        TerminalUtils.cabecalho("EDITAR E-MAIL");
        try {
            System.out.println("  Atual : " + clienteLogado.getEmail());
            System.out.print("  Novo  : ");
            String novo = scanner.nextLine().trim();
            if (novo.isBlank()) {
                TerminalUtils.aviso("Nenhuma alteracao realizada.");
            } else {
                clienteService.editarEmail(clienteLogado, novo);
                TerminalUtils.sucesso("E-mail atualizado.");
            }
        } catch (Exception e) {
            TerminalUtils.erro(e.getMessage());
        }
        TerminalUtils.pausar();
    }

    private void acaoEditarCpf() {
        TerminalUtils.limparTela();
        TerminalUtils.cabecalho("EDITAR CPF");
        try {
            System.out.println("  Atual : " + clienteLogado.getCpf());
            System.out.print("  Novo (11 digitos): ");
            String novo = scanner.nextLine().trim();
            if (novo.isBlank()) {
                TerminalUtils.aviso("Nenhuma alteracao realizada.");
            } else {
                clienteService.editarCpf(clienteLogado, novo);
                TerminalUtils.sucesso("CPF atualizado.");
            }
        } catch (Exception e) {
            TerminalUtils.erro(e.getMessage());
        }
        TerminalUtils.pausar();
    }

    private void acaoEditarTelefone() {
        TerminalUtils.limparTela();
        TerminalUtils.cabecalho("EDITAR TELEFONE");
        try {
            System.out.println("  Atual : " + clienteLogado.getTelefone());
            System.out.print("  Novo  : ");
            String novo = scanner.nextLine().trim();
            if (novo.isBlank()) {
                TerminalUtils.aviso("Nenhuma alteracao realizada.");
            } else {
                clienteService.editarTelefone(clienteLogado, novo);
                TerminalUtils.sucesso("Telefone atualizado.");
            }
        } catch (Exception e) {
            TerminalUtils.erro(e.getMessage());
        }
        TerminalUtils.pausar();
    }

    private void acaoEditarEndereco() {
        TerminalUtils.limparTela();
        TerminalUtils.cabecalho("MEU ENDERECO");
        try {
            Endereco atual = clienteLogado.getEnderecoEntrega();
            if (atual != null) {
                System.out.println("  Atual: " + atual);
            } else {
                System.out.println("  Nenhum endereco cadastrado.");
            }
            System.out.println();
            System.out.print("  Rua    : "); String rua = scanner.nextLine().trim();
            System.out.print("  Numero : "); String numero = scanner.nextLine().trim();
            System.out.print("  Bairro : "); String bairro = scanner.nextLine().trim();
            System.out.print("  Cidade : "); String cidade = scanner.nextLine().trim();
            System.out.print("  Estado : "); String estado = scanner.nextLine().trim();
            System.out.print("  CEP    : "); String cep = scanner.nextLine().trim();

            clienteService.salvarEndereco(clienteLogado, rua, numero, bairro, cidade, estado, cep);
            TerminalUtils.sucesso("Endereco salvo com sucesso.");
        } catch (Exception e) {
            TerminalUtils.erro(e.getMessage());
        }
        TerminalUtils.pausar();
    }

    private void acaoAlterarSenha() {
        TerminalUtils.limparTela();
        TerminalUtils.cabecalho("ALTERAR SENHA");
        try {
            System.out.print("  Senha atual         : ");
            String atual = scanner.nextLine();
            System.out.print("  Nova senha          : ");
            String nova = scanner.nextLine();
            System.out.print("  Confirmar nova senha: ");
            String confirmacao = scanner.nextLine();

            clienteService.alterarSenha(clienteLogado, atual, nova, confirmacao);
            TerminalUtils.sucesso("Senha alterada com sucesso.");
        } catch (Exception e) {
            TerminalUtils.erro(e.getMessage());
        }
        TerminalUtils.pausar();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Meus Pedidos
    // ─────────────────────────────────────────────────────────────────────────

    private void menuMeusPedidos() {
        TerminalUtils.limparTela();
        TerminalUtils.cabecalho("MEUS PEDIDOS", clienteLogado.getNome());
        try {
            List<Pedido> pedidos = pedidoService.listarPorCliente(clienteLogado.getId());
            if (pedidos.isEmpty()) {
                TerminalUtils.aviso("Nenhum pedido encontrado.");
                TerminalUtils.pausar();
                return;
            }

            exibirListaPedidos(pedidos);

            Pedido paraConfirmar = null;
            for (Pedido p : pedidos) {
                if (p.getStatus() == StatusPedido.SAIU_PARA_ENTREGA) {
                    paraConfirmar = p;
                    break;
                }
            }

            if (paraConfirmar != null) {
                System.out.println("  Pedido " + paraConfirmar.getId().substring(0, 8) + " saiu para entrega!");
                System.out.print("  Digite o codigo de confirmacao (ou Enter para pular): ");
                String codigo = scanner.nextLine().trim();
                if (!codigo.isBlank()) {
                    pedidoService.confirmarEntrega(paraConfirmar.getId(), codigo);
                    TerminalUtils.sucesso("Entrega confirmada!");
                }
            }
        } catch (Exception e) {
            TerminalUtils.erro(e.getMessage());
        }
        TerminalUtils.pausar();
    }

    private void exibirListaPedidos(List<Pedido> pedidos) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        System.out.println(TerminalUtils.TOPO);
        System.out.println(TerminalUtils.linha("  #   ID        Data              Status         Total"));
        System.out.println(TerminalUtils.SEPARADOR);
        for (int i = 0; i < pedidos.size(); i++) {
            Pedido p = pedidos.get(i);
            System.out.println(TerminalUtils.linha(String.format(
                    "  %-3d %-8s  %-17s %-14s %s",
                    (i + 1),
                    p.getId().substring(0, 8),
                    p.getDataPedido().format(fmt),
                    p.getStatus(),
                    moeda.format(p.calcularTotal()))));
        }
        System.out.println(TerminalUtils.BASE);
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Fazer Pedido
    // ─────────────────────────────────────────────────────────────────────────

    private void menuFazerPedido() {
        while (true) {
            TerminalUtils.limparTela();
            TerminalUtils.cabecalho("FAZER PEDIDO", clienteLogado.getNome());

            System.out.println(TerminalUtils.TOPO);
            System.out.println(TerminalUtils.linha("  1  \u00bb  Escolher Restaurante"));
            System.out.println(TerminalUtils.linha("  2  \u00bb  Ver Carrinho"));
            System.out.println(TerminalUtils.linha("  3  \u00bb  Finalizar Pedido (Checkout)"));
            System.out.println(TerminalUtils.SEPARADOR);
            System.out.println(TerminalUtils.linha("  0  \u00bb  Voltar"));
            System.out.println(TerminalUtils.BASE);
            System.out.print("\n  Escolha uma opcao: ");

            int opcao = EntradaSegura.lerOpcao(scanner, 0, 3);

            switch (opcao) {
                case 1 -> acaoEscolherRestaurante();
                case 2 -> acaoVerCarrinho();
                case 3 -> acaoCheckout();
                case 0 -> {
                    return;
                }
            }
        }
    }

    private void acaoEscolherRestaurante() {
        TerminalUtils.limparTela();
        TerminalUtils.cabecalho("ESCOLHER RESTAURANTE");
        try {
            List<Restaurante> ativos = restauranteRepo.listarRestaurantes().stream()
                    .filter(Restaurante::isStatusAtivo)
                    .collect(Collectors.toList());

            if (ativos.isEmpty()) {
                TerminalUtils.aviso("Nenhum restaurante disponivel no momento.");
                TerminalUtils.pausar();
                return;
            }

            System.out.println(TerminalUtils.TOPO);
            System.out.println(TerminalUtils.linha("  #   Nome"));
            System.out.println(TerminalUtils.SEPARADOR);
            for (int i = 0; i < ativos.size(); i++) {
                System.out.println(TerminalUtils.linha(String.format(
                        "  %-3d %s", (i + 1), ativos.get(i).getNome())));
            }
            System.out.println(TerminalUtils.BASE);
            System.out.print("\n  Escolha o numero do restaurante: ");

            Restaurante escolhido = ativos.get(EntradaSegura.lerOpcao(scanner, 1, ativos.size()) - 1);

            carrinhoService.iniciarCarrinho(clienteLogado.getId(), escolhido.getId());
            TerminalUtils.sucesso("Restaurante \"" + escolhido.getNome() + "\" selecionado.");

            acaoNavegarCardapio(escolhido);

        } catch (Exception e) {
            TerminalUtils.erro(e.getMessage());
        }
        TerminalUtils.pausar();
    }

    private void acaoNavegarCardapio(Restaurante restaurante) {
        while (true) {
            TerminalUtils.limparTela();
            TerminalUtils.cabecalho("CARDAPIO", restaurante.getNome());
            try {
                List<Produto> produtos = produtoService.listarAtivosPorRestaurante(restaurante.getId());

                if (produtos.isEmpty()) {
                    TerminalUtils.aviso("Nenhum produto disponivel.");
                    TerminalUtils.pausar();
                    return;
                }

                System.out.println(TerminalUtils.TOPO);
                System.out.println(TerminalUtils.linha("  #   Nome                         Preco"));
                System.out.println(TerminalUtils.SEPARADOR);
                NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
                for (int i = 0; i < produtos.size(); i++) {
                    Produto p = produtos.get(i);
                    System.out.println(TerminalUtils.linha(String.format(
                            "  %-3d %-28s  %s",
                            (i + 1), p.getNome(), moeda.format(p.getPreco()))));
                }
                System.out.println(TerminalUtils.SEPARADOR);
                System.out.println(TerminalUtils.linha("  0  \u00bb  Voltar"));
                System.out.println(TerminalUtils.BASE);
                System.out.print("\n  Escolha o produto (0 para voltar): ");

                int num = EntradaSegura.lerOpcao(scanner, 0, produtos.size());
                if (num == 0) return;

                Produto selecionado = produtos.get(num - 1);

                System.out.print("  Quantidade: ");
                int qtd;
                try {
                    qtd = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    TerminalUtils.erro("Quantidade invalida.");
                    TerminalUtils.pausar();
                    continue;
                }

                carrinhoService.adicionarItem(selecionado, qtd);
                TerminalUtils.sucesso("\"" + selecionado.getNome() + "\" adicionado ao carrinho.");
                TerminalUtils.pausar();

            } catch (Exception e) {
                TerminalUtils.erro(e.getMessage());
                TerminalUtils.pausar();
            }
        }
    }

    private void acaoVerCarrinho() {
        TerminalUtils.limparTela();
        TerminalUtils.cabecalho("MEU CARRINHO");
        try {
            if (!carrinhoService.temCarrinhoAtivo()) {
                TerminalUtils.aviso("Carrinho vazio. Escolha um restaurante primeiro.");
                TerminalUtils.pausar();
                return;
            }

            Carrinho carrinho = carrinhoService.getCarrinho();
            NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

            System.out.println(TerminalUtils.TOPO);
            System.out.println(TerminalUtils.linha("  #   Produto                  Qtd   Subtotal"));
            System.out.println(TerminalUtils.SEPARADOR);
            List<ItemPedido> itens = carrinho.getItens();
            for (int i = 0; i < itens.size(); i++) {
                ItemPedido item = itens.get(i);
                System.out.println(TerminalUtils.linha(String.format(
                        "  %-3d %-24s %-5d %s",
                        (i + 1), item.getNomeProduto(),
                        item.getQuantidade(),
                        moeda.format(item.calcularSubtotal()))));
            }
            System.out.println(TerminalUtils.SEPARADOR);
            System.out.println(TerminalUtils.linha("  Subtotal: " + moeda.format(carrinho.calcularSubtotal())));
            System.out.println(TerminalUtils.BASE);

            System.out.println("\n  1 - Remover item");
            System.out.println("  2 - Esvaziar carrinho");
            System.out.println("  0 - Voltar");
            System.out.print("\n  Escolha: ");

            int opcao = EntradaSegura.lerOpcao(scanner, 0, 2);
            if (opcao == 1) {
                System.out.print("  Numero do item a remover: ");
                int num = EntradaSegura.lerOpcao(scanner, 1, itens.size());
                carrinhoService.removerItem(itens.get(num - 1).getProdutoId());
                TerminalUtils.sucesso("Item removido.");
            } else if (opcao == 2) {
                carrinhoService.limpar();
                TerminalUtils.sucesso("Carrinho esvaziado.");
            }

        } catch (Exception e) {
            TerminalUtils.erro(e.getMessage());
        }
        TerminalUtils.pausar();
    }

    private void acaoCheckout() {
        TerminalUtils.limparTela();
        TerminalUtils.cabecalho("CHECKOUT");
        try {
            if (!carrinhoService.temCarrinhoAtivo()) {
                TerminalUtils.aviso("Carrinho vazio. Adicione produtos antes de finalizar.");
                TerminalUtils.pausar();
                return;
            }

            Carrinho carrinho = carrinhoService.getCarrinho();
            NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

            System.out.println(TerminalUtils.TOPO);
            System.out.println(TerminalUtils.linha("  RESUMO DO PEDIDO"));
            System.out.println(TerminalUtils.SEPARADOR);
            carrinho.getItens().forEach(item -> System.out.println(TerminalUtils.linha(
                    String.format("  %-24s x%-3d %s",
                            item.getNomeProduto(), item.getQuantidade(),
                            moeda.format(item.calcularSubtotal())))));
            System.out.println(TerminalUtils.SEPARADOR);
            System.out.println(TerminalUtils.linha("  Subtotal : " + moeda.format(carrinho.calcularSubtotal())));
            System.out.println(TerminalUtils.BASE);

            Endereco endereco = clienteLogado.getEnderecoEntrega();
            if (endereco == null) {
                TerminalUtils.erro("Voce nao tem endereco cadastrado. Acesse Perfil > Meu Endereco.");
                TerminalUtils.pausar();
                return;
            }
            System.out.println(TerminalUtils.linha("  Entregar em: " + endereco));

            if (!TerminalUtils.confirmarPerigo("Confirmar pedido?", scanner)) {
                TerminalUtils.aviso("Pedido cancelado.");
                TerminalUtils.pausar();
                return;
            }

            String codigo = clienteLogado.getCpf().substring(0, 4);
            Pedido pedido = pedidoService.criarPedido(
                    clienteLogado.getId(), carrinho.getRestauranteId(), carrinho, endereco, codigo);

            carrinhoService.encerrarCarrinho();

            TerminalUtils.sucesso("Pedido realizado! ID: " + pedido.getId().substring(0, 8)
                    + " | Total: " + moeda.format(pedido.calcularTotal()));
            System.out.println("  Seu codigo de confirmacao de entrega: [ " + codigo + " ]");

        } catch (Exception e) {
            TerminalUtils.erro(e.getMessage());
        }
        TerminalUtils.pausar();
    }
}
