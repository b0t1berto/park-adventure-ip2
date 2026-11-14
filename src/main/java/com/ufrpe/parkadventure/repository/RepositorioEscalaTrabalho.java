package com.ufrpe.parkadventure.repository;

import com.ufrpe.parkadventure.model.pessoa.Operador;
import com.ufrpe.parkadventure.model.pessoa.PeriodoTrabalho;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Escala de trabalho: quem trabalha em qual dia e em qual turno.
// Regras de carga horária (turnos de 4 horas):
//  - no máximo UM turno por operador por dia (no máximo 4 horas diárias, nunca dois turnos seguidos);
//  - no máximo 6 dias de trabalho por semana (segunda a domingo), garantindo a folga semanal.
public class RepositorioEscalaTrabalho {

    public static final int MAX_DIAS_POR_SEMANA = 6;

    private static RepositorioEscalaTrabalho instancia;
    public record Turno(LocalDate data, Operador operador, PeriodoTrabalho periodo) {}
    private static final List<Turno> turnos = new ArrayList<>();

    public RepositorioEscalaTrabalho() {}

    public static synchronized RepositorioEscalaTrabalho getInstancia() {
        if (instancia == null) {
            instancia = new RepositorioEscalaTrabalho();
        }
        return instancia;
    }

    // Motivo pelo qual o operador NÃO pode ser escalado nesse dia/turno; null se pode
    public String motivoRecusa(LocalDate data, Operador operador, PeriodoTrabalho periodo) {
        if (data == null || operador == null || periodo == null) {
            return "Informe a data, o operador e o turno.";
        }
        if (estaEscalado(data, operador)) {
            return operador.getNome() + " já tem turno nesse dia (máximo de um turno de "
                    + periodo.horas() + " horas por dia).";
        }
        if (diasTrabalhadosNaSemana(data, operador) >= MAX_DIAS_POR_SEMANA) {
            return operador.getNome() + " já trabalha " + MAX_DIAS_POR_SEMANA
                    + " dias nessa semana; ele precisa de uma folga semanal.";
        }
        return null;
    }

    public boolean escalar(LocalDate data, Operador operador, PeriodoTrabalho periodo) {
        if (motivoRecusa(data, operador, periodo) != null) {
            return false;
        }
        return turnos.add(new Turno(data, operador, periodo));
    }

    public boolean remover(Turno turno) {
        return turnos.remove(turno);
    }

    // Quando um operador deixa o parque, seus turnos saem da escala
    public void removerDoOperador(Operador operador) {
        turnos.removeIf(t -> t.operador() == operador);
    }

    public boolean estaEscalado(LocalDate data, Operador operador) {
        return turnos.stream().anyMatch(t -> t.data().equals(data) && t.operador() == operador);
    }

    // Quantos dias o operador já trabalha na semana (segunda a domingo) em que 'data' está
    public int diasTrabalhadosNaSemana(LocalDate data, Operador operador) {
        LocalDate segunda = data.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate domingo = segunda.plusDays(6);
        return (int) turnos.stream()
                .filter(t -> t.operador() == operador && !t.data().isBefore(segunda) && !t.data().isAfter(domingo))
                .count();
    }

    public int contarTurnos(Operador operador) {
        return (int) turnos.stream().filter(t -> t.operador() == operador).count();
    }

    public List<Operador> operadoresDoDia(LocalDate data) {
        return turnos.stream()
                .filter(t -> t.data().equals(data))
                .map(Turno::operador)
                .distinct()
                .toList();
    }

    public List<Operador> operadoresDoTurno(LocalDate data, PeriodoTrabalho periodo) {
        return turnos.stream()
                .filter(t -> t.data().equals(data) && t.periodo() == periodo)
                .map(Turno::operador)
                .toList();
    }

    public List<Turno> turnosDoDia(LocalDate data) {
        return listarOrdenado().stream().filter(t -> t.data().equals(data)).toList();
    }

    public List<Turno> listarOrdenado() {
        return turnos.stream()
                .sorted(Comparator.comparing(Turno::data)
                        .thenComparing(Turno::periodo)
                        .thenComparing(t -> t.operador().getNome()))
                .toList();
    }
}
