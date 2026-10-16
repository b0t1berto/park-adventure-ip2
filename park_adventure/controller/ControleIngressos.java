package park_adventure.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import park_adventure.model.ingresso.Ingresso;

public class ControleIngressos {
    private final List<Ingresso> ingressos = new ArrayList<>();

    public void adicionarIngresso(Ingresso ingresso) {
        ingressos.add(ingresso);
    }

    public void removerIngresso(Ingresso ingresso) {
        ingressos.remove(ingresso);
    }

    public List<Ingresso> getIngressos() {
        return new ArrayList<>(ingressos);
    }

    public int consultarQuantidadePorData(LocalDate data) {
        int count = 0;
        for (Ingresso ingresso : ingressos) {
            if (ingresso.getDataCompra().equals(data)) {
                count++;
            }
        }
        return count;
    }
}
