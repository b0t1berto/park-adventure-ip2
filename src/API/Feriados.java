package API;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class Feriados {

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Feriado(String date, String name, String type) {}

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://brasilapi.com.br/api/feriados/v1/2026"))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            ObjectMapper mapper = new ObjectMapper();
            List<Feriado> feriados = mapper.readValue(
                    response.body(),
                    new TypeReference<List<Feriado>>() {});

            feriados.forEach(f -> System.out.println(f.date() + " - " + f.name()));
        }
    }
}
