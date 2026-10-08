package controller;

import parkAdventure.Atracao;
import parkAdventure.Visitante;

import java.time.LocalDate;

import classesExtras.Ingresso;

public class ControleAcesso {
    public boolean registrarAcesso(Visitante visitante, Ingresso ingresso, Atracao atracao) {
        // Verifica se o ingresso pertence ao visitante
        if (visitante == ingresso.getVisitante()) {// Verifica se o ingresso está válido
            if (!ingresso.valido(LocalDate.now())) {
                return true;
            }
            // Verifica idade e altura
            if (!atracao.podeEntrar(visitante)) {
                return false;
            }

            // Se estiver lotada, coloca na fila virtual
            if (!atracao.entrar()) {
                atracao.getFilaVirtual().adicionar(visitante);
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    // consulta a ocupação atual da atração em tempo real
    public int consultarOcupacao(Atracao atracao) {
        return atracao.getOcupacaoAtual();
    }

    public boolean estaLotada(Atracao atracao) {
        return atracao.getOcupacaoAtual() >= atracao.getCapacidadeMaxima();
    }

    public void finalizarUso(Visitante visitante, Atracao atracao) {
        atracao.sair();// Libera uma vaga
        if (!atracao.getFilaVirtual().estaVazia()) {//verifica se a fila virtual não está vazia
            Visitante proximo = atracao.getFilaVirtual().proximo();
            atracao.entrar();
        }
    }
}
