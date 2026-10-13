package parkAdventure;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import parkAdventure.controller.ListaAtracao;
import parkAdventure.controller.ListaVisitante;
import parkAdventure.model.atracao.Aquatico;
import parkAdventure.model.atracao.Atracao;
import parkAdventure.model.atracao.Infantil;
import parkAdventure.model.atracao.Radical;
import parkAdventure.model.pessoa.Operador;
import parkAdventure.model.pessoa.Responsavel;
import parkAdventure.model.pessoa.Visitante;

// Menu para executar as operações CRUD de visitantes e atrações.
public class Main {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final ListaAtracao ATRACOES = new ListaAtracao();
    private static final ListaVisitante VISITANTES = new ListaVisitante();

    public static void main(String[] args) {
        boolean executando = true;
        while (executando) {
            System.out.println("\n=== PARK ADVENTURE ===");
            System.out.println("1. Gerenciar visitantes");
            System.out.println("2. Gerenciar atrações");
            System.out.println("0. Sair");
            int opcao = lerInteiro("Escolha: ");
            switch (opcao) {
                case 1 -> menuVisitantes();
                case 2 -> menuAtracoes();
                case 0 -> executando = false;
                default -> System.out.println("Opção inválida.");
            }
        }
        System.out.println("Programa encerrado.");
        SCANNER.close();
    }

    private static void menuVisitantes() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n--- CRUD DE VISITANTES ---");
            System.out.println("1. Cadastrar");
            System.out.println("2. Listar");
            System.out.println("3. Buscar por nome");
            System.out.println("4. Atualizar");
            System.out.println("5. Remover");
            System.out.println("0. Voltar");
            switch (lerInteiro("Escolha: ")) {
                case 1 -> cadastrarVisitante();
                case 2 -> listarVisitantes();
                case 3 -> buscarVisitante();
                case 4 -> atualizarVisitante();
                case 5 -> removerVisitante();
                case 0 -> voltar = true;
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private static void cadastrarVisitante() {
        try {
            String nome = lerTexto("Nome: ");
            String nascimento = lerData("Data de nascimento (dd/MM/aaaa): ");
            String cpf = lerTextoOpcional("CPF (opcional): ");
            int altura = lerInteiroPositivo("Altura em centímetros: ");
            Visitante visitante = new Visitante(nome, nascimento, vazioParaNull(cpf), altura);
            if (lerSimNao("O visitante possui responsável? (s/n): ")) {
                visitante.setResponsavel(new Responsavel(
                        lerTexto("Nome do responsável: "),
                        lerData("Nascimento do responsável (dd/MM/aaaa): "),
                        vazioParaNull(lerTextoOpcional("CPF do responsável (opcional): "))));
            }
            System.out.println(VISITANTES.cadastrar(visitante)
                    ? "Visitante cadastrado com sucesso."
                    : "Já existe um visitante com esse nome.");
        } catch (IllegalArgumentException | DateTimeParseException e) {
            System.out.println("Não foi possível cadastrar: " + e.getMessage());
        }
    }

    private static void listarVisitantes() {
        if (VISITANTES.listarTodos().isEmpty()) {
            System.out.println("Nenhum visitante cadastrado.");
            return;
        }
        for (Visitante v : VISITANTES.listarTodos()) exibirVisitante(v);
    }

    private static void buscarVisitante() {
        Visitante v = VISITANTES.buscar(lerTexto("Nome para buscar: "));
        if (v == null) System.out.println("Visitante não encontrado.");
        else exibirVisitante(v);
    }

    private static void atualizarVisitante() {
        String nomeAtual = lerTexto("Nome atual do visitante: ");
        Visitante existente = VISITANTES.buscar(nomeAtual);
        if (existente == null) {
            System.out.println("Visitante não encontrado.");
            return;
        }
        String novoNome = lerTexto("Novo nome: ");
        String nascimento = lerData("Nova data de nascimento (dd/MM/aaaa): ");
        String cpf = lerTextoOpcional("Novo CPF (opcional): ");
        int altura = lerInteiroPositivo("Nova altura em centímetros: ");
        Visitante atualizado = new Visitante(novoNome, nascimento, vazioParaNull(cpf), altura);
        if (existente.temResponsavel()) {
            atualizado.setResponsavel(existente.getResponsavel());
        }
        System.out.println(VISITANTES.atualizar(nomeAtual, atualizado)
                ? "Visitante atualizado com sucesso."
                : "Não foi possível atualizar. Verifique se o novo nome já está cadastrado.");
    }

    private static void removerVisitante() {
        String nome = lerTexto("Nome do visitante a remover: ");
        System.out.println(VISITANTES.remover(nome)
                ? "Visitante removido com sucesso."
                : "Visitante não encontrado.");
    }

    private static void exibirVisitante(Visitante v) {
        System.out.println("\nNome: " + v.getNome()
                + "\nNascimento: " + v.getDataNascimento().format(FORMATO_DATA)
                + "\nIdade: " + v.getIdade()
                + "\nCPF: " + (v.getCpf() == null ? "não informado" : v.getCpf())
                + "\nAltura: " + v.getAltura() + " cm"
                + "\nResponsável: " + (v.temResponsavel() ? v.getResponsavel().getNome() : "não informado"));
    }

    private static void menuAtracoes() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n--- CRUD DE ATRAÇÕES ---");
            System.out.println("1. Cadastrar");
            System.out.println("2. Listar");
            System.out.println("3. Buscar por nome");
            System.out.println("4. Atualizar");
            System.out.println("5. Remover");
            System.out.println("0. Voltar");
            switch (lerInteiro("Escolha: ")) {
                case 1 -> cadastrarAtracao();
                case 2 -> listarAtracoes();
                case 3 -> buscarAtracao();
                case 4 -> atualizarAtracao();
                case 5 -> removerAtracao();
                case 0 -> voltar = true;
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private static Atracao lerDadosAtracao() {
        String nome = lerTexto("Nome da atração: ");
        int capacidade = lerInteiroPositivo("Capacidade máxima: ");
        String nomeOperador = lerTexto("Nome do operador responsável: ");
        String cargo = lerTexto("Cargo/atração do operador: ");
        Operador operador = new Operador(nomeOperador, "01/01/1990", null, cargo);
        System.out.println("Tipo: 1. Aquática  2. Radical  3. Infantil");
        int tipo = lerInteiro("Escolha o tipo: ");
        if (tipo == 3) {
            int alturaMaxima = lerInteiroPositivo("Altura máxima em centímetros: ");
            int idadeMaxima = lerInteiroNaoNegativo("Idade máxima: ");
            return new Infantil(nome, capacidade, operador, alturaMaxima, idadeMaxima);
        }
        int alturaMinima = lerInteiroNaoNegativo("Altura mínima em centímetros: ");
        int idadeMinima = lerInteiroNaoNegativo("Idade mínima: ");
        if (tipo == 1) return new Aquatico(nome, capacidade, operador, alturaMinima, idadeMinima);
        if (tipo == 2) return new Radical(nome, capacidade, operador, alturaMinima, idadeMinima);
        throw new IllegalArgumentException("Tipo de atração inválido.");
    }

    private static void cadastrarAtracao() {
        try {
            Atracao atracao = lerDadosAtracao();
            System.out.println(ATRACOES.cadastrar(atracao)
                    ? "Atração cadastrada com sucesso."
                    : "Já existe uma atração com esse nome.");
        } catch (IllegalArgumentException e) {
            System.out.println("Não foi possível cadastrar: " + e.getMessage());
        }
    }

    private static void listarAtracoes() {
        if (ATRACOES.listarTodas().isEmpty()) {
            System.out.println("Nenhuma atração cadastrada.");
            return;
        }
        for (Atracao a : ATRACOES.listarTodas()) exibirAtracao(a);
    }

    private static void buscarAtracao() {
        Atracao a = ATRACOES.buscar(lerTexto("Nome para buscar: "));
        if (a == null) System.out.println("Atração não encontrada.");
        else exibirAtracao(a);
    }

    private static void atualizarAtracao() {
        String nomeAtual = lerTexto("Nome atual da atração: ");
        if (ATRACOES.buscar(nomeAtual) == null) {
            System.out.println("Atração não encontrada.");
            return;
        }
        try {
            Atracao atualizada = lerDadosAtracao();
            System.out.println(ATRACOES.atualizar(nomeAtual, atualizada)
                    ? "Atração atualizada com sucesso."
                    : "Não foi possível atualizar. Verifique se o novo nome já está cadastrado.");
        } catch (IllegalArgumentException e) {
            System.out.println("Não foi possível atualizar: " + e.getMessage());
        }
    }

    private static void removerAtracao() {
        String nome = lerTexto("Nome da atração a remover: ");
        System.out.println(ATRACOES.remover(nome)
                ? "Atração removida com sucesso."
                : "Atração não encontrada.");
    }

    private static void exibirAtracao(Atracao a) {
        System.out.println("\nTipo: " + a.getTipo()
                + "\nNome: " + a.getNome()
                + "\nCapacidade: " + a.getCapacidadeMaxima()
                + "\nOcupação: " + a.getOcupacaoAtual()
                + "\nOperador: " + (a.temOperador() ? a.getOperador().getNome() : "não informado"));
        if (a instanceof Infantil i) {
            System.out.println("Altura máxima: " + i.getAlturaMaxima() + " cm"
                    + "\nIdade máxima: " + i.getIdadeMaxima());
        } else {
            System.out.println("Altura mínima: " + a.getAlturaMinima() + " cm"
                    + "\nIdade mínima: " + a.getIdadeMinima());
        }
    }

    private static String lerTexto(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String valor = SCANNER.nextLine().trim();
            if (!valor.isEmpty()) return valor;
            System.out.println("O valor não pode ficar vazio.");
        }
    }

    private static String lerTextoOpcional(String mensagem) {
        System.out.print(mensagem);
        return SCANNER.nextLine().trim();
    }

    private static String lerData(String mensagem) {
        while (true) {
            String valor = lerTexto(mensagem);
            try {
                LocalDate.parse(valor, FORMATO_DATA);
                return valor;
            } catch (DateTimeParseException e) {
                System.out.println("Data inválida. Use dd/MM/aaaa.");
            }
        }
    }

    private static int lerInteiro(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            try {
                return Integer.parseInt(SCANNER.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Digite um número inteiro válido.");
            }
        }
    }

    private static int lerInteiroPositivo(String mensagem) {
        while (true) {
            int valor = lerInteiro(mensagem);
            if (valor > 0) return valor;
            System.out.println("O valor deve ser maior que zero.");
        }
    }

    private static int lerInteiroNaoNegativo(String mensagem) {
        while (true) {
            int valor = lerInteiro(mensagem);
            if (valor >= 0) return valor;
            System.out.println("O valor não pode ser negativo.");
        }
    }

    private static boolean lerSimNao(String mensagem) {
        while (true) {
            String resposta = lerTexto(mensagem);
            if (resposta.equalsIgnoreCase("s")) return true;
            if (resposta.equalsIgnoreCase("n")) return false;
            System.out.println("Responda com 's' ou 'n'.");
        }
    }

    private static String vazioParaNull(String valor) {
        return valor == null || valor.isBlank() ? null : valor;
    }
}
