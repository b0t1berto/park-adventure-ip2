package model.atracao;
import model.pessoa.*;

public abstract class Atracao {
    private String nome;
    private int capacidadeMaxima;
    private Operador operador;
    // private int ocupacaoAtual;
    // private FilaVirtual filaVirtual;
    private int alturaMinima;
    private int idadeMinima;

    public Atracao(String nome, int capacidadeMaxima, Operador operador, int alturaMinima, int idadeMinima) {
        this.nome = nome;
        this.capacidadeMaxima = capacidadeMaxima;
        this.operador = operador;
        this.alturaMinima = alturaMinima;
        this.idadeMinima = idadeMinima;
        // this.filaVirtual = filaVirtual;
    }
     public Atracao(String nome, int capacidadeMaxima, Operador operador) {
        this.nome = nome;
        this.capacidadeMaxima = capacidadeMaxima;
        this.operador = operador;
     }
    //Registra a entrada de uma pessoa
    // public boolean entrar() {
    //     if (ocupacaoAtual < capacidadeMaxima) {
    //         ocupacaoAtual++;
    //         return true;
    //     }
    //     return false;
    // }

    //Registra a saída de uma pessoa
    // public void sair() {
    //     if (ocupacaoAtual > 0) {
    //         ocupacaoAtual--;
    //     }
    // }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getCapacidadeMaxima() { return capacidadeMaxima; }
    public void setCapacidadeMaxima(int capacidadeMaxima) { this.capacidadeMaxima = capacidadeMaxima; }

    public String getOperador() { return "O Operador do " + nome + " é o " + operador.getNome(); }
    public void setOperador(Operador operador) { this.operador = operador; }

    public String getTipo() { return this.getClass().getSimpleName(); }

    public String toString() {
        return getTipo() + "\nNome: " + nome + "\nCapacidade Maxima: " + capacidadeMaxima + "\nOperador: " + getOperador()
                + "\nAltura Mínima: " + alturaMinima + "cm" + "\nIdade Mínima: " + idadeMinima;
    }
    
    public int getAlturaMinima() {
        return alturaMinima;
    }
    public void setAlturaMinima(int alturaMinima) {
        this.alturaMinima = alturaMinima;
    }
    public int getIdadeMinima() {
        return idadeMinima;
    }
    public void setIdadeMinima(int idadeMinima) {
        this.idadeMinima = idadeMinima;
    }
    

    // public FilaVirtual getFilaVirtual() { return filaVirtual; }
    // public void setFilaVirtual(FilaVirtual filaVirtual) { this.filaVirtual = filaVirtual; }

    // public int getOcupacaoAtual() { return ocupacaoAtual; }
    // public void setOcupacaoAtual(int ocupacaoAtual) { this.ocupacaoAtual = ocupacaoAtual; }

    

    // public String brincar() {
    //     return "Estou me divertindo muito!";
    // }


}

//     public boolean podeEntrar(Visitante visitante) {
//         return true;
//     }
// }
