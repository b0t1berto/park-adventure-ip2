package parkAdventure.model.ingresso;

import java.time.LocalDate;
import parkAdventure.model.pessoa.Visitante;

public class Ingresso {
    private Visitante visitante;
    private TipoIngresso tipoIngresso;
    private LocalDate dataCompra;
    private LocalDate dataValidade;
    private double valor;

    public Ingresso(Visitante visitante, TipoIngresso tipoIngresso, LocalDate dataCompra,
                    LocalDate dataValidade, double valor) {
        this.visitante = visitante;
        this.tipoIngresso = tipoIngresso;
        this.dataCompra = dataCompra;
        this.dataValidade = dataValidade;
        this.valor = valor;
    }

    // Verifica se o ingresso continua válido na data informada
    public boolean valido(LocalDate dataAtual) {
        return !dataAtual.isAfter(dataValidade);
    }

    public Visitante getVisitante() { return visitante; }
    public void setVisitante(Visitante visitante) { this.visitante = visitante; }

    public TipoIngresso getTipoIngresso() { return tipoIngresso; }
    public void setTipoIngresso(TipoIngresso tipoIngresso) { this.tipoIngresso = tipoIngresso; }

    public LocalDate getDataCompra() { return dataCompra; }
    public void setDataCompra(LocalDate dataCompra) { this.dataCompra = dataCompra; }

    public LocalDate getDataValidade() { return dataValidade; }
    public void setDataValidade(LocalDate dataValidade) { this.dataValidade = dataValidade; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
}
