package parkAdventure;

import classesExtras.FilaVirtual;
import classesExtras.Responsavel;

public class Infantil extends Atracao {
    private Responsavel responsavel;
    public Infantil(int capacidadeMaxima, FilaVirtual filaVirtual, String nome, int ocupacaoAtual, Operador operador) {
        super(capacidadeMaxima, filaVirtual, nome, ocupacaoAtual, operador);
    }
}
