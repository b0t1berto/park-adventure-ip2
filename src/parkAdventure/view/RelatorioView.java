package parkAdventure.view;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import parkAdventure.model.api.Feriados.Feriado;
import parkAdventure.model.pessoa.Operador;
import parkAdventure.repository.EscalaOperadores;
import parkAdventure.repository.ListaAcesso;
import parkAdventure.repository.ListaAtracao;
import parkAdventure.repository.ListaIngresso;
import parkAdventure.repository.Relatorio;
import parkAdventure.repository.Relatorio.LinhaEscala;
import parkAdventure.repository.Relatorio.LinhaVenda;
import parkAdventure.repository.Relatorio.ResumoReceita;

public class RelatorioView {
    private static final int QTD_PROXIMOS_FERIADOS = 5;

    private final ConsoleView console;
    private final Relatorio relatorio;
    private final ListaAtracao atracoes;
    private final ListaIngresso ingressos;
    private final ListaAcesso acessos;
    private final EscalaOperadores escala;

    public RelatorioView(ConsoleView console, Relatorio relatorio, ListaAtracao atracoes,
                         ListaIngresso ingressos, ListaAcesso acessos, EscalaOperadores escala) {
        this.console = console;
        this.relatorio = relatorio;
        this.atracoes = atracoes;
        this.ingressos = ingressos;
        this.acessos = acessos;
        this.escala = escala;
    }

    public void menu() {
        boolean voltar = false;
        while (!voltar) {
            console.exibir("\n--- RELATÓRIOS E ESCALA ---");
            console.exibir("1. Atrações mais acessadas por período (exporta CSV)");
            console.exibir("2. Receita de ingressos por mês");
            console.exibir("3. Escalar operador em um feriado");
            console.exibir("4. Operadores escalados nos próximos feriados");
            console.exibir("0. Voltar");
            switch (console.lerInteiro("Escolha: ")) {
                case 1 -> exportarCsv();
                case 2 -> receitaDoMes();
                case 3 -> escalarOperador();
                case 4 -> listarEscala();
                case 0 -> voltar = true;
                default -> console.exibir("Opção inválida.");
            }
        }
    }

    // REQ13
    private void exportarCsv() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicio = console.lerDataOpcional("Data inicial", hoje.withDayOfMonth(1));
        LocalDate fim = console.lerDataOpcional("Data final", hoje);
        if (fim.isBefore(inicio)) {
            exibirErro("a data final não pode ser anterior à inicial.");
            return;
        }
        String caminho = console.lerTextoOpcional("Arquivo de saída [atracoes.csv]: ");
        if (caminho.isEmpty()) caminho = "atracoes.csv";
        try {
            relatorio.salvarCsvAtracoes(atracoes.listarTodas(), acessos.listarTodos(), inicio, fim, caminho);
            exibirCsvGerado(caminho);
        } catch (Exception e) {
            exibirErro(e.getMessage());
        }
    }

    // REQ14
    private void receitaDoMes() {
        LocalDate hoje = LocalDate.now();
        int mes = console.lerInteiro("Mês (1-12): ");
        int ano = console.lerInteiroOpcional("Ano", hoje.getYear());
        try {
            exibirReceita(relatorio.gerarRelatorioIngressos(ingressos.listarTodos(), Month.of(mes), ano), Month.of(mes), ano);
        } catch (Exception e) {
            exibirErro(e.getMessage());
        }
    }

    // REQ15: cadastro da escala
    private void escalarOperador() {
        try {
            List<Feriado> feriados = relatorio.proximosFeriados(LocalDate.now(), QTD_PROXIMOS_FERIADOS);
            if (feriados.isEmpty()) {
                console.exibir("Nenhum feriado encontrado (a BrasilAPI pode estar indisponível).");
                return;
            }
            for (int i = 0; i < feriados.size(); i++) {
                Feriado f = feriados.get(i);
                console.exibir((i + 1) + ". " + console.formatar(LocalDate.parse(f.date())) + " - " + f.name());
            }
            int escolha = console.lerInteiro("Feriado (número): ");
            if (escolha < 1 || escolha > feriados.size()) {
                console.exibir("Opção inválida.");
                return;
            }
            Operador operador = atracoes.buscarOperador(console.lerTexto("Nome do operador: "));
            if (operador == null) {
                console.exibir("Operador não encontrado (ele precisa estar vinculado a uma atração).");
                return;
            }
            LocalDate data = LocalDate.parse(feriados.get(escolha - 1).date());
            console.exibir(escala.escalar(data, operador)
                    ? operador.getNome() + " escalado para " + console.formatar(data) + "."
                    : operador.getNome() + " já está escalado nessa data.");
        } catch (Exception e) {
            exibirErro(e.getMessage());
        }
    }

    // REQ15: consulta da escala
    private void listarEscala() {
        try {
            List<LinhaEscala> linhas = relatorio.listarEscalaProximosFeriados(
                    escala, LocalDate.now(), QTD_PROXIMOS_FERIADOS);
            if (linhas.isEmpty()) {
                console.exibir("Nenhum feriado encontrado (a BrasilAPI pode estar indisponível).");
                return;
            }
            exibirEscala(linhas);
        } catch (Exception e) {
            exibirErro(e.getMessage());
        }
    }

    public void exibirReceita(ResumoReceita resumo, Month mes, int ano) {
        System.out.println("Relatório de Vendas de Ingressos - " + mes + "/" + ano);
        for (LinhaVenda venda : resumo.vendas()) {
            String destaque = venda.feriado() ? "[Destaque Feriado] " : "";
            System.out.printf("%sData %s | Valor: R$ %.2f%n", destaque, venda.data(), venda.valor());
        }
        System.out.printf("Receita Total: R$ %.2f%n", resumo.receitaTotal());
        System.out.printf("Receita em Feriados: R$ %.2f%n", resumo.receitaFeriados());
    }

    public void exibirEscala(List<LinhaEscala> linhas) {
        System.out.println("Escala de operadores nos próximos feriados nacionais (BrasilAPI)");
        for (LinhaEscala linha : linhas) {
            System.out.printf("Feriado: %s (%s) | Operador(es): %s%n",
                    linha.feriado(), console.formatar(linha.data()), linha.operador());
        }
    }

    public void exibirCsvGerado(String caminho) {
        System.out.println("Relatório gerado com sucesso em: " + caminho);
    }

    public void exibirErro(String mensagem) {
        System.out.println("Erro ao gerar relatório: " + mensagem);
    }
}
