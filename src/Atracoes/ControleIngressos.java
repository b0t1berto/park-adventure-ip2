package Atracoes;

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
}
