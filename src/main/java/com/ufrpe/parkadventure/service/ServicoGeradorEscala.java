package com.ufrpe.parkadventure.service;

import com.ufrpe.parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.model.pessoa.Operador;
import com.ufrpe.parkadventure.model.pessoa.PeriodoTrabalho;

import java.time.LocalDate;
import java.util.Comparator;

/**
 * Serviço de geração automática de escala: todo dia, cada atração precisa de um operador
 * em CADA turno (manhã e tarde). Preenche o que está vazio e respeita as regras de carga horária.
 * Entre os operadores livres, escolhe o que tem menos dias na semana para rodízio.
 *
 * Para cobrir os dois turnos nos 7 dias da semana com folga semanal, cada atração precisa
 * de pelo menos 3 operadores (14 turnos por semana; cada operador faz no máximo 6).
 */
public class ServicoGeradorEscala {

    public static final int DIAS_A_FRENTE = 30;
    public static final int MIN_OPERADORES_POR_ATRACAO = 3;

    public record Resultado(int turnosCriados, int turnosSemOperador) { }

    private final ServicoAtracao servicoAtracao;
    private final ServicoEscala servicoEscala;

    public ServicoGeradorEscala(ServicoAtracao servicoAtracao, ServicoEscala servicoEscala) {
        this.servicoAtracao = servicoAtracao;
        this.servicoEscala = servicoEscala;
    }

    /**
     * Preenche a escala para [inicio, inicio + dias).
     */
    public Resultado gerar(LocalDate inicio, int dias) {
        int criados = 0;
        int semOperador = 0;
        for (int i = 0; i < dias; i++) {
            LocalDate dia = inicio.plusDays(i);
            for (Atracao atracao : servicoAtracao.listarTodas()) {
                for (PeriodoTrabalho periodo : PeriodoTrabalho.values()) {
                    if (temOperadorNoTurno(atracao, dia, periodo)) {
                        continue;
                    }
                    Operador escolhido = atracao.getOperadores().stream()
                            .filter(op -> servicoEscala.motivoRecusa(dia, op, periodo) == null)
                            .min(Comparator
                                    .comparingInt((Operador op) -> servicoEscala.diasTrabalhadosNaSemana(dia, op))
                                    .thenComparingInt(servicoEscala::contarTurnos)
                                    .thenComparing(op -> op.getNome() == null ? "" : op.getNome()))
                            .orElse(null);
                    if (escolhido != null && servicoEscala.escalar(dia, escolhido, periodo)) {
                        criados++;
                    } else {
                        semOperador++;
                    }
                }
            }
        }
        return new Resultado(criados, semOperador);
    }

    /**
     * Gera a escala para os próximos DIAS_A_FRENTE dias a partir de hoje.
     */
    public Resultado gerarProximosDias() {
        return gerar(LocalDate.now(), DIAS_A_FRENTE);
    }

    /**
     * Algum operador DESTA atração está escalado nesse dia e turno?
     */
    public boolean temOperadorNoTurno(Atracao atracao, LocalDate dia, PeriodoTrabalho periodo) {
        for (Operador op : servicoEscala.operadoresDoTurno(dia, periodo)) {
            if (atracao.getOperadores().contains(op)) {
                return true;
            }
        }
        return false;
    }

    /**
     * A atração tem operador nos dois turnos do dia?
     */
    public boolean atracaoCoberta(Atracao atracao, LocalDate dia) {
        for (PeriodoTrabalho periodo : PeriodoTrabalho.values()) {
            if (!temOperadorNoTurno(atracao, dia, periodo)) {
                return false;
            }
        }
        return true;
    }
}
