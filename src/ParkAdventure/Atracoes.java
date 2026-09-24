package ParkAdventure;

public abstract class Atracoes {
    private String nome;
    private int capacidadeMaxima;
    private Operador operador;
    private int ocupacaoAtual;
    private FilaVirtual filaVirtual;

    public Atracoes(String nome, int capacidadeMaxima, Operador operador, FilaVirtual filaVirtual) {
        this.nome = nome;
        this.capacidadeMaxima = capacidadeMaxima;
        this.operador = operador;
        this.filaVirtual = filaVirtual;
        this.ocupacaoAtual = 0;
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
