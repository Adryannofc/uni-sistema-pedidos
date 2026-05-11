package com.pedidos.presentation.restaurante;

import com.pedidos.application.service.HorarioService;
import com.pedidos.domain.entities.HorarioFuncionamento;
import com.pedidos.domain.entities.Restaurante;
import com.pedidos.presentation.util.EntradaSegura;
import com.pedidos.presentation.util.TerminalUtils;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

public class MenuHorarios {
    private final HorarioService horarioService;
    private final Scanner scanner;

    public MenuHorarios(HorarioService horarioService, Scanner scanner) {
        this.horarioService = horarioService;
        this.scanner = scanner;
    }

    public void exibir(Restaurante restauranteLogado) {
        while (true) {
            TerminalUtils.limparTela();
            TerminalUtils.cabecalho("RESTAURANTE | HORÁRIOS DE FUNCIONAMENTO");
            System.out.println("1 - Listar Horários");
            System.out.println("2 - Adicionar Horário");
            System.out.println("3 - Editar Horário");
            System.out.println("4 - Remover Horário");
            System.out.println("0 - Voltar");
            System.out.println();
            System.out.print("Escolha uma opção: ");

            int opcao = EntradaSegura.lerOpcao(scanner, 0, 4);

            switch (opcao) {
                case 1:
                    try {
                        List<HorarioFuncionamento> horarios = horarioService.listarHorarioPorRestaurante(restauranteLogado.getId());
                        if (horarios.isEmpty()) {
                            System.out.println("Nenhum horário cadastrado.");
                        } else {
                            for (int i = 0; i < horarios.size(); i++) {
                                HorarioFuncionamento h = horarios.get(i);
                                System.out.println((i + 1) + " - " + traduzirDia(h.getDiaSemana())
                                        + " | " + h.getHoraInicio().toString()
                                        + " às " + h.getHoraFim().toString());
                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    TerminalUtils.pausar();
                    break;

                case 2:
                    try {
                        List<DayOfWeek> diasDisponiveis = horarioService.listarDiasDisponiveis(restauranteLogado.getId());
                        if (diasDisponiveis.isEmpty()) {
                            System.out.println("Todos os dias da semana já estão cadastrados.");
                            TerminalUtils.pausar();
                            break;
                        }

                        System.out.println("Dias disponíveis:");
                        for (int i = 0; i < diasDisponiveis.size(); i++) {
                            System.out.println((i + 1) + " - " + traduzirDia(diasDisponiveis.get(i)));
                        }
                        System.out.print("Escolha o número do dia: ");
                        int numDia = EntradaSegura.lerOpcao(scanner, 1, diasDisponiveis.size());
                        DayOfWeek diaSelecionado = diasDisponiveis.get(numDia - 1);

                        System.out.print("Hora de início (HH:mm): ");
                        String inicioStr = scanner.nextLine();
                        LocalTime horaInicio;
                        try {
                            horaInicio = LocalTime.parse(inicioStr);
                        } catch (Exception e) {
                            System.out.println("Formato inválido. Use HH:mm");
                            TerminalUtils.pausar();
                            break;
                        }

                        System.out.print("Hora de fim (HH:mm): ");
                        String fimStr = scanner.nextLine();
                        LocalTime horaFim;
                        try {
                            horaFim = LocalTime.parse(fimStr);
                        } catch (Exception e) {
                            System.out.println("Formato inválido. Use HH:mm");
                            TerminalUtils.pausar();
                            break;
                        }

                        horarioService.criarHorario(restauranteLogado, diaSelecionado, horaInicio, horaFim);
                        System.out.println("Horário cadastrado com sucesso.");
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    TerminalUtils.pausar();
                    break;

                case 3:
                    try {
                        List<HorarioFuncionamento> horarios = horarioService.listarHorarioPorRestaurante(restauranteLogado.getId());
                        if (horarios.isEmpty()) {
                            System.out.println("Nenhum horário para editar.");
                            TerminalUtils.pausar();
                            break;
                        }

                        for (int i = 0; i < horarios.size(); i++) {
                            HorarioFuncionamento h = horarios.get(i);
                            System.out.println((i + 1) + " - " + traduzirDia(h.getDiaSemana())
                                    + " | " + h.getHoraInicio() + " às " + h.getHoraFim());
                        }
                        System.out.print("Escolha o número do horário: ");
                        int num = EntradaSegura.lerOpcao(scanner, 1, horarios.size());
                        HorarioFuncionamento selecionado = horarios.get(num - 1);

                        System.out.println("⚠ Para alterar o dia, remova e cadastre novamente.");
                        System.out.println("Horário atual: " + selecionado.getHoraInicio() + " às " + selecionado.getHoraFim());

                        System.out.print("Nova hora de início (HH:mm): ");
                        String inicioStr = scanner.nextLine();
                        LocalTime novaHoraInicio;
                        try {
                            novaHoraInicio = LocalTime.parse(inicioStr);
                        } catch (Exception e) {
                            System.out.println("Formato inválido. Use HH:mm");
                            TerminalUtils.pausar();
                            break;
                        }

                        System.out.print("Nova hora de fim (HH:mm): ");
                        String fimStr = scanner.nextLine();
                        LocalTime novaHoraFim;
                        try {
                            novaHoraFim = LocalTime.parse(fimStr);
                        } catch (Exception e) {
                            System.out.println("Formato inválido. Use HH:mm");
                            TerminalUtils.pausar();
                            break;
                        }

                        horarioService.editarHorario(selecionado.getId(), novaHoraInicio, novaHoraFim);
                        System.out.println("Horário atualizado com sucesso.");
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    TerminalUtils.pausar();
                    break;

                case 4:
                    try {
                        List<HorarioFuncionamento> horarios = horarioService.listarHorarioPorRestaurante(restauranteLogado.getId());
                        if (horarios.isEmpty()) {
                            System.out.println("Nenhum horário cadastrado.");
                            TerminalUtils.pausar();
                            break;
                        }

                        for (int i = 0; i < horarios.size(); i++) {
                            HorarioFuncionamento h = horarios.get(i);
                            System.out.println((i + 1) + " - " + traduzirDia(h.getDiaSemana())
                                    + " | " + h.getHoraInicio() + " às " + h.getHoraFim());
                        }
                        System.out.print("Escolha o número do horário: ");
                        int num = EntradaSegura.lerOpcao(scanner, 1, horarios.size());
                        HorarioFuncionamento selecionado = horarios.get(num - 1);

                        System.out.print("Tem certeza? O restaurante ficará fechado neste dia. (S/N): ");
                        String confirmacao = scanner.nextLine();
                        if (confirmacao.equalsIgnoreCase("S")) {
                            horarioService.removerHorario(selecionado.getId());
                            System.out.println("Horário removido com sucesso.");
                        } else {
                            System.out.println("Operação cancelada.");
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
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

    private String traduzirDia(DayOfWeek dia) {
        switch (dia) {
            case MONDAY:    return "Segunda-feira";
            case TUESDAY:   return "Terça-feira";
            case WEDNESDAY: return "Quarta-feira";
            case THURSDAY:  return "Quinta-feira";
            case FRIDAY:    return "Sexta-feira";
            case SATURDAY:  return "Sábado";
            case SUNDAY:    return "Domingo";
            default:        return dia.toString();
        }
    }
}