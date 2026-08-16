package br.com.MeuPlanner.pluggy;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.MeuPlanner.config.AppConfig;
import br.com.MeuPlanner.exception.BusinessException;

/**
 * Cliente pro fluxo "Meu Pluggy" (uso pessoal, gratuito): autentica com
 * CLIENT_ID/CLIENT_SECRET, gera um Connect Token pro widget de conexão do
 * banco, e depois consulta contas/transações do item conectado.
 */
public class PluggyClient {

    private static final String BASE_URL = "https://api.pluggy.ai";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final ObjectMapper json = new ObjectMapper();

    private String apiKey;
    private Instant apiKeyExpiraEm = Instant.EPOCH;

    public String criarConnectToken(String clientUserId) {
        Map<String, Object> corpo = new HashMap<>();
        if (clientUserId != null && !clientUserId.isBlank()) {
            corpo.put("clientUserId", clientUserId);
        }
        JsonNode resposta = post("/connect_token", corpo, true);
        return textoOuNulo(resposta, "accessToken");
    }

    public List<PluggyConta> listarContas(String itemId) {
        JsonNode resposta = get("/accounts?itemId=" + itemId);
        List<PluggyConta> contas = new ArrayList<>();
        for (JsonNode c : resposta.path("results")) {
            contas.add(new PluggyConta(
                    textoOuNulo(c, "id"),
                    textoOuNulo(c, "name"),
                    textoOuNulo(c, "type"),
                    c.path("balance").isMissingNode() ? BigDecimal.ZERO : new BigDecimal(c.get("balance").asText())
            ));
        }
        return contas;
    }

    public List<PluggyTransacao> listarTransacoes(String accountId) {
        List<PluggyTransacao> transacoes = new ArrayList<>();
        int pagina = 1;
        int totalPaginas = 1;
        do {
            JsonNode resposta = get("/transactions?accountId=" + accountId + "&page=" + pagina + "&pageSize=500");
            for (JsonNode t : resposta.path("results")) {
                transacoes.add(new PluggyTransacao(
                        textoOuNulo(t, "id"),
                        textoOuNulo(t, "description"),
                        new BigDecimal(t.get("amount").asText()),
                        LocalDate.parse(t.get("date").asText().substring(0, 10), DateTimeFormatter.ISO_LOCAL_DATE)
                ));
            }
            totalPaginas = resposta.path("totalPages").asInt(1);
            pagina++;
        } while (pagina <= totalPaginas);
        return transacoes;
    }

    private synchronized String apiKey() {
        if (apiKey == null || Instant.now().isAfter(apiKeyExpiraEm)) {
            autenticar();
        }
        return apiKey;
    }

    private void autenticar() {
        String clientId = AppConfig.getInstance().get("pluggy.client.id", "");
        String clientSecret = AppConfig.getInstance().get("pluggy.client.secret", "");
        if (clientId.isBlank() || clientSecret.isBlank()) {
            throw new BusinessException(
                    "Configure pluggy.client.id e pluggy.client.secret (ou PLUGGY_CLIENT_ID/PLUGGY_CLIENT_SECRET) "
                    + "pra usar o Open Finance — pegue as credenciais em meu.pluggy.ai.");
        }
        JsonNode resposta = post("/auth", Map.of("clientId", clientId, "clientSecret", clientSecret), false);
        apiKey = textoOuNulo(resposta, "apiKey");
        if (apiKey == null) {
            throw new BusinessException("Pluggy não retornou uma apiKey — confira suas credenciais.");
        }
        apiKeyExpiraEm = Instant.now().plus(Duration.ofMinutes(110));
    }

    private JsonNode get(String caminho) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + caminho))
                    .timeout(Duration.ofSeconds(30))
                    .header("X-API-KEY", apiKey())
                    .GET()
                    .build();
            return enviar(request);
        } catch (IOException | InterruptedException e) {
            throw new BusinessException("Não foi possível falar com a API do Pluggy: " + e.getMessage());
        }
    }

    private JsonNode post(String caminho, Map<String, Object> corpo, boolean autenticado) {
        try {
            String corpoJson = json.writeValueAsString(corpo);
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + caminho))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(corpoJson));
            if (autenticado) {
                builder.header("X-API-KEY", apiKey());
            }
            return enviar(builder.build());
        } catch (IOException | InterruptedException e) {
            throw new BusinessException("Não foi possível falar com a API do Pluggy: " + e.getMessage());
        }
    }

    private JsonNode enviar(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> resposta = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (resposta.statusCode() >= 400) {
            throw new BusinessException("Pluggy respondeu " + resposta.statusCode() + ": " + resposta.body());
        }
        return json.readTree(resposta.body());
    }

    private String textoOuNulo(JsonNode node, String campo) {
        JsonNode valor = node.get(campo);
        return valor == null || valor.isNull() ? null : valor.asText();
    }
}
