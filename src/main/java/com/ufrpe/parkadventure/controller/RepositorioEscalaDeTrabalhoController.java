package com.ufrpe.parkadventure.controller;

import java.time.LocalDate;
import java.util.List;
import com.ufrpe.parkadventure.model.pessoa.Operador;
import com.ufrpe.parkadventure.model.pessoa.PeriodoTrabalho;
import com.ufrpe.parkadventure.repository.RepositorioEscalaTrabalho;
import com.ufrpe.parkadventure.repository.RepositorioEscalaTrabalho.Turno;

public class RepositorioEscalaDeTrabalhoController {
    private static final RepositorioEscalaTrabalho escalaTrabalho = RepositorioEscalaTrabalho.getInstancia();

    public static boolean escalar(LocalDate data, Operador operador, PeriodoTrabalho periodo) {
        return escalaTrabalho.escalar(data, operador, periodo);
    }

    public static String motivoRecusa(LocalDate data, Operador operador, PeriodoTrabalho periodo) {
        return escalaTrabalho.motivoRecusa(data, operador, periodo);
    }

    public static void removerDoOperador(Operador operador) {
        escalaTrabalho.removerDoOperador(operador);
    }

    public static int diasTrabalhadosNaSemana(LocalDate data, Operador operador) {
        return escalaTrabalho.diasTrabalhadosNaSemana(data, operador);
    }

    public static int contarTurnos(Operador operador) {
        return escalaTrabalho.contarTurnos(operador);
    }

    public static List<Operador> operadoresDoTurno(LocalDate data, PeriodoTrabalho periodo) {
        return escalaTrabalho.operadoresDoTurno(data, periodo);
    }

    public static List<Turno> turnosDoDia(LocalDate data) {
        return escalaTrabalho.turnosDoDia(data);
    }

    public static boolean remover(Turno turno) {
        return escalaTrabalho.remover(turno);
    }

    public static boolean estaEscalado(LocalDate data, Operador operador) {
        return escalaTrabalho.estaEscalado(data, operador);
    }

    public static List<Operador> operadoresDoDia(LocalDate data) {
        return escalaTrabalho.operadoresDoDia(data);
    }

    public static List<Turno> listarOrdenado() {
        return escalaTrabalho.listarOrdenado();
    }
}
