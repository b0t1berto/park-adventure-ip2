package parkAdventure.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import parkAdventure.model.acesso.RegistroAcesso;
import parkAdventure.model.api.Feriados;
import parkAdventure.model.api.Feriados.Feriado;
import parkAdventure.model.atracao.Atracao;
import parkAdventure.model.ingresso.Ingresso;

// Monta os dados dos relatórios
public class Relatorio {

    public record LinhaVenda(LocalDate data, double valor, boolean feriado) {}

    public record ResumoReceita(List<LinhaVenda> vendas, double receitaTotal, double receitaFeriados) {}

    public record LinhaEscala(String feriado, LocalDate data, String operador) {}

    // REQ13: atrações mais acessadas no período (entradas registradas), da maior para a menor
    public String montarCsvAtracoes(List<Atracao> atracoes, List<RegistroAcesso> acessos,
                                    LocalDate inicio, LocalDate fim) {
        Map<String, Integer> contagem = new LinkedHashMap<>();
        for (Atracao a : atracoes) {
            contagem.put(a.getNome(), 0);
        }
        for (RegistroAcesso r : acessos) {
            LocalDate data = r.getEntrada().toLocalDate();
            if (!data.isBefore(inicio) && !data.isAfter(fim)) {
                contagem.merge(r.getNomeAtracao(), 1, Integer::sum);
            }
        }
        List<Map.Entry<String, Integer>> ordenadas = new ArrayList<>(contagem.entrySet());
        ordenadas.sort((e1, e2) -> e2.getValue() != e1.getValue().intValue()
                ? Integer.compare(e2.getValue(), e1.getValue())
                : e1.getKey().compareToIgnoreCase(e2.getKey()));

        StringBuilder csv = new StringBuilder("Atracao,Acessos,Inicio,Fim\n");
        for (Map.Entry<String, Integer> e : ordenadas) {
            csv.append(escapar(e.getKey())).append(",").append(e.getValue()).append(",")
               .append(inicio).append(",").append(fim).append("\n");
        }
        return csv.toString();
    }

    public void salvarCsvAtracoes(List<Atracao> atracoes, List<RegistroAcesso> acessos,
                                  LocalDate inicio, LocalDate fim, String caminhoArquivo) throws IOException {
        Files.writeString(Path.of(caminhoArquivo), montarCsvAtracoes(atracoes, acessos, inicio, fim));
    }

    private String escapar(String texto) {
        if (texto.contains(",") || texto.contains("\"") || texto.contains("\n")) {
            return "\"" + texto.replace("\"", "\"\"") + "\"";
        }
        return texto;
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

    // Próximos feriados nacionais a partir de hoje (ano atual e seguinte), em ordem de data
    public List<Feriado> proximosFeriados(LocalDate hoje, int quantidade)
            throws IOException, InterruptedException {
        List<Feriado> todos = new ArrayList<>(Feriados.buscarFeriadosBrasilAPI(hoje.getYear()));
        todos.addAll(Feriados.buscarFeriadosBrasilAPI(hoje.getYear() + 1));
        return todos.stream()
                .filter(f -> !LocalDate.parse(f.date()).isBefore(hoje))
                .sorted(Comparator.comparing(f -> LocalDate.parse(f.date())))
                .limit(quantidade)
                .toList();
    }

    // REQ15: operadores escalados para os próximos feriados nacionais
    public List<LinhaEscala> listarEscalaProximosFeriados(EscalaOperadores escala, LocalDate hoje, int quantidade)
            throws IOException, InterruptedException {
        List<LinhaEscala> linhas = new ArrayList<>();
        for (Feriado feriado : proximosFeriados(hoje, quantidade)) {
            LocalDate data = LocalDate.parse(feriado.date());
            linhas.add(new LinhaEscala(feriado.name(), data, escala.descrever(data)));
        }
        return linhas;
    }

    private Set<LocalDate> buscarDatasFeriado(int ano) throws IOException, InterruptedException {
        Set<LocalDate> datas = new HashSet<>();
        for (Feriado f : Feriados.buscarFeriadosBrasilAPI(ano)) {
            datas.add(LocalDate.parse(f.date()));
        }
        return datas;
    }
}
