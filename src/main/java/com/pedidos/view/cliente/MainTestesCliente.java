package com.pedidos.view.cliente;

import com.pedidos.domain.entities.Cliente;
import com.pedidos.view.cliente.ClienteFrame;

import javax.swing.*;

public class MainTestesCliente {
    public static void main(String[] args) {
        Cliente cliente = new Cliente(
                "João Dev",
                "dev@cliente.com",
                "hash-fake",
                "12345678901",
                "44988880001"
        );
        cliente.setId("u-cli-01");

        SwingUtilities.invokeLater(() -> {
            new ClienteFrame(cliente).setVisible(true);
        });
    }
}