package parkAdventure.view;

import parkAdventure.model.atracao.Aquatico;
import parkAdventure.model.atracao.Atracao;
import parkAdventure.model.atracao.Infantil;
import parkAdventure.model.atracao.Radical;
import parkAdventure.model.pessoa.Operador;
import parkAdventure.repository.ListaAtracao;

// Menu do CRUD de atrações e exibição dos dados de uma atração
public class AtracaoView {
    private final ConsoleView console;
    private final ListaAtracao atracoes;

    public AtracaoView(ConsoleView console, ListaAtracao atracoes) {
        this.console = console;
        this.atracoes = atracoes;
    }

    public void menu() {
        boolean voltar = false;
        while (!voltar) {
            console.exibir("\n--- CRUD DE ATRAÇÕES ---");
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

    private Atracao lerDadosAtracao() {
        String nome = console.lerTexto("Nome da atração: ");
        int capacidade = console.lerInteiroPositivo("Capacidade máxima: ");
        String nomeOperador = console.lerTexto("Nome do operador responsável: ");
        String cargo = console.lerTexto("Cargo/atração do operador: ");
        Operador operador = new Operador(nomeOperador, "01/01/1990", null, cargo);
        console.exibir("Tipo: 1. Aquática  2. Radical  3. Infantil");
        int tipo = console.lerInteiro("Escolha o tipo: ");
        if (tipo == 3) {
            int alturaMaxima = console.lerInteiroPositivo("Altura máxima em centímetros: ");
            int idadeMaxima = console.lerInteiroNaoNegativo("Idade máxima: ");
            return new Infantil(nome, capacidade, operador, alturaMaxima, idadeMaxima);
        }
        int alturaMinima = console.lerInteiroNaoNegativo("Altura mínima em centímetros: ");
        int idadeMinima = console.lerInteiroNaoNegativo("Idade mínima: ");
        if (tipo == 1) return new Aquatico(nome, capacidade, operador, alturaMinima, idadeMinima);
        if (tipo == 2) return new Radical(nome, capacidade, operador, alturaMinima, idadeMinima);
        throw new IllegalArgumentException("Tipo de atração inválido.");
    }

    private void cadastrar() {
        try {
            Atracao atracao = lerDadosAtracao();
            console.exibir(atracoes.cadastrar(atracao)
                    ? "Atração cadastrada com sucesso."
                    : "Já existe uma atração com esse nome.");
        } catch (IllegalArgumentException e) {
            console.exibir("Não foi possível cadastrar: " + e.getMessage());
        }
    }

    private void listar() {
        if (atracoes.listarTodas().isEmpty()) {
            console.exibir("Nenhuma atração cadastrada.");
            return;
        }
        for (Atracao a : atracoes.listarTodas()) {
            console.pularLinha();
            exibirDetalhes(a);
            exibirOcupacao(a);
        }
    }

    private void buscar() {
        Atracao a = atracoes.buscar(console.lerTexto("Nome para buscar: "));
        if (a == null) {
            console.exibir("Atração não encontrada.");
            return;
        }
        exibirDetalhes(a);
        exibirOcupacao(a);
    }

    private void atualizar() {
        String nomeAtual = console.lerTexto("Nome atual da atração: ");
        if (atracoes.buscar(nomeAtual) == null) {
            console.exibir("Atração não encontrada.");
            return;
        }
        try {
            Atracao atualizada = lerDadosAtracao();
            console.exibir(atracoes.atualizar(nomeAtual, atualizada)
                    ? "Atração atualizada com sucesso."
                    : "Não foi possível atualizar. Verifique se o novo nome já está cadastrado.");
        } catch (IllegalArgumentException e) {
            console.exibir("Não foi possível atualizar: " + e.getMessage());
        }
    }

    private void remover() {
        String nome = console.lerTexto("Nome da atração a remover: ");
        console.exibir(atracoes.remover(nome)
                ? "Atração removida com sucesso."
                : "Atração não encontrada.");
    }

    public void exibirDetalhes(Atracao atracao) {
        console.exibir(atracao.getTipo());
        console.exibir("Nome: " + atracao.getNome());
        console.exibir("Capacidade Máxima: " + atracao.getCapacidadeMaxima());
        if (atracao.temOperador()) {
            console.exibir("Operador: " + atracao.getOperador().getNome());
        } else {
            console.exibir("Operador: (nenhum vinculado)");
        }
        if (atracao instanceof Infantil infantil) {
            console.exibir("Altura Máxima: " + infantil.getAlturaMaxima() + "cm");
            console.exibir("Idade Máxima: " + infantil.getIdadeMaxima());
        } else {
            console.exibir("Altura Mínima: " + atracao.getAlturaMinima() + "cm");
            console.exibir("Idade Mínima: " + atracao.getIdadeMinima());
        }
    }

    public void exibirOcupacao(Atracao atracao) {
        console.exibir(atracao.getNome() + ": " + atracao.getOcupacaoAtual()
                + "/" + atracao.getCapacidadeMaxima() + " (fila: " + atracao.getFilaVirtual().tamanhoTotal() + ")");
    }
}
