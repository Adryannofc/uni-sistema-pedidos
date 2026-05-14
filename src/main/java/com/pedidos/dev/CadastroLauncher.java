package com.pedidos.dev;

import com.pedidos.application.service.AutenticacaoService;
import com.pedidos.application.service.ClienteService;
import com.pedidos.application.service.RestauranteService;
import com.pedidos.infra.repository.impl.*;
import com.pedidos.domain.repository.*;
import com.pedidos.view.cadastro.CadastroFrame;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javax.swing.*;

public class CadastroLauncher {

    public static void main(String[] args) {

        // 1. Criar a conexão usando o nome EXATO do seu persistence.xml
        // Mudamos de "sistema-pedidos-pu" para "deliveryPU"
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("deliveryPU");
        EntityManager em = emf.createEntityManager();

        SwingUtilities.invokeLater(() -> {
            try {
                // 2. Instanciando os Repositórios JPA passando o EntityManager
                ClienteRepository clienteRepo = new ClienteRepositoryJPA(em);
                AdminRepository adminRepo = new AdminRepositoryJPA(em);
                RestauranteRepository restauranteRepo = new RestauranteRepositoryJPA(em);
                EnderecoRepository enderecoRepo = new EnderecoRepositoryJPA(em);
                CategoriaGlobalRepository categoriaRepo = new CategoriaGlobalRepositoryJPA(em);

                // 3. Instanciando o serviço de autenticação
                // Ordem: admin, restaurante, cliente (verifique se é essa no seu AutenticacaoService)
                AutenticacaoService authService = new AutenticacaoService(
                        adminRepo,
                        restauranteRepo,
                        clienteRepo
                );

                // 4. Montando os Services principais
                ClienteService clienteService = new ClienteService(
                        clienteRepo,
                        authService,
                        adminRepo,
                        restauranteRepo,
                        enderecoRepo
                );

                RestauranteService restauranteService = new RestauranteService(
                        restauranteRepo,
                        categoriaRepo,
                        authService
                );

                // 5. Abrindo o Frame de Cadastro
                CadastroFrame frame = new CadastroFrame(clienteService, restauranteService);
                frame.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erro ao iniciar os serviços: " + e.getMessage(),
                        "Erro de Inicialização", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}