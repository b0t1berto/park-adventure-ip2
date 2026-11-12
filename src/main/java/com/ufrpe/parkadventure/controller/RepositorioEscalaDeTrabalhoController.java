package com.ufrpe.parkadventure.controller;

import java.time.LocalDate;
import java.util.List;
import com.ufrpe.parkadventure.model.pessoa.Operador;
import com.ufrpe.parkadventure.repository.RepositorioEscalaTrabalho;
import com.ufrpe.parkadventure.repository.RepositorioEscalaTrabalho.Turno;

public class RepositorioEscalaDeTrabalhoController {
    private static final RepositorioEscalaTrabalho escalaTrabalho = RepositorioEscalaTrabalho.getInstancia();

    public static boolean escalar(LocalDate data, Operador operador) {
        return escalaTrabalho.escalar(data, operador);
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
