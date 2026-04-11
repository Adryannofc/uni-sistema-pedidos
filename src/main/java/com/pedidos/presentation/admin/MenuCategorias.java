package com.pedidos.presentation.admin;

import com.pedidos.application.service.AdminService;
import com.pedidos.application.service.CategoriaService;
import com.pedidos.domain.entities.CategoriaGlobalEntity;
import com.pedidos.presentation.util.EntradaSegura;
import com.pedidos.presentation.util.TerminalUtils;

import java.util.List;
import java.util.Scanner;

public class MenuCategorias {
    private final AdminService adminService;
    private final CategoriaService categoriaService;
    private final Scanner scanner;

    public MenuCategorias(AdminService adminService, CategoriaService categoriaService, Scanner scanner) {
        this.adminService = adminService;
        this.categoriaService = categoriaService;
        this.scanner = scanner;
    }

    public void exibir() {

        while (true) {
            TerminalUtils.limparTela();
            TerminalUtils.cabecalho("CATEGORIAS GLOBAIS");
            System.out.println("1 - Listar todas as categorias");
            System.out.println("2 - Adicionar nova categoria");
            System.out.println("3 - Editar categoria");
            System.out.println("4 - Remover categoria");
            System.out.println("0 - Voltar ao MenuAdmin");
            System.out.println();
            System.out.print("Escolha uma opção: ");

            int opcao = EntradaSegura.lerOpcao(scanner, 0, 4);

            switch (opcao) {
                case 1:
                    try {
                        List<CategoriaGlobalEntity> categorias = categoriaService.listarCategoriasGlobais();
                        if (categorias.isEmpty()) {
                            System.out.println("Nenhuma categoria cadastrada.");
                        } else {
                            for (int i = 0; i < categorias.size(); i++) {
                                CategoriaGlobalEntity c = categorias.get(i);
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
                        System.out.print("Descrição: ");
                        String descricao = scanner.nextLine();

                        if (nome.isBlank()) {
                            System.out.println("O nome não pode ser vazio.");
                        } else {
                            categoriaService.criarCategoriaGlobal(nome, descricao);
                            System.out.println("Categoria " + nome + " criada com sucesso.");
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    TerminalUtils.pausar();
                    break;

                case 3:
                    try {
                        List<CategoriaGlobalEntity> categorias = categoriaService.listarCategoriasGlobais();
                        if (categorias.isEmpty()) {
                            System.out.println("Nenhuma categoria cadastrada.");
                        } else {
                            for (int i = 0; i < categorias.size(); i++) {
                                CategoriaGlobalEntity c = categorias.get(i);
                                System.out.println((i + 1) + " - " + c.getNome() + " | ID: " + c.getId());
                            }
                            System.out.print("Digite o número da categoria: ");
                            int numero = EntradaSegura.lerOpcao(scanner, 1, categorias.size());
                            CategoriaGlobalEntity selecionada = categorias.get(numero - 1);

                            System.out.print("Novo nome: ");
                            String novoNome = scanner.nextLine();
                            System.out.print("Nova descrição: ");
                            String novaDescricao = scanner.nextLine();

                            categoriaService.editarCategoriaGlobal(selecionada.getId(), novoNome, novaDescricao);
                            System.out.println("Categoria atualizada com sucesso.");
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    TerminalUtils.pausar();
                    break;

                case 4:
                    try {
                        List<CategoriaGlobalEntity> categorias = categoriaService.listarCategoriasGlobais();
                        if (categorias.isEmpty()) {
                            System.out.println("Nenhuma categoria cadastrada.");
                        } else {
                            for (int i = 0; i < categorias.size(); i++) {
                                CategoriaGlobalEntity c = categorias.get(i);
                                System.out.println((i + 1) + " - " + c.getNome() + " | ID: " + c.getId());
                            }
                            System.out.print("Digite o número da categoria: ");
                            int numero = EntradaSegura.lerOpcao(scanner, 1, categorias.size());
                            CategoriaGlobalEntity selecionada = categorias.get(numero - 1);

                            System.out.print("Tem certeza? (S/N): ");
                            String confirmacao = scanner.nextLine();
                            if (confirmacao.equalsIgnoreCase("S")) {
                                categoriaService.removerCategoriaGlobal(selecionada.getId());
                                System.out.println("Categoria removida com sucesso.");
                            } else {
                                System.out.println("Operação cancelada.");
                            }
                        }
                    } catch (Exception e) {
                        if (e.getMessage().contains("em uso")) {
                            System.out.println("Não é possível remover: categoria vinculada a restaurante(s).");
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