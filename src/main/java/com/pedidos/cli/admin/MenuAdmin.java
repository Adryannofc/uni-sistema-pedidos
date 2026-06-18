package com.pedidos.cli.admin;

import com.pedidos.model.service.AdminService;
import com.pedidos.model.entity.Admin;
import com.pedidos.model.entity.Restaurante;
import com.pedidos.cli.util.EntradaSegura;
import com.pedidos.cli.util.TerminalUtils;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MenuAdmin {
    private final AdminService adminService;
    private final Scanner scanner;
    private final MenuCategorias menuCategorias;

    public MenuAdmin(AdminService adminService, Scanner scanner, MenuCategorias menuCategorias) {
        this.adminService = adminService;
        this.scanner = scanner;
        this.menuCategorias = menuCategorias;
    }

    public void exibir(Admin adminLogado) {
        while (true) {
            TerminalUtils.limparTela();
            TerminalUtils.cabecalho("MENU ADMIN | Olá, " + adminLogado.getNome());
            System.out.println("1 - Listar Restaurantes");
            System.out.println("2 - Aprovar Restaurante");
            System.out.println("3 - Bloquear Restaurante");
            System.out.println("4 - Remover Restaurante");
            System.out.println("5 - Gerenciar Categorias Globais");
            System.out.println("6 - Alterar Senha");
            System.out.println("7 - Visualizar Perfil");
            System.out.println("0 - Logout");
            System.out.println();
            System.out.print("Escolha uma opção: ");

            int opcao = EntradaSegura.lerOpcao(scanner, 0, 7);

            switch (opcao) {
                case 1:
                    try {
                        List<Restaurante> restaurantes = adminService.listarRestaurantes();
                        if (restaurantes.isEmpty()) {
                            System.out.println("Nenhum restaurante cadastrado.");
                        } else {
                            int i = 1;
                            for (Restaurante r : restaurantes) {
                                String status = r.isStatusAtivo() ? "ATIVO" : "AGUARDANDO APROVAÇÃO/BLOQUEADO";
                                System.out.println(i++ + " - " + r.getNome() +
                                        " | Categoria: " + r.getCategoriaGlobalId() +
                                        " | Status: " + status);
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Erro: " + e.getMessage());
                    }
                    TerminalUtils.pausar();
                    break;

                case 2: // Aprovar
                    try {
                        List<Restaurante> pendentes = adminService.listarRestaurantes()
                                .stream().filter(r -> !r.isStatusAtivo()).collect(Collectors.toList());

                        if (pendentes.isEmpty()) {
                            System.out.println("Nenhum restaurante pendente de aprovação.");
                        } else {
                            System.out.println("Restaurantes pendentes:");
                            for (int i = 0; i < pendentes.size(); i++) {
                                Restaurante r = pendentes.get(i);
                                System.out.println((i + 1) + " - " + r.getNome() + " | ID: " + r.getId());
                            }

                            System.out.print("Digite o ID do restaurante: ");
                            String id = scanner.nextLine();
                            adminService.aprovarRestaurante(id);
                            System.out.println("Restaurante aprovado com sucesso!");
                        }
                    } catch (Exception e) {
                        System.out.println("Erro: " + e.getMessage());
                    }
                    TerminalUtils.pausar();
                    break;

                case 3: // Bloquear
                    try {
                        List<Restaurante> ativos = adminService.listarRestaurantes()
                                .stream().filter(Restaurante::isStatusAtivo).collect(Collectors.toList());

                        if (ativos.isEmpty()) {
                            System.out.println("Nenhum restaurante ativo para bloquear.");
                        } else {
                            System.out.println("Restaurantes ativos:");
                            for (int i = 0; i < ativos.size(); i++) {
                                Restaurante r = ativos.get(i);
                                System.out.println((i + 1) + " - " + r.getNome() + " | ID: " + r.getId());
                            }

                            System.out.print("Digite o ID do restaurante: ");
                            String id = scanner.nextLine();
                            adminService.bloquearRestaurante(id);
                            System.out.println("Restaurante bloqueado com sucesso!");
                        }
                    } catch (Exception e) {
                        System.out.println("Erro: " + e.getMessage());
                    }
                    TerminalUtils.pausar();
                    break;

                case 4: // Remover
                    try {
                        List<Restaurante> todos = adminService.listarRestaurantes();
                        if (todos.isEmpty()) {
                            System.out.println("Nenhum restaurante cadastrado.");
                        } else {
                            System.out.println("Restaurantes cadastrados:");
                            for (int i = 0; i < todos.size(); i++) {
                                Restaurante r = todos.get(i);
                                System.out.println((i + 1) + " - " + r.getNome() + " | ID: " + r.getId());
                            }

                            System.out.print("Digite o ID do restaurante para remover: ");
                            String id = scanner.nextLine();

                            System.out.print("Tem certeza? Esta ação é irreversível. (S/N): ");
                            String confirmacao = scanner.nextLine();
                            if (confirmacao.equalsIgnoreCase("S")) {
                                adminService.removerRestaurante(id);
                                System.out.println("Restaurante removido permanentemente.");
                            } else {
                                System.out.println("Operação cancelada.");
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Erro: " + e.getMessage());
                    }
                    TerminalUtils.pausar();
                    break;

                case 5:
                    try {
                        menuCategorias.exibir();
                    } catch (Exception e) {
                        System.out.println("Erro: " + e.getMessage());
                    }
                    TerminalUtils.pausar();
                    break;

                case 6: // Alterar senha
                    try {
                        System.out.print("Senha atual: ");
                        String senhaAtual = scanner.nextLine();
                        System.out.print("Nova senha: ");
                        String novaSenha = scanner.nextLine();
                        System.out.print("Confirmar nova senha: ");
                        String confirmacao = scanner.nextLine();

                        adminService.alterarSenha(adminLogado, senhaAtual, novaSenha, confirmacao);
                        System.out.println("Senha alterada com sucesso.");
                    } catch (Exception e) {
                        System.out.println("Erro: " + e.getMessage());
                    }
                    TerminalUtils.pausar();
                    break;

                case 7:
                    TerminalUtils.cabecalho("PAINEL DO ADMINISTRADOR");
                    System.out.println("Id    : " + adminLogado.getId());
                    System.out.println("Nome  : " + adminLogado.getNome());
                    System.out.println("E-mail: " + adminLogado.getEmail());
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
