public class Operador {
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

public abstract class Atracoes {
    private String nome;
    private int capacidadeMaxima;
    private int alturaMinima;
    private int idadeMinima;
    private int ocupacaoAtual;

    public Atracoes(String nome, int capacidadeMaxima, int alturaMinima, int idadeMinima) {
        this.nome = nome;
        this.capacidadeMaxima = capacidadeMaxima;
        this.alturaMinima = alturaMinima;
        this.idadeMinima = idadeMinima;
        this.ocupacaoAtual = 0;
    }

    // Getters and Setters
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
    public int getOcupacaoAtual() {
        return ocupacaoAtual;
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
    public void setOcupacaoAtual(int ocupacaoAtual) {
        this.ocupacaoAtual = ocupacaoAtual;
    }

}

public class Radical extends Atracoes {
    public Radical(String nome, int capacidadeMaxima, int alturaMinima, int idadeMinima) {
        super(nome, capacidadeMaxima, alturaMinima, idadeMinima);
    }
}

public class Aquatico extends Atracoes {
    public Aquatico(String nome, int capacidadeMaxima, int alturaMinima, int idadeMinima) {
        super(nome, capacidadeMaxima, alturaMinima, idadeMinima);
    }   
}

public class Infantil extends Atracoes {
    public Infantil(String nome, int capacidadeMaxima, int alturaMinima, int idadeMinima) {
        super(nome, capacidadeMaxima, alturaMinima, idadeMinima);
    }
}

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

public class Ingresso {
    private Visitante visitante;
    private String tipoIngresso;
    private LocalDate dataCompra;
    public LocalDate dataValidade;

    public Ingresso(Visitante visitante, String tipoIngresso, LocalDate dataCompra, LocalDate dataValidade) {
        this.visitante = visitante;
        this.tipoIngresso = tipoIngresso;
        this.dataCompra = dataCompra;
        this.dataValidade = dataValidade;
    }

    // Getters and Setters
    public Visitante getVisitante() {
        return visitante;
    }
    public String getTipoIngresso() {
        return tipoIngresso;
    }
    public LocalDate getDataCompra() {
        return dataCompra;
    }
    public LocalDate getDataValidade() {
        return dataValidade;
    }
    public void setVisitante(Visitante visitante) {
        this.visitante = visitante;
    }
    public void setTipoIngresso(String tipoIngresso) {
        this.tipoIngresso = tipoIngresso;
    }
    public void setDataCompra(LocalDate dataCompra) {
        this.dataCompra = dataCompra;
    }
    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }
}

public static void main(String[] args) {

}
