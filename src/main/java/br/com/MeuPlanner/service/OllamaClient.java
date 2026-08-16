package br.com.MeuPlanner.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.MeuPlanner.exception.BusinessException;

public class OllamaClient {

    private static final String URL_BASE = "http://localhost:11434/api/generate";
    private static final String MODELO = "llama3.2";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private final ObjectMapper json = new ObjectMapper();

    public String gerar(String prompt) {
        try {
            String corpo = json.writeValueAsString(Map.of(
                    "model", MODELO,
                    "prompt", prompt,
                    "stream", false
            ));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL_BASE))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(corpo))
                    .build();

            HttpResponse<String> resposta = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (resposta.statusCode() != 200) {
                throw new BusinessException(
                        "Ollama respondeu " + resposta.statusCode()
                        + " — confira se o modelo está baixado (ollama pull " + MODELO + ")");
            }

            Map<?, ?> corpoResposta = json.readValue(resposta.body(), Map.class);
            return String.valueOf(corpoResposta.get("response")).trim();
        } catch (IOException | InterruptedException e) {
            throw new BusinessException("Não foi possível falar com o Ollama — ele está rodando? (ollama serve)");
        }
    }
}