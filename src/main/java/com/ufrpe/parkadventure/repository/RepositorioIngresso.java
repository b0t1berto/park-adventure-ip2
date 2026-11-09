package com.ufrpe.parkadventure.repository;
import java.time.LocalDate;
import java.util.ArrayList;
import com.ufrpe.parkadventure.model.ingresso.Ingresso;

public class RepositorioIngresso {
    private static RepositorioIngresso instancia;
    private static final ArrayList<Ingresso> ingressos = new ArrayList<>();
    
    private RepositorioIngresso() {}

    public static synchronized RepositorioIngresso getInstancia() {
        if (instancia == null) {
            instancia = new RepositorioIngresso();
        }
        return instancia;
    }

    public boolean cadastrar(Ingresso ingresso) {
        if (ingresso == null || ingresso.getVisitante() == null) {
            return false;
        }
        return ingressos.add(ingresso);
    }

    public ArrayList<Ingresso> listarPorVisitante(String visitante) {
        ArrayList<Ingresso> resultado = new ArrayList<>();
        for (Ingresso ingresso : ingressos) {
            if (ingresso.getVisitante().getNome() == visitante) {
                resultado.add(ingresso);
            }
        }
        return resultado;
    }

    // Ingresso mais recente do visitante (null se ele nunca comprou)
    public Ingresso buscarUltimo(String visitante) {
        Ingresso ultimo = null;
        for (Ingresso ingresso : ingressos) {
            if (ingresso.getVisitante().getNome() == visitante) {
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

    public ArrayList<Ingresso> listarTodos() {
        return ingressos;
    }
}
