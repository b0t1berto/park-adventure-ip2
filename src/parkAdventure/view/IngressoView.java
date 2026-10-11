package parkAdventure.view;

import parkAdventure.model.ingresso.Ingresso;

public class IngressoView {

    public void exibirIngresso(Ingresso ingresso, boolean feriado) {
        if (feriado) {
            System.out.println("*** ATENÇÃO: a data escolhida é feriado nacional (acréscimo aplicado) ***");
        }
        System.out.println("Ingresso " + ingresso.getTipoIngresso()
                + " | Visitante: " + ingresso.getVisitante().getNome()
                + " | Data: " + ingresso.getDataCompra()
                + " | Validade: " + ingresso.getDataValidade());
        System.out.printf("Valor: R$ %.2f%n", ingresso.getValor());
    }
}
