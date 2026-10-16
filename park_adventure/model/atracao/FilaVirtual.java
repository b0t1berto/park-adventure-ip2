package park_adventure.model.atracao;

import java.util.LinkedList;
import java.util.Queue;
import park_adventure.model.pessoa.Visitante;

// Fila FIFO (primeiro a entrar, primeiro a sair)
public class FilaVirtual {
    private final int capacidadeMaxima;
    private final Queue<Visitante> fila = new LinkedList<>();

    public FilaVirtual(int capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
    }

    // Retorna false quando está cheia
    public boolean adicionar(Visitante visitante) {
        if (fila.size() >= capacidadeMaxima) {
            return false;
        }
        return fila.add(visitante);
    }

    public Visitante proximo() { return fila.poll(); }
    public int tamanho() { return fila.size(); }
    public boolean estaVazia() { return fila.isEmpty(); }
    public int getCapacidadeMaxima() { return capacidadeMaxima; }
}
