package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.repository.ListaAtracao;
import com.ufrpe.parkadventure.repository.ListaIngresso;
import com.ufrpe.parkadventure.repository.ListaVisitante;
import com.ufrpe.parkadventure.repository.EscalaTrabalho;

public final class ContextoController {
    private ContextoController() { }
    public static final ListaAtracao atracoes = ListaAtracao.getInstancia();
    public static final ListaVisitante VISITANTES = ListaVisitante.getInstancia();
    public static final ListaIngresso INGRESSOS = ListaIngresso.getInstancia();
    public static final EscalaTrabalho ESCALA = EscalaTrabalho.getInstancia();

    public static final IngressoController CONTROLE_INGRESSO = new IngressoController(INGRESSOS, 100.0, 800.0, 20.0);
    public static final AcessoController CONTROLE_ACESSO = AcessoController.getInstancia();
    public static final FeriadosController FERIADOS = FeriadosController.getInstancia();
    public static final SessaoController SESSAO = SessaoController.getInstancia();
}