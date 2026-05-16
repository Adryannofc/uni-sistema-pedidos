package com.pedidos.view.util.base;

import com.pedidos.view.util.AppColors;
import com.pedidos.view.util.AppFonts;

import javax.swing.*;

public class BaseFrame extends JFrame {

    protected BaseFrame(String titulo) {
        super(titulo);
        configurar();
    }

    protected BaseFrame(String titulo, int largura, int altura) {
        super(titulo);
        setSize(largura, altura);
        configurar(false);
    }

    private void configurar() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(AppColors.CINZA_FUNDO);
        UIManager.put("OptionPane.messageFont", AppFonts.LABEL);
        UIManager.put("OptionPane.buttonFont",  AppFonts.BOTAO);
    }

    private void configurar(boolean definirTamanho) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        if (definirTamanho) setSize(520, 400);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(AppColors.CINZA_FUNDO);
    }

    protected void trocarPara(JFrame proximo) {
        proximo.setVisible(true);
        this.dispose();
    }
}
