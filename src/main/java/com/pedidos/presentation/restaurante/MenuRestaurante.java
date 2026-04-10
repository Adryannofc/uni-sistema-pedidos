package com.pedidos.presentation.restaurante;

import com.pedidos.application.service.CategoriaService;
import com.pedidos.application.service.PedidoService;
import com.pedidos.application.service.RestauranteService;
import com.pedidos.domain.enums.StatusPedido;
import com.pedidos.domain.model.CategoriaGlobal;
import com.pedidos.domain.model.Pedido;
import com.pedidos.domain.model.Restaurante;
import com.pedidos.presentation.util.EntradaSegura;
import com.pedidos.presentation.util.TerminalUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MenuRestaurante {
    private final MenuProdutos menuProdutos;
    private final MenuCategoriasCardapio menuCategorias;
    private final RestauranteService restauranteService;
    private final CategoriaService categoriaService;
    private final PedidoService pedidoService;
    private final Scanner scanner;

    public MenuRestaurante(MenuProdutos menuProdutos, MenuCategoriasCardapio menuCategorias,
                           RestauranteService restauranteService,
                           CategoriaService categoriaService,
                           PedidoService pedidoService, Scanner scanner) {
        this.menuProdutos = menuProdutos;
        this.menuCategorias = menuCategorias;
        this.restauranteService = restauranteService;
        this.categoriaService = categoriaService;
        this.pedidoService = pedidoService;
        this.scanner = scanner;
    }

    // ─── Helpers locais ───────────────────────────────────────────────────────

    private boolean confirmar(String pergunta) {
        System.out.println();
        System.out.print(TerminalUtils.AVISO + pergunta + " (s/N): ");
        return scanner.nextLine().trim().equalsIgnoreCase("s");
    }

    private String lerCampoEditavel(String label, String valorAtual) {
        System.out.printf("  %s%n", label);
        System.out.printf("  Atual: [ %s ]%n", valorAtual);
        System.out.print("  Novo  (Enter para manter): ");
        String entrada = scanner.nextLine().trim();
        return entrada.isEmpty() ? valorAtual : entrada;
    }

    // ─── Menu principal ───────────────────────────────────────────────────────

    public void exibir(Restaurante restauranteLogado) {
        while (true) {
            TerminalUtils.limparTela();
            TerminalUtils.cabecalho("PAINEL DO RESTAURANTE", "Restaurante: " + restauranteLogado.getNome());

            System.out.println(TerminalUtils.TOPO);
            System.out.println(TerminalUtils.linha("  GERENCIAMENTO"));
            System.out.println(TerminalUtils.SEPARADOR);
            System.out.println(TerminalUtils.linha("    1  \u00bb  Produtos"));
            System.out.println(TerminalUtils.linha("    2  \u00bb  Categorias do Cardapio"));
            System.out.println(TerminalUtils.linha("    3  \u00bb  Pedidos"));
            System.out.println(TerminalUtils.SEPARADOR);
            System.out.println(TerminalUtils.linha("  CONTA"));
            System.out.println(TerminalUtils.SEPARADOR);
            System.out.println(TerminalUtils.linha("    4  \u00bb  Perfil e Configuracoes"));
            System.out.println(TerminalUtils.SEPARADOR);
            System.out.println(TerminalUtils.linha("    0  \u00bb  Sair (Logout)"));
            System.out.println(TerminalUtils.BASE);
            System.out.println();
            System.out.print("  Escolha uma opcao: ");

            int opcao = EntradaSegura.lerOpcao(scanner, 0, 4);

            switch (opcao) {
                case 1 -> menuProdutos.exibir(restauranteLogado);
                case 2 -> menuCategorias.exibir(restauranteLogado);
                case 3 -> menuPedidos(restauranteLogado);
                case 4 -> exibirMenuPerfil(restauranteLogado);
                case 0 -> {
                    if (confirmar("Deseja realmente sair da sua conta?")) {
                        return;
                    }
                }
            }
        }
    }

    // ─── Menu de pedidos ──────────────────────────────────────────────────────

    private void menuPedidos(Restaurante restauranteLogado) {
        TerminalUtils.limparTela();
        TerminalUtils.cabecalho("PEDIDOS", restauranteLogado.getNome());

        List<Pedido> todos = pedidoService.listarPorRestaurante(restauranteLogado.getId());
        List<Pedido> ativos = new ArrayList<>();
        for (Pedido p : todos) {
            if (p.getStatus() != StatusPedido.ENTREGUE && p.getStatus() != StatusPedido.CANCELADO) {
                ativos.add(p);
            }
        }

        if (ativos.isEmpty()) {
            TerminalUtils.aviso("Nenhum pedido ativo no momento.");
            TerminalUtils.pausar();
            return;
        }

        System.out.println(TerminalUtils.TOPO);
        System.out.println(TerminalUtils.linha("  #   ID        Status"));
        System.out.println(TerminalUtils.SEPARADOR);
        for (int i = 0; i < ativos.size(); i++) {
            Pedido p = ativos.get(i);
            System.out.println(TerminalUtils.linha(String.format(
                    "  %-3d %-8s  %s", (i + 1), p.getId().substring(0, 8), p.getStatus())));
        }
        System.out.println(TerminalUtils.SEPARADOR);
        System.out.println(TerminalUtils.linha("  0  \u00bb  Voltar"));
        System.out.println(TerminalUtils.BASE);
        System.out.print("\n  Selecione um pedido (0 para voltar): ");

        int num = EntradaSegura.lerOpcao(scanner, 0, ativos.size());
        if (num == 0) return;

        Pedido pedido = ativos.get(num - 1);
        avancarStatus(pedido);
    }

    private void avancarStatus(Pedido pedido) {
        TerminalUtils.limparTela();
        TerminalUtils.cabecalho("PEDIDO " + pedido.getId().substring(0, 8));
        System.out.println("  Endereco : " + pedido.getEnderecoEntrega());
        System.out.println("  Status   : " + pedido.getStatus());
        System.out.println();

        StatusPedido proximo;
        String acao;

        switch (pedido.getStatus()) {
            case AGUARDANDO_CONFIRMACAO -> { proximo = StatusPedido.CONFIRMADO;         acao = "Confirmar pedido"; }
            case CONFIRMADO             -> { proximo = StatusPedido.EM_PREPARO;         acao = "Iniciar preparo"; }
            case EM_PREPARO             -> { proximo = StatusPedido.SAIU_PARA_ENTREGA;  acao = "Marcar saiu para entrega"; }
            default -> {
                TerminalUtils.aviso("Aguardando confirmacao do cliente para concluir.");
                TerminalUtils.pausar();
                return;
            }
        }

        if (confirmar(acao + "?")) {
            try {
                pedidoService.atualizarStatus(pedido.getId(), proximo);
                TerminalUtils.sucesso("Status atualizado para: " + proximo);
            } catch (Exception e) {
                TerminalUtils.erro(e.getMessage());
            }
        } else {
            TerminalUtils.aviso("Nenhuma alteracao realizada.");
        }
        TerminalUtils.pausar();
    }

    // ─── Menu de perfil ───────────────────────────────────────────────────────

    private void exibirMenuPerfil(Restaurante restauranteLogado) {
        while (true) {
            TerminalUtils.limparTela();
            TerminalUtils.cabecalho("PERFIL E CONFIGURACOES", "Restaurante: " + restauranteLogado.getNome());

            System.out.println(TerminalUtils.SEPARADOR);
            System.out.println(TerminalUtils.linha("  INFORMACOES"));
            System.out.println(TerminalUtils.SEPARADOR);
            System.out.println(TerminalUtils.linha("    1  »  Visualizar Perfil"));
            System.out.println(TerminalUtils.linha("    2  \u00bb  Editar Dados  (nome, CNPJ, telefone)"));
            System.out.println(TerminalUtils.linha("    3  \u00bb  Editar E-mail"));
            System.out.println(TerminalUtils.linha("    4  \u00bb  Alterar Categoria Global"));
            System.out.println(TerminalUtils.SEPARADOR);
            System.out.println(TerminalUtils.linha("  SEGURANCA"));
            System.out.println(TerminalUtils.SEPARADOR);
            System.out.println(TerminalUtils.linha("    5  \u00bb  Alterar Senha"));
            System.out.println(TerminalUtils.SEPARADOR);
            System.out.println(TerminalUtils.linha("    0  \u00bb  Voltar"));
            System.out.println(TerminalUtils.BASE);
            System.out.println();
            System.out.print("  Escolha uma opcao: ");

            int opcao = EntradaSegura.lerOpcao(scanner, 0, 5);

            switch (opcao) {
                case 1 -> acaoVisualizarPerfil(restauranteLogado);
                case 2 -> acaoEditarDados(restauranteLogado);
                case 3 -> acaoEditarEmail(restauranteLogado);
                case 4 -> acaoAlterarCategoria(restauranteLogado);
                case 5 -> acaoAlterarSenha(restauranteLogado);
                case 0 -> { return; }
            }
        }
    }

    // ─── Acoes do perfil ──────────────────────────────────────────────────────

    private void acaoVisualizarPerfil(Restaurante restauranteLogado) {
        TerminalUtils.limparTela();
        TerminalUtils.cabecalho("VISUALIZAR PERFIL", "Restaurante: " + restauranteLogado.getNome());
        System.out.println("  ID       : " + restauranteLogado.getId());
        System.out.println("  Nome     : " + restauranteLogado.getNome());
        System.out.println("  CNPJ     : " + restauranteLogado.getCnpj());
        System.out.println("  Telefone : " + restauranteLogado.getTelefone());
        System.out.println("  Categoria: " + restauranteLogado.getCategoriaGlobalId());
        System.out.println("  Status   : " + (restauranteLogado.isStatusAtivo() ? "ATIVO" : "BLOQUEADO"));
        TerminalUtils.pausar();
    }

    private void acaoEditarDados(Restaurante restauranteLogado) {
        TerminalUtils.limparTela();
        TerminalUtils.cabecalho("EDITAR DADOS", "Restaurante: " + restauranteLogado.getNome());

        System.out.println("  Preencha os campos abaixo.");
        System.out.println("  Pressione Enter para manter o valor atual.");
        System.out.println();

        try {
            String novoNome     = lerCampoEditavel("Nome",     restauranteLogado.getNome());
            String novoCnpj     = lerCampoEditavel("CNPJ",     restauranteLogado.getCnpj());
            String novoTelefone = lerCampoEditavel("Telefone", restauranteLogado.getTelefone());

            System.out.println();
            System.out.println(TerminalUtils.TOPO);
            System.out.println(TerminalUtils.linha("  Resumo das alteracoes"));
            System.out.println(TerminalUtils.SEPARADOR);
            System.out.println(TerminalUtils.linha(String.format("  Nome      : %s -> %s", restauranteLogado.getNome(), novoNome)));
            System.out.println(TerminalUtils.linha(String.format("  CNPJ      : %s -> %s", restauranteLogado.getCnpj(), novoCnpj)));
            System.out.println(TerminalUtils.linha(String.format("  Telefone  : %s -> %s", restauranteLogado.getTelefone(), novoTelefone)));
            System.out.println(TerminalUtils.BASE);

            if (confirmar("Confirmar essas alteracoes?")) {
                restauranteService.editarPerfil(restauranteLogado, novoNome, novoCnpj, novoTelefone);
                TerminalUtils.sucesso("Dados atualizados com sucesso!");
            } else {
                TerminalUtils.aviso("Alteracoes canceladas.");
            }
        } catch (Exception e) {
            TerminalUtils.erro(e.getMessage());
        }
        TerminalUtils.pausar();
    }

    private void acaoEditarEmail(Restaurante restauranteLogado) {
        TerminalUtils.limparTela();
        TerminalUtils.cabecalho("EDITAR E-MAIL", "Restaurante: " + restauranteLogado.getNome());

        try {
            String novoEmail = lerCampoEditavel("E-mail", restauranteLogado.getEmail());

            if (novoEmail.equals(restauranteLogado.getEmail())) {
                TerminalUtils.aviso("Nenhuma alteracao detectada.");
                TerminalUtils.pausar();
                return;
            }

            if (confirmar("Alterar e-mail para \"" + novoEmail + "\"?")) {
                restauranteService.editarEmail(restauranteLogado, novoEmail);
                TerminalUtils.sucesso("E-mail atualizado com sucesso!");
            } else {
                TerminalUtils.aviso("Alteracao cancelada.");
            }
        } catch (Exception e) {
            TerminalUtils.erro(e.getMessage());
        }
        TerminalUtils.pausar();
    }

    private void acaoAlterarCategoria(Restaurante restauranteLogado) {
        TerminalUtils.limparTela();
        TerminalUtils.cabecalho("ALTERAR CATEGORIA GLOBAL", "Restaurante: " + restauranteLogado.getNome());

        try {
            List<CategoriaGlobal> categorias = categoriaService.listarCategoriasGlobais();

            if (categorias.isEmpty()) {
                TerminalUtils.aviso("Nenhuma categoria global disponivel no momento.");
                TerminalUtils.pausar();
                return;
            }

            System.out.println(TerminalUtils.TOPO);
            System.out.println(TerminalUtils.linha("  Categorias disponiveis"));
            System.out.println(TerminalUtils.SEPARADOR);
            for (int i = 0; i < categorias.size(); i++) {
                System.out.println(TerminalUtils.linha(
                        String.format("    %2d  \u00bb  %s", (i + 1), categorias.get(i).getNome())));
            }
            System.out.println(TerminalUtils.BASE);
            System.out.println();
            System.out.print("  Escolha o numero da categoria: ");

            int num = EntradaSegura.lerOpcao(scanner, 1, categorias.size());
            CategoriaGlobal escolhida = categorias.get(num - 1);

            if (confirmar("Definir categoria como \"" + escolhida.getNome() + "\"?")) {
                restauranteService.alterarCategoria(restauranteLogado, escolhida.getId());
                TerminalUtils.sucesso("Categoria atualizada para: " + escolhida.getNome());
            } else {
                TerminalUtils.aviso("Alteracao cancelada.");
            }
        } catch (Exception e) {
            TerminalUtils.erro(e.getMessage());
        }
        TerminalUtils.pausar();
    }

    private void acaoAlterarSenha(Restaurante restauranteLogado) {
        TerminalUtils.limparTela();
        TerminalUtils.cabecalho("ALTERAR SENHA", "Restaurante: " + restauranteLogado.getNome());

        System.out.println("  Por seguranca, confirme sua senha atual antes de definir a nova.");
        System.out.println();

        try {
            System.out.print("  Senha atual          : ");
            String senhaAtual = scanner.nextLine();

            System.out.print("  Nova senha           : ");
            String novaSenha = scanner.nextLine();

            System.out.print("  Confirmar nova senha : ");
            String confirmacao = scanner.nextLine();

            if (!novaSenha.equals(confirmacao)) {
                TerminalUtils.erro("As senhas nao coincidem. Nenhuma alteracao foi feita.");
                TerminalUtils.pausar();
                return;
            }

            if (novaSenha.equals(senhaAtual)) {
                TerminalUtils.aviso("A nova senha nao pode ser igual a senha atual.");
                TerminalUtils.pausar();
                return;
            }

            restauranteService.alterarSenha(restauranteLogado, senhaAtual, novaSenha);
            TerminalUtils.sucesso("Senha alterada com sucesso!");
        } catch (Exception e) {
            TerminalUtils.erro(e.getMessage());
        }
        TerminalUtils.pausar();
    }
}
