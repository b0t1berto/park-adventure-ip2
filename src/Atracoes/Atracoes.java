package Atracoes;

abstract class Atracao {
    private String nome;
    private int capacidadeMaxima;
    private int alturaMinima;
    private int idadeMinima;
    private Operador operador;
    private int ocupacaoAtual;
    private FilaVirtual filaVirtual;

    public Atracao(String nome, int capacidadeMaxima, int alturaMinima, int idadeMinima, Operador operador, FilaVirtual filaVirtual) {
        this.nome = nome;
        this.capacidadeMaxima = capacidadeMaxima;
        this.alturaMinima = alturaMinima;
        this.idadeMinima = idadeMinima;
        this.operador = operador;
        this.filaVirtual = filaVirtual;
        this.ocupacaoAtual = 0;
    }

    //Verifica se o visitante cumpre os requisitos
    public boolean podeEntrar(Visitante visitante) {
        return visitante.getAltura() >= alturaMinima && visitante.getIdade() >= idadeMinima;
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

    public int getAlturaMinima() { return alturaMinima; }
    public void setAlturaMinima(int alturaMinima) { this.alturaMinima = alturaMinima; }

    public int getIdadeMinima() { return idadeMinima; }
    public void setIdadeMinima(int idadeMinima) { this.idadeMinima = idadeMinima; }

    public Operador getOperador() { return operador; }
    public void setOperador(Operador operador) { this.operador = operador; }

    public FilaVirtual getFilaVirtual() { return filaVirtual; }
    public void setFilaVirtual(FilaVirtual filaVirtual) { this.filaVirtual = filaVirtual; }

    public int getOcupacaoAtual() { return ocupacaoAtual; }
    public void setOcupacaoAtual(int ocupacaoAtual) { this.ocupacaoAtual = ocupacaoAtual; }
}
