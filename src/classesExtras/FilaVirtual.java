package classesExtras;

import java.util.LinkedList;
import java.util.Queue;
import parkAdventure.model.pessoa.Visitante;

public class FilaVirtual {
    private int capacidadeFila = 0;
    private int capacidadeMaximaFila;
    private int capacidadeMaxima;
    private final Queue<Visitante> fila;//usa uma fila para gerir os visitantes que estão esperando para entrar na atração
    public FilaVirtual() {//estilo primeiro a entrar, primeiro a sair (FIFO)
        fila = new LinkedList<>();//cria uma fila virtual usando o LinkedList
    }
    public void adicionar(Visitante visitante) {
        if (capacidadeMaximaFila < capacidadeMaxima ) {
            fila.add(visitante);
            capacidadeFila++;
        } else if (capacidadeMaximaFila > capacidadeMaxima) {
            System.out.println("Fila está cheia");
        }
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
