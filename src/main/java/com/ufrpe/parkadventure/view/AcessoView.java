package com.ufrpe.parkadventure.view;

import com.ufrpe.parkadventure.controller.AcessoController;
import com.ufrpe.parkadventure.controller.ResultadoAcesso;
import com.ufrpe.parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.model.atracao.FilaVirtual;
import com.ufrpe.parkadventure.model.atracao.Reserva;
import com.ufrpe.parkadventure.model.ingresso.Ingresso;
import com.ufrpe.parkadventure.model.pessoa.Visitante;
import com.ufrpe.parkadventure.controller.RepositorioAtracaoController;
import com.ufrpe.parkadventure.controller.RepositorioIngressoController;
import com.ufrpe.parkadventure.controller.RepositorioVisitanteController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

// Menu de acesso às atrações e fura-fila (reserva de horário)
public class AcessoView {
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final ConsoleView console;
    private final AcessoController controleAcesso;

    public AcessoView(ConsoleView console, AcessoController controleAcesso) {
        this.console = console;
        this.controleAcesso = controleAcesso;
    }

    public void menu() {
        boolean voltar = false;
        while (!voltar) {
            console.exibir("\n--- ACESSO E FURA-FILA ---");
            console.exibir("1. Registrar acesso a uma atração");
            console.exibir("2. Reservar o fura-fila de hoje (escolhendo o horário)");
            console.exibir("3. Registrar saída (o operador faz o -1)");
            console.exibir("5. Liberar quem chegou ao fura-fila (check-in do operador)");
            console.exibir("4. Ver ocupação e reservas de hoje");
            console.exibir("0. Voltar");
            switch (console.lerInteiro("Escolha: ")) {
                case 1 -> registrarAcesso();
                case 2 -> entrarNaFila();
                case 3 -> finalizarUso();
                case 4 -> verOcupacao();
                case 5 -> liberarFuraFila();
                case 0 -> voltar = true;
                default -> console.exibir("Opção inválida.");
            }
        }
    }

    private void registrarAcesso() {
        Visitante visitante = lerVisitante();
        Atracao atracao = visitante == null ? null : lerAtracao();
        Ingresso ingresso = visitante == null ? null : ultimoIngresso(visitante);
        if (atracao == null || ingresso == null) return;
        exibirResultado(visitante, atracao, controleAcesso.registrarAcesso(visitante, ingresso, atracao));
    }

    private void entrarNaFila() {
        Visitante visitante = lerVisitante();
        Atracao atracao = visitante == null ? null : lerAtracao();
        Ingresso ingresso = visitante == null ? null : ultimoIngresso(visitante);
        if (atracao == null || ingresso == null) return;
        StringBuilder horarios = new StringBuilder();
        for (LocalTime h : FilaVirtual.horarios()) {
            horarios.append(horarios.length() == 0 ? "" : ", ").append(h.format(HORA));
        }
        console.exibir("Horários: " + horarios);
        try {
            LocalTime horario = LocalTime.parse(console.lerTexto("Horário desejado (ex.: 14:00): ").trim(), HORA);
            exibirResultado(visitante, atracao,
                    controleAcesso.reservarFuraFila(visitante, ingresso, atracao, LocalDate.now(), horario));
        } catch (DateTimeParseException e) {
            console.exibir("Horário inválido. Use o formato HH:mm, de 11:00 a 18:00.");
        }
    }

    // O operador confere a reserva do visitante dentro da janela do horário e faz o +1
    private void liberarFuraFila() {
        Visitante visitante = lerVisitante();
        Atracao atracao = visitante == null ? null : lerAtracao();
        if (atracao == null) return;
        exibirResultado(visitante, atracao, controleAcesso.liberarFuraFila(visitante, atracao, LocalDateTime.now()));
    }

    private void finalizarUso() {
        Atracao atracao = lerAtracao();
        if (atracao == null) return;
        controleAcesso.finalizarUso(atracao);
        exibirSaida(atracao);
    }

    private void verOcupacao() {
        if (RepositorioAtracaoController.listarTodas().isEmpty()) {
            console.exibir("Nenhuma atração cadastrada.");
            return;
        }
        for (Atracao a : RepositorioAtracaoController.listarTodas()) {
            exibirStatus(a);
        }
    }

    private Visitante lerVisitante() {
        Visitante visitante = RepositorioVisitanteController.buscarVisitante(console.lerTexto("Nome do visitante: "));
        if (visitante == null) console.exibir("Visitante não encontrado.");
        return visitante;
    }

    private Atracao lerAtracao() {
        Atracao atracao = RepositorioAtracaoController.buscar(console.lerTexto("Nome da atração: "));
        if (atracao == null) console.exibir("Atração não encontrada.");
        return atracao;
    }

    private Ingresso ultimoIngresso(Visitante visitante) {
        Ingresso ingresso = RepositorioIngressoController.buscarUltimo(visitante.getNome());
        if (ingresso == null) console.exibir(visitante.getNome() + " ainda não comprou ingresso.");
        return ingresso;
    }

    public void exibirResultado(Visitante visitante, Atracao atracao, ResultadoAcesso resultado) {
        String nome = visitante.getNome();
        switch (resultado) {
            case AUTORIZADO -> console.exibir("Aproveite o brinquedo " + atracao.getNome() + ", " + nome + "!");
            case ATRACAO_LOTADA -> console.exibir(atracao.getNome() + " está lotada. " + nome + " pode aguardar na fila ou reservar o fura-fila para outro horário.");
            case NA_FILA, RESERVADO -> console.exibir(nome + " reservou o fura-fila de " + atracao.getNome() + ".");
            case FILA_CHEIA -> console.exibir("Os passes de " + atracao.getNome() + " nesse horário acabaram. " + nome + " não conseguiu reservar.");
            case HORARIO_INVALIDO -> console.exibir("Horário inválido. Use de 11:00 a 18:00, de 30 em 30 minutos.");
            case INGRESSO_DE_OUTRO_VISITANTE -> console.exibir("O ingresso apresentado não pertence a " + nome + ".");
            case INGRESSO_EXPIRADO -> console.exibir("O ingresso de " + nome + " está fora da validade.");
            case FORA_DOS_REQUISITOS -> console.exibir(nome + " não atinge os requisitos mínimos para " + atracao.getNome() + "!");
            case SEM_RESPONSAVEL -> console.exibir(nome + " precisa de um responsável para entrar em " + atracao.getNome() + "!");
            case SEM_OPERADOR -> console.exibir("A atração " + atracao.getNome() + " está sem operador no momento.");
            case JA_NA_FILA -> console.exibir(nome + " já tem reserva nesta atração nesse dia.");
            case SEM_INGRESSO -> console.exibir(nome + " precisa comprar um ingresso.");
            case CONFLITO_DE_HORARIO -> console.exibir(nome + " já tem reserva em outra atração nesse horário.");
            case SEM_RESERVA_NO_HORARIO -> console.exibir(nome + " não tem reserva válida agora (fora da janela do horário, ou já usou/cancelou).");
        }
    }

    public void exibirSaida(Atracao atracao) {
        console.exibir("Saída registrada em " + atracao.getNome() + ": " + atracao.getOcupacaoAtual()
                + "/" + atracao.getCapacidadeMaxima() + ".");
    }

    // Ocupação atual e as reservas de fura-fila de hoje
    public void exibirStatus(Atracao atracao) {
        console.exibir(atracao.getNome() + ": " + atracao.getOcupacaoAtual() + "/" + atracao.getCapacidadeMaxima());
        StringBuilder fila = new StringBuilder();
        for (Reserva r : atracao.getFilaVirtual().reservasDoDia(LocalDate.now())) {
            if (r.estaAtiva()) {
                fila.append(fila.length() == 0 ? "" : " | ")
                        .append(r.getHorario().format(HORA)).append(" -> ").append(r.getVisitante().getNome());
            }
        }
        console.exibir("  Fura-fila de hoje: " + (fila.length() == 0 ? "sem reservas" : fila));
    }
}
