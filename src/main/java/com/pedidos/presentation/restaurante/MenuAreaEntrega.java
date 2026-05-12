package com.pedidos.presentation.restaurante;

import com.pedidos.application.service.AreaEntregaService;
import com.pedidos.domain.entities.AreaEntrega;
import com.pedidos.domain.entities.Restaurante;
import com.pedidos.presentation.util.EntradaSegura;
import com.pedidos.presentation.util.TerminalUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class MenuAreaEntrega {
    private final AreaEntregaService areaEntregaService;
    private final Scanner scanner;

    public MenuAreaEntrega(AreaEntregaService areaEntregaService, Scanner scanner) {
        this.areaEntregaService = areaEntregaService;
        this.scanner = scanner;
    }

    public void exibir(Restaurante restauranteLogado) {
        while (true) {
            TerminalUtils.limparTela();
            TerminalUtils.cabecalho("RESTAURANTE | ÁREA DE ENTREGA");
            System.out.println("1 - Listar Áreas");
            System.out.println("2 - Adicionar Área");
            System.out.println("3 - Editar Área");
            System.out.println("4 - Remover Área");
            System.out.println("0 - Voltar");
            System.out.print("\nEscolha uma opção: ");

            int opcao = EntradaSegura.lerOpcao(scanner, 0, 4);

            switch (opcao) {
                case 1:
                    try {
                        List<AreaEntrega> areas = areaEntregaService.listarAreasPorRestaurante(restauranteLogado.getId());
                        if (areas.isEmpty()) {
                            System.out.println("Nenhuma área cadastrada.");
                        } else {
                            for (int i = 0; i < areas.size(); i++) {
                                AreaEntrega a = areas.get(i);
                                System.out.println((i + 1) + " - Bairro: " + a.getBairro()
                                        + " | Taxa: R$ " + a.getTaxaEntrega()
                                        + " | Previsão: " + a.getPrevisaoMinutos() + " min"
                                        + " | ID: " + a.getId());
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Erro: " + e.getMessage());
                    }
                    TerminalUtils.pausar();
                    break;

                case 2:
                    try {
                        System.out.print("Bairro: ");
                        String bairro = scanner.nextLine();

                        System.out.print("Distância (km, ex: 3.5): ");
                        BigDecimal distancia = new BigDecimal(scanner.nextLine().replace(",", "."));

                        System.out.print("Taxa de entrega (R$, ex: 8.00): ");
                        BigDecimal taxa = new BigDecimal(scanner.nextLine().replace(",", "."));

                        System.out.print("Previsão (minutos): ");
                        int previsao = Integer.parseInt(scanner.nextLine().trim());

                        areaEntregaService.criarAreaEntrega(restauranteLogado, bairro, distancia, taxa, previsao);
                        System.out.println("Área cadastrada com sucesso.");
                    } catch (Exception e) {
                        System.out.println("Erro: " + e.getMessage());
                    }
                    TerminalUtils.pausar();
                    break;

                case 3:
                    try {
                        List<AreaEntrega> areas = areaEntregaService.listarAreasPorRestaurante(restauranteLogado.getId());
                        if (areas.isEmpty()) {
                            System.out.println("Nenhuma área cadastrada.");
                            TerminalUtils.pausar();
                            break;
                        }
                        for (int i = 0; i < areas.size(); i++) {
                            System.out.println((i + 1) + " - " + areas.get(i).getBairro());
                        }
                        System.out.print("Escolha o número da área: ");
                        int num = EntradaSegura.lerOpcao(scanner, 1, areas.size());
                        AreaEntrega selecionada = areas.get(num - 1);

                        System.out.print("Novo bairro (" + selecionada.getBairro() + "): ");
                        String novoBairro = scanner.nextLine();

                        System.out.print("Nova distância (" + selecionada.getDistanciaKm() + " km): ");
                        BigDecimal novaDistancia = new BigDecimal(scanner.nextLine().replace(",", "."));

                        System.out.print("Nova taxa (R$ " + selecionada.getTaxaEntrega() + "): ");
                        BigDecimal novaTaxa = new BigDecimal(scanner.nextLine().replace(",", "."));

                        System.out.print("Nova previsão (" + selecionada.getPrevisaoMinutos() + " min): ");
                        int novaPrevisao = Integer.parseInt(scanner.nextLine().trim());

                        areaEntregaService.editarAreaEntrega(selecionada.getId(), novoBairro, novaDistancia, novaTaxa, novaPrevisao);
                        System.out.println("Área atualizada com sucesso.");
                    } catch (Exception e) {
                        System.out.println("Erro: " + e.getMessage());
                    }
                    TerminalUtils.pausar();
                    break;

                case 4:
                    try {
                        List<AreaEntrega> areas = areaEntregaService.listarAreasPorRestaurante(restauranteLogado.getId());
                        if (areas.isEmpty()) {
                            System.out.println("Nenhuma área cadastrada.");
                            TerminalUtils.pausar();
                            break;
                        }
                        for (int i = 0; i < areas.size(); i++) {
                            System.out.println((i + 1) + " - " + areas.get(i).getBairro());
                        }
                        System.out.print("Escolha o número da área: ");
                        int num = EntradaSegura.lerOpcao(scanner, 1, areas.size());
                        AreaEntrega selecionada = areas.get(num - 1);

                        System.out.print("Tem certeza? (S/N): ");
                        if (scanner.nextLine().equalsIgnoreCase("S")) {
                            areaEntregaService.deletarAreaEntrega(selecionada.getId());
                            System.out.println("Área removida com sucesso.");
                        } else {
                            System.out.println("Operação cancelada.");
                        }
                    } catch (Exception e) {
                        System.out.println("Erro: " + e.getMessage());
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
