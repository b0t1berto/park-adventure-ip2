package parkAdventure.model.pessoa;

public class Visitante extends Pessoa {
    private int altura;
    private Responsavel responsavel;
    private boolean variavelDeControle;

    public Visitante (String nome, String dataNascimentoTexto, String cpf, int altura) {
        super(nome, dataNascimentoTexto, cpf);   
        this.altura = altura;
    }
    
    public int getAltura() {
        return altura;
    }

    public void setResponsavel(Responsavel responsavel) {
        this.responsavel = responsavel;
    }

    public String getResponsavel() {
        return this.responsavel.getNome();
    }

    public boolean temResponsavel() {
        return this.responsavel != null;
    }

    public void setVariavelDeControle() {
        if (variavelDeControle == true) {
            variavelDeControle = false;
        } else {
            variavelDeControle = true;
        }
    } 

    public boolean getVariavlDeControle() {
        return this.variavelDeControle;
    }

    public void verificadorResponsavel () {
        if (getIdade() < 18) {
            if (!temResponsavel()) {
                System.out.println("Você precisa de um Responsável!");
            } else {
                System.out.println("Tudo certo, aproveite bastante!");
            }
        }
    }
}