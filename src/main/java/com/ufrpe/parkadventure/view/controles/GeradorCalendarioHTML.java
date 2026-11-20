package com.ufrpe.parkadventure.view.controles;

import com.ufrpe.parkadventure.app.Contexto;
import com.ufrpe.parkadventure.service.ServicoEscala;
import com.ufrpe.parkadventure.service.ServicoFeriados;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Gera uma página HTML estática com o calendário anual de feriados e os dias
 * de maior movimento (mais operadores escalados), e a abre no navegador padrão.
 */
public final class GeradorCalendarioHTML {

    private GeradorCalendarioHTML() {}

    /**
     * Gera o arquivo HTML e o abre no navegador.
     *
     * @param ano o ano do calendário
     * @return o arquivo HTML gerado
     * @throws IOException se não for possível criar o arquivo ou abrir o navegador
     */
    public static File gerarEAbrir(int ano) throws IOException {
        File arquivo = gerarHTML(ano);
        abrirNoNavegador(arquivo);
        return arquivo;
    }

    /**
     * Gera o arquivo HTML do calendário.
     */
    public static File gerarHTML(int ano) throws IOException {
        ServicoFeriados feriados = Contexto.FERIADOS;
        ServicoEscala escala = Contexto.SERVICO_ESCALA;

        // Carregar feriados do ano (pode estar em cache)
        Map<LocalDate, String> feriadosDoAno = new TreeMap<>();
        if (feriados.anoCarregado(ano)) {
            // O cache é interno; usamos nomeDoFeriado para iterar mês a mês
            for (int mes = 1; mes <= 12; mes++) {
                YearMonth ym = YearMonth.of(ano, mes);
                for (int dia = 1; dia <= ym.lengthOfMonth(); dia++) {
                    LocalDate d = ym.atDay(dia);
                    feriados.nomeDoFeriado(d).ifPresent(nome -> feriadosDoAno.put(d, nome));
                }
            }
        }

        // Calcular dias de maior movimento (top dias com mais operadores escalados)
        Map<LocalDate, Integer> operadoresPorDia = new TreeMap<>();
        YearMonth inicio = YearMonth.of(ano, 1);
        YearMonth fim = YearMonth.of(ano, 12);
        LocalDate cursor = inicio.atDay(1);
        LocalDate limite = fim.atEndOfMonth().plusDays(1);
        while (!cursor.isBefore(limite)) {
            int count = escala.operadoresDoDia(cursor).size();
            if (count > 0) {
                operadoresPorDia.put(cursor, count);
            }
            cursor = cursor.plusDays(1);
        }

        // Determinar o limiar para "maior movimento" — dias no top 10% ou com >= 5 operadores
        int limiar = Math.max(5, operadoresPorDia.values().stream()
                .mapToInt(Integer::intValue).max().orElse(1));
        Set<LocalDate> diasMaiorMovimento = operadoresPorDia.entrySet().stream()
                .filter(e -> e.getValue() >= limiar)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        // Arquivo de saída
        File dir = new File(System.getProperty("user.home"), "ParkAdventure");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File arquivo = new File(dir, "calendario-" + ano + ".html");

        try (PrintWriter pw = new PrintWriter(arquivo, "UTF-8")) {
            escreverHTML(pw, ano, feriadosDoAno, operadoresPorDia, diasMaiorMovimento);
        }
        return arquivo;
    }

    private static void abrirNoNavegador(File arquivo) throws IOException {
        try {
            java.awt.Desktop.getDesktop().browse(arquivo.toURI());
        } catch (Exception e) {
            throw new IOException("Não foi possível abrir o navegador", e);
        }
    }

    private static void escreverHTML(PrintWriter pw, int ano,
                                      Map<LocalDate, String> feriadosDoAno,
                                      Map<LocalDate, Integer> operadoresPorDia,
                                      Set<LocalDate> diasMaiorMovimento) {
        pw.println("<!DOCTYPE html>");
        pw.println("<html lang=\"pt-BR\">");
        pw.println("<head>");
        pw.println("  <meta charset=\"UTF-8\">");
        pw.println("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        pw.println("  <title>Park Adventure — Calendário " + ano + "</title>");
        pw.println("  <style>");
        pw.println("    * { margin: 0; padding: 0; box-sizing: border-box; }");
        pw.println("    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: #f5f5f5; color: #333; padding: 20px; }");
        pw.println("    h1 { text-align: center; margin-bottom: 10px; color: #2c6e49; font-size: 2em; }");
        pw.println("    .legenda { text-align: center; margin-bottom: 20px; font-size: 0.9em; }");
        pw.println("    .legenda span { display: inline-block; margin: 0 10px; padding: 2px 8px; border-radius: 4px; }");
        pw.println("    .legenda .feriado { background: #ffe0e0; border: 1px solid #e06060; }");
        pw.println("    .legenda .movimento { background: #e0f0ff; border: 1px solid #60a0e0; }");
        pw.println("    .legenda .hoje { background: #e0ffe0; border: 1px solid #60c060; }");
        pw.println("    .grid-anual { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 20px; max-width: 1400px; margin: 0 auto; }");
        pw.println("    .mes { background: #fff; border-radius: 8px; box-shadow: 0 2px 6px rgba(0,0,0,0.1); overflow: hidden; }");
        pw.println("    .mes-titulo { background: #2c6e49; color: #fff; text-align: center; padding: 8px; font-weight: bold; font-size: 1.1em; }");
        pw.println("    table { width: 100%; border-collapse: collapse; }");
        pw.println("    th { background: #e8e8e8; padding: 6px 2px; font-size: 0.8em; color: #555; }");
        pw.println("    td { text-align: center; padding: 6px 2px; font-size: 0.9em; position: relative; }");
        pw.println("    td.feriado { background: #ffe0e0; font-weight: bold; color: #c0392b; }");
        pw.println("    td.movimento { background: #e0f0ff; font-weight: bold; color: #2471a3; }");
        pw.println("    td.hoje { background: #e0ffe0; border: 2px solid #27ae60; }");
        pw.println("    td.vazio { background: #fafafa; }");
        pw.println("    .tooltip { display: none; position: absolute; bottom: 100%; left: 50%; transform: translateX(-50%); background: #333; color: #fff; padding: 4px 8px; border-radius: 4px; font-size: 0.75em; white-space: nowrap; z-index: 10; }");
        pw.println("    td:hover .tooltip { display: block; }");
        pw.println("    .rodape { text-align: center; margin-top: 20px; font-size: 0.8em; color: #888; }");
        pw.println("  </style>");
        pw.println("</head>");
        pw.println("<body>");
        pw.println("  <h1>🎢 Park Adventure — Calendário " + ano + "</h1>");
        pw.println("  <div class=\"legenda\">");
        pw.println("    <span class=\"feriado\">🔴 Feriado nacional</span>");
        pw.println("    <span class=\"movimento\">🔵 Maior movimento</span>");
        pw.println("    <span class=\"hoje\">🟢 Hoje</span>");
        pw.println("  </div>");
        pw.println("  <div class=\"grid-anual\">");

        LocalDate hoje = LocalDate.now();

        String[] nomesMeses = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        String[] diasSemana = {"Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb"};

        for (int mes = 1; mes <= 12; mes++) {
            YearMonth ym = YearMonth.of(ano, mes);
            pw.println("    <div class=\"mes\">");
            pw.println("      <div class=\"mes-titulo\">" + nomesMeses[mes - 1] + " " + ano + "</div>");
            pw.println("      <table>");
            pw.println("        <tr>");
            for (String ds : diasSemana) {
                pw.println("          <th>" + ds + "</th>");
            }
            pw.println("        </tr>");

            int primeiroDiaSemana = ym.atDay(1).getDayOfWeek().getValue(); // 1=Mon .. 7=Sun
            int offset = (primeiroDiaSemana == 7) ? 6 : primeiroDiaSemana - 1; // DOM=0
            int diasNoMes = (int) ym.lengthOfMonth();

            pw.println("        <tr>");
            int col = 0;
            // Células vazias antes do primeiro dia
            for (int i = 0; i < offset; i++) {
                pw.println("          <td class=\"vazio\"></td>");
                col++;
            }

            for (int dia = 1; dia <= diasNoMes; dia++) {
                if (col == 7) {
                    pw.println("        </tr><tr>");
                    col = 0;
                }
                LocalDate d = ym.atDay(dia);
                StringBuilder classes = new StringBuilder();
                StringBuilder tooltip = new StringBuilder();

                boolean isHoje = d.equals(hoje);
                String feriado = feriadosDoAno.get(d);
                Integer operCount = operadoresPorDia.get(d);
                boolean isMaiorMovimento = diasMaiorMovimento.contains(d);

                if (isHoje) classes.append("hoje");
                if (feriado != null) {
                    if (classes.length() > 0) classes.append(" ");
                    classes.append("feriado");
                    tooltip.append(feriado);
                }
                if (isMaiorMovimento) {
                    if (classes.length() > 0) classes.append(" ");
                    classes.append("movimento");
                    if (tooltip.length() > 0) tooltip.append(" | ");
                    tooltip.append(operCount).append(" operadores");
                } else if (operCount != null && operCount > 0) {
                    if (tooltip.length() > 0) tooltip.append(" | ");
                    tooltip.append(operCount).append(" operadores");
                }

                String cls = classes.length() > 0 ? " class=\"" + classes + "\"" : "";
                pw.print("          <td" + cls + ">" + dia);
                if (tooltip.length() > 0) {
                    pw.println("<span class=\"tooltip\">" + tooltip + "</span>");
                } else {
                    pw.println();
                }
                pw.println("</td>");
                col++;
            }
            // Completar a última linha
            while (col < 7) {
                pw.println("          <td class=\"vazio\"></td>");
                col++;
            }
            pw.println("        </tr>");
            pw.println("      </table>");
            pw.println("    </div>");
        }

        pw.println("  </div>");
        pw.println("  <div class=\"rodape\">Gerado em " + LocalDate.now().toString() + " — Park Adventure</div>");
        pw.println("</body>");
        pw.println("</html>");
    }
}
