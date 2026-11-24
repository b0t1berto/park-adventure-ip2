package com.ufrpe.parkadventure.app;

import com.ufrpe.parkadventure.model.pessoa.Operador;
import com.ufrpe.parkadventure.model.pessoa.Visitante;
import com.ufrpe.parkadventure.service.ServicoAutenticacao.Papel;

public class Sessao {
    private static Sessao instancia;
    private Visitante visitanteAtual;
    private Operador operadorAtual;
    private Papel papel;

    Sessao() {}

    public static synchronized Sessao getInstancia() {
        if (instancia == null) {
            instancia = new Sessao();
        }
        return instancia;
    }

    public Visitante getVisitanteAtual() { return visitanteAtual; }

    public void setVisitanteAtual(Visitante visitanteAtual) { this.visitanteAtual = visitanteAtual; }

    public Operador getOperadorAtual() { return operadorAtual; }

    public void setOperadorAtual(Operador operadorAtual) { this.operadorAtual = operadorAtual; }

    public Papel getPapel() { return papel; }

    public void setPapel(Papel papel) { this.papel = papel; }

    // Chamado no login: quem entra no sistema substitui a sessão anterior
    public void limpar() {
        visitanteAtual = null;
        operadorAtual = null;
        papel = null;
    }
}
