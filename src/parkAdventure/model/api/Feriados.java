package parkAdventure.model.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Feriado(String date, String name, String type) {}
}
