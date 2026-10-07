package classesExtras;

import parkAdventure.Visitante;

import java.time.LocalDate;

public class Responsavel extends Pessoa {
    private String cpf;
    private Visitante visitante;

    public Responsavel(String nome, LocalDate nascimento, String cpf, Visitante visitante) {
        super(nome, nascimento);
        this.cpf = cpf;
        this.visitante = visitante;
    }
}