package com.ufrpe.parkadventure.model.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Fonte de dados externa usada pelo sistema
public class Feriados {

    private static final String URL_BASE = "https://brasilapi.com.br/api/feriados/v1/";

    public static List<Feriado> buscarFeriadosBrasilAPI(int ano) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + ano))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            return List.of();
        }
        return new ObjectMapper().readValue(response.body(), new TypeReference<List<Feriado>>() {});
    }

    /**
     * Retorna os feriados nacionais brasileiros embutidos como fallback
     * quando a BrasilAPI está indisponível.
     * Inclui feriados fixos e móveis (Carnaval, Páscoa, Sexta-feira Santa).
     */
    public static List<Feriado> feriadosNacionaisEmbutidos(int ano) {
        List<Feriado> lista = new ArrayList<>();

        // --- Feriados fixos ---
        lista.add(new Feriado(LocalDate.of(ano, 1, 1).toString(), "Conmemoração do Ano Novo", "F"));
        lista.add(new Feriado(LocalDate.of(ano, 4, 21).toString(), "Tiradentes", "F"));
        lista.add(new Feriado(LocalDate.of(ano, 5, 1).toString(), "Dia do Trabalho", "F"));
        lista.add(new Feriado(LocalDate.of(ano, 9, 7).toString(), "Independência do Brasil", "F"));
        lista.add(new Feriado(LocalDate.of(ano, 10, 12).toString(), "Nossa Senhora Aparecida", "F"));
        lista.add(new Feriado(LocalDate.of(ano, 10, 28).toString(), "Dia de Finados", "F"));
        lista.add(new Feriado(LocalDate.of(ano, 11, 15).toString(), "Proclamação da República", "F"));
        lista.add(new Feriado(LocalDate.of(ano, 11, 20).toString(), "Dia da Consciência Negra", "F"));

        // --- Feriados móveis baseados na Páscoa ---
        LocalDate pascoa = calcularPascoa(ano);
        LocalDate sextaSanta = pascoa.minusDays(2);
        LocalDate carnaval = pascoa.minusWeeks(47);

        lista.add(new Feriado(carnaval.toString(), "Carnaval", "F"));
        lista.add(new Feriado(sextaSanta.toString(), "Sexta-feira Santa", "F"));

        return lista;
    }

    /**
     * Algoritmo de Computus (Meeus/Jones/Butcher) para calcular a Páscoa no calendário gregoriano.
     */
    private static LocalDate calcularPascoa(int ano) {
        int a = ano % 19;
        int b = ano / 100;
        int c = ano % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int mes = (h + l - 7 * m + 114) / 31;
        int dia = ((h + l - 7 * m + 114) % 31) + 1;
        return LocalDate.of(ano, mes, dia);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Feriado(String date, String name, String type) {}
}
