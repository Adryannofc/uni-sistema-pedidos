package com.pedidos.presentation.restaurante;

import com.pedidos.application.service.CategoriaService;
import com.pedidos.application.service.ProdutoService;
import com.pedidos.domain.entities.CategoriaCardapioEntity;
import com.pedidos.domain.entities.ProdutoEntity;
import com.pedidos.domain.entities.RestauranteEntity;
import com.pedidos.presentation.util.EntradaSegura;
import com.pedidos.presentation.util.TerminalUtils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class MenuProdutos {
    private final ProdutoService produtoService;
    private final CategoriaService categoriaService;
    private final Scanner scanner;

    private static final NumberFormat MOEDA =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public MenuProdutos(ProdutoService produtoService, CategoriaService categoriaService, Scanner scanner) {
        this.produtoService = produtoService;
        this.categoriaService = categoriaService;
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

    /**
     * Exibe cada produto como um card completo.
     * Usado na listagem de visualizacao.
     */
    private void exibirCardProduto(int indice, ProdutoEntity p) {
        String status = p.isStatusAtivo() ? "[ ATIVO ]" : "[ INATIVO ]";
        String preco  = MOEDA.format(p.getPreco());
        String desc   = p.getDescricao().isBlank() ? "-" : p.getDescricao();

        System.out.println(TerminalUtils.TOPO);
        System.out.println(TerminalUtils.linha(String.format("  %d. %s  %s", indice, p.getNome(), status)));
        System.out.println(TerminalUtils.SEPARADOR);
        System.out.println(TerminalUtils.linha("  Descricao : " + desc));
        System.out.println(TerminalUtils.linha("  Preco     : " + preco));
        System.out.println(TerminalUtils.linha("  Categoria : " + p.getCategoriaCardapioId()));
        System.out.println(TerminalUtils.BASE);
    }

    /**
     * Exibe lista resumida (numero, nome, status) para fluxos de selecao.
     */
    private void exibirListaResumida(List<ProdutoEntity> produtoEntities) {
        System.out.println(TerminalUtils.TOPO);
        System.out.println(TerminalUtils.linha("  #    Nome                         Status"));
        System.out.println(TerminalUtils.SEPARADOR);
        for (int i = 0; i < produtoEntities.size(); i++) {
            ProdutoEntity p      = produtoEntities.get(i);
            String  status = p.isStatusAtivo() ? "ATIVO" : "INATIVO";
            System.out.println(TerminalUtils.linha(
                    String.format("  %-3d  %-28s  %s", (i + 1), p.getNome(), status)));
        }
        System.out.println(TerminalUtils.BASE);
    }

    /** Le e valida um preco. Retorna null e exibe erro se o formato for invalido. */
    private BigDecimal lerPreco(String prompt) {
        System.out.print(prompt);
        String entrada = scanner.nextLine().trim();
        try {
            return new BigDecimal(entrada.replace(",", "."));
        } catch (Exception e) {
            TerminalUtils.erro("Preco invalido. Use o formato: 12,90");
            return null;
        }
    }

    /**
     * Exibe categorias em caixa e retorna o ID da escolhida.
     * Retorna null se a lista estiver vazia.
     */
    private String selecionarCategoria(List<CategoriaCardapioEntity> categorias) {
        if (categorias.isEmpty()) {
            TerminalUtils.aviso("Nenhuma categoria disponivel.");
            return null;
        }
        System.out.println();
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
        return categorias.get(num - 1).getId();
    }

    // ─── Menu principal ───────────────────────────────────────────────────────

    public void exibir(RestauranteEntity restauranteEntityLogado) {
        while (true) {
            TerminalUtils.limparTela();
            TerminalUtils.cabecalho("PRODUTOS", "Restaurante: " + restauranteEntityLogado.getNome());

            System.out.println(TerminalUtils.SEPARADOR);
            System.out.println(TerminalUtils.linha("  ACOES"));
            System.out.println(TerminalUtils.SEPARADOR);
            System.out.println(TerminalUtils.linha("    1  \u00bb  Listar Produtos"));
            System.out.println(TerminalUtils.linha("    2  \u00bb  Adicionar Produto"));
            System.out.println(TerminalUtils.linha("    3  \u00bb  Editar Produto"));
            System.out.println(TerminalUtils.linha("    4  \u00bb  Ativar / Inativar Produto"));
            System.out.println(TerminalUtils.linha("    5  \u00bb  Remover Produto"));
            System.out.println(TerminalUtils.SEPARADOR);
            System.out.println(TerminalUtils.linha("    0  \u00bb  Voltar"));
            System.out.println(TerminalUtils.BASE);
            System.out.println();
            System.out.print("  Escolha uma opcao: ");

            int opcao = EntradaSegura.lerOpcao(scanner, 0, 5);

            switch (opcao) {
                case 1 -> acaoListar(restauranteEntityLogado);
                case 2 -> acaoAdicionar(restauranteEntityLogado);
                case 3 -> acaoEditar(restauranteEntityLogado);
                case 4 -> acaoAtivarInativar(restauranteEntityLogado);
                case 5 -> acaoRemover(restauranteEntityLogado);
                case 0 -> { return; }
            }
        }
    }

    // ─── Acoes ────────────────────────────────────────────────────────────────

    private void acaoListar(RestauranteEntity restauranteEntityLogado) {
        TerminalUtils.limparTela();
        TerminalUtils.cabecalho("LISTA DE PRODUTOS", "Restaurante: " + restauranteEntityLogado.getNome());
        try {
            List<ProdutoEntity> produtoEntities = produtoService.listarPorRestaurante(restauranteEntityLogado.getId());
            if (produtoEntities.isEmpty()) {
                TerminalUtils.aviso("Nenhum produto cadastrado ainda.");
            } else {
                for (int i = 0; i < produtoEntities.size(); i++) {
                    exibirCardProduto(i + 1, produtoEntities.get(i));
                    System.out.println();
                }
            }
        } catch (Exception e) {
            TerminalUtils.erro(e.getMessage());
        }
        TerminalUtils.pausar();
    }

    private void acaoAdicionar(RestauranteEntity restauranteEntityLogado) {
        TerminalUtils.limparTela();
        TerminalUtils.cabecalho("ADICIONAR PRODUTO", "Restaurante: " + restauranteEntityLogado.getNome());
        try {
            List<CategoriaCardapioEntity> categorias =
                    categoriaService.listarCategoriasCardapio(restauranteEntityLogado.getId());
            if (categorias.isEmpty()) {
                TerminalUtils.aviso("Cadastre ao menos uma categoria antes de adicionar produtos.");
                TerminalUtils.pausar();
                return;
            }

            System.out.print("  Nome do produto      : ");
            String nome = scanner.nextLine().trim();

            System.out.print("  Descricao (opcional) : ");
            String descricao = scanner.nextLine().trim();

            BigDecimal preco = lerPreco("  Preco (ex: 12,90)   : ");
            if (preco == null) { TerminalUtils.pausar(); return; }

            String categoriaId = selecionarCategoria(categorias);
            if (categoriaId == null) { TerminalUtils.pausar(); return; }

            System.out.println();
            System.out.println(TerminalUtils.TOPO);
            System.out.println(TerminalUtils.linha("  Resumo do novo produto"));
            System.out.println(TerminalUtils.SEPARADOR);
            System.out.println(TerminalUtils.linha("  Nome      : " + nome));
            System.out.println(TerminalUtils.linha("  Descricao : " + (descricao.isBlank() ? "-" : descricao)));
            System.out.println(TerminalUtils.linha("  Preco     : " + MOEDA.format(preco)));
            System.out.println(TerminalUtils.BASE);

            if (confirmar("Cadastrar este produto?")) {
                produtoService.criarProduto(nome, descricao, preco, categoriaId, restauranteEntityLogado.getId());
                TerminalUtils.sucesso("Produto \"" + nome + "\" cadastrado com sucesso!");
            } else {
                TerminalUtils.aviso("Cadastro cancelado.");
            }
        } catch (Exception e) {
            TerminalUtils.erro(e.getMessage());
        }
        TerminalUtils.pausar();
    }

    private void acaoEditar(RestauranteEntity restauranteEntityLogado) {
        TerminalUtils.limparTela();
        TerminalUtils.cabecalho("EDITAR PRODUTO", "Restaurante: " + restauranteEntityLogado.getNome());
        try {
            List<ProdutoEntity> produtoEntities = produtoService.listarPorRestaurante(restauranteEntityLogado.getId());
            if (produtoEntities.isEmpty()) {
                TerminalUtils.aviso("Nenhum produto cadastrado para editar.");
                TerminalUtils.pausar();
                return;
            }

            exibirListaResumida(produtoEntities);
            System.out.println();
            System.out.print("  Escolha o numero do produto: ");
            int num = EntradaSegura.lerOpcao(scanner, 1, produtoEntities.size());
            ProdutoEntity selecionado = produtoEntities.get(num - 1);

            System.out.println();
            System.out.println("  Preencha os campos abaixo.");
            System.out.println("  Pressione Enter para manter o valor atual.");
            System.out.println();

            String novoNome      = lerCampoEditavel("Nome",      selecionado.getNome());
            String novaDescricao = lerCampoEditavel("Descricao", selecionado.getDescricao());

            System.out.printf("  Preco%n");
            System.out.printf("  Atual: [ %s ]%n", MOEDA.format(selecionado.getPreco()));
            System.out.print("  Novo  (Enter para manter): ");
            String precoStr = scanner.nextLine().trim();
            BigDecimal novoPreco = selecionado.getPreco();
            if (!precoStr.isBlank()) {
                try {
                    novoPreco = new BigDecimal(precoStr.replace(",", "."));
                } catch (Exception e) {
                    TerminalUtils.erro("Preco invalido. Nenhuma alteracao foi feita.");
                    TerminalUtils.pausar();
                    return;
                }
            }

            List<CategoriaCardapioEntity> categorias =
                    categoriaService.listarCategoriasCardapio(restauranteEntityLogado.getId());
            String novaCategoriaId = selecionarCategoria(categorias);
            if (novaCategoriaId == null) { TerminalUtils.pausar(); return; }

            if (confirmar("Salvar alteracoes em \"" + selecionado.getNome() + "\"?")) {
                produtoService.editarProduto(
                        selecionado.getId(), restauranteEntityLogado.getId(),
                        novoNome, novaDescricao, novoPreco, novaCategoriaId);
                TerminalUtils.sucesso("Produto atualizado com sucesso!");
            } else {
                TerminalUtils.aviso("Edicao cancelada.");
            }
        } catch (Exception e) {
            TerminalUtils.erro(e.getMessage());
        }
        TerminalUtils.pausar();
    }

    private void acaoAtivarInativar(RestauranteEntity restauranteEntityLogado) {
        TerminalUtils.limparTela();
        TerminalUtils.cabecalho("ATIVAR / INATIVAR PRODUTO", "Restaurante: " + restauranteEntityLogado.getNome());
        try {
            List<ProdutoEntity> produtoEntities = produtoService.listarPorRestaurante(restauranteEntityLogado.getId());
            if (produtoEntities.isEmpty()) {
                TerminalUtils.aviso("Nenhum produto cadastrado.");
                TerminalUtils.pausar();
                return;
            }

            exibirListaResumida(produtoEntities);
            System.out.println();
            System.out.print("  Escolha o numero do produto: ");
            int num = EntradaSegura.lerOpcao(scanner, 1, produtoEntities.size());
            ProdutoEntity selecionado = produtoEntities.get(num - 1);

            String statusAtual = selecionado.isStatusAtivo() ? "ATIVO" : "INATIVO";
            String acao        = selecionado.isStatusAtivo() ? "INATIVAR" : "ATIVAR";

            if (confirmar("Produto esta " + statusAtual + ". Deseja " + acao + "?")) {
                produtoService.ativarInativar(selecionado.getId(), restauranteEntityLogado.getId());
                TerminalUtils.sucesso("Status alterado com sucesso!");
            } else {
                TerminalUtils.aviso("Operacao cancelada.");
            }
        } catch (Exception e) {
            TerminalUtils.erro(e.getMessage());
        }
        TerminalUtils.pausar();
    }

    private void acaoRemover(RestauranteEntity restauranteEntityLogado) {
        TerminalUtils.limparTela();
        TerminalUtils.cabecalho("REMOVER PRODUTO", "Restaurante: " + restauranteEntityLogado.getNome());
        try {
            List<ProdutoEntity> produtoEntities = produtoService.listarPorRestaurante(restauranteEntityLogado.getId());
            if (produtoEntities.isEmpty()) {
                TerminalUtils.aviso("Nenhum produto cadastrado.");
                TerminalUtils.pausar();
                return;
            }

            exibirListaResumida(produtoEntities);
            System.out.println();
            System.out.print("  Escolha o numero do produto: ");
            int num = EntradaSegura.lerOpcao(scanner, 1, produtoEntities.size());
            ProdutoEntity selecionado = produtoEntities.get(num - 1);

            if (TerminalUtils.confirmarPerigo(
                    "Remover permanentemente \"" + selecionado.getNome() + "\"?", scanner)) {
                produtoService.removerProduto(selecionado.getId(), restauranteEntityLogado.getId());
                TerminalUtils.sucesso("Produto removido permanentemente.");
            } else {
                TerminalUtils.aviso("Remocao cancelada.");
            }
        } catch (Exception e) {
            TerminalUtils.erro(e.getMessage());
        }
        TerminalUtils.pausar();
    }
}