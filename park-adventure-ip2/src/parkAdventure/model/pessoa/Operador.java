package model.pessoa;

public class Operador extends Pessoa {
    private String cargo;

    public Operador (String nome, String dataNascimentoTexto, String cpf, String cargo) {
        super(nome, dataNascimentoTexto, cpf);
        this.cargo = cargo;
    }

    public void verificadorCargo() {
        if (cargo != null) {
            System.err.println("O funcionário " + getNome() + " está empregado!");
        } else {
            System.out.println("O funcionário " + getNome() + " não está empregado!");
        }
    }

    public String getCargo() {
        return cargo;
    }

    

}