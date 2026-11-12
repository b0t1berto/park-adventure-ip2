package com.ufrpe.parkadventure.repository;

import com.ufrpe.parkadventure.model.pessoa.Operador;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RepositorioEscalaTrabalho {

    private static RepositorioEscalaTrabalho instancia;
    public record Turno(LocalDate data, Operador operador) {}
    private static final List<Turno> turnos = new ArrayList<>();

    public RepositorioEscalaTrabalho() {}

    public static synchronized RepositorioEscalaTrabalho getInstancia() {
        if (instancia == null) {
            instancia = new RepositorioEscalaTrabalho();
        }
        return instancia;
    }

    public boolean escalar(LocalDate data, Operador operador) {
        if (data == null || operador == null || estaEscalado(data, operador)) {
            return false;
        }
        return turnos.add(new Turno(data, operador));
    }

    public boolean remover(Turno turno) {
        return turnos.remove(turno);
    }

    public boolean estaEscalado(LocalDate data, Operador operador) {
        return turnos.stream().anyMatch(t -> t.data().equals(data) && t.operador() == operador);
    }

    public List<Operador> operadoresDoDia(LocalDate data) {
        return turnos.stream()
                .filter(t -> t.data().equals(data))
                .map(Turno::operador)
                .toList();
    }

    public List<Turno> listarOrdenado() {
        return turnos.stream()
                .sorted(Comparator.comparing(Turno::data).thenComparing(t -> t.operador().getNome()))
                .toList();
    }
}
