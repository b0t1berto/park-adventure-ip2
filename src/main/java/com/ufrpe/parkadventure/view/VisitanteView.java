package com.ufrpe.parkadventure.view;

import java.time.format.DateTimeParseException;

import com.ufrpe.parkadventure.controller.RepositorioVisitanteController;
import com.ufrpe.parkadventure.model.pessoa.Responsavel;
import com.ufrpe.parkadventure.model.pessoa.Visitante;

// Menu do CRUD de visitantes
public class VisitanteView {
    private final ConsoleView console;

    public VisitanteView(ConsoleView console) {
        this.console = console;
    }

    public void menu() {
        boolean voltar = false;
        while (!voltar) {
            console.exibir("\n--- CRUD DE VISITANTES ---");
            console.exibir("1. Cadastrar");
            console.exibir("2. Listar");
            console.exibir("3. Buscar por nome");
            console.exibir("4. Atualizar");
            console.exibir("5. Remover");
            console.exibir("0. Voltar");
            switch (console.lerInteiro("Escolha: ")) {
                case 1 -> cadastrar();
                case 2 -> listar();
                case 3 -> buscar();
                case 4 -> atualizar();
                case 5 -> remover();
                case 0 -> voltar = true;
                default -> console.exibir("Opção inválida.");
            }
        }
    }

    private void cadastrar() {
        try {
            String nome = console.lerTexto("Nome: ");
            String nascimento = console.lerData("Data de nascimento (dd/MM/aaaa): ");
            String cpf = console.lerTextoOpcional("CPF (opcional): ");
            int altura = console.lerInteiroPositivo("Altura em centímetros: ");
            Visitante visitante = new Visitante(nome, nascimento, console.vazioParaNull(cpf), altura);
            if (console.lerSimNao("O visitante possui responsável? (s/n): ")) {
                visitante.setResponsavel(new Responsavel(
                        console.lerTexto("Nome do responsável: "),
                        console.lerData("Nascimento do responsável (dd/MM/aaaa): "),
                        console.vazioParaNull(console.lerTextoOpcional("CPF do responsável (opcional): "))));
            }
            console.exibir(RepositorioVisitanteController.cadastrarVisitante(visitante)
                    ? "Visitante cadastrado com sucesso."
                    : "Já existe um visitante com esse nome.");
        } catch (IllegalArgumentException | DateTimeParseException e) {
            console.exibir("Não foi possível cadastrar: " + e.getMessage());
        }
    }

    private void listar() {
        if (RepositorioVisitanteController.listarTodos().isEmpty()) {
            console.exibir("Nenhum visitante cadastrado.");
            return;
        }
        for (Visitante v : RepositorioVisitanteController.listarTodos()) exibirVisitante(v);
    }

    private void buscar() {
        Visitante v = RepositorioVisitanteController.buscarVisitante(console.lerTexto("Nome para buscar: "));
        if (v == null) console.exibir("Visitante não encontrado.");
        else exibirVisitante(v);
    }

    private void atualizar() {
        String nomeAtual = console.lerTexto("Nome atual do visitante: ");
        Visitante existente = RepositorioVisitanteController.buscarVisitante(nomeAtual);
        if (existente == null) {
            console.exibir("Visitante não encontrado.");
            return;
        }
        String novoNome = console.lerTexto("Novo nome: ");
        String nascimento = console.lerData("Nova data de nascimento (dd/MM/aaaa): ");
        String cpf = console.lerTextoOpcional("Novo CPF (opcional): ");
        int altura = console.lerInteiroPositivo("Nova altura em centímetros: ");
        Visitante atualizado = new Visitante(novoNome, nascimento, console.vazioParaNull(cpf), altura);
        if (existente.temResponsavel()) {
            atualizado.setResponsavel(existente.getResponsavel());
        }
        console.exibir(RepositorioVisitanteController.atualizarVisitante(nomeAtual, atualizado)
                ? "Visitante atualizado com sucesso."
                : "Não foi possível atualizar. Verifique se o novo nome já está cadastrado.");
    }

    private void remover() {
        String nome = console.lerTexto("Nome do visitante a remover: ");
        console.exibir(RepositorioVisitanteController.removerVisitante(nome)
                ? "Visitante removido com sucesso."
                : "Visitante não encontrado.");
    }

    public void exibirVisitante(Visitante v) {
        console.exibir("\nNome: " + v.getNome()
                + "\nNascimento: " + console.formatar(v.getDataNascimento())
                + "\nIdade: " + v.getIdade()
                + "\nCPF: " + (v.getCpf() == null ? "não informado" : v.getCpf())
                + "\nAltura: " + v.getAltura() + " cm"
                + "\nResponsável: " + (v.temResponsavel() ? v.getResponsavel().getNome() : "não informado"));
    }
}
