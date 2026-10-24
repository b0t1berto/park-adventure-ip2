package park_adventure.model.atracao;

import park_adventure.model.pessoa.Operador;

public class Aquatico extends Atracao {
    public Aquatico(String nome, int capacidadeMaxima, Operador operador, int alturaMinima, int idadeMinima) {
        super(nome, capacidadeMaxima, operador, alturaMinima, idadeMinima);
    }
}
