package parkAdventure;

import classesExtras.Pessoa;

import java.time.LocalDate;

public class Visitante extends Pessoa {
    public Visitante (String nome, LocalDate nascimento, int altura) {
        super(nome, nascimento, altura);   
    }
}