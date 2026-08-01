package com.sanh.chungcu.chat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnthropicClient {

    private final AnthropicProperties properties;
    private final HttpClient httpClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public AnthropicClient(AnthropicProperties properties) {
        this.properties = properties;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public boolean isConfigured() {
        return properties.getApiKey() != null && !properties.getApiKey().isBlank();
    }

    /**
     * Gọi Anthropic Messages API. systemPrompt chứa vai trò + dữ liệu ngữ cảnh cư dân,
     * turns là lịch sử hội thoại (đã bao gồm câu hỏi mới nhất của người dùng).
     */
    public String sendMessage(String systemPrompt, List<ChatRequest.ChatMessage> turns) throws Exception {
        List<Map<String, String>> messages = new ArrayList<>();
        for (ChatRequest.ChatMessage t : turns) {
            Map<String, String> m = new LinkedHashMap<>();
            m.put("role", "user".equals(t.getRole()) ? "user" : "assistant");
            m.put("content", t.getContent());
            messages.add(m);
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", properties.getModel());
        body.put("max_tokens", 600);
        body.put("system", systemPrompt);
        body.put("messages", messages);

        String json = mapper.writeValueAsString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(properties.getBaseUrl()))
                .header("Content-Type", "application/json")
                .header("x-api-key", properties.getApiKey())
                .header("anthropic-version", "2023-06-01")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Anthropic API lỗi (" + response.statusCode() + "): " + response.body());
        }

        JsonNode root = mapper.readTree(response.body());
        StringBuilder text = new StringBuilder();
        for (JsonNode block : root.path("content")) {
            if ("text".equals(block.path("type").asText())) {
                text.append(block.path("text").asText());
            }
        }
        return text.toString();
    }
}
