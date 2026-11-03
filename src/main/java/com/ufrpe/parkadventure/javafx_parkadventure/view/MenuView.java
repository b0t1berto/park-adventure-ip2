package com.ufrpe.parkadventure.javafx_parkadventure.view;

import java.util.NoSuchElementException;

// Menu principal: só direciona para o menu de cada view
public class MenuView {
    private final ConsoleView console;
    private final VisitanteView visitanteView;
    private final AtracaoView atracaoView;
    private final IngressoView ingressoView;
    private final AcessoView acessoView;
    private final RelatorioView relatorioView;

    public MenuView(ConsoleView console, VisitanteView visitanteView, AtracaoView atracaoView,
                    IngressoView ingressoView, AcessoView acessoView, RelatorioView relatorioView) {
        this.console = console;
        this.visitanteView = visitanteView;
        this.atracaoView = atracaoView;
        this.ingressoView = ingressoView;
        this.acessoView = acessoView;
        this.relatorioView = relatorioView;
    }

    public void iniciar() {
        try {
            boolean executando = true;
            while (executando) {
                console.exibirTitulo("PARK ADVENTURE");
                console.exibir("1. Visitantes");
                console.exibir("2. Atrações");
                console.exibir("3. Ingressos");
                console.exibir("4. Acesso e fila virtual");
                console.exibir("5. Relatórios");
                console.exibir("0. Sair");
                switch (console.lerInteiro("Escolha: ")) {
                    case 1 -> visitanteView.menu();
                    case 2 -> atracaoView.menu();
                    case 3 -> ingressoView.menu();
                    case 4 -> acessoView.menu();
                    case 5 -> relatorioView.menu();
                    case 0 -> executando = false;
                    default -> console.exibir("Opção inválida.");
                }
            }
            console.exibir("Programa encerrado.");
        } catch (NoSuchElementException e) {
            console.exibir("\nEntrada encerrada.");
        }
    }
}
