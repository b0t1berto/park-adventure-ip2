package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.repository.RepositorioIngresso;

public final class ContextoController {
    private ContextoController() { }
    public static final IngressoController CONTROLE_INGRESSO = new IngressoController(RepositorioIngresso.getInstancia(), 100.0, 800.0, 20.0);
    public static final AcessoController CONTROLE_ACESSO = AcessoController.getInstancia();
    public static final FeriadosController FERIADOS = FeriadosController.getInstancia();
    public static final SessaoController SESSAO = SessaoController.getInstancia();
}