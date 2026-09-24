package ParkAdventure;

import java.util.LinkedList;
import java.util.Queue;

public class FilaVirtual {
    private final Queue<Visitante> fila;//usa uma fila para gerenciar os visitantes que estão esperando para entrar na atração
    public FilaVirtual() {//estilo primeiro a entrar, primeiro a sair (FIFO)
        fila = new LinkedList<>();//cria uma fila virtual usando o LinkedList
    }
    public void adicionar(Visitante visitante) {
        fila.add(visitante);
    }

    public Visitante proximo() {
        return fila.poll();
    }

    public int tamanho() {
        return fila.size();
    }

    public boolean estaVazia() {
        return fila.isEmpty();
    }
}
