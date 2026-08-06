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
        return sanitizedApiKey() != null;
    }

    /**
     * Làm sạch API key trước khi gửi đi.
     * Khi dán key vào ô Environment variables của IDE hoặc vào file .env, key rất dễ
     * dính dấu nháy bao ngoài hoặc khoảng trắng/xuống dòng ở hai đầu — những ký tự này
     * khiến Anthropic trả về lỗi 401 "API key is invalid".
     */
    private String sanitizedApiKey() {
        String key = properties.getApiKey();
        if (key == null) {
            return null;
        }
        key = key.trim();
        // Bỏ dấu nháy đơn/kép bao quanh nếu người dùng lỡ dán cả dấu nháy
        if (key.length() >= 2
                && ((key.startsWith("\"") && key.endsWith("\""))
                || (key.startsWith("'") && key.endsWith("'")))) {
            key = key.substring(1, key.length() - 1).trim();
        }
        return key.isBlank() ? null : key;
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
                .header("x-api-key", sanitizedApiKey())
                .header("anthropic-version", "2023-06-01")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            if (response.statusCode() == 401) {
                // In ra "vân tay" của key (đã che) để tự kiểm tra mà không lộ key thật.
                String k = sanitizedApiKey();
                String fingerprint = k == null ? "(rỗng)"
                        : k.substring(0, Math.min(14, k.length())) + "..." + " [độ dài: " + k.length() + "]";
                System.err.println("[Sanh AI] Anthropic từ chối API key. Key đang dùng: " + fingerprint);
                System.err.println("[Sanh AI] Key hợp lệ thường bắt đầu bằng 'sk-ant-api03-' và dài trên 100 ký tự.");
            }
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