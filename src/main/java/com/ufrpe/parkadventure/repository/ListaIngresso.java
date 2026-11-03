package com.ufrpe.parkadventure.repository;
import java.time.LocalDate;
import java.util.ArrayList;
import com.ufrpe.parkadventure.model.ingresso.Ingresso;
import com.ufrpe.parkadventure.model.pessoa.Visitante;

public class ListaIngresso {
    private static ListaIngresso instancia;
    private static final ArrayList<Ingresso> ingressos = new ArrayList<>();

    public ListaIngresso() {}

    public static synchronized ListaIngresso getInstancia() {
        if (instancia == null) {
            instancia = new ListaIngresso();
        }
        return instancia;
    }

    public boolean cadastrar(Ingresso ingresso) {
        if (ingresso == null || ingresso.getVisitante() == null) {
            return false;
        }
        return ingressos.add(ingresso);
    }

    public ArrayList<Ingresso> listarTodos() {
        return ingressos;
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
