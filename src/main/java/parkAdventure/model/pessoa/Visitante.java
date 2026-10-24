package parkAdventure.model.pessoa;

public class Visitante extends Pessoa {
    private static final int IDADE_MAIORIDADE = 18;

    private int altura;
    private Responsavel responsavel;

    public Visitante(String nome, String dataNascimentoTexto, String cpf, int altura) {
        super(nome, dataNascimentoTexto, cpf);
        this.altura = altura;
    }

    public int getAltura() { return altura; }
    public void setAltura(int altura) { this.altura = altura; }

    public Responsavel getResponsavel() { return responsavel; }
    public void setResponsavel(Responsavel responsavel) { this.responsavel = responsavel; }

    public boolean temResponsavel() {
        return responsavel != null;
    }

    public boolean ehMenorDeIdade() {
        return getIdade() < IDADE_MAIORIDADE;
    }

    // Menor de idade sem responsável não pode acessar atrações
    public boolean precisaDeResponsavel() {
        return ehMenorDeIdade() && !temResponsavel();
    }
}
