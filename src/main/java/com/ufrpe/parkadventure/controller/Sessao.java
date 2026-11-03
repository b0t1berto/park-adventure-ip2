package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.model.pessoa.Visitante;

public class Sessao {
    private Visitante visitanteAtual;

    public Visitante getVisitanteAtual() { return visitanteAtual; }

    public void setVisitanteAtual(Visitante visitanteAtual) { this.visitanteAtual = visitanteAtual; }
}