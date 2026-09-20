public class Atracoes {
    private String nome;
    private int capacidadeMaxima;
    private int alturaMinima;
    private int idadeMinima;

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

public class Radical extends Atracoes {
    
}

public class Aquatico extends Atracoes {
    
}

public class Infantil extends Atracoes {
    
}

public class Visitante {
    private String nome;
    private int idade;
    private int altura;
    
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

public static void main(String[] args) {

}
