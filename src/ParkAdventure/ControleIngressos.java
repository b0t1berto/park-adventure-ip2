package ParkAdventure;

import java.util.ArrayList;
import java.util.List;

class ControleIngressos {
    private List<Ingresso> ingressos;
    public ControleIngressos() {
        this.ingressos = new ArrayList<>();
    }
    public void adicionarIngresso(Ingresso ingresso) {
        ingressos.add(ingresso);
    }
    public void removerIngresso(Ingresso ingresso) {
        ingressos.remove(ingresso);
    }
    public List<Ingresso> getIngressos() {
        return ingressos;
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
