package com.ufrpe.parkadventure.repository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.ufrpe.parkadventure.model.atracao.Aquatico;
import com.ufrpe.parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.model.atracao.EstadoReserva;
import com.ufrpe.parkadventure.model.atracao.Infantil;
import com.ufrpe.parkadventure.model.atracao.Radical;
import com.ufrpe.parkadventure.model.atracao.Reserva;
import com.ufrpe.parkadventure.model.ingresso.Ingresso;
import com.ufrpe.parkadventure.model.ingresso.TipoIngresso;
import com.ufrpe.parkadventure.repository.RepositorioEscalaTrabalho.Turno;

// Calcula os números das telas de relatório (Geral, Ingresso, Lucro e Atração).
// Não usa JavaFX: os controllers das telas só pedem os dados daqui e os mostram.
// Quem consulta a BrasilAPI é o controller; os feriados chegam prontos por parâmetro.
public class RelatorioGerencial {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    // =============================================
    // || REGISTROS (uma linha/resumo de cada tela)
    // =============================================

    // Aba "Geral"
    public record ResumoGeral(int visitantes, int ingressosEmitidos, double receitaTotal,
                              int ingressosHoje, double receitaHoje,
                              int atracoes, int atracoesSemOperador, int atracoesLotadas,
                              int ocupacaoAtual, int capacidadeTotal,
                              int reservasAtivasHoje, int operadoresEscaladosHoje) {
        // de 0.0 a 1.0 (serve direto no ProgressBar)
        public double fracaoOcupacao() {
            return capacidadeTotal == 0 ? 0 : (double) ocupacaoAtual / capacidadeTotal;
        }
    }

    // Aba "Ingresso": feriado é o nome do feriado, ou null se o dia não for feriado
    public record VendaDetalhada(LocalDate data, String visitante, TipoIngresso tipo,
                                 double valor, String formaPagamento, String feriado) {
        public boolean ehFeriado() { return feriado != null; }
    }

    public record RelatorioIngressos(List<VendaDetalhada> vendas, int quantidade, int diarias,
                                     int passesAnuais, double receitaTotal, double receitaFeriados,
                                     int vendasEmFeriados) {
        public double ticketMedio() {
            return quantidade == 0 ? 0 : receitaTotal / quantidade;
        }
    }

    // Aba "Lucro": uma linha por mês
    public record LinhaLucro(YearMonth mes, int ingressos, double receita, int turnos, double custo) {
        public double lucro() { return receita - custo; }

        // margem em % da receita (0 quando não houve receita)
        public double margem() { return receita > 0 ? lucro() / receita * 100 : 0; }
    }

    public record RelatorioLucro(List<LinhaLucro> meses) {
        public double receitaTotal() { return meses.stream().mapToDouble(LinhaLucro::receita).sum(); }
        public double custoTotal() { return meses.stream().mapToDouble(LinhaLucro::custo).sum(); }
        public double lucroTotal() { return receitaTotal() - custoTotal(); }
        public int ingressosTotal() { return meses.stream().mapToInt(LinhaLucro::ingressos).sum(); }
        public int turnosTotal() { return meses.stream().mapToInt(LinhaLucro::turnos).sum(); }
        public double margem() {
            double receita = receitaTotal();
            return receita > 0 ? lucroTotal() / receita * 100 : 0;
        }
    }

    // Aba "Dia": um ingresso que tem relação com o dia (comprado nele e/ou válido para a visita nele)
    public record LinhaDia(String visitante, TipoIngresso tipo, LocalDate compra, LocalDate validade,
                           double valor, String formaPagamento, boolean compradoNoDia, boolean visitaNoDia) {
        public String relacao() {
            if (compradoNoDia && visitaNoDia) return "Compra e visita";
            return compradoNoDia ? "Comprado neste dia" : "Visita neste dia";
        }
    }

    public record RelatorioDia(LocalDate dia, List<LinhaDia> ingressos,
                               int vendidosNoDia, double receitaNoDia,
                               int visitasPrevistas, int capacidadeMaxima,
                               int reservasFuraFila, int operadoresEscalados) {
        public int vagasRestantes() { return Math.max(0, capacidadeMaxima - visitasPrevistas); }

        // de 0.0 a 1.0 (serve direto no ProgressBar)
        public double fracaoOcupacao() {
            return capacidadeMaxima == 0 ? 1 : Math.min(1.0, (double) visitasPrevistas / capacidadeMaxima);
        }

        public boolean esgotado() { return visitasPrevistas >= capacidadeMaxima; }
    }

    // Aba "Atração"
    public record LinhaAtracao(String nome, String tipo, String operador, int capacidade, int ocupacao,
                               int reservas, int usadas, int noShows, int canceladas, String situacao) {
        public double percentualOcupacao() {
            return capacidade == 0 ? 0 : ocupacao * 100.0 / capacidade;
        }
    }

    // =============================================
    // || DEPENDÊNCIAS
    // =============================================
    private final RepositorioIngresso ingressos;
    private final RepositorioAtracao atracoes;
    private final RepositorioVisitante visitantes;
    private final RepositorioEscalaTrabalho escala;

    public RelatorioGerencial() {
        this(RepositorioIngresso.getInstancia(), RepositorioAtracao.getInstancia(),
                RepositorioVisitante.getInstancia(), RepositorioEscalaTrabalho.getInstancia());
    }

    public RelatorioGerencial(RepositorioIngresso ingressos, RepositorioAtracao atracoes,
                              RepositorioVisitante visitantes, RepositorioEscalaTrabalho escala) {
        this.ingressos = ingressos;
        this.atracoes = atracoes;
        this.visitantes = visitantes;
        this.escala = escala;
    }

    // =============================================
    // || RELATÓRIO GERAL
    // =============================================
    public ResumoGeral resumoGeral(LocalDate hoje) {
        double receitaTotal = 0;
        double receitaHoje = 0;
        int ingressosHoje = 0;
        int ingressosEmitidos = 0;
        for (Ingresso ingresso : ingressos.listarTodos()) {
            ingressosEmitidos++;
            receitaTotal += ingresso.getValor();
            if (hoje.equals(ingresso.getDataCompra())) {
                ingressosHoje++;
                receitaHoje += ingresso.getValor();
            }
        }

        int semOperador = 0;
        int lotadas = 0;
        int ocupacao = 0;
        int capacidade = 0;
        int reservasHoje = 0;
        List<Atracao> todas = atracoes.getAtracoes();
        for (Atracao atracao : todas) {
            if (!atracao.temOperador()) semOperador++;
            if (atracao.estaLotada()) lotadas++;
            ocupacao += atracao.getOcupacaoAtual();
            capacidade += atracao.getCapacidadeMaxima();
            reservasHoje += atracao.getFilaVirtual().totalAtivas(hoje);
        }

        return new ResumoGeral(visitantes.getVisitantes().size(), ingressosEmitidos, receitaTotal,
                ingressosHoje, receitaHoje, todas.size(), semOperador, lotadas,
                ocupacao, capacidade, reservasHoje, escala.operadoresDoDia(hoje).size());
    }

    // =============================================
    // || RELATÓRIO DE INGRESSOS (REQ14: destaca os feriados)
    // =============================================
    // 'feriados' = data -> nome do feriado (pode vir vazio se a BrasilAPI estiver fora do ar)
    public RelatorioIngressos relatorioIngressos(YearMonth periodo, Map<LocalDate, String> feriados) {
        List<VendaDetalhada> vendas = new ArrayList<>();
        int diarias = 0;
        int passes = 0;
        int emFeriados = 0;
        double receitaTotal = 0;
        double receitaFeriados = 0;

        for (Ingresso ingresso : ingressos.listarTodos()) {
            LocalDate data = ingresso.getDataCompra();
            if (data == null || !YearMonth.from(data).equals(periodo)) {
                continue;
            }
            String feriado = feriados == null ? null : feriados.get(data);
            String visitante = ingresso.getVisitante() == null || ingresso.getVisitante().getNome() == null
                    ? "—" : ingresso.getVisitante().getNome();

            vendas.add(new VendaDetalhada(data, visitante, ingresso.getTipoIngresso(),
                    ingresso.getValor(), ingresso.getFormaPagamento(), feriado));

            receitaTotal += ingresso.getValor();
            if (ingresso.getTipoIngresso() == TipoIngresso.PASSE_ANUAL) {
                passes++;
            } else {
                diarias++;
            }
            if (feriado != null) {
                emFeriados++;
                receitaFeriados += ingresso.getValor();
            }
        }

        vendas.sort(Comparator.comparing(VendaDetalhada::data).thenComparing(VendaDetalhada::visitante));
        return new RelatorioIngressos(vendas, vendas.size(), diarias, passes,
                receitaTotal, receitaFeriados, emFeriados);
    }

    // Primeiro ano com venda registrada (ou o ano informado, se ainda não houve nenhuma venda).
    // A tela usa para montar a lista de anos.
    public int primeiroAnoComVenda(int anoPadrao) {
        int menor = anoPadrao;
        for (Ingresso ingresso : ingressos.listarTodos()) {
            if (ingresso.getDataCompra() != null && ingresso.getDataCompra().getYear() < menor) {
                menor = ingresso.getDataCompra().getYear();
            }
        }
        return menor;
    }

    // =============================================
    // || RELATÓRIO POR DIA
    // =============================================
    // 'capacidadeMaxima' = capacidade do parque no dia (soma das atrações x 5); vem por parâmetro porque
    // quem calcula essa regra é o ServicoCapacidade.
    public RelatorioDia relatorioDia(LocalDate dia, int capacidadeMaxima) {
        List<LinhaDia> linhas = new ArrayList<>();
        int vendidos = 0;
        double receita = 0;

        for (Ingresso ingresso : ingressos.listarTodos()) {
            boolean comprado = dia.equals(ingresso.getDataCompra());
            boolean visita = ingresso.getTipoIngresso() == TipoIngresso.DIARIA
                    && dia.equals(ingresso.getDataValidade());
            if (!comprado && !visita) {
                continue;
            }
            if (comprado) {
                vendidos++;
                receita += ingresso.getValor();
            }
            String nome = ingresso.getVisitante() == null || ingresso.getVisitante().getNome() == null
                    ? "—" : ingresso.getVisitante().getNome();
            linhas.add(new LinhaDia(nome, ingresso.getTipoIngresso(), ingresso.getDataCompra(),
                    ingresso.getDataValidade(), ingresso.getValor(), ingresso.getFormaPagamento(),
                    comprado, visita));
        }
        linhas.sort(Comparator.comparing(LinhaDia::compra).thenComparing(LinhaDia::visitante));

        int reservas = 0;
        for (Atracao atracao : atracoes.getAtracoes()) {
            reservas += atracao.getFilaVirtual().totalAtivas(dia);
        }
        return new RelatorioDia(dia, linhas, vendidos, receita,
                ingressos.consultarQuantidadeParaVisita(dia), capacidadeMaxima,
                reservas, escala.operadoresDoDia(dia).size());
    }

    // =============================================
    // || RELATÓRIO DE LUCRO
    // =============================================
    // O sistema não guarda custos, então eles vêm por parâmetro:
    //   custoOperadorDia = quanto custa cada operador escalado em um dia (conta os turnos da escala)
    //   custoFixoMensal  = custo fixo do parque por mês (só entra em meses que já começaram)
    // A receita entra no mês da COMPRA do ingresso (mesma regra do Relatorio de console).
    public RelatorioLucro relatorioLucro(int ano, double custoOperadorDia, double custoFixoMensal,
                                         YearMonth mesAtual) {
        int[] quantidade = new int[12];
        double[] receita = new double[12];
        int[] turnos = new int[12];

        for (Ingresso ingresso : ingressos.listarTodos()) {
            LocalDate data = ingresso.getDataCompra();
            if (data != null && data.getYear() == ano) {
                quantidade[data.getMonthValue() - 1]++;
                receita[data.getMonthValue() - 1] += ingresso.getValor();
            }
        }
        for (Turno turno : escala.listarOrdenado()) {
            if (turno.data().getYear() == ano) {
                turnos[turno.data().getMonthValue() - 1]++;
            }
        }

        List<LinhaLucro> meses = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            YearMonth mes = YearMonth.of(ano, i + 1);
            double custo = turnos[i] * custoOperadorDia;
            if (!mes.isAfter(mesAtual)) {
                custo += custoFixoMensal;   // mês futuro ainda não tem custo fixo
            }
            meses.add(new LinhaLucro(mes, quantidade[i], receita[i], turnos[i], custo));
        }
        return new RelatorioLucro(meses);
    }

    // =============================================
    // || RELATÓRIO DE ATRAÇÕES (REQ13: mais acessadas no período)
    // =============================================
    // Do mais para o menos acessado. "Acessos" = reservas de fura-fila USADAS no período
    // (a entrada na fila comum não fica registrada; só a ocupação do momento).
    public List<LinhaAtracao> desempenhoAtracoes(LocalDate de, LocalDate ate) {
        List<LinhaAtracao> linhas = new ArrayList<>();
        for (Atracao atracao : atracoes.getAtracoes()) {
            int reservas = 0;
            int usadas = 0;
            int noShows = 0;
            int canceladas = 0;
            for (Reserva reserva : atracao.getFilaVirtual().getReservas()) {
                LocalDate dia = reserva.getData();
                if (dia.isBefore(de) || dia.isAfter(ate)) {
                    continue;
                }
                reservas++;
                EstadoReserva estado = reserva.getEstado();
                if (estado == EstadoReserva.USADA) usadas++;
                else if (estado == EstadoReserva.NO_SHOW) noShows++;
                else if (estado == EstadoReserva.CANCELADA) canceladas++;
            }

            String operador = atracao.getOperador() == null ? "—" : atracao.getOperador().getNome();
            linhas.add(new LinhaAtracao(atracao.getNome(), nomeDoTipo(atracao), operador,
                    atracao.getCapacidadeMaxima(), atracao.getOcupacaoAtual(),
                    reservas, usadas, noShows, canceladas, situacao(atracao)));
        }

        linhas.sort(Comparator.comparingInt(LinhaAtracao::usadas).reversed()
                .thenComparing(Comparator.comparingInt(LinhaAtracao::ocupacao).reversed())
                .thenComparing(LinhaAtracao::nome, String.CASE_INSENSITIVE_ORDER));
        return linhas;
    }

    // CSV da tabela de atrações (separador ';' para o Excel em português abrir em colunas)
    public String montarCsvAtracoes(List<LinhaAtracao> linhas) {
        StringBuilder csv = new StringBuilder(
                "Posicao;Atracao;Tipo;Operador;Capacidade;Ocupacao atual;Ocupacao (%);"
                        + "Reservas;Usadas;No-show;Canceladas;Situacao\n");
        int posicao = 1;
        for (LinhaAtracao l : linhas) {
            csv.append(posicao++).append(';')
                    .append(campoCsv(l.nome())).append(';')
                    .append(campoCsv(l.tipo())).append(';')
                    .append(campoCsv(l.operador())).append(';')
                    .append(l.capacidade()).append(';')
                    .append(l.ocupacao()).append(';')
                    .append(String.format(PT_BR, "%.1f", l.percentualOcupacao())).append(';')
                    .append(l.reservas()).append(';')
                    .append(l.usadas()).append(';')
                    .append(l.noShows()).append(';')
                    .append(l.canceladas()).append(';')
                    .append(campoCsv(l.situacao())).append('\n');
        }
        return csv.toString();
    }

    // =============================================
    // || AUXILIARES
    // =============================================
    private String situacao(Atracao atracao) {
        if (!atracao.temOperador()) return "Sem operador";
        if (atracao.estaLotada()) return "Lotada";
        return "Aberta";
    }

    private String nomeDoTipo(Atracao atracao) {
        if (atracao instanceof Aquatico) return "Aquática";
        if (atracao instanceof Radical) return "Radical";
        if (atracao instanceof Infantil) return "Infantil";
        return atracao.getTipo();
    }

    // Campo com ; aspas ou quebra de linha precisa ir entre aspas
    private String campoCsv(String texto) {
        if (texto == null) return "";
        if (texto.contains(";") || texto.contains("\"") || texto.contains("\n")) {
            return "\"" + texto.replace("\"", "\"\"") + "\"";
        }
        return texto;
    }
}
