package com.pedidos.view.restaurante;

import com.pedidos.application.service.AutenticacaoService;
import com.pedidos.application.service.RestauranteService;
import com.pedidos.domain.entities.Restaurante;
import com.pedidos.domain.entities.Usuario;

import javax.swing.*;
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

        restaurante = restauranteService.buscarRestaurantePorId(usuario.getId());

        abasDados.addTab("Dados", criarFormularioDados());
        abasDados.addTab("E-mail", criarFormularioEmail());
        abasDados.addTab("Senha", criarFormularioSenha());

        add(abasDados, BorderLayout.CENTER);
    }

    private JPanel criarFormularioDados() {

        JPanel painel = new JPanel();

        painel.add(new JLabel("Nome:"));
        JTextField campoNome = new JTextField(usuario.getNome());
        painel.add(campoNome);

        painel.add(new JLabel("CNPJ:"));
        JTextField campoCnpj = new JTextField(restaurante.getCnpj());
        painel.add(campoCnpj);

        painel.add(new JLabel("Telefone:"));
        JTextField campoTelefone = new JTextField(restaurante.getTelefone());
        painel.add(campoTelefone);

        JButton btnSalvar = new JButton("Salvar");
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

        painel.add(new JLabel("E-mail atual:"));
        JTextField campoEmailAtual = new JTextField(usuario.getEmail(), 20);
        painel.add(campoEmailAtual);

        painel.add(new JLabel("Novo e-mail:"));
        JTextField campoEmailNovo = new JTextField(20);
        painel.add(campoEmailNovo);

        painel.add(new JLabel("Confirmar:"));
        JTextField campoEmailConfirmar = new JTextField(20);
        painel.add(campoEmailConfirmar);

        JButton btnSalvarEmail = new JButton("Salvar");
        painel.add(btnSalvarEmail);

        btnSalvarEmail.addActionListener(e -> {
            String emailAtual = campoEmailAtual.getText().trim();
            String emailNovo = campoEmailNovo.getText().trim();
            String emailConfirmar = campoEmailConfirmar.getText().trim();

            if (emailNovo.isEmpty()) {
                JOptionPane.showMessageDialog(painel, "Informe o novo e-mail", "Erro", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!emailNovo.equals(emailConfirmar)) {
                JOptionPane.showMessageDialog(painel, "Os e-mails não batem", "Erro", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!emailAtual.equals(usuario.getEmail())) {
                JOptionPane.showMessageDialog(painel, "O e-mail atual incorreto", "Erro", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (emailAtual.equals(emailNovo)) {
                JOptionPane.showMessageDialog(painel, "O novo email deve ser diferente do atual", "Erro", JOptionPane.WARNING_MESSAGE);
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

            campoEmailAtual.setText(emailNovo);
            campoEmailNovo.setText("");
            campoEmailConfirmar.setText("");
        });

        return painel;

    }

    private JPanel criarFormularioSenha() {

        JPanel painel = new JPanel();

        painel.add(new JLabel("Senha atual:"));
        JPasswordField campoSenhaAtual = new JPasswordField(20);
        painel.add(campoSenhaAtual);

        painel.add(new JLabel("Nova senha:"));
        JTextField campoNovaSenha = new JTextField(20);
        painel.add(campoNovaSenha);

        painel.add(new JLabel("Confirmar:"));
        JTextField campoConfirmarSenha = new JTextField(20);
        painel.add(campoConfirmarSenha);

        JButton btnSalvarSenha = new JButton("Salvar");
        painel.add(btnSalvarSenha);

        btnSalvarSenha.addActionListener(e -> {
            String senhaAtual = new String(campoSenhaAtual.getPassword()).trim();
            String novaSenha = campoNovaSenha.getText().trim();
            String confirmarSenha = campoConfirmarSenha.getText().trim();

            String senhaAtualHash = pegarSenhaHash(senhaAtual);

            if (!senhaAtualHash.equals(restaurante.getSenhaHash())) {
                JOptionPane.showMessageDialog(painel, "Senha atual incorreta!", "Erro", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (novaSenha.isEmpty()) {
                JOptionPane.showMessageDialog(painel, "Informe a nova senha", "Erro", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!novaSenha.equals(confirmarSenha)) {
                JOptionPane.showMessageDialog(painel, "Nova senha e confirmação não batem", "Erro", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (novaSenha.equals(senhaAtual)) {
                JOptionPane.showMessageDialog(painel, "A nova senha deve ser diferente da atual", "Erro", JOptionPane.WARNING_MESSAGE);
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
            campoNovaSenha.setText("");
            campoConfirmarSenha.setText("");
        });

        return painel;
    }

    private String pegarSenhaHash (String senhaAtual) {

        String senhaAtualHash = autenticacaoService.hashSenha(senhaAtual);

        return senhaAtualHash;

    }

}

