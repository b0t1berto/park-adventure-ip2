package parkAdventure.model.pessoa;

public class Operador extends Pessoa {
    private String cargo;

    public Operador(String nome, String dataNascimentoTexto, String cpf, String cargo) {
        super(nome, dataNascimentoTexto, cpf);
        this.cargo = cargo;
    }

    // O model só informa o estado
    public boolean estaEmpregado() {
        return cargo != null;
    }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
}
