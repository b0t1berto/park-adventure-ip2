import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// OPERADOR

class Operador {
    private String nome;

    public Operador(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}

// ATRAÇÃO E AS SUBCLASSES DE ATRAÇÃO

abstract class Atracao {
    private String nome;
    private int capacidadeMaxima;
    private int alturaMinima;
    private int idadeMinima;
    private Operador operador;
    private int ocupacaoAtual;

    public Atracao(String nome, int capacidadeMaxima, int alturaMinima, int idadeMinima, Operador operador) {
        this.nome = nome;
        this.capacidadeMaxima = capacidadeMaxima;
        this.alturaMinima = alturaMinima;
        this.idadeMinima = idadeMinima;
        this.operador = operador;
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

    public int getOcupacaoAtual() { return ocupacaoAtual; }
    public void setOcupacaoAtual(int ocupacaoAtual) { this.ocupacaoAtual = ocupacaoAtual; }
}

class Radical extends Atracao {
    public Radical(String nome, int capacidadeMaxima, int alturaMinima, int idadeMinima, Operador operador) {
        super(nome, capacidadeMaxima, alturaMinima, idadeMinima, operador);
    }
}

class Aquatico extends Atracao {
    public Aquatico(String nome, int capacidadeMaxima, int alturaMinima, int idadeMinima, Operador operador) {
        super(nome, capacidadeMaxima, alturaMinima, idadeMinima, operador);
    }
}

class Infantil extends Atracao {
    public Infantil(String nome, int capacidadeMaxima, int alturaMinima, int idadeMinima, Operador operador) {
        super(nome, capacidadeMaxima, alturaMinima, idadeMinima, operador);
    }
}

// VISITANTE

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


// INGRESSO

class Ingresso {
    private Visitante visitante;
    private String tipoIngresso;
    private LocalDate dataCompra;
    private LocalDate dataValidade;

    public Ingresso(Visitante visitante, String tipoIngresso, LocalDate dataCompra, LocalDate dataValidade) {
        this.visitante = visitante;
        this.tipoIngresso = tipoIngresso;
        this.dataCompra = dataCompra;
        this.dataValidade = dataValidade;
    }

    //Verifica se o ingresso ainda está válido
    public boolean isValido(LocalDate dataAtual) {
        return !dataAtual.isAfter(dataValidade);
    }

    public Visitante getVisitante() { return visitante; }
    public void setVisitante(Visitante visitante) { this.visitante = visitante; }

    public String getTipoIngresso() { return tipoIngresso; }
    public void setTipoIngresso(String tipoIngresso) { this.tipoIngresso = tipoIngresso; }

    public LocalDate getDataCompra() { return dataCompra; }
    public void setDataCompra(LocalDate dataCompra) { this.dataCompra = dataCompra; }

    public LocalDate getDataValidade() { return dataValidade; }
    public void setDataValidade(LocalDate dataValidade) { this.dataValidade = dataValidade; }
}

// CONTROLE DE INGRESSOS QUE SAIRAAM NO DIA

class ControleIngressos {
    private List<Ingresso> ingressos;

    public ControleIngressos() {
        this.ingressos = new ArrayList<>();
    }

    public void adicionarIngresso(Ingresso ingresso) {
        ingressos.add(ingresso);
    }

    public void removerIngresso(Ingresso ingresso) {
        ingressos.remove(ingresso);
    }

    public List<Ingresso> getIngressos() {
        return ingressos;
    }
}

public class Main {
    public static void main(String[] args) {
    }
}
