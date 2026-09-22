package Atracoes;

import java.time.LocalDate;

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
    public boolean valido(LocalDate dataAtual) {
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
