package com.moneycontrol.util;

import com.moneycontrol.model.Usuario;

public class Session {
    private static Session instance;
    private Usuario usuarioAtual;

    private Session() {}

    public static Session getInstance() {
        if (instance == null) instance = new Session();
        return instance;
    }

    public Usuario getUsuarioAtual() { return usuarioAtual; }
    public void setUsuarioAtual(Usuario u) { this.usuarioAtual = u; }

    public int getUserId() {
        return usuarioAtual != null ? usuarioAtual.getId() : -1;
    }

    public void logout() { usuarioAtual = null; }
}
