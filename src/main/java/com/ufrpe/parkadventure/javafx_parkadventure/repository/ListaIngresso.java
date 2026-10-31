package com.ufrpe.parkadventure.javafx_parkadventure.repository;

import java.time.LocalDate;
import java.util.ArrayList;

import com.ufrpe.parkadventure.javafx_parkadventure.model.ingresso.Ingresso;
import com.ufrpe.parkadventure.javafx_parkadventure.model.pessoa.Visitante;

public class ListaIngresso {
    private final ArrayList<Ingresso> ingressos = new ArrayList<>();

    public boolean cadastrar(Ingresso ingresso) {
        if (ingresso == null || ingresso.getVisitante() == null) {
            return false;
        }
        return ingressos.add(ingresso);
    }

    public ArrayList<Ingresso> listarTodos() {
        return new ArrayList<>(ingressos);
    }

    public ArrayList<Ingresso> listarPorVisitante(Visitante visitante) {
        ArrayList<Ingresso> resultado = new ArrayList<>();
        for (Ingresso ingresso : ingressos) {
            if (ingresso.getVisitante() == visitante) {
                resultado.add(ingresso);
            }
        }
        return resultado;
    }

    // Ingresso mais recente do visitante (null se ele nunca comprou)
    public Ingresso buscarUltimo(Visitante visitante) {
        Ingresso ultimo = null;
        for (Ingresso ingresso : ingressos) {
            if (ingresso.getVisitante() == visitante) {
                ultimo = ingresso;
            }
        }
        return ultimo;
    }

    // REQ06: quantidade de ingressos emitidos em uma data
    public int consultarQuantidadePorData(LocalDate data) {
        int total = 0;
        for (Ingresso ingresso : ingressos) {
            if (ingresso.getDataCompra().equals(data)) {
                total++;
            }
        }
        return total;
    }

    public boolean remover(Ingresso ingresso) {
        return ingressos.remove(ingresso);
    }
}
