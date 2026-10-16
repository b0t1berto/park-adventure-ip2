package park_adventure.view;

import park_adventure.model.atracao.Atracao;
import park_adventure.model.atracao.Infantil;

public class AtracaoView {

    public void exibirDetalhes(Atracao atracao) {
        System.out.println(atracao.getTipo());
        System.out.println("Nome: " + atracao.getNome());
        System.out.println("Capacidade Máxima: " + atracao.getCapacidadeMaxima());
        if (atracao.temOperador()) {
            System.out.println("Operador: " + atracao.getOperador().getNome());
        } else {
            System.out.println("Operador: (nenhum vinculado)");
        }
        if (atracao instanceof Infantil infantil) {
            System.out.println("Altura Máxima: " + infantil.getAlturaMaxima() + "cm");
            System.out.println("Idade Máxima: " + infantil.getIdadeMaxima());
        } else {
            System.out.println("Altura Mínima: " + atracao.getAlturaMinima() + "cm");
            System.out.println("Idade Mínima: " + atracao.getIdadeMinima());
        }
    }

    public void exibirOcupacao(Atracao atracao) {
        System.out.println(atracao.getNome() + ": " + atracao.getOcupacaoAtual()
                + "/" + atracao.getCapacidadeMaxima() + " (fila: " + atracao.getFilaVirtual().tamanho() + ")");
    }
}
