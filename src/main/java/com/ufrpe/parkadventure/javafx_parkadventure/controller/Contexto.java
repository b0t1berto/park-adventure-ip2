package com.ufrpe.parkadventure.javafx_parkadventure.controller;

import com.ufrpe.parkadventure.javafx_parkadventure.repository.EscalaTrabalho;
import com.ufrpe.parkadventure.javafx_parkadventure.repository.ListaAtracao;
import com.ufrpe.parkadventure.javafx_parkadventure.repository.ListaIngresso;
import com.ufrpe.parkadventure.javafx_parkadventure.repository.ListaVisitante;

public final class Contexto {
    private Contexto() { }
    public static final ListaAtracao ATRACOES = new ListaAtracao();
    public static final ListaVisitante VISITANTES = new ListaVisitante();
    public static final ListaIngresso INGRESSOS = new ListaIngresso();
    public static final EscalaTrabalho ESCALA = new EscalaTrabalho();

    public static final ControleIngresso CONTROLE_INGRESSO = new ControleIngresso(INGRESSOS, 100.0, 800.0, 20.0);
    public static final ControleAcesso CONTROLE_ACESSO = new ControleAcesso();
    public static final ControleFeriados FERIADOS = new ControleFeriados();
    public static final Sessao SESSAO = new Sessao();
}