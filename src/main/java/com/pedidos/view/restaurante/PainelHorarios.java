package com.pedidos.view.restaurante;

import com.pedidos.application.service.HorarioService;
import com.pedidos.domain.entities.HorarioFuncionamento;
import com.pedidos.domain.entities.Restaurante;
import com.pedidos.domain.entities.Usuario;
import com.pedidos.view.util.AppColors;
import com.pedidos.view.util.AppFonts;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class PainelHorarios extends JPanel {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm");

    private static final Map<DayOfWeek, String> NOMES_DIAS = new EnumMap<>(DayOfWeek.class);
    static {
        NOMES_DIAS.put(DayOfWeek.MONDAY,    "Segunda-feira");
        NOMES_DIAS.put(DayOfWeek.TUESDAY,   "Terça-feira");
        NOMES_DIAS.put(DayOfWeek.WEDNESDAY, "Quarta-feira");
        NOMES_DIAS.put(DayOfWeek.THURSDAY,  "Quinta-feira");
        NOMES_DIAS.put(DayOfWeek.FRIDAY,    "Sexta-feira");
        NOMES_DIAS.put(DayOfWeek.SATURDAY,  "Sábado");
        NOMES_DIAS.put(DayOfWeek.SUNDAY,    "Domingo");
    }

    private static final Color COR_FECHADO_FUNDO = new Color(255, 235, 235);
    private static final Color COR_ABERTO_FUNDO  = Color.WHITE;
    private static final Color COR_BADGE         = new Color(220, 0, 0);

    private final Restaurante restaurante;
    private final HorarioService horarioService;
    private final List<LinhaHorario> linhas = new ArrayList<>();

    public PainelHorarios(Usuario usuario, HorarioService horarioService) {
        super(new BorderLayout());
        this.restaurante = (Restaurante) usuario;
        this.horarioService = horarioService;
        construir();
        carregarHorarios();
    }

    // ─────────────────────────── build UI ────────────────────────────────────

    private void construir() {
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Horário de Funcionamento",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                AppFonts.TITULO));

        JLabel instrucao = new JLabel(
                "Edite os horários diretamente na tabela. Marque \"Fechado?\" para indicar folga.");
        instrucao.setFont(AppFonts.STATUS);
        instrucao.setBorder(BorderFactory.createEmptyBorder(4, 6, 8, 6));
        topo.add(instrucao, BorderLayout.CENTER);

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setBackground(COR_ABERTO_FUNDO);

        adicionarCabecalho(grid);

        DayOfWeek[] ordem = {
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY,
            DayOfWeek.SUNDAY
        };
        for (int i = 0; i < ordem.length; i++) {
            LinhaHorario linha = criarLinha(ordem[i], i + 1, grid);
            linhas.add(linha);
        }

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);

        JButton btnSalvar = new JButton("Salvar Horários");
        btnSalvar.setFont(AppFonts.BOTAO);
        btnSalvar.addActionListener(e -> salvarHorarios());

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rodape.add(btnSalvar);

        add(topo,   BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);
    }

    private void adicionarCabecalho(JPanel grid) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill  = GridBagConstraints.BOTH;
        gbc.ipady = 6;

        String[] cabecalhos = {"Dia da Semana", "Abre", "Fecha", "Fechado?"};
        double[] pesos      = {0.5, 0.2, 0.2, 0.1};

        for (int col = 0; col < cabecalhos.length; col++) {
            JLabel lbl = new JLabel(cabecalhos[col]);
            lbl.setFont(AppFonts.TITULO);
            lbl.setOpaque(true);
            lbl.setBackground(AppColors.CINZA_FUNDO);
            lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)));
            if (col == 3) lbl.setHorizontalAlignment(SwingConstants.CENTER);

            gbc.gridx   = col;
            gbc.weightx = pesos[col];
            grid.add(lbl, gbc);
        }
    }

    private LinhaHorario criarLinha(DayOfWeek dia, int row, JPanel grid) {
        LinhaHorario linha = new LinhaHorario();
        linha.dia = dia;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = row;
        gbc.fill  = GridBagConstraints.BOTH;
        gbc.ipady = 4;

        // ── Col 0: nome do dia + badge ────────────────────────────────────────
        JPanel painelDia = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        painelDia.setOpaque(true);
        painelDia.setBackground(COR_ABERTO_FUNDO);
        painelDia.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(2, 8, 2, 4)));

        JLabel labelDia = new JLabel(NOMES_DIAS.get(dia));
        boolean fimDeSemana = (dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY);
        labelDia.setFont(fimDeSemana ? AppFonts.TITULO : AppFonts.LABEL);
        painelDia.add(labelDia);

        JLabel labelFechado = new JLabel("FECHADO");
        labelFechado.setFont(new Font("Segoe UI", Font.BOLD, 10));
        labelFechado.setForeground(Color.WHITE);
        labelFechado.setOpaque(true);
        labelFechado.setBackground(COR_BADGE);
        labelFechado.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
        labelFechado.setVisible(false);
        painelDia.add(labelFechado);
        linha.labelFechado = labelFechado;
        linha.painelDia    = painelDia;

        gbc.gridx   = 0;
        gbc.weightx = 0.5;
        grid.add(painelDia, gbc);

        // ── Col 1: campo Abre ─────────────────────────────────────────────────
        linha.campoAbre = criarCampoHora();
        gbc.gridx   = 1;
        gbc.weightx = 0.2;
        grid.add(wrapCampo(linha.campoAbre), gbc);

        // ── Col 2: campo Fecha ────────────────────────────────────────────────
        linha.campoFecha = criarCampoHora();
        gbc.gridx   = 2;
        gbc.weightx = 0.2;
        grid.add(wrapCampo(linha.campoFecha), gbc);

        // ── Col 3: checkbox Fechado? ──────────────────────────────────────────
        linha.checkFechado = new JCheckBox();
        linha.checkFechado.setOpaque(true);
        linha.checkFechado.setBackground(COR_ABERTO_FUNDO);
        linha.checkFechado.setHorizontalAlignment(SwingConstants.CENTER);
        linha.checkFechado.addActionListener(e -> atualizarEstadoLinha(linha));

        JPanel painelCheck = new JPanel(new GridBagLayout());
        painelCheck.setOpaque(true);
        painelCheck.setBackground(COR_ABERTO_FUNDO);
        painelCheck.setBorder(BorderFactory.createMatteBorder(
                0, 0, 1, 0, new Color(220, 220, 220)));
        painelCheck.add(linha.checkFechado);
        linha.painelCheck = painelCheck;

        gbc.gridx   = 3;
        gbc.weightx = 0.1;
        grid.add(painelCheck, gbc);

        return linha;
    }

    private JTextField criarCampoHora() {
        JTextField f = new JTextField("00:00", 6);
        f.setFont(AppFonts.CAMPO);
        f.setHorizontalAlignment(SwingConstants.CENTER);
        return f;
    }

    private JPanel wrapCampo(JTextField campo) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
        p.setOpaque(true);
        p.setBackground(COR_ABERTO_FUNDO);
        p.setBorder(BorderFactory.createMatteBorder(
                0, 0, 1, 0, new Color(220, 220, 220)));
        p.add(campo);
        return p;
    }

    // ─────────────────────────── data loading ────────────────────────────────

    private void carregarHorarios() {
        List<HorarioFuncionamento> lista =
                horarioService.listarHorarioPorRestaurante(restaurante.getId());

        Map<DayOfWeek, HorarioFuncionamento> porDia = new EnumMap<>(DayOfWeek.class);
        for (HorarioFuncionamento h : lista) {
            porDia.put(h.getDiaSemana(), h);
        }

        for (LinhaHorario linha : linhas) {
            HorarioFuncionamento h = porDia.get(linha.dia);
            if (h != null) {
                linha.idExistente = h.getId();
                linha.campoAbre.setText(h.getHoraInicio().format(FMT));
                linha.campoFecha.setText(h.getHoraFim().format(FMT));
                linha.checkFechado.setSelected(false);
            } else {
                linha.idExistente = null;
                linha.campoAbre.setText("00:00");
                linha.campoFecha.setText("00:00");
                linha.checkFechado.setSelected(true);
            }
            atualizarEstadoLinha(linha);
        }
    }

    // ─────────────────────────── state update ────────────────────────────────

    private void atualizarEstadoLinha(LinhaHorario linha) {
        boolean fechado = linha.checkFechado.isSelected();
        Color fundo = fechado ? COR_FECHADO_FUNDO : COR_ABERTO_FUNDO;

        linha.campoAbre.setEnabled(!fechado);
        linha.campoFecha.setEnabled(!fechado);
        linha.labelFechado.setVisible(fechado);

        linha.painelDia.setBackground(fundo);
        for (Component c : linha.painelDia.getComponents()) {
            if (c instanceof JLabel lbl && lbl != linha.labelFechado) {
                lbl.setBackground(fundo);
            }
        }

        Component wrapAbre  = (Component) linha.campoAbre.getParent();
        Component wrapFecha = (Component) linha.campoFecha.getParent();
        if (wrapAbre  instanceof JPanel p) p.setBackground(fundo);
        if (wrapFecha instanceof JPanel p) p.setBackground(fundo);

        linha.checkFechado.setBackground(fundo);
        linha.painelCheck.setBackground(fundo);

        revalidate();
        repaint();
    }

    // ─────────────────────────── save ────────────────────────────────────────

    private void salvarHorarios() {
        // ── 1. Validar todos os campos abertos antes de qualquer persistência ──
        for (LinhaHorario linha : linhas) {
            if (linha.checkFechado.isSelected()) continue;

            LocalTime inicio, fim;
            try {
                inicio = LocalTime.parse(linha.campoAbre.getText().trim(), FMT);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "Horário de abertura inválido para " + NOMES_DIAS.get(linha.dia)
                        + ".\nUse o formato HH:mm (ex: 09:00).",
                        "Horário inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                fim = LocalTime.parse(linha.campoFecha.getText().trim(), FMT);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "Horário de fechamento inválido para " + NOMES_DIAS.get(linha.dia)
                        + ".\nUse o formato HH:mm (ex: 22:00).",
                        "Horário inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!fim.isAfter(inicio)) {
                JOptionPane.showMessageDialog(this,
                        NOMES_DIAS.get(linha.dia)
                        + ": o horário de fechamento deve ser posterior ao de abertura.",
                        "Horário inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        // ── 2. Persistir ───────────────────────────────────────────────────────
        try {
            for (LinhaHorario linha : linhas) {
                if (linha.checkFechado.isSelected()) {
                    if (linha.idExistente != null) {
                        horarioService.removerHorario(linha.idExistente);
                        linha.idExistente = null;
                    }
                } else {
                    LocalTime inicio = LocalTime.parse(linha.campoAbre.getText().trim(), FMT);
                    LocalTime fim    = LocalTime.parse(linha.campoFecha.getText().trim(), FMT);

                    if (linha.idExistente != null) {
                        horarioService.editarHorario(linha.idExistente, inicio, fim);
                    } else {
                        HorarioFuncionamento novo =
                                horarioService.criarHorario(restaurante, linha.dia, inicio, fim);
                        linha.idExistente = novo.getId();
                    }
                }
            }
            JOptionPane.showMessageDialog(this,
                    "Horários salvos com sucesso.",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar horários:\n" + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ─────────────────────────── inner class ─────────────────────────────────

    private static class LinhaHorario {
        DayOfWeek  dia;
        JTextField campoAbre;
        JTextField campoFecha;
        JCheckBox  checkFechado;
        JLabel     labelFechado;
        JPanel     painelDia;
        JPanel     painelCheck;
        String     idExistente;
    }
}
