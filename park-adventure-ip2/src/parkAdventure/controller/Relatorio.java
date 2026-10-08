package controller;

import api.Feriados;
import api.Feriados.Feriado;
import classesExtras.Ingresso;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Relatorio {

    public void gerarRelatorioAtracoesCSV(List<Atracao> atracoes, String caminhoArquivo) {

        atracoes.sort((a1, a2) -> Integer.compare(a2.getOcupacaoAtual(), a1.getOcupacaoAtual()));

        try (FileWriter writer = new FileWriter(caminhoArquivo)) {
            writer.append("Atracao,Ocupacao\n");
            for (Atracao a : atracoes) {
                writer.append(a.getNome()).append(",").append(String.valueOf(a.getOcupacaoAtual())).append("\n");
            }
            System.out.println("Relatório gerado com sucesso em: " + caminhoArquivo);
        } catch (IOException e) {
            System.out.println("Erro ao gerar relatório: " + e.getMessage());
        }
    }

    public void gerarRelatorioIngressos(List<Ingresso> vendas, Month mes, int ano) throws Exception {
        List<Feriado> feriadosAPI = Feriados.buscarFeriadosBrasilAPI(ano);
        Set<LocalDate> datasFeriado = new HashSet<>();
        for (Feriado f : feriadosAPI) {
            datasFeriado.add(LocalDate.parse(f.date()));
        }

        double receitaTotal = 0;
        double receitaFeriados = 0;

        System.out.println("Relatório de Vendas de Ingressos - " + mes + "/" + ano);
        for (Ingresso venda : vendas) {
            if (venda.getDataCompra().getMonth() == mes && venda.getDataCompra().getYear() == ano) {
                receitaTotal += venda.getValor();
                boolean isFeriado = datasFeriado.contains(venda.getDataCompra());

                if (isFeriado) {
                    receitaFeriados += venda.getValor();
                    System.out.printf("[Destaque Feriado] Data %s | Valor: R$ %.2f%n", venda.getDataCompra(), venda.getValor());
                } else {
                    System.out.printf("Data %s | Valor: R$ %.2f%n", venda.getDataCompra(), venda.getValor());
                }
            }
        }
        System.out.printf("Receita Total: R$ %.2f%n", receitaTotal);
        System.out.printf("Receita em Feriados: R$ %.2f%n", receitaFeriados);
    }

    public void listaEscalaProximosFeriados(Map<LocalDate, String> escalaOperadores, int ano) throws Exception {
        List<Feriado> feriadosNacionais = Feriados.buscarFeriadosBrasilAPI(ano);

        System.out.println("Escala de Operadores para os Próximos Feriados Nacionais de " + ano + " (BrasilAPI) --");
        for (Feriado feriado : feriadosNacionais) {
            LocalDate data = LocalDate.parse(feriado.date());
            String operador = escalaOperadores.getOrDefault(data, "Sem operador escalado");
            System.out.printf("Feriado: %s (%s) | Operador: %s%n", feriado.name(), feriado.date(), operador);
        }
    }
}
