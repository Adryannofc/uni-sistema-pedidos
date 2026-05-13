package com.pedidos.view.util.session;

import com.pedidos.domain.entities.Usuario;
import com.pedidos.domain.enums.TipoUsuario;

import javax.swing.*;

public class SessionManager {
    private static SessionManager instancia;

    private Usuario   usuarioLogado;
    private JFrame frameAtual;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instancia == null) instancia = new SessionManager();
        return instancia;
    }

    public void iniciarSessao(Usuario usuario, JFrame frameOrigem) {
        this.usuarioLogado = usuario;
        this.frameAtual    = frameOrigem;
    }

    public void encerrarSessao() {
        this.usuarioLogado = null;
    }

    public void trocarFrame(JFrame novoFrame) {
        if (frameAtual != null) frameAtual.dispose();
        frameAtual = novoFrame;
        novoFrame.setVisible(true);
    }

    public Usuario    getUsuarioLogado() { return usuarioLogado; }
    public boolean    estaLogado()       { return usuarioLogado != null; }
    public TipoUsuario getTipo()         { return usuarioLogado != null ? usuarioLogado.getTipoUsuario() : null; }
    public String     getNome()          { return usuarioLogado != null ? usuarioLogado.getNome() : "Desconectado"; }
}
