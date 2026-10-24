package parkAdventure.model.atracao;

import parkAdventure.model.pessoa.Operador;
import parkAdventure.model.pessoa.Visitante;

public class Infantil extends Atracao {
    private int alturaMaxima;
    private int idadeMaxima;

    public Infantil(String nome, int capacidadeMaxima, Operador operador, int alturaMaxima, int idadeMaxima) {
        super(nome, capacidadeMaxima, operador, 0, 0);
        this.alturaMaxima = alturaMaxima;
        this.idadeMaxima = idadeMaxima;
    }

    // Na atração infantil o limite é máximo
    @Override
    protected boolean atendeRequisitos(Visitante visitante) {
        return visitante.getIdade() <= idadeMaxima && visitante.getAltura() <= alturaMaxima;
    }

    public int getAlturaMaxima() { return alturaMaxima; }
    public void setAlturaMaxima(int alturaMaxima) { this.alturaMaxima = alturaMaxima; }

    public int getIdadeMaxima() { return idadeMaxima; }
    public void setIdadeMaxima(int idadeMaxima) { this.idadeMaxima = idadeMaxima; }
}
