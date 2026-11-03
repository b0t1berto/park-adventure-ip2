package com.ufrpe.parkadventure.javafx_parkadventure.view;

import com.ufrpe.parkadventure.javafx_parkadventure.controller.ControleAcesso;
import com.ufrpe.parkadventure.javafx_parkadventure.controller.ResultadoAcesso;
import com.ufrpe.parkadventure.javafx_parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.javafx_parkadventure.model.ingresso.Ingresso;
import com.ufrpe.parkadventure.javafx_parkadventure.model.pessoa.Visitante;
import com.ufrpe.parkadventure.javafx_parkadventure.repository.ListaAtracao;
import com.ufrpe.parkadventure.javafx_parkadventure.repository.ListaIngresso;
import com.ufrpe.parkadventure.javafx_parkadventure.repository.ListaVisitante;

// Menu de acesso às atrações e fila virtual
public class AcessoView {
    private final ConsoleView console;
    private final ListaAtracao atracoes;
    private final ListaVisitante visitantes;
    private final ListaIngresso ingressos;
    private final ControleAcesso controleAcesso;

    public AcessoView(ConsoleView console, ListaAtracao atracoes, ListaVisitante visitantes,
                      ListaIngresso ingressos, ControleAcesso controleAcesso) {
        this.console = console;
        this.atracoes = atracoes;
        this.visitantes = visitantes;
        this.ingressos = ingressos;
        this.controleAcesso = controleAcesso;
    }

    public void menu() {
        boolean voltar = false;
        while (!voltar) {
            console.exibir("\n--- ACESSO E FILA VIRTUAL ---");
            console.exibir("1. Registrar acesso a uma atração");
            console.exibir("2. Entrar na fila virtual (escolhendo o horário)");
            console.exibir("3. Finalizar uso (libera vaga e chama a fila)");
            console.exibir("4. Ver ocupação e filas");
            console.exibir("0. Voltar");
            switch (console.lerInteiro("Escolha: ")) {
                case 1 -> registrarAcesso();
                case 2 -> entrarNaFila();
                case 3 -> finalizarUso();
                case 4 -> verOcupacao();
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
        console.exibir("Horários: " + String.join(", ", atracao.getFilaVirtual().getHorarios()));
        String horario = console.lerTexto("Horário desejado (ex.: 14:00): ");
        exibirResultado(visitante, atracao, controleAcesso.entrarNaFilaVirtual(visitante, ingresso, atracao, horario));
    }

    private void finalizarUso() {
        Atracao atracao = lerAtracao();
        if (atracao == null) return;
        exibirLiberacao(atracao, controleAcesso.finalizarUso(atracao));
    }

    private void verOcupacao() {
        if (atracoes.listarTodas().isEmpty()) {
            console.exibir("Nenhuma atração cadastrada.");
            return;
        }
        for (Atracao a : atracoes.listarTodas()) {
            exibirStatus(a);
        }
    }

    private Visitante lerVisitante() {
        Visitante visitante = visitantes.buscar(console.lerTexto("Nome do visitante: "));
        if (visitante == null) console.exibir("Visitante não encontrado.");
        return visitante;
    }

    private Atracao lerAtracao() {
        Atracao atracao = atracoes.buscar(console.lerTexto("Nome da atração: "));
        if (atracao == null) console.exibir("Atração não encontrada.");
        return atracao;
    }

    private Ingresso ultimoIngresso(Visitante visitante) {
        Ingresso ingresso = ingressos.buscarUltimo(visitante);
        if (ingresso == null) console.exibir(visitante.getNome() + " ainda não comprou ingresso.");
        return ingresso;
    }

    public void exibirResultado(Visitante visitante, Atracao atracao, ResultadoAcesso resultado) {
        String nome = visitante.getNome();
        switch (resultado) {
            case AUTORIZADO -> console.exibir("Aproveite o brinquedo " + atracao.getNome() + ", " + nome + "!");
            case ATRACAO_LOTADA -> console.exibir(atracao.getNome() + " está lotada. " + nome + " pode entrar na fila virtual escolhendo um horário.");
            case NA_FILA -> console.exibir(nome + " entrou na fila virtual de " + atracao.getNome() + ".");
            case FILA_CHEIA -> console.exibir("A fila de " + atracao.getNome() + " está cheia nesse horário. " + nome + " não pôde entrar.");
            case HORARIO_INVALIDO -> console.exibir("Horário inválido. Use de 11:00 a 18:00, de 30 em 30 minutos.");
            case INGRESSO_DE_OUTRO_VISITANTE -> console.exibir("O ingresso apresentado não pertence a " + nome + ".");
            case INGRESSO_EXPIRADO -> console.exibir("O ingresso de " + nome + " está fora da validade.");
            case FORA_DOS_REQUISITOS -> console.exibir(nome + " não atinge os requisitos mínimos para " + atracao.getNome() + "!");
            case SEM_RESPONSAVEL -> console.exibir(nome + " precisa de um responsável para entrar em " + atracao.getNome() + "!");
        }
    }

    public void exibirLiberacao(Atracao atracao, Visitante proximo) {
        if (proximo == null) {
            console.exibir("Vaga liberada em " + atracao.getNome() + ".");
        } else {
            console.exibir("Vaga liberada em " + atracao.getNome() + ". " + proximo.getNome() + " saiu da fila e entrou!");
        }
    }

    // Ocupação atual e quem espera em cada horário da fila virtual
    public void exibirStatus(Atracao atracao) {
        console.exibir(atracao.getNome() + ": " + atracao.getOcupacaoAtual() + "/" + atracao.getCapacidadeMaxima());
        StringBuilder fila = new StringBuilder();
        for (String horario : atracao.getFilaVirtual().getHorarios()) {
            int qtd = atracao.getFilaVirtual().tamanho(horario);
            if (qtd > 0) {
                fila.append(fila.length() == 0 ? "" : " | ").append(horario).append(" -> ").append(qtd);
            }
        }
        console.exibir("  Fila virtual: " + (fila.length() == 0 ? "vazia" : fila));
    }
}
