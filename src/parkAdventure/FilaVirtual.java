package parkadventure;

import java.util.LinkedList;
import java.util.Queue;
import parkadventure.Atracao;

public class FilaVirtual {
    private int capacidadeFila;
    private int capacidadeMaximaFila;
    private final Queue<Visitante> fila;//usa uma fila para gerir os visitantes que estão esperando para entrar na atração
    capacidadeMaximaFila = capacidadeMaxima;
    capacidadeFila = 0; 
    public FilaVirtual() {//estilo primeiro a entrar, primeiro a sair (FIFO)
        fila = new LinkedList<>();//cria uma fila virtual usando o LinkedList
    }
    public void adicionar(Visitante visitante) {
        if (capacidadeMaximaFila < capacidadeMaxima ) {
            fila.add(visitante);
            capacidadeFila++;
        } else if (capacidadeMaximaFila => capacidadeMaxima) {
            system.out.println('Fila está cheia')
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
