package com.pedidos.cli.restaurante;

import com.pedidos.model.service.CategoriaService;
import com.pedidos.model.entity.CategoriaCardapio;
import com.pedidos.model.entity.Restaurante;
import com.pedidos.cli.util.EntradaSegura;
import com.pedidos.cli.util.TerminalUtils;

import java.util.List;
import java.util.Scanner;

public class MenuCategoriasCardapio {
    private final CategoriaService categoriaService;
    private final Scanner scanner;

    public MenuCategoriasCardapio(CategoriaService categoriaService, Scanner scanner) {
        this.categoriaService = categoriaService;
        this.scanner = scanner;
    }

    public void exibir(Restaurante restauranteLogado) {
        while (true) {
            TerminalUtils.limparTela();
            TerminalUtils.cabecalho("RESTAURANTE | CATEGORIAS DO CARDÁPIO");
            System.out.println("1 - Listar Categorias");
            System.out.println("2 - Adicionar Categoria");
            System.out.println("3 - Editar Categoria");
            System.out.println("4 - Remover Categoria");
            System.out.println("0 - Voltar");
            System.out.println();
            System.out.print("Escolha uma opção: ");

            int opcao = EntradaSegura.lerOpcao(scanner, 0, 4);

            switch (opcao) {
                case 1:
                    try {
                        List<CategoriaCardapio> categorias = categoriaService.listarCategoriasCardapio(restauranteLogado.getId());
                        if (categorias.isEmpty()) {
                            System.out.println("Nenhuma categoria cadastrada.");
                        } else {
                            for (int i = 0; i < categorias.size(); i++) {
                                CategoriaCardapio c = categorias.get(i);
                                System.out.println((i + 1) + " - " + c.getNome() + " | ID: " + c.getId());
                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    TerminalUtils.pausar();
                    break;

                case 2:
                    try {
                        System.out.print("Nome da categoria: ");
                        String nome = scanner.nextLine();

                        if (nome.isBlank()) {
                            System.out.println("O nome não pode ser vazio.");
                        } else {
                            System.out.print("Descrição (opcional): ");
                            String descricao = scanner.nextLine();

                            categoriaService.criarCategoriaCardapio(nome, descricao, restauranteLogado.getId());
                            System.out.println("Categoria " + nome + " criada com sucesso.");
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    TerminalUtils.pausar();
                    break;

                case 3:
                    try {
                        List<CategoriaCardapio> categorias = categoriaService.listarCategoriasCardapio(restauranteLogado.getId());
                        if (categorias.isEmpty()) {
                            System.out.println("Nenhuma categoria para editar.");
                            TerminalUtils.pausar();
                            break;
                        }

                        for (int i = 0; i < categorias.size(); i++) {
                            CategoriaCardapio c = categorias.get(i);
                            System.out.println((i + 1) + " - " + c.getNome());
                        }
                        System.out.print("Escolha o número da categoria: ");
                        int num = EntradaSegura.lerOpcao(scanner, 1, categorias.size());
                        CategoriaCardapio selecionada = categorias.get(num - 1);

                        System.out.print("Nome atual: " + selecionada.getNome() + ". Novo nome: ");
                        String novoNome = scanner.nextLine();

                        System.out.print("Descrição atual: " + selecionada.getDescricao() + ". Nova descrição: ");
                        String novaDescricao = scanner.nextLine();

                        categoriaService.editarCategoriaCardapio(selecionada.getId(), novoNome, novaDescricao);
                        System.out.println("Categoria atualizada com sucesso.");
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    TerminalUtils.pausar();
                    break;

                case 4:
                    try {
                        List<CategoriaCardapio> categorias = categoriaService.listarCategoriasCardapio(restauranteLogado.getId());
                        if (categorias.isEmpty()) {
                            System.out.println("Nenhuma categoria cadastrada.");
                            TerminalUtils.pausar();
                            break;
                        }

                        for (int i = 0; i < categorias.size(); i++) {
                            CategoriaCardapio c = categorias.get(i);
                            System.out.println((i + 1) + " - " + c.getNome());
                        }
                        System.out.print("Escolha o número da categoria: ");
                        int num = EntradaSegura.lerOpcao(scanner, 1, categorias.size());
                        CategoriaCardapio selecionada = categorias.get(num - 1);

                        System.out.print("Tem certeza? Produtos vinculados perderão a categoria. (S/N): ");
                        String confirmacao = scanner.nextLine();
                        if (confirmacao.equalsIgnoreCase("S")) {
                            categoriaService.removerCategoriaCardapio(selecionada.getId());
                            System.out.println("Categoria removida com sucesso.");
                        } else {
                            System.out.println("Operação cancelada.");
                        }
                    } catch (Exception e) {
                        if (e.getMessage().contains("em uso")) {
                            System.out.println("Não é possível remover: categoria vinculada a produto(s).");
                        } else {
                            System.out.println(e.getMessage());
                        }
                    }
                    TerminalUtils.pausar();
                    break;

                case 0:
                    return;

                default:
                    System.out.println("Opção inválida. Tente novamente.");
                    TerminalUtils.pausar();
            }
        }
    }
}