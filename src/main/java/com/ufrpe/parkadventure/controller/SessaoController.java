package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.model.pessoa.Operador;
import com.ufrpe.parkadventure.model.pessoa.Visitante;

public class SessaoController {
    private static SessaoController instancia;
    private Visitante visitanteAtual;
    private Operador operadorAtual;

    SessaoController() {}

    public static synchronized SessaoController getInstancia() {
        if (instancia == null) {
            instancia = new SessaoController();
        }
        return instancia;
    }

    public Visitante getVisitanteAtual() { return visitanteAtual; }

    public void setVisitanteAtual(Visitante visitanteAtual) { this.visitanteAtual = visitanteAtual; }

    public Operador getOperadorAtual() { return operadorAtual; }

    public void setOperadorAtual(Operador operadorAtual) { this.operadorAtual = operadorAtual; }

    // Chamado no login: quem entra no sistema substitui a sessão anterior
    public void limpar() {
        visitanteAtual = null;
        operadorAtual = null;
    }
}
