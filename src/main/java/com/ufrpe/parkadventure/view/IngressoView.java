package com.ufrpe.parkadventure.view;

import java.time.LocalDate;
import java.util.List;

import com.ufrpe.parkadventure.controller.ContextoController;
import com.ufrpe.parkadventure.controller.RepositorioIngressoController;
import com.ufrpe.parkadventure.model.ingresso.Ingresso;
import com.ufrpe.parkadventure.model.ingresso.TipoIngresso;
import com.ufrpe.parkadventure.model.pessoa.Visitante;
import com.ufrpe.parkadventure.repository.RepositorioIngresso;
import com.ufrpe.parkadventure.repository.RepositorioVisitante;

// Menu de compra de ingressos e exibição dos ingressos
public class IngressoView {
    private final ConsoleView console;
    private final RepositorioVisitante visitantes;

    public IngressoView(ConsoleView console, RepositorioIngresso ingresso, RepositorioVisitante visitantes) {
        this.console = console;
        this.visitantes = visitantes;
    }

    public void menu() {
        boolean voltar = false;
        while (!voltar) {
            console.exibir("\n--- INGRESSOS ---");
            console.exibir("1. Comprar ingresso");
            console.exibir("2. Listar todos");
            console.exibir("3. Listar por visitante");
            console.exibir("4. Quantidade emitida em uma data");
            console.exibir("0. Voltar");
            switch (console.lerInteiro("Escolha: ")) {
                case 1 -> comprar();
                case 2 -> listar(RepositorioIngressoController.listarTodos());
                case 3 -> listarPorVisitante();
                case 4 -> quantidadePorData();
                case 0 -> voltar = true;
                default -> console.exibir("Opção inválida.");
            }
        }
    }

    private void comprar() {
        Visitante visitante = visitantes.buscar(console.lerTexto("Nome do visitante: "));
        if (visitante == null) {
            console.exibir("Visitante não encontrado.");
            return;
        }
        console.exibir("Tipo: 1. Diária  2. Passe anual");
        TipoIngresso tipo = switch (console.lerInteiro("Escolha o tipo: ")) {
            case 1 -> TipoIngresso.DIARIA;
            case 2 -> TipoIngresso.PASSE_ANUAL;
            default -> null;
        };
        if (tipo == null) {
            console.exibir("Tipo inválido.");
            return;
        }
        LocalDate data = console.lerDataOpcional("Data da compra (dd/MM/aaaa)", LocalDate.now());
        // REQ09: aviso quando a data escolhida é feriado (só a diária tem acréscimo)
        boolean feriado = tipo == TipoIngresso.DIARIA && ContextoController.CONTROLE_INGRESSO.ehFeriado(data);
        exibirIngresso(ContextoController.CONTROLE_INGRESSO.emitirIngresso(visitante, tipo, data, feriado), feriado);
    }

    private void listarPorVisitante() {
        Visitante visitante = visitantes.buscar(console.lerTexto("Nome do visitante: "));
        if (visitante == null) {
            console.exibir("Visitante não encontrado.");
            return;
        }
        listar(RepositorioIngressoController.listarPorVisitante(visitante.getNome()));
    }

    private void quantidadePorData() {
        LocalDate data = console.lerDataOpcional("Data (dd/MM/aaaa)", LocalDate.now());
        exibirQuantidade(data, RepositorioIngressoController.consultarQuantidadePorData(data));
    }

    private void listar(List<Ingresso> lista) {
        if (lista.isEmpty()) {
            console.exibir("Nenhum ingresso encontrado.");
            return;
        }
        for (Ingresso ingresso : lista) {
            exibirIngresso(ingresso, false);
        }
    }

    public void exibirIngresso(Ingresso ingresso, boolean feriado) {
        if (feriado) {
            console.exibir("*** ATENÇÃO: a data escolhida é feriado nacional (acréscimo aplicado) ***");
        }
        console.exibir("Ingresso " + ingresso.getTipoIngresso()
                + " | Visitante: " + ingresso.getVisitante().getNome()
                + " | Data: " + console.formatar(ingresso.getDataCompra())
                + " | Validade: " + console.formatar(ingresso.getDataValidade())
                + String.format(" | Valor: R$ %.2f", ingresso.getValor()));
    }

    public void exibirQuantidade(LocalDate data, int quantidade) {
        console.exibir("Ingressos emitidos em " + console.formatar(data) + ": " + quantidade);
    }
}
