package com.ufrpe.parkadventure.service;

import com.ufrpe.parkadventure.model.atracao.Atracao;

import java.time.LocalDate;

/**
 * Capacidade máxima do parque por dia: soma das capacidades de todas as atrações multiplicada por 5.
 * Quando o número de ingressos para um dia atinge esse limite, não se vendem mais ingressos para ele.
 */
public class ServicoCapacidade {

    // Quantas "rodadas" de visitantes o parque comporta em um dia
    public static final int FATOR_CAPACIDADE = 5;

    private final ServicoAtracao servicoAtracao;
    private final ServicoIngresso servicoIngresso;

    public ServicoCapacidade(ServicoAtracao servicoAtracao, ServicoIngresso servicoIngresso) {
        this.servicoAtracao = servicoAtracao;
        this.servicoIngresso = servicoIngresso;
    }

    /** Soma das capacidades das atrações x 5 (acompanha as atrações cadastradas/editadas no momento). */
    public int capacidadeMaximaDiaria() {
        int soma = 0;
        for (Atracao atracao : servicoAtracao.listarTodas()) {
            soma += atracao.getCapacidadeMaxima();
        }
        return soma * FATOR_CAPACIDADE;
    }

    /** Ingressos (diárias) já emitidos para visitar o parque na data. */
    public int ingressosParaDia(LocalDate dia) {
        return servicoIngresso.consultarQuantidadeParaVisita(dia);
    }

    public int vagasRestantes(LocalDate dia) {
        return Math.max(0, capacidadeMaximaDiaria() - ingressosParaDia(dia));
    }

    /** true quando a capacidade do dia foi atingida (inclui o caso de não haver atrações cadastradas). */
    public boolean estaEsgotado(LocalDate dia) {
        return !podeVender(dia, 1);
    }

    public boolean podeVender(LocalDate dia, int quantidade) {
        return ingressosParaDia(dia) + quantidade <= capacidadeMaximaDiaria();
    }

    public String mensagemEsgotado(LocalDate dia) {
        return "A capacidade máxima do parque para " + dia.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                + " (" + capacidadeMaximaDiaria() + " visitantes) foi atingida. Não há mais ingressos para este dia.";
    }
}
