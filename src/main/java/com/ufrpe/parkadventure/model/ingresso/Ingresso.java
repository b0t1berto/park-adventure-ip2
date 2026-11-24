package com.ufrpe.parkadventure.model.ingresso;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import com.ufrpe.parkadventure.model.pessoa.Visitante;

public class Ingresso {
    private Visitante visitante;
    private TipoIngresso tipoIngresso;
    private LocalDate dataCompra;
    private LocalDate dataValidade;
    private double valor;
    private String formaPagamento;
    // Passe anual: os dias que o visitante escolheu para usar o passe (no máximo 1 por mês)
    private final TreeSet<LocalDate> diasPasse = new TreeSet<>();

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

    // ---- Passe anual: 1 entrada por mês, no dia que o visitante escolher
    public SortedSet<LocalDate> getDiasPasse() { return Collections.unmodifiableSortedSet(diasPasse); }

    public boolean temDiaPasse(LocalDate dia) { return dia != null && diasPasse.contains(dia); }

    public boolean adicionarDiaPasse(LocalDate dia) { return dia != null && diasPasse.add(dia); }

    public boolean removerDiaPasse(LocalDate dia) { return diasPasse.remove(dia); }

    // Dia escolhido para o mês informado (null se o mês ainda não foi usado)
    public LocalDate diaPasseNoMes(YearMonth mes) {
        for (LocalDate dia : diasPasse) {
            if (YearMonth.from(dia).equals(mes)) {
                return dia;
            }
        }
        return null;
    }

    public Visitante getVisitante() { return visitante; }
    public void setVisitante(Visitante visitante) { this.visitante = visitante; }

    public TipoIngresso getTipoIngresso() { return tipoIngresso; }
    public void setTipoIngresso(TipoIngresso tipoIngresso) { this.tipoIngresso = tipoIngresso; }

    public LocalDate getDataCompra() { return dataCompra; }
    public void setDataCompra(LocalDate dataCompra) { this.dataCompra = dataCompra; }

    public LocalDate getDataValidade() { return dataValidade; }
    public void setDataValidade(LocalDate dataValidade) { this.dataValidade = dataValidade; }

    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
}
