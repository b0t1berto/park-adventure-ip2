package parkAdventure.controller;

import java.time.LocalDate;

import parkAdventure.model.atracao.Atracao;
import parkAdventure.model.ingresso.Ingresso;
import parkAdventure.model.pessoa.Visitante;

public class ControleAcesso {

    public ResultadoAcesso registrarAcesso(Visitante visitante, Ingresso ingresso, Atracao atracao) {
        // O ingresso tem que pertencer ao visitante e estar dentro da validade
        if (ingresso.getVisitante() != visitante) {
            return ResultadoAcesso.INGRESSO_DE_OUTRO_VISITANTE;
        }
        if (!ingresso.valido(LocalDate.now())) {
            return ResultadoAcesso.INGRESSO_EXPIRADO;
        }

        // Idade, altura e responsável
        switch (atracao.verificarRestricao(visitante)) {
            case FORA_DOS_REQUISITOS: return ResultadoAcesso.FORA_DOS_REQUISITOS;
            case SEM_RESPONSAVEL:     return ResultadoAcesso.SEM_RESPONSAVEL;
            default:                  break;
        }

        // Se estiver lotada, vai para a fila virtual
        if (atracao.entrar()) {
            return ResultadoAcesso.AUTORIZADO;
        }
        return atracao.getFilaVirtual().adicionar(visitante)
                ? ResultadoAcesso.NA_FILA
                : ResultadoAcesso.FILA_CHEIA;
    }

    // Consulta a ocupação da atração em tempo real
    public int consultarOcupacao(Atracao atracao) {
        return atracao.getOcupacaoAtual();
    }

    public boolean estaLotada(Atracao atracao) {
        return atracao.estaLotada();
    }

    // Chama o próximo da fila virtual (retorna null se não tiver ninguém)
    public Visitante finalizarUso(Visitante visitante, Atracao atracao) {
        atracao.sair();
        if (atracao.getFilaVirtual().estaVazia()) {
            return null;
        }
        Visitante proximo = atracao.getFilaVirtual().proximo();
        atracao.entrar();
        return proximo;
    }
}
