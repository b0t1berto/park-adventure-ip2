package com.ufrpe.parkadventure.service;

import com.ufrpe.parkadventure.model.ResultadoAcesso;
import com.ufrpe.parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.model.atracao.FilaVirtual;
import com.ufrpe.parkadventure.model.atracao.Reserva;
import com.ufrpe.parkadventure.model.ingresso.Ingresso;
import com.ufrpe.parkadventure.model.ingresso.TipoIngresso;
import com.ufrpe.parkadventure.model.pessoa.Visitante;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço de acesso e fura-fila: regras de negócio para controle de acesso às atrações,
 * reservas de fura-fila, validação de ingressos e restrições.
 */
public class ServicoAcesso {

    // Com o Passe Anual, o visitante só pode reservar o fura-fila para os próximos dias
    private static final int DIAS_DE_ANTECEDENCIA = 30;

    private final ServicoAtracao servicoAtracao;
    private final ServicoIngresso servicoIngresso;

    public ServicoAcesso(ServicoAtracao servicoAtracao, ServicoIngresso servicoIngresso) {
        this.servicoAtracao = servicoAtracao;
        this.servicoIngresso = servicoIngresso;
    }

    // ---------------------------------------------------------------- entrada comum (feita pelo operador)

    /**
     * Entrada na fila comum: AUTORIZADO (o operador faz o +1) ou ATRACAO_LOTADA.
     */
    public ResultadoAcesso registrarAcesso(Visitante visitante, Ingresso ingresso, Atracao atracao) {
        ResultadoAcesso problema = validar(visitante, ingresso, atracao, LocalDate.now());
        if (problema != null) {
            return problema;
        }
        return atracao.entrar() ? ResultadoAcesso.AUTORIZADO : ResultadoAcesso.ATRACAO_LOTADA;
    }

    /**
     * Entrada comum conferida pelo operador: usa o ingresso do visitante que vale HOJE.
     * Sem ingresso válido o resultado é SEM_INGRESSO; os demais motivos vêm de registrarAcesso().
     */
    public ResultadoAcesso registrarEntrada(Visitante visitante, Atracao atracao) {
        Ingresso ingresso = ingressoParaData(visitante, LocalDate.now());
        return registrarAcesso(visitante, ingresso, atracao);
    }

    /**
     * Saída de uma pessoa (o operador faz o -1).
     */
    public void finalizarUso(Atracao atracao) {
        atracao.sair();
    }

    // ---------------------------------------------------------------- fura-fila (reserva com horário)

    /**
     * Visitante reserva o passe: um dia, um horário, uma atração.
     */
    public ResultadoAcesso reservarFuraFila(Visitante visitante, Ingresso ingresso, Atracao atracao,
                                            LocalDate data, LocalTime horario) {
        if (data == null || data.isBefore(LocalDate.now())) {
            return ResultadoAcesso.INGRESSO_EXPIRADO;
        }
        ResultadoAcesso problema = validar(visitante, ingresso, atracao, data);
        if (problema != null) {
            return problema;
        }
        // Passe anual: limite de 1 reserva de fura-fila por mês
        if (ingresso.getTipoIngresso() == TipoIngresso.PASSE_ANUAL && jaReservouNoMes(visitante, ingresso)) {
            return ResultadoAcesso.LIMITE_MENSAL_EXCEDIDO;
        }
        if (!FilaVirtual.horarioValido(horario)) {
            return ResultadoAcesso.HORARIO_INVALIDO;
        }
        // hoje: não adianta reservar um horário cuja janela já terminou
        if (data.equals(LocalDate.now())
                && LocalDateTime.of(data, horario).plus(FilaVirtual.TOLERANCIA).isBefore(LocalDateTime.now())) {
            return ResultadoAcesso.HORARIO_INVALIDO;
        }
        // a mesma pessoa não pode estar em duas atrações no mesmo horário
        for (Atracao outra : servicoAtracao.listarTodas()) {
            if (outra != atracao && outra.getFilaVirtual().temReservaEm(visitante, data, horario)) {
                return ResultadoAcesso.CONFLITO_DE_HORARIO;
            }
        }
        switch (atracao.getFilaVirtual().reservar(visitante, data, horario)) {
            case OK:                        return ResultadoAcesso.RESERVADO;
            case JA_RESERVOU_NESTA_ATRACAO: return ResultadoAcesso.JA_NA_FILA;
            case HORARIO_CHEIO:             return ResultadoAcesso.FILA_CHEIA;
            default:                        return ResultadoAcesso.HORARIO_INVALIDO;
        }
    }

    /**
     * Cancela uma reserva de fura-fila.
     */
    public boolean cancelarReserva(Visitante visitante, Atracao atracao, LocalDate data) {
        return atracao.getFilaVirtual().cancelar(visitante, data);
    }

    /**
     * OPERADOR, na entrada de prioridade: confere a reserva e faz o +1.
     */
    public ResultadoAcesso liberarFuraFila(Visitante visitante, Atracao atracao, LocalDateTime agora) {
        if (!atracao.temOperador()) {
            return ResultadoAcesso.SEM_OPERADOR;
        }
        atracao.getFilaVirtual().expirarNoShows(agora);
        Reserva reserva = atracao.getFilaVirtual().elegivel(visitante, agora);
        if (reserva == null) {
            return ResultadoAcesso.SEM_RESERVA_NO_HORARIO;
        }
        if (!temIngressoValido(visitante, agora.toLocalDate())) {
            return ResultadoAcesso.INGRESSO_EXPIRADO;
        }
        ResultadoAcesso restricao = restricao(visitante, atracao);
        if (restricao != null) {
            return restricao;
        }
        if (!atracao.entrar()) {
            return ResultadoAcesso.ATRACAO_LOTADA;
        }
        atracao.getFilaVirtual().marcarUsada(reserva);
        return ResultadoAcesso.AUTORIZADO;
    }

    // ---------------------------------------------------------------- ingressos e datas

    /**
     * O ingresso vale no dia? Diária: só no dia de validade. Passe anual: da compra até a validade.
     */
    public boolean ingressoVale(Ingresso ingresso, LocalDate dia) {
        if (ingresso == null || dia == null) {
            return false;
        }
        if (ingresso.getTipoIngresso() == TipoIngresso.PASSE_ANUAL) {
            return !dia.isBefore(ingresso.getDataCompra()) && !dia.isAfter(ingresso.getDataValidade());
        }
        return dia.equals(ingresso.getDataValidade());
    }

    /**
     * Dias em que este ingresso permite reservar fura-fila (a partir de hoje).
     */
    public List<LocalDate> datasDisponiveis(Ingresso ingresso) {
        List<LocalDate> datas = new ArrayList<>();
        if (ingresso == null) {
            return datas;
        }
        LocalDate hoje = LocalDate.now();
        if (ingresso.getTipoIngresso() == TipoIngresso.PASSE_ANUAL) {
            LocalDate fim = hoje.plusDays(DIAS_DE_ANTECEDENCIA);
            for (LocalDate dia = hoje; !dia.isAfter(fim); dia = dia.plusDays(1)) {
                if (ingressoVale(ingresso, dia)) {
                    datas.add(dia);
                }
            }
        } else if (!ingresso.getDataValidade().isBefore(hoje)) {
            datas.add(ingresso.getDataValidade());
        }
        return datas;
    }

    /**
     * Todos os dias em que o visitante pode reservar, somando TODOS os ingressos dele.
     */
    public List<LocalDate> datasDisponiveis(Visitante visitante) {
        java.util.TreeSet<LocalDate> uniao = new java.util.TreeSet<>();
        for (Ingresso ingresso : servicoIngresso.listarPorVisitante(visitante.getNome())) {
            uniao.addAll(datasDisponiveis(ingresso));
        }
        return new ArrayList<>(uniao);
    }

    /**
     * Ingresso do visitante que vale no dia informado (o mais recente que servir).
     */
    public Ingresso ingressoParaData(Visitante visitante, LocalDate dia) {
        List<Ingresso> ingressos = servicoIngresso.listarPorVisitante(visitante.getNome());
        for (int i = ingressos.size() - 1; i >= 0; i--) {
            if (ingressoVale(ingressos.get(i), dia)) {
                return ingressos.get(i);
            }
        }
        return null;
    }

    /**
     * Ingresso mais recente do visitante que ainda permite reservar algum dia.
     */
    public Ingresso ingressoParaReserva(Visitante visitante) {
        List<Ingresso> ingressos = servicoIngresso.listarPorVisitante(visitante.getNome());
        for (int i = ingressos.size() - 1; i >= 0; i--) {
            if (!datasDisponiveis(ingressos.get(i)).isEmpty()) {
                return ingressos.get(i);
            }
        }
        return null;
    }

    private boolean temIngressoValido(Visitante visitante, LocalDate dia) {
        for (Ingresso ingresso : servicoIngresso.listarPorVisitante(visitante.getNome())) {
            if (ingressoVale(ingresso, dia)) {
                return true;
            }
        }
        return false;
    }

    // ---------------------------------------------------------------- regras comuns

    /**
     * Retorna null quando o visitante pode seguir.
     */
    private ResultadoAcesso validar(Visitante visitante, Ingresso ingresso, Atracao atracao, LocalDate dia) {
        if (ingresso == null) {
            return ResultadoAcesso.SEM_INGRESSO;
        }
        if (!atracao.temOperador()) {
            return ResultadoAcesso.SEM_OPERADOR;
        }
        if (ingresso.getVisitante() != visitante) {
            return ResultadoAcesso.INGRESSO_DE_OUTRO_VISITANTE;
        }
        if (!ingressoVale(ingresso, dia)) {
            return ResultadoAcesso.INGRESSO_EXPIRADO;
        }
        return restricao(visitante, atracao);
    }

    /**
     * Idade, altura e responsável.
     */
    private ResultadoAcesso restricao(Visitante visitante, Atracao atracao) {
        switch (atracao.verificarRestricao(visitante)) {
            case FORA_DOS_REQUISITOS: return ResultadoAcesso.FORA_DOS_REQUISITOS;
            case SEM_RESPONSAVEL:     return ResultadoAcesso.SEM_RESPONSAVEL;
            default:                  return null;
        }
    }

    /**
     * Consulta a ocupação da atração em tempo real.
     */
    public int consultarOcupacao(Atracao atracao) {
        return atracao.getOcupacaoAtual();
    }

    /**
     * Verifica se uma atração está lotada.
     */
    public boolean estaLotada(Atracao atracao) {
        return atracao.estaLotada();
    }

    /**
     * Verifica se um visitante com passe anual já fez reserva de fura-fila no mês corrente.
     * O passe anual permite no máximo 1 reserva por mês (12 entradas no total).
     */
    private boolean jaReservouNoMes(Visitante visitante, Ingresso ingresso) {
        LocalDate agora = LocalDate.now();
        int anoAtual = agora.getYear();
        int mesAtual = agora.getMonthValue();
        for (Atracao atracao : servicoAtracao.listarTodas()) {
            for (Reserva reserva : atracao.getFilaVirtual().getReservas()) {
                if (reserva.getVisitante() == visitante
                        && reserva.estaAtiva()
                        && reserva.getData().getYear() == anoAtual
                        && reserva.getData().getMonthValue() == mesAtual) {
                    return true;
                }
            }
        }
        return false;
    }
}
