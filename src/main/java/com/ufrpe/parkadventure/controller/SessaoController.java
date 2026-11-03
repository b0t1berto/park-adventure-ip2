package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.model.pessoa.Visitante;

public class SessaoController {
    private static SessaoController instancia;
    private Visitante visitanteAtual;

    private SessaoController() {}

    public static synchronized SessaoController getInstancia() {
        if (instancia == null) {
            instancia = new SessaoController();
        }
        return instancia;
    }

    public Visitante getVisitanteAtual() { return visitanteAtual; }

    public void setVisitanteAtual(Visitante visitanteAtual) { this.visitanteAtual = visitanteAtual; }
}