package com.ufrpe.parkadventure.service;

import com.ufrpe.parkadventure.model.api.Feriados;
import com.ufrpe.parkadventure.model.ingresso.Ingresso;
import com.ufrpe.parkadventure.model.ingresso.TipoIngresso;
import com.ufrpe.parkadventure.model.pessoa.Visitante;

import java.time.LocalDate;

/**
 * Serviço de emissão de ingressos: cálculo de preço com acréscimo em feriados
 * e emissão de ingressos com validade adequada ao tipo.
 */
public class ServicoEmissaoIngresso {

    private final ServicoIngresso servicoIngresso;
    private final double precoDiaria;
    private final double precoPasseAnual;
    private final double acrescimoFeriadoPercentual;

    public ServicoEmissaoIngresso(ServicoIngresso servicoIngresso, double precoDiaria,
                                   double precoPasseAnual, double acrescimoFeriadoPercentual) {
        this.servicoIngresso = servicoIngresso;
        this.precoDiaria = precoDiaria;
        this.precoPasseAnual = precoPasseAnual;
        this.acrescimoFeriadoPercentual = acrescimoFeriadoPercentual;
    }

    /**
     * Verifica se uma data é feriado usando a API de feriados.
     */
    public boolean ehFeriado(LocalDate data) {
        try {
            String dataTexto = data.toString();
            return Feriados.buscarFeriadosBrasilAPI(data.getYear()).stream()
                    .anyMatch(f -> f.date().equals(dataTexto));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Emite um ingresso verificando automaticamente se é feriado.
     */
    public Ingresso emitirIngresso(Visitante visitante, TipoIngresso tipo, LocalDate dataCompra) {
        return emitirIngresso(visitante, tipo, dataCompra, tipo == TipoIngresso.DIARIA && ehFeriado(dataCompra));
    }

    /**
     * Calcula o valor de um ingresso considerando se é feriado.
     */
    public double calcularValor(TipoIngresso tipo, boolean feriado) {
        if (tipo == TipoIngresso.PASSE_ANUAL) {
            return precoPasseAnual;
        }
        double valor = precoDiaria;
        if (feriado) {
            valor += valor * acrescimoFeriadoPercentual / 100.0;
        }
        return valor;
    }

    public double getAcrescimoFeriadoPercentual() {
        return acrescimoFeriadoPercentual;
    }

    /**
     * Emite um ingresso com a informação de feriado já conhecida.
     */
    public Ingresso emitirIngresso(Visitante visitante, TipoIngresso tipo, LocalDate dataCompra, boolean feriado) {
        LocalDate validade = (tipo == TipoIngresso.PASSE_ANUAL) ? dataCompra.plusYears(1) : dataCompra;
        Ingresso ingresso = new Ingresso(visitante, tipo, dataCompra, validade, calcularValor(tipo, feriado));
        servicoIngresso.cadastrar(ingresso);
        return ingresso;
    }
}
