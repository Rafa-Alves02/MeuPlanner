package br.com.MeuPlanner.app;

import br.com.MeuPlanner.model.Usuario;

public final class SessaoAtual {

    private static Usuario usuario;

    private SessaoAtual() {}

    public static void setUsuario(Usuario usuarioLogado) {
        usuario = usuarioLogado;
    }

    public static Usuario getUsuario() {
        return usuario;
    }

    public static void encerrar() {
        usuario = null;
    }
}
