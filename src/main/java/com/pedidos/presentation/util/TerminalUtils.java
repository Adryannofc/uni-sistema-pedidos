package com.pedidos.presentation.util;

import java.util.Scanner;

/**
 * Utilitarios de terminal: limpeza de tela, pausas e sistema
 * de caixas com bordas duplas (box-drawing Unicode).
 *
 * Todas as constantes visuais ficam aqui — os Menus apenas chamam
 * os metodos, sem precisar redefinir LARGURA, TOPO, BASE, etc.
 */
public class TerminalUtils {

    private TerminalUtils() {}  // classe utilitaria: sem instancias

    private static final Scanner SCN = new Scanner(System.in);

    // ─── Dimensoes e caracteres da caixa ──────────────────────────────────────
    public static final int    LARGURA   = 47;
    public static final String TOPO      = "\u2554" + "\u2550".repeat(LARGURA) + "\u2557";
    public static final String BASE      = "\u255a" + "\u2550".repeat(LARGURA) + "\u255d";
    public static final String SEPARADOR = "\u2560" + "\u2550".repeat(LARGURA) + "\u2563";

    // ─── Prefixos de feedback ─────────────────────────────────────────────────
    public static final String OK     = "  \u2713  ";  // ✓
    public static final String ERRO   = "  \u2717  ";  // ✗
    public static final String AVISO  = "  !  ";
    public static final String PERIGO = "  \u26a0  "; // ⚠

    // ─── Formatacao de linhas ─────────────────────────────────────────────────

    /**
     * Formata uma linha lateral da caixa:  ║  conteudo preenchido  ║
     *
     * O formato %-Ns garante largura fixa.
     * Se o conteudo for maior que a largura, ele e truncado com "..."
     * para que a borda direita nunca quebre.
     */
    public static String linha(String conteudo) {
        int interno = LARGURA - 2;
        if (conteudo.length() > interno) {
            conteudo = conteudo.substring(0, interno - 3) + "...";
        }
        return String.format("\u2551 %-" + interno + "s \u2551", conteudo);
    }

    /**
     * Formata uma linha com o texto centralizado dentro da caixa.
     * Usado principalmente em cabecalhos e titulos de secao.
     */
    public static String linhaCentralizada(String conteudo) {
        int interno = LARGURA - 2;
        if (conteudo.length() >= interno) {
            return linha(conteudo);  // fallback: alinhado a esquerda se nao couber
        }
        int totalEspacos = interno - conteudo.length();
        int esqEspacos   = totalEspacos / 2;
        int dirEspacos   = totalEspacos - esqEspacos;  // garante simetria exata
        String centrado  = " ".repeat(esqEspacos) + conteudo + " ".repeat(dirEspacos);
        return "\u2551 " + centrado + " \u2551";
    }

    // ─── Cabecalhos prontos ───────────────────────────────────────────────────

    /** Cabecalho com titulo centralizado (1 linha). */
    public static void cabecalho(String titulo) {
        System.out.println();
        System.out.println(TOPO);
        System.out.println(linhaCentralizada(titulo));
        System.out.println(BASE);
        System.out.println();
    }

    /** Cabecalho com titulo e subtitulo (ex: nome do restaurante logado). */
    public static void cabecalho(String titulo, String subtitulo) {
        System.out.println();
        System.out.println(TOPO);
        System.out.println(linhaCentralizada(titulo));
        System.out.println(linha("  " + subtitulo));
        System.out.println(BASE);
        System.out.println();
    }

    // ─── Mensagens de feedback ────────────────────────────────────────────────

    public static void sucesso(String msg) { System.out.println("\n" + OK    + msg); }
    public static void erro(String msg)    { System.out.println("\n" + ERRO  + msg); }
    public static void aviso(String msg)   { System.out.println("\n" + AVISO + msg); }

    /**
     * Exibe aviso de perigo antes de acoes irreversiveis
     * e retorna true se o usuario confirmar.
     */
    public static boolean confirmarPerigo(String pergunta, Scanner scanner) {
        System.out.println();
        System.out.println(PERIGO + "ATENCAO: esta acao e irreversivel!");
        System.out.print(PERIGO + pergunta + " (s/N): ");
        return scanner.nextLine().trim().equalsIgnoreCase("s");
    }

    // ─── Controles de fluxo ───────────────────────────────────────────────────

    /** Imprime 50 linhas em branco — funciona em qualquer SO sem depender de ANSI. */
    public static void limparTela() {
        for (int i = 0; i < 50; i++) System.out.println();
    }

    /** Pausa a execucao ate o usuario pressionar Enter. */
    public static void pausar() {
        System.out.print("\n  Pressione Enter para continuar...");
        SCN.nextLine();
    }
}