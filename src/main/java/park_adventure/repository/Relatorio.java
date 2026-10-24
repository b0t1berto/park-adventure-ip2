package park_adventure.repository;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import park_adventure.model.api.Feriados;
import park_adventure.model.api.Feriados.Feriado;
import park_adventure.model.atracao.Atracao;
import park_adventure.model.ingresso.Ingresso;

// Monta os dados dos relatórios
public class Relatorio {

    public record LinhaVenda(LocalDate data, double valor, boolean feriado) {}

    public record ResumoReceita(List<LinhaVenda> vendas, double receitaTotal, double receitaFeriados) {}

    public record LinhaEscala(String feriado, LocalDate data, String operador) {}

    public String montarCsvAtracoes(List<Atracao> atracoes) {
        List<Atracao> ordenadas = new ArrayList<>(atracoes);
        ordenadas.sort((a1, a2) -> Integer.compare(a2.getOcupacaoAtual(), a1.getOcupacaoAtual()));

        StringBuilder csv = new StringBuilder("Atracao,Ocupacao\n");
        for (Atracao a : ordenadas) {
            csv.append(a.getNome()).append(",").append(a.getOcupacaoAtual()).append("\n");
        }
        return csv.toString();
    }

    public void salvarCsvAtracoes(List<Atracao> atracoes, String caminhoArquivo) throws IOException {
        try (FileWriter writer = new FileWriter(caminhoArquivo)) {
            writer.write(montarCsvAtracoes(atracoes));
        }
    }

    public ResumoReceita gerarRelatorioIngressos(List<Ingresso> vendas, Month mes, int ano)
            throws IOException, InterruptedException {
        Set<LocalDate> datasFeriado = buscarDatasFeriado(ano);

        List<LinhaVenda> linhas = new ArrayList<>();
        double receitaTotal = 0;
        double receitaFeriados = 0;

        for (Ingresso venda : vendas) {
            LocalDate data = venda.getDataCompra();
            if (data.getMonth() == mes && data.getYear() == ano) {
                boolean feriado = datasFeriado.contains(data);
                receitaTotal += venda.getValor();
                if (feriado) {
                    receitaFeriados += venda.getValor();
                }
                linhas.add(new LinhaVenda(data, venda.getValor(), feriado));
            }
        }
        return new ResumoReceita(linhas, receitaTotal, receitaFeriados);
    }

    public List<LinhaEscala> listarEscalaProximosFeriados(Map<LocalDate, String> escalaOperadores, int ano)
            throws IOException, InterruptedException {
        List<LinhaEscala> escala = new ArrayList<>();
        for (Feriado feriado : Feriados.buscarFeriadosBrasilAPI(ano)) {
            LocalDate data = LocalDate.parse(feriado.date());
            escala.add(new LinhaEscala(feriado.name(), data,
                    escalaOperadores.getOrDefault(data, "Sem operador escalado")));
        }
        return escala;
    }

    private Set<LocalDate> buscarDatasFeriado(int ano) throws IOException, InterruptedException {
        Set<LocalDate> datas = new HashSet<>();
        for (Feriado f : Feriados.buscarFeriadosBrasilAPI(ano)) {
            datas.add(LocalDate.parse(f.date()));
        }
        return datas;
    }
}
