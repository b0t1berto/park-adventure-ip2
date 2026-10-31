package com.ufrpe.parkadventure.javafx_parkadventure.controller;

import java.time.LocalDate;

import com.ufrpe.parkadventure.javafx_parkadventure.model.api.Feriados;
import com.ufrpe.parkadventure.javafx_parkadventure.model.ingresso.Ingresso;
import com.ufrpe.parkadventure.javafx_parkadventure.model.ingresso.TipoIngresso;
import com.ufrpe.parkadventure.javafx_parkadventure.model.pessoa.Visitante;
import com.ufrpe.parkadventure.javafx_parkadventure.repository.ListaIngresso;

// Emissão de ingressos com acréscimo de preço em feriados
public class ControleIngresso {
    private final ListaIngresso ingressos;
    private final double precoDiaria;
    private final double precoPasseAnual;
    private final double acrescimoFeriadoPercentual;

    public ControleIngresso(ListaIngresso ingressos, double precoDiaria,
                            double precoPasseAnual, double acrescimoFeriadoPercentual) {
        this.ingressos = ingressos;
        this.precoDiaria = precoDiaria;
        this.precoPasseAnual = precoPasseAnual;
        this.acrescimoFeriadoPercentual = acrescimoFeriadoPercentual;
    }

    // Se a API estiver fora do ar considera que não é feriado
    public boolean ehFeriado(LocalDate data) {
        try {
            String dataTexto = data.toString();
            return Feriados.buscarFeriadosBrasilAPI(data.getYear()).stream()
                    .anyMatch(f -> f.date().equals(dataTexto));
        } catch (Exception e) {
            return false;
        }
    }

    public Ingresso emitirIngresso(Visitante visitante, TipoIngresso tipo, LocalDate dataCompra) {
        return emitirIngresso(visitante, tipo, dataCompra, tipo == TipoIngresso.DIARIA && ehFeriado(dataCompra));
    }

    // Para quem já consultou a API ou quer simular um feriado
    public Ingresso emitirIngresso(Visitante visitante, TipoIngresso tipo, LocalDate dataCompra, boolean feriado) {
        double valor;
        LocalDate validade;
        if (tipo == TipoIngresso.PASSE_ANUAL) {
            valor = precoPasseAnual;
            validade = dataCompra.plusYears(1);
        } else {
            valor = precoDiaria;
            validade = dataCompra;
            if (feriado) {
                valor += valor * acrescimoFeriadoPercentual / 100.0;
            }
        }
        Ingresso ingresso = new Ingresso(visitante, tipo, dataCompra, validade, valor);
        ingressos.cadastrar(ingresso);
        return ingresso;
    }
}
