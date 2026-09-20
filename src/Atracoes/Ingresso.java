package Atracoes;

import java.time.LocalDate;

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

