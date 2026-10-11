package parkAdventure.view;

import java.time.Month;
import java.util.List;

import parkAdventure.controller.Relatorio.LinhaEscala;
import parkAdventure.controller.Relatorio.LinhaVenda;
import parkAdventure.controller.Relatorio.ResumoReceita;

public class RelatorioView {

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
