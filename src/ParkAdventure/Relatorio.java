package ParkAdventure;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Relatorio {
    public void gerarRelatorioAtracoesCSV(List<Atracao> atraçoes, String caminhoArquivo) {

        Atracao.sort((a1, a2) -> Integer.compare(a2.acesso, a1.acesso));

        try(FileWriter writer = new FileWriter(caminhoArquivo)){
            writer.append("Atracao, Acesso\n");
            for(Atracao a : atracoes){

                write.append(a.nome).append(",").append(String.valueOf(a.acesso)).append("\n");
            }
            System.out.println("Relatório gerado com sucesso em: " + caminhoArquivo);
        }
        catch (IOException e) {
            System.out.println("Erro ao gerar relatório: " + e.getMessage());
        }
    }

    public void gerarRelatorioInressos(list<VendaIngresso> vendas, Month mes, int ano) {
        List<FeriadoBrasilAPI> feriadosPI = buscarFeriadosBrasilAPI(ano);
        Set<LocalDate> datasFeriado= new HashSet<>();
        for (FeriadoBrasilAPI f: feriadosAPI) {
            datasFeriado.add(f.getData());
        }

        double receitaTotal = 0;
        double receitaFeriados = 0;

        System.out.println("Relatório de Vendas de Ingressos - " + mes + "/" + ano);
        for (VendasIngresso venda: vendas)
            if (venda.data.getMonth() == mes && venda.data.getYear() == ano) {
                receitaTotal += venda.valor;
                boolean isFeriado = datasFeriado.contains(venda.data);

                if (isFeriado) {
                    receitaFeriados += venda.valor;
                    System.out.printf("`[Destaque Feriado] Data %s | Valor: R$ %.2f\n", venda.data, venda.valor);
                } else {
                    System.out.printf("Data %s | Valor: R$ %.2f\n", venda.data, venda.valor);
                }
            }
        System.out.printf("Receita Total: R$ %.2f\n", receitaTotal);
        System.out.printf("Receita em Feriados: R$ %.2f\n", receitaFeriados);
    }

    public void listaEscalaProximosFeriados(Map<LocalDate, String> escalaOperadores, int ano) {
        List<FeriadoBrasilAPI> feriadosNacionais = buscarFeriadosBrasilAPI(ano);

        System.out.println("Escala de Operadores para os Próximos Feriados Nacionais de - " + ano + "(BrasilAPI)-- ");
        for (FeriadoBrasilAPI feriado : feriadosNacionais) {
            LocalDate data = feriado.getData();
            String operador =  escalaOperadores.getOrDefault(data, "Sem operador escalado");
            System.out.printf("Feriado: %s (%s) | Operador: %s\n", feriado.name, feriado.date, operador);
        }
    }

}
