package com.ufrpe.parkadventure.service;

import com.ufrpe.parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.model.ingresso.Ingresso;
import com.ufrpe.parkadventure.model.ingresso.TipoIngresso;
import com.ufrpe.parkadventure.model.pessoa.Visitante;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Passe anual: dá direito a UMA entrada por mês (12 no total). A cada mês o visitante escolhe
 * em que dia vai usar o passe. Mês sem dia escolhido que passa é perdido (não acumula).
 */
public class ServicoPasseAnual {

    public static final int MESES_DO_PASSE = 12;

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public enum SituacaoMes { DISPONIVEL, AGENDADO, USADO, PERDIDO }

    // Um dos 12 meses do passe: o dia escolhido (null se ainda não escolheu) e a situação
    public record MesPasse(YearMonth mes, LocalDate dia, SituacaoMes situacao) { }

    private final ServicoIngresso servicoIngresso;
    private final ServicoCapacidade servicoCapacidade;
    private final ServicoAtracao servicoAtracao;

    public ServicoPasseAnual(ServicoIngresso servicoIngresso, ServicoCapacidade servicoCapacidade,
                             ServicoAtracao servicoAtracao) {
        this.servicoIngresso = servicoIngresso;
        this.servicoCapacidade = servicoCapacidade;
        this.servicoAtracao = servicoAtracao;
    }

    /** Passe anual ainda dentro da validade (o mais recente); null se o visitante não tem. */
    public Ingresso passeAtivo(Visitante visitante) {
        if (visitante == null) {
            return null;
        }
        LocalDate hoje = LocalDate.now();
        Ingresso ativo = null;
        for (Ingresso ingresso : servicoIngresso.listarPorVisitante(visitante.getNome())) {
            if (ingresso.getTipoIngresso() == TipoIngresso.PASSE_ANUAL && ingresso.valido(hoje)) {
                ativo = ingresso;
            }
        }
        return ativo;
    }

    /** Os 12 meses do passe, a partir do mês da compra, com o dia escolhido em cada um. */
    public List<MesPasse> meses(Ingresso passe) {
        List<MesPasse> meses = new ArrayList<>();
        if (passe == null) {
            return meses;
        }
        LocalDate hoje = LocalDate.now();
        YearMonth primeiro = YearMonth.from(passe.getDataCompra());
        for (int i = 0; i < MESES_DO_PASSE; i++) {
            YearMonth mes = primeiro.plusMonths(i);
            LocalDate dia = passe.diaPasseNoMes(mes);
            SituacaoMes situacao;
            if (dia != null) {
                situacao = dia.isBefore(hoje) ? SituacaoMes.USADO : SituacaoMes.AGENDADO;
            } else {
                situacao = mes.isBefore(YearMonth.from(hoje)) ? SituacaoMes.PERDIDO : SituacaoMes.DISPONIVEL;
            }
            meses.add(new MesPasse(mes, dia, situacao));
        }
        return meses;
    }

    /** Motivo pelo qual o passe NÃO pode ser usado nesse dia; null se pode. */
    public String motivoRecusa(Ingresso passe, LocalDate dia) {
        if (passe == null) {
            return "Você não tem um passe anual ativo.";
        }
        if (dia == null) {
            return "Escolha o dia da visita.";
        }
        LocalDate hoje = LocalDate.now();
        if (dia.isBefore(hoje)) {
            return "Esse dia já passou.";
        }
        if (dia.isBefore(passe.getDataCompra()) || dia.isAfter(passe.getDataValidade())) {
            return "O passe só vale de " + passe.getDataCompra().format(DATA)
                    + " até " + passe.getDataValidade().format(DATA) + ".";
        }
        YearMonth mes = YearMonth.from(dia);
        YearMonth primeiro = YearMonth.from(passe.getDataCompra());
        if (mes.isBefore(primeiro) || !mes.isBefore(primeiro.plusMonths(MESES_DO_PASSE))) {
            return "Esse mês não faz parte das 12 entradas do seu passe.";
        }
        LocalDate jaEscolhido = passe.diaPasseNoMes(mes);
        if (jaEscolhido != null) {
            return jaEscolhido.equals(dia)
                    ? "Esse dia já está escolhido."
                    : "Você já escolheu o dia " + jaEscolhido.format(DATA)
                    + " para este mês. O passe dá direito a 1 entrada por mês.";
        }
        if (servicoCapacidade.estaEsgotado(dia)) {
            return servicoCapacidade.mensagemEsgotado(dia);
        }
        return null;
    }

    /** Usa a entrada do mês no dia escolhido. Devolve null se deu certo, ou o motivo da recusa. */
    public String usar(Ingresso passe, LocalDate dia) {
        String motivo = motivoRecusa(passe, dia);
        if (motivo != null) {
            return motivo;
        }
        passe.adicionarDiaPasse(dia);
        return null;
    }

    /**
     * Desfaz a escolha de um dia que ainda não chegou (a entrada do mês volta a ficar livre) e cancela as
     * reservas de fura-fila que o visitante tinha para esse dia. Devolve null se deu certo, ou o motivo.
     */
    public String cancelar(Ingresso passe, LocalDate dia) {
        if (passe == null || dia == null || !passe.temDiaPasse(dia)) {
            return "Esse dia não está escolhido no seu passe.";
        }
        if (!dia.isAfter(LocalDate.now())) {
            return "Só dá para trocar um dia que ainda não chegou.";
        }
        passe.removerDiaPasse(dia);
        for (Atracao atracao : servicoAtracao.listarTodas()) {
            atracao.getFilaVirtual().cancelar(passe.getVisitante(), dia);
        }
        return null;
    }
}
