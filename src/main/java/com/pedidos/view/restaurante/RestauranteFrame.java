package com.pedidos.view.restaurante;

import com.pedidos.domain.entities.Usuario;
import com.pedidos.view.util.AppColors;
import com.pedidos.view.util.AppFonts;
import com.pedidos.view.util.base.BaseFrame;
import com.pedidos.view.util.session.SessionManager;

import javax.swing.*;
import java.awt.*;

public class RestauranteFrame extends BaseFrame {
    private final Usuario usuario;

    public RestauranteFrame(Usuario usuario) {
        super("Sistema de Delivery — Painel do Restaurante", 700, 500);
        this.usuario = usuario;
        construirInterface();
    }

    private void construirInterface() {
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppColors.AZUL_PRIMARIO);
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titulo = new JLabel("Painel do Restaurante");
        titulo.setFont(AppFonts.TITULO);
        titulo.setForeground(AppColors.TEXTO_BRANCO);

        JLabel nomeLabel = new JLabel("Olá, " + usuario.getNome());
        nomeLabel.setFont(AppFonts.STATUS);
        nomeLabel.setForeground(AppColors.TEXTO_BRANCO);

        header.add(titulo,    BorderLayout.WEST);
        header.add(nomeLabel, BorderLayout.EAST);

        JLabel centro = new JLabel("Área do restaurante — em desenvolvimento",
                SwingConstants.CENTER);
        centro.setFont(AppFonts.LABEL);

        JButton btnSair = new JButton("Sair");
        btnSair.addActionListener(e -> {
            SessionManager.getInstance().encerrarSessao();
            dispose();
            System.exit(0);
        });
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rodape.add(btnSair);

        add(header, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);
    }
}
