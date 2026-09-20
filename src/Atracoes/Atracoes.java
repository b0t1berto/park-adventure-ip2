package Atracoes;

public class Atracoes {
    private String nome;
    private int capacidadeMaxima;
    private int alturaMinima;
    private int idadeMinima;

    public Atracoes(String nome, int capacidadeMaxima, int alturaMinima, int idadeMinima) {

    }

    public String getNome() {
        return nome;
    }

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public int getAlturaMinima() {
        return alturaMinima;
    }

    public int getIdadeMinima() {
        return idadeMinima;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCapacidadeMaxima(int capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public void setAlturaMinima(int alturaMinima) {
        this.alturaMinima = alturaMinima;
    }

    public void setIdadeMinima(int idadeMinima) {
        this.idadeMinima = idadeMinima;
    }

}
