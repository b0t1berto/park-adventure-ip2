package Atracoes;

class Visitante {
    private String nome;
    private int idade;
    private int altura;

    public Visitante(String nome, int idade, int altura) {
        this.nome = nome;
        this.idade = idade;
        this.altura = altura;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getIdade() { return idade; }
    public void setIdade(int idade) { this.idade = idade; }

    public int getAltura() { return altura; }
    public void setAltura(int altura) { this.altura = altura; }
}
