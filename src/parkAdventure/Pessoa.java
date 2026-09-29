package parkadventure;

import java.time.LocalDate;
import java.time.Period;

public abstract class Pessoa {
    private String nome;
    private LocalDate nascimento;
    private int altura;
    
    public Visitante(String nome, LocalDate nascimento, int altura) {
        this.nome = nome;
        this.nascimento = nascimento;
        this.altura = altura;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getIdade() { return Period.between(nascimento, LocalDate.now()).getYears(); }
    public void setnascimento(LocalDate nascimento) { this.nascimento = dataNascimento; }
    
    public int getAltura() { return altura; }
    public void setAltura(int altura) { this.altura = altura; }
}
