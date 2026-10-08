package parkAdventure.model.atracao;

import parkAdventure.model.pessoa.Visitante;

public class Aquatico extends Atracao implements IRestricao {

    public Aquatico(String nome, int capacidadeMaxima, parkAdventure.model.pessoa.Operador operador, int alturaMinima, int idadeMinima) {
        super();
    }

    public boolean verificadorPodeEntrar(Visitante visitante) {
        if (visitante.getIdade() < getIdadeMinima() || visitante.getAltura() < getAlturaMinima()) {
            System.out.println("O visitante " + visitante.getNome() + " não atinge os requesítos mínimos!");
        } else if (visitante.getIdade() < 18 && !visitante.temResponsavel()) {
            System.out.println("O visitante  " + visitante.getNome() + " não atinge os requesítos mínimos!");
        } else {
            System.out.println("Tudo certo, " + visitante.getNome() + " pode avançar!");
            visitante.setVariavelDeControle();
        }
        return false;
    }

    public void entrar(Visitante visitante) {
        if (visitante.getVariavlDeControle() == false) {
            System.out.println(visitante.getNome() + " não pode entrar nesta Atração!");
        } else {
            System.out.println("Aproveite o Brinquedo " + visitante.getNome() + "!");
        }
    }
}
