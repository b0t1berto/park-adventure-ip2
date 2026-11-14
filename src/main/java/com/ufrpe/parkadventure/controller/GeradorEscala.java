package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.model.pessoa.Operador;
import com.ufrpe.parkadventure.model.pessoa.PeriodoTrabalho;

import java.time.LocalDate;
import java.util.Comparator;

// Monta a escala automática: todo dia, cada atração precisa de um operador em CADA turno (manhã e tarde).
// O gerador só preenche o que está vazio (turnos já escalados, inclusive à mão, são mantidos) e respeita as
// regras de carga horária do RepositorioEscalaTrabalho. Entre os operadores livres, escolhe o que tem menos
// dias na semana, para a carga ficar dividida por igual (rodízio).
//
// Para cobrir os dois turnos nos 7 dias da semana com folga semanal, cada atração precisa de pelo menos
// 3 operadores (14 turnos por semana; cada operador faz no máximo 6).
public final class GeradorEscala {
    private GeradorEscala() { }

    public static final int DIAS_A_FRENTE = 30;
    public static final int MIN_OPERADORES_POR_ATRACAO = 3;

    public record Resultado(int turnosCriados, int turnosSemOperador) { }

    // Preenche [inicio, inicio + dias)
    public static Resultado gerar(LocalDate inicio, int dias) {
        int criados = 0;
        int semOperador = 0;
        for (int i = 0; i < dias; i++) {
            LocalDate dia = inicio.plusDays(i);
            for (Atracao atracao : RepositorioAtracaoController.listarTodas()) {
                for (PeriodoTrabalho periodo : PeriodoTrabalho.values()) {
                    if (temOperadorNoTurno(atracao, dia, periodo)) {
                        continue;
                    }
                    Operador escolhido = atracao.getOperadores().stream()
                            .filter(op -> RepositorioEscalaDeTrabalhoController.motivoRecusa(dia, op, periodo) == null)
                            .min(Comparator
                                    .comparingInt((Operador op) -> RepositorioEscalaDeTrabalhoController.diasTrabalhadosNaSemana(dia, op))
                                    .thenComparingInt(RepositorioEscalaDeTrabalhoController::contarTurnos)
                                    .thenComparing(op -> op.getNome() == null ? "" : op.getNome()))
                            .orElse(null);
                    if (escolhido != null && RepositorioEscalaDeTrabalhoController.escalar(dia, escolhido, periodo)) {
                        criados++;
                    } else {
                        semOperador++;
                    }
                }
            }
        }
        return new Resultado(criados, semOperador);
    }

    public static Resultado gerarProximosDias() {
        return gerar(LocalDate.now(), DIAS_A_FRENTE);
    }

    // Algum operador DESTA atração está escalado nesse dia e turno?
    public static boolean temOperadorNoTurno(Atracao atracao, LocalDate dia, PeriodoTrabalho periodo) {
        for (Operador op : RepositorioEscalaDeTrabalhoController.operadoresDoTurno(dia, periodo)) {
            if (atracao.getOperadores().contains(op)) {
                return true;
            }
        }
        return false;
    }

    // A atração tem operador nos dois turnos do dia?
    public static boolean atracaoCoberta(Atracao atracao, LocalDate dia) {
        for (PeriodoTrabalho periodo : PeriodoTrabalho.values()) {
            if (!temOperadorNoTurno(atracao, dia, periodo)) {
                return false;
            }
        }
        return true;
    }
}
