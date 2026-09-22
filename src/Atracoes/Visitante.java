package Atracoes;

import java.time.LocalDate;
import java.time.Period;

class Visitante {
    private String nome;
    private LocalDate dataNascimento;
    private int altura;

    public Visitante(String nome, LocalDate dataNascimento, int altura) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.altura = altura;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getIdade() { return Period.between(dataNascimento, LocalDate.now()).getYears(); }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    
    public int getAltura() { return altura; }
    public void setAltura(int altura) { this.altura = altura; }
}
