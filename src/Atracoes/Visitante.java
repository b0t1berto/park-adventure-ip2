package Atracoes;

public class Visitante {
    private String nome;
    private int idade;
    private int altura;

    public Visitante(String nome,int idade, int altura) {
        this.nome = nome;
        this.idade = idade;
        this.altura = altura;
    }



    // Getters and Setters
    public String getNome() {
        return nome;
    }
    public int getIdade() {
        return idade;
    }
    public int getAltura() {
        return altura;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public void setIdade(int idade) {
        this.idade = idade;
    }
    public void setAltura(int altura) {
        this.altura = altura;
    }

}

