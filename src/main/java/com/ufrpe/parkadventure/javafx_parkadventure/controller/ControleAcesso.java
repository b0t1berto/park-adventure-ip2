package com.ufrpe.parkadventure.javafx_parkadventure.controller;

import java.time.LocalDate;

import com.ufrpe.parkadventure.javafx_parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.javafx_parkadventure.model.ingresso.Ingresso;
import com.ufrpe.parkadventure.javafx_parkadventure.model.pessoa.Visitante;

public class ControleAcesso {

    // Entrada imediata: AUTORIZADO, ou ATRACAO_LOTADA (aí o visitante escolhe um horário na fila virtual)
    public ResultadoAcesso registrarAcesso(Visitante visitante, Ingresso ingresso, Atracao atracao) {
        ResultadoAcesso problema = validar(visitante, ingresso, atracao);
        if (problema != null) {
            return problema;
        }
        return atracao.entrar() ? ResultadoAcesso.AUTORIZADO : ResultadoAcesso.ATRACAO_LOTADA;
    }

    // Cadastro na fila virtual da atração, em um horário (11:00 às 18:00, de 30 em 30 minutos)
    public ResultadoAcesso entrarNaFilaVirtual(Visitante visitante, Ingresso ingresso, Atracao atracao, String horario) {
        ResultadoAcesso problema = validar(visitante, ingresso, atracao);
        if (problema != null) {
            return problema;
        }
        if (!atracao.getFilaVirtual().validarHorario(horario)) {
            return ResultadoAcesso.HORARIO_INVALIDO;
        }
        return atracao.getFilaVirtual().adicionar(horario, visitante)
                ? ResultadoAcesso.NA_FILA
                : ResultadoAcesso.FILA_CHEIA;
    }

    // Regras comuns às duas operações; retorna null quando o visitante pode seguir
    private ResultadoAcesso validar(Visitante visitante, Ingresso ingresso, Atracao atracao) {
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
            default:                  return null;
        }
    }

    // Consulta a ocupação da atração em tempo real
    public int consultarOcupacao(Atracao atracao) {
        return atracao.getOcupacaoAtual();
    }

    public boolean estaLotada(Atracao atracao) {
        return atracao.estaLotada();
    }

    // Libera uma vaga e chama o próximo da fila virtual (null se não tiver ninguém esperando)
    public Visitante finalizarUso(Atracao atracao) {
        atracao.sair();
        if (atracao.getFilaVirtual().estaVazia()) {
            return null;
        }
        Visitante proximo = atracao.getFilaVirtual().proximo();
        atracao.entrar();
        return proximo;
    }

    public Visitante finalizarUso(Visitante visitante, Atracao atracao) {
        return finalizarUso(atracao);
    }
}
