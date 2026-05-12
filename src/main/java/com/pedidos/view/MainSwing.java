package com.pedidos.view;

import com.pedidos.application.service.AutenticacaoService;
import com.pedidos.infra.config.FlyWayconfig;
import com.pedidos.infra.config.JPAUtil;
import com.pedidos.infra.repository.impl.AdminRepositoryJPA;
import com.pedidos.infra.repository.impl.ClienteRepositoryJPA;
import com.pedidos.infra.repository.impl.RestauranteRepositoryJPA;
import com.pedidos.view.login.LoginFrame;
import jakarta.persistence.EntityManager;

import javax.swing.*;

public class MainSwing {
    public static void main(String[] args) {

        // 1. Infraestrutura fora da EDT (operação pesada — conecta ao banco)
        try {
            FlyWayconfig.migrate();
        } catch (Exception e) {
            System.out.println("Flyway não executado: " + e.getMessage());
        }

        EntityManager em = JPAUtil.getEntityManager();

        AdminRepositoryJPA adminRepo = new AdminRepositoryJPA(em);
        RestauranteRepositoryJPA restauranteRepo = new RestauranteRepositoryJPA(em);
        ClienteRepositoryJPA clienteRepo = new ClienteRepositoryJPA(em);

        AutenticacaoService authService = new AutenticacaoService(adminRepo, restauranteRepo, clienteRepo);

        // 2. UI na EDT
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            LoginFrame frame = new LoginFrame(authService);
            frame.setVisible(true);
        });
    }
}
