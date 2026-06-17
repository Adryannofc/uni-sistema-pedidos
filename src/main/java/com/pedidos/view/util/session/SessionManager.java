package com.pedidos.view.util.session;

import com.pedidos.dto.UsuarioSessaoDTO;
import com.pedidos.model.enums.TipoUsuario;

import javax.swing.*;

public class SessionManager {
    private static SessionManager instancia;

    private UsuarioSessaoDTO usuarioLogado;
    private JFrame frameAtual;

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instancia == null) instancia = new SessionManager();
        return instancia;
    }

    public void iniciarSessao(UsuarioSessaoDTO usuario, JFrame frameOrigem) {
        this.usuarioLogado = usuario;
        this.frameAtual    = frameOrigem;
    }

    public void encerrarSessao() {
        this.usuarioLogado = null;
        this.frameAtual = null;
    }

    public void trocarFrame(JFrame novoFrame) {
        if (frameAtual != null) frameAtual.dispose();
        frameAtual = novoFrame;
        novoFrame.setVisible(true);
    }

    public UsuarioSessaoDTO getUsuarioLogado() { return usuarioLogado; }
    public boolean    estaLogado()       { return usuarioLogado != null; }
    public TipoUsuario getTipo()         { return usuarioLogado != null ? usuarioLogado.tipo() : null; }
    public String     getNome()          { return usuarioLogado != null ? usuarioLogado.nome() : "Desconectado"; }
}
