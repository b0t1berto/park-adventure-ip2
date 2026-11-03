package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.model.api.Feriados;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class FeriadosController {
    private static FeriadosController instancia;
    private final Map<Integer, Map<LocalDate, String>> cache = new ConcurrentHashMap<>();

    private FeriadosController() {}

    public static synchronized FeriadosController getInstancia() {
        if (instancia == null) {
            instancia = new FeriadosController();
        }
        return instancia;
    }
    public Map<LocalDate, String> carregarAno(int ano) throws IOException, InterruptedException {
        Map<LocalDate, String> emCache = cache.get(ano);
        if (emCache != null) {
            return emCache;
        }
        List<Feriados.Feriado> lista = Feriados.buscarFeriadosBrasilAPI(ano);
        if (lista.isEmpty()) {
            throw new IOException("A BrasilAPI não retornou feriados para " + ano);
        }
        Map<LocalDate, String> porData = new TreeMap<>();
        for (Feriados.Feriado f : lista) {
            porData.put(LocalDate.parse(f.date()), f.name());
        }
        Map<LocalDate, String> somenteLeitura = Collections.unmodifiableMap(porData);
        cache.put(ano, somenteLeitura);
        return somenteLeitura;
    }

    public boolean anoCarregado(int ano) {
        return cache.containsKey(ano);
    }

    public Optional<String> nomeDoFeriado(LocalDate data) {
        Map<LocalDate, String> ano = cache.get(data.getYear());
        return ano == null ? Optional.empty() : Optional.ofNullable(ano.get(data));
    }

    public Optional<Map.Entry<LocalDate, String>> proximoFeriado(LocalDate aPartirDe) {
        for (int ano = aPartirDe.getYear(); ano <= aPartirDe.getYear() + 1; ano++) {
            Map<LocalDate, String> feriadosDoAno = cache.get(ano);
            if (feriadosDoAno == null) {
                continue;
            }
            for (Map.Entry<LocalDate, String> entrada : feriadosDoAno.entrySet()) {
                if (!entrada.getKey().isBefore(aPartirDe)) {
                    return Optional.of(entrada);
                }
            }
        }
        return Optional.empty();
    }
}