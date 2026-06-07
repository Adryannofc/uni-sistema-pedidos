package com.pedidos.view.restaurante;

import com.pedidos.model.service.AutenticacaoService;
import com.pedidos.model.service.RestauranteService;
import com.pedidos.model.entity.Restaurante;
import com.pedidos.model.entity.Usuario;
import com.pedidos.view.util.AppColors;
import com.pedidos.view.util.AppFonts;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class PainelPerfil extends JPanel {
    private final Usuario usuario;
    private final AutenticacaoService autenticacaoService;
    private final RestauranteService restauranteService;
    private Restaurante restaurante;

    public PainelPerfil(Usuario usuario, AutenticacaoService autenticacaoService, RestauranteService restauranteService) {
        this.usuario = usuario;
        this.autenticacaoService = autenticacaoService;
        this.restauranteService = restauranteService;
        criarAbas();
    }

    private void criarAbas() {
        JTabbedPane abasDados = new JTabbedPane();
        abasDados.setFont(AppFonts.LABEL);

        restaurante = restauranteService.buscarRestaurantePorId(usuario.getId());

        abasDados.addTab("Dados", criarFormularioDados());
        abasDados.addTab("E-mail", criarFormularioEmail());
        abasDados.addTab("Senha", criarFormularioSenha());

        add(abasDados, BorderLayout.CENTER);
    }

    private JPanel criarFormularioDados() {

        JPanel painel = new JPanel();
        painel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "Dados:",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                AppFonts.LABEL,
                AppColors.TEXTO_SECUNDARIO));

        painel.add(rotulo("Nome:"));
        JTextField campoNome = campo(restaurante.getNome());
        painel.add(campoNome);

        painel.add(rotulo("CNPJ:"));
        JTextField campoCnpj = campo(restaurante.getCnpj());
        painel.add(campoCnpj);

        painel.add(rotulo("Telefone:"));
        JTextField campoTelefone = campo(restaurante.getTelefone());
        painel.add(campoTelefone);

        JButton btnSalvar = criarButtonSalvar();
        painel.add(btnSalvar);

        btnSalvar.addActionListener( e -> {

            String novoNome = campoNome.getText().trim();
            String novoCnpj = campoCnpj.getText().trim();
            String novoTelefone = campoTelefone.getText().trim();

            if (novoNome.isEmpty()) {
                JOptionPane.showMessageDialog(painel, "Nome obrigatório", "Erro", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (novoCnpj.isEmpty()) {
                JOptionPane.showMessageDialog(painel, "CNPJ obrigatório", "Erro", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (novoNome.equals(usuario.getNome()) && novoCnpj.equals(restaurante.getCnpj()) && novoTelefone.equals(restaurante.getTelefone())) {
                JOptionPane.showMessageDialog(painel, "Nenhuma alteração detectada", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int op = JOptionPane.showConfirmDialog(painel,
                    "Salvar alterações?",
                    "Confirmar",
                    JOptionPane.OK_CANCEL_OPTION);

            if (op != JOptionPane.OK_OPTION) {
                return;
            }

            restauranteService.editarPerfil(restaurante, novoNome, novoCnpj, novoTelefone);

            JOptionPane.showMessageDialog(painel, "Dados atualizados com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

        });

        return painel;

    }

    private JPanel criarFormularioEmail() {

        JPanel painel = new JPanel();
        painel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "E-mail:",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                AppFonts.LABEL,
                AppColors.TEXTO_SECUNDARIO));

        painel.add(rotulo("E-mail atual:"));
        JTextField campoEmailAtual = campo(restaurante.getEmail());
        campoEmailAtual.setEditable(false);
        painel.add(campoEmailAtual);

        painel.add(rotulo("Novo e-mail:"));;
        JTextField campoEmailNovo = campo("");
        painel.add(campoEmailNovo);

        painel.add(new JLabel("Confirmar:"));
        JTextField campoEmailConfirmar = campo("");
        painel.add(campoEmailConfirmar);

        JButton btnSalvar = criarButtonSalvar();
        painel.add(btnSalvar);

        btnSalvar.addActionListener(e -> {
            String emailAtual = campo(restaurante.getEmail()).getText().trim();
            String emailNovo = campoEmailNovo.getText().trim();
            String emailConfirmar = campoEmailConfirmar.getText().trim();

            if (emailNovo.isEmpty()) {
                JOptionPane.showMessageDialog(painel, "Informe o novo e-mail!", "Erro", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!emailNovo.equals(emailConfirmar)) {
                JOptionPane.showMessageDialog(painel, "O e-mail a confirmar não corresponde ao novo!", "Erro", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (emailAtual.equals(emailNovo)) {
                JOptionPane.showMessageDialog(painel, "O novo email deve ser diferente do atual!", "Erro", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int op = JOptionPane.showConfirmDialog(painel,
                    "Salvar alterações?",
                    "Confirmar",
                    JOptionPane.OK_CANCEL_OPTION);

            if (op != JOptionPane.OK_OPTION) {
                return;
            }

            restauranteService.editarEmail(restaurante, emailNovo); // Exemplo

            JOptionPane.showMessageDialog(painel, "E-mail atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            campoEmailAtual.setText(restaurante.getEmail());
            limparCampo(campoEmailNovo);
            limparCampo(campoEmailConfirmar);
        });

        return painel;

    }

    private JPanel criarFormularioSenha() {

        JPanel painel = new JPanel();
        painel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "Senha:",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                AppFonts.LABEL,
                AppColors.TEXTO_SECUNDARIO));

        painel.add(rotulo("Senha atual:"));
        JPasswordField campoSenhaAtual = new JPasswordField(20);
        campoSenhaAtual.setFont(AppFonts.CAMPO);
        painel.add(campoSenhaAtual);

        painel.add(rotulo("Nova senha:"));
        JTextField campoNovaSenha = campo("");
        painel.add(campoNovaSenha);

        painel.add(rotulo("Confirmar:"));
        JTextField campoConfirmarSenha = campo("");
        painel.add(campoConfirmarSenha);

        JButton btnSalvar = criarButtonSalvar();
        painel.add(btnSalvar);

        btnSalvar.addActionListener(e -> {
            String senhaAtual = new String(campoSenhaAtual.getPassword()).trim();
            String novaSenha = campoNovaSenha.getText().trim();
            String confirmarSenha = campoConfirmarSenha.getText().trim();

            String senhaAtualHash = pegarSenhaHash(senhaAtual);

            if (senhaAtual.isEmpty()) {
                JOptionPane.showMessageDialog(painel, "Informe a senha atual!", "Erro", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!senhaAtualHash.equals(restaurante.getSenhaHash())) {
                JOptionPane.showMessageDialog(painel, "Senha atual incorreta!", "Erro", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (novaSenha.isEmpty()) {
                JOptionPane.showMessageDialog(painel, "Informe a nova senha!", "Erro", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!novaSenha.equals(confirmarSenha)) {
                JOptionPane.showMessageDialog(painel, "Nova senha e confirmação não correspondem!", "Erro", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (novaSenha.equals(senhaAtual)) {
                JOptionPane.showMessageDialog(painel, "A nova senha deve ser diferente da atual!", "Erro", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (novaSenha.length() < 6) {
                JOptionPane.showMessageDialog(painel, "A nova senha deve conter pelo menos 6 caracteres!", "Erro", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int op = JOptionPane.showConfirmDialog(painel,
                    "Salvar alterações?",
                    "Confirmar",
                    JOptionPane.OK_CANCEL_OPTION);

            if (op != JOptionPane.OK_OPTION) {
                return;
            }
            restauranteService.alterarSenha(restaurante,senhaAtual, novaSenha);

            JOptionPane.showMessageDialog(painel, "Senha atualizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            campoSenhaAtual.setText("");
            limparCampo(campoNovaSenha);
            limparCampo(campoConfirmarSenha);
        });

        return painel;
    }

    private String pegarSenhaHash (String senhaAtual) {

        String senhaAtualHash = autenticacaoService.hashSenha(senhaAtual);

        return senhaAtualHash;
    }

    private void limparCampo(JTextField campo) {
        campo.setText("");
    }

    private JTextField campo(String texto) {
        JTextField c = new JTextField(texto, 20);
        c.setFont(AppFonts.CAMPO);
        return c;
    }


    private JLabel rotulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(AppFonts.LABEL);
        return l;
    }

    private JButton criarButtonSalvar () {
        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.setFont(AppFonts.BOTAO);
        btnSalvar.setForeground(AppColors.TEXTO_BRANCO);
        btnSalvar.setBackground(AppColors.AZUL_PRIMARIO);
        btnSalvar.setOpaque(true);
        btnSalvar.setBorderPainted(false);
        btnSalvar.setFocusPainted(false);
        btnSalvar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btnSalvar;
    }

}

