package model.pessoa;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
// import atracao.*;

public class Pessoa {
    private String nome;
    private String dataNascimentoTexto;
    private LocalDate dataNascimento;
    private int idade;
    private String cpf;

    public Pessoa(String nome,String dataNascimentoTexto,String cpf) {
        this.nome = nome;
        this.dataNascimentoTexto = dataNascimentoTexto;
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.dataNascimento = LocalDate.parse(dataNascimentoTexto, formatador);
        this.idade = Period.between(dataNascimento, LocalDate.now()).getYears();
        this.cpf = cpf;
    }

    public int getIdade() {
        return idade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

}
