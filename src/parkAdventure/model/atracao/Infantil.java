package parkAdventure.model.atracao;

import parkAdventure.model.pessoa.Operador;
import parkAdventure.model.pessoa.Visitante;

public class Infantil extends Atracao implements IRestricao {
    private parkAdventure.model.pessoa.Responsavel responsavel;
    private int alturaMaxima;
    private int idadeMaxima;

    public Infantil(String nome, int capacidadeMaxima, Operador operador, int alturaMaxima, int idadeMaxima) {
        super(nome, capacidadeMaxima, operador);
        this.alturaMaxima = alturaMaxima;
        this.idadeMaxima = idadeMaxima;
    }

    public int getAlturaMaxima() {
        return alturaMaxima;
    }

    public void setAlturaMaxima(int alturaMaxima) {
        this.alturaMaxima = alturaMaxima;
    }

    public int getIdadeMaxima() {
        return idadeMaxima;
    }

    public void setIdadeMaxima(int idadeMaxima) {
        this.idadeMaxima = idadeMaxima;
    }

    public String toString() {
        return getTipo() + "\nNome: " + getNome() + "\nCapacidadeMaxima: " + getCapacidadeMaxima() + "\nOperador: " + getOperador()
                + "\nAltura Máxima: " + getAlturaMaxima() + "cm" + "\nIdade Máxima: " + getIdadeMaxima();
    }

    public boolean verificadorPodeEntrar(Visitante visitante) {
        if (visitante.getIdade() > getIdadeMaxima() || visitante.getAltura() > getAlturaMaxima()) {
            System.out.println("O visitante " + visitante.getNome() + " não cumpre os requesítos mínimos!");
        } else if (visitante.getIdade() < 18 && !visitante.temResponsavel()) {
            System.out.println("O visitante  " + visitante.getNome() + " não cumpre os requesítos mínimos!");
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
