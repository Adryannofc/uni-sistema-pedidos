package com.pedidos.cli.util;
import java.util.Scanner;

public class EntradaSegura {
     public static int lerOpcao(Scanner sc, int min, int max) {
        while (true) {
            String entrada = sc.nextLine().trim();
                try {
                int opcao = Integer.parseInt(entrada);

                if (opcao >= min && opcao <= max) {
                    return opcao;
                } else {
                    System.out.println("⚠ Opção fora do intervalo permitido!");
                }
            } catch (NumberFormatException e) {
                System.out.println("⚠ Digite apenas números válidos!");
            }
        }
    }

    public static String lerString(Scanner sc, String prompt){
         String entrada;
         do {
             System.out.print(prompt);
             entrada = sc.nextLine().trim();
         } while (entrada.isBlank()); // isBlank verifica se a String está vazia ou tem só espaços.
         return entrada;
    }
}
