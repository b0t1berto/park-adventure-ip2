package parkAdventure;

import classesExtras.FilaVirtual;

public abstract class Atracao {
    private String nome;
    private int capacidadeMaxima;
    private Operador operador;
    private int ocupacaoAtual;
    private FilaVirtual filaVirtual;
    private double alturaMinima;
    private double idadeMinima;

    public Atracao(double alturaMinima, FilaVirtual filaVirtual, String nome, int ocupacaoAtual, Operador operador) {
        this.alturaMinima = alturaMinima;
        this.capacidadeMaxima = capacidadeMaxima;
        this.filaVirtual = filaVirtual;
        this.idadeMinima = idadeMinima;
        this.nome = nome;
        this.ocupacaoAtual = ocupacaoAtual;
        this.operador = operador;
    }

    //Registra a entrada de uma pessoa
    public boolean entrar() {
        if (ocupacaoAtual < capacidadeMaxima) {
            ocupacaoAtual++;
            return true;
        }
        return false;
    }

    //Registra a saída de uma pessoa
    public void sair() {
        if (ocupacaoAtual > 0) {
            ocupacaoAtual--;
        }
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getCapacidadeMaxima() { return capacidadeMaxima; }
    public void setCapacidadeMaxima(int capacidadeMaxima) { this.capacidadeMaxima = capacidadeMaxima; }

    public Operador getOperador() { return operador; }
    public void setOperador(Operador operador) { this.operador = operador; }

    public FilaVirtual getFilaVirtual() { return filaVirtual; }
    public void setFilaVirtual(FilaVirtual filaVirtual) { this.filaVirtual = filaVirtual; }

    public int getOcupacaoAtual() { return ocupacaoAtual; }
    public void setOcupacaoAtual(int ocupacaoAtual) { this.ocupacaoAtual = ocupacaoAtual; }

    public boolean podeEntrar(Visitante visitante) {
        return true;
    }
}
