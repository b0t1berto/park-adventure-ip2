package ParkAdventure;

import java.time.LocalDate;

class ControleAcesso {
    public boolean registrarAcesso(Visitante visitante, Ingresso ingresso, Atracoes atracao) {
        // Verifica se o ingresso pertence ao visitante
        if (ingresso.getVisitante() != visitante) {
            return false;
        }
        // Verifica se o ingresso está válido
        if (!ingresso.valido(LocalDate.now())) {
            return false;
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
    }

    // consulta a ocupação atual da atração em tempo real
    public int consultarOcupacao(Atracoes atracao) {
        return atracao.getOcupacaoAtual();
    }

    public boolean estaLotada(Atracoes atracao) {
        return atracao.getOcupacaoAtual() >= atracao.getCapacidadeMaxima();
    }

    public void finalizarUso(Visitante visitante, Atracoes atracao) {
        atracao.sair();// Libera uma vaga
        if (!atracao.getFilaVirtual().estaVazia()) {//verifica se a fila virtual não está vazia
            Visitante proximo = atracao.getFilaVirtual().proximo();
            atracao.entrar();
        }
    }
}
