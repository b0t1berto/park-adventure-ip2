package parkAdventure.view;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import parkAdventure.repository.ListaAtracao;
import parkAdventure.repository.ListaIngresso;
import parkAdventure.repository.Relatorio;
import parkAdventure.repository.Relatorio.LinhaEscala;
import parkAdventure.repository.Relatorio.LinhaVenda;
import parkAdventure.repository.Relatorio.ResumoReceita;

public class RelatorioView {
    private final ConsoleView console;
    private final Relatorio relatorio;
    private final ListaAtracao atracoes;
    private final ListaIngresso ingressos;

    public RelatorioView(ConsoleView console, Relatorio relatorio, ListaAtracao atracoes, ListaIngresso ingressos) {
        this.console = console;
        this.relatorio = relatorio;
        this.atracoes = atracoes;
        this.ingressos = ingressos;
    }

    public void menu() {
        boolean voltar = false;
        while (!voltar) {
            console.exibir("\n--- RELATÓRIOS ---");
            console.exibir("1. Exportar atrações em CSV");
            console.exibir("2. Receita de ingressos por mês");
            console.exibir("0. Voltar");
            switch (console.lerInteiro("Escolha: ")) {
                case 1 -> exportarCsv();
                case 2 -> receitaDoMes();
                case 0 -> voltar = true;
                default -> console.exibir("Opção inválida.");
            }
        }
    }

    private void exportarCsv() {
        String caminho = console.lerTextoOpcional("Arquivo de saída [atracoes.csv]: ");
        if (caminho.isEmpty()) caminho = "atracoes.csv";
        try {
            relatorio.salvarCsvAtracoes(atracoes.listarTodas(), caminho);
            exibirCsvGerado(caminho);
        } catch (Exception e) {
            exibirErro(e.getMessage());
        }
    }

    private void receitaDoMes() {
        LocalDate hoje = LocalDate.now();
        int mes = console.lerInteiro("Mês (1-12): ");
        int ano = console.lerInteiro("Ano [" + hoje.getYear() + "]: ");
        try {
            exibirReceita(relatorio.gerarRelatorioIngressos(ingressos.listarTodos(), Month.of(mes), ano), Month.of(mes), ano);
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

    public void exibirEscala(List<LinhaEscala> escala, int ano) {
        System.out.println("Escala de Operadores para os Feriados Nacionais de " + ano + " (BrasilAPI)");
        for (LinhaEscala linha : escala) {
            System.out.printf("Feriado: %s (%s) | Operador: %s%n", linha.feriado(), linha.data(), linha.operador());
        }
    }

    public void exibirCsvGerado(String caminho) {
        System.out.println("Relatório gerado com sucesso em: " + caminho);
    }

    public void exibirErro(String mensagem) {
        System.out.println("Erro ao gerar relatório: " + mensagem);
    }
}
