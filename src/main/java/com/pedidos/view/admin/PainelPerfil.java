package com.pedidos.view.admin;

import com.pedidos.controller.AdminController;
import com.pedidos.model.entity.Usuario;
import com.pedidos.view.util.AppColors;
import com.pedidos.view.util.AppFonts;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class PainelPerfil extends JPanel {

    private final Usuario usuario;
    private final AdminController adminController;

    public PainelPerfil(Usuario usuario, AdminController adminController) {
        super(new BorderLayout());
        this.usuario = usuario;
        this.adminController = adminController;
        construir();
    }

    private void construir() {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(AppColors.CINZA_FUNDO);
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        wrapper.add(criarPainelInfo());
        wrapper.add(Box.createVerticalStrut(20));
        wrapper.add(criarPainelSenha());

        add(wrapper, BorderLayout.NORTH);
        setBackground(AppColors.CINZA_FUNDO);
    }

    private JPanel criarPainelInfo() {
        JPanel painel = new JPanel(new GridLayout(4, 2, 8, 6));
        painel.setBackground(AppColors.CINZA_FUNDO);
        painel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Dados do Administrador",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                AppFonts.LABEL, AppColors.TEXTO_SECUNDARIO));

        JTextField campoNome  = campoReadOnly(usuario.getNome());
        JTextField campoEmail = campoReadOnly(usuario.getEmail());
        JTextField campoPerfil = campoReadOnly("Administrador do Sistema");

        painel.add(rotulo("Nome:"));    painel.add(campoNome);
        painel.add(rotulo("E-mail:"));  painel.add(campoEmail);
        painel.add(rotulo("Perfil:"));  painel.add(campoPerfil);

        return painel;
    }

    private JPanel criarPainelSenha() {
        JPasswordField campoAtual    = new JPasswordField(22);
        JPasswordField campoNova     = new JPasswordField(22);
        JPasswordField campoConfirma = new JPasswordField(22);
        campoAtual.setFont(AppFonts.CAMPO);
        campoNova.setFont(AppFonts.CAMPO);
        campoConfirma.setFont(AppFonts.CAMPO);

        JButton btnSalvar = new JButton("Alterar Senha");
        btnSalvar.setFont(AppFonts.BOTAO);

        JPanel painel = new JPanel(new GridLayout(7, 2, 8, 6));
        painel.setBackground(AppColors.CINZA_FUNDO);
        painel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Alterar Senha",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                AppFonts.LABEL, AppColors.TEXTO_SECUNDARIO));

        painel.add(rotulo("Senha atual:"));      painel.add(campoAtual);
        painel.add(rotulo("Nova senha:"));        painel.add(campoNova);
        painel.add(rotulo("Confirmar senha:"));   painel.add(campoConfirma);
        painel.add(new JLabel());
        painel.add(btnSalvar);

        btnSalvar.addActionListener(e -> {
            String atual    = new String(campoAtual.getPassword());
            String nova     = new String(campoNova.getPassword());
            String confirma = new String(campoConfirma.getPassword());
            if (atual.isEmpty() || nova.isEmpty() || confirma.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Preencha todos os campos de senha.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                adminController.alterarSenha(usuario, atual, nova, confirma);
                JOptionPane.showMessageDialog(this,
                        "Senha alterada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                campoAtual.setText("");
                campoNova.setText("");
                campoConfirma.setText("");
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        return painel;
    }

    // ─── helpers ──────────────────────────────────────────────────────────────

    private static JLabel rotulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(AppFonts.LABEL);
        return l;
    }

    private static JTextField campoReadOnly(String valor) {
        JTextField f = new JTextField(valor);
        f.setFont(AppFonts.CAMPO);
        f.setEditable(false);
        f.setBackground(new Color(245, 245, 245));
        return f;
    }
}
