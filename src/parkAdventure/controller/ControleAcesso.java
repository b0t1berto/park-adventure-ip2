package parkAdventure.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;

import parkAdventure.model.acesso.RegistroAcesso;
import parkAdventure.model.atracao.Atracao;
import parkAdventure.model.ingresso.Ingresso;
import parkAdventure.model.pessoa.Visitante;
import parkAdventure.repository.ListaAcesso;
import parkAdventure.repository.ListaIngresso;

public class ControleAcesso {
    private final ListaAcesso acessos;
    private final ListaIngresso ingressos;

    public ControleAcesso(ListaAcesso acessos, ListaIngresso ingressos) {
        this.acessos = acessos;
        this.ingressos = ingressos;
    }

    // Entrada imediata: AUTORIZADO, ou ATRACAO_LOTADA (aí o visitante escolhe um horário na fila virtual)
    public ResultadoAcesso registrarAcesso(Visitante visitante, Ingresso ingresso, Atracao atracao) {
        ResultadoAcesso problema = validar(visitante, ingresso, atracao);
        if (problema != null) {
            return problema;
        }
        if (!atracao.entrar()) {
            return ResultadoAcesso.ATRACAO_LOTADA;
        }
        // REQ10: guarda o vínculo visitante + ingresso + atração
        acessos.registrar(new RegistroAcesso(visitante, ingresso, atracao, LocalDateTime.now()));
        return ResultadoAcesso.AUTORIZADO;
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
        // REQ21: atração só recebe público se estiver aberta (exige operador vinculado)
        if (!atracao.estaAberta()) {
            return ResultadoAcesso.ATRACAO_FECHADA;
        }
        // O ingresso tem que pertencer ao visitante e estar dentro da validade (REQ20)
        if (ingresso.getVisitante() != visitante) {
            return ResultadoAcesso.INGRESSO_DE_OUTRO_VISITANTE;
        }
        if (!ingresso.valido(LocalDate.now())) {
            return ResultadoAcesso.INGRESSO_EXPIRADO;
        }
        // Idade, altura e responsável (REQ16 e REQ17)
        switch (atracao.verificarRestricao(visitante)) {
            case FORA_DOS_REQUISITOS: return ResultadoAcesso.FORA_DOS_REQUISITOS;
            case SEM_RESPONSAVEL:     return ResultadoAcesso.SEM_RESPONSAVEL;
            default:                  break;
        }
        if (estaNaAtracao(visitante, atracao)) {
            return ResultadoAcesso.JA_NA_ATRACAO;
        }
        return null;
    }

    // Consulta a ocupação da atração em tempo real
    public int consultarOcupacao(Atracao atracao) {
        return atracao.getOcupacaoAtual();
    }

    public boolean estaLotada(Atracao atracao) {
        return atracao.estaLotada();
    }

    public boolean estaNaAtracao(Visitante visitante, Atracao atracao) {
        return acessos.buscarAtivo(visitante, atracao) != null;
    }

    // REQ12: o visitante finaliza o uso, a vaga é liberada e o próximo da fila virtual
    // (com ingresso ainda válido) entra. Retorna quem entrou, ou null se ninguém entrou.
    public Visitante finalizarUso(Visitante visitante, Atracao atracao) {
        RegistroAcesso registro = acessos.buscarAtivo(visitante, atracao);
        if (registro == null) {
            return null;
        }
        registro.finalizar(LocalDateTime.now());
        atracao.sair();
        return chamarProximoDaFila(atracao);
    }

    private Visitante chamarProximoDaFila(Atracao atracao) {
        if (!atracao.estaAberta()) {
            return null;
        }
        while (!atracao.estaLotada() && !atracao.getFilaVirtual().estaVazia()) {
            Visitante proximo = atracao.getFilaVirtual().proximo();
            Ingresso ingresso = ingressos.buscarUltimo(proximo);
            // quem não tem mais ingresso válido perde a vez
            if (ingresso == null || validar(proximo, ingresso, atracao) != null) {
                continue;
            }
            atracao.entrar();
            acessos.registrar(new RegistroAcesso(proximo, ingresso, atracao, LocalDateTime.now()));
            return proximo;
        }
        return null;
    }
}
