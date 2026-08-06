package com.sanh.chungcu.chat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Gọi Google Gemini API (Generative Language API).
 *
 * Gemini có gói miễn phí không cần thẻ tín dụng, phù hợp cho môi trường học tập.
 * Lấy API key tại: https://aistudio.google.com/app/apikey
 *
 * Khác biệt về định dạng so với Anthropic:
 *  - Vai trò của trợ lý là "model" thay vì "assistant".
 *  - Nội dung nằm trong mảng "parts", mỗi phần tử có trường "text".
 *  - System prompt truyền qua trường riêng "systemInstruction".
 */
@Service
public class GeminiClient implements AiChatClient {

    private final GeminiProperties properties;
    private final HttpClient httpClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public GeminiClient(GeminiProperties properties) {
        this.properties = properties;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public String providerName() {
        return "Google Gemini";
    }

    @Override
    public boolean isConfigured() {
        return AiChatClient.sanitizeKey(properties.getApiKey()) != null;
    }

    @Override
    public String sendMessage(String systemPrompt, List<ChatRequest.ChatMessage> turns) throws Exception {
        String apiKey = AiChatClient.sanitizeKey(properties.getApiKey());

        // Chuyển hội thoại sang định dạng "contents" của Gemini
        List<Map<String, Object>> contents = new ArrayList<>();
        for (ChatRequest.ChatMessage t : turns) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("role", "user".equals(t.getRole()) ? "user" : "model");
            item.put("parts", List.of(Map.of("text", t.getContent())));
            contents.add(item);
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("contents", contents);
        body.put("systemInstruction", Map.of("parts", List.of(Map.of("text", systemPrompt))));
        body.put("generationConfig", Map.of("maxOutputTokens", 600, "temperature", 0.7));

        String json = mapper.writeValueAsString(body);

        String url = properties.getBaseUrl()
                + "/" + URLEncoder.encode(properties.getModel(), StandardCharsets.UTF_8)
                + ":generateContent";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                // Gemini nhận key qua header x-goog-api-key (an toàn hơn đặt trên URL)
                .header("x-goog-api-key", apiKey)
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            if (response.statusCode() == 400 || response.statusCode() == 403) {
                String k = apiKey;
                String fingerprint = k == null ? "(rỗng)"
                        : k.substring(0, Math.min(10, k.length())) + "... [độ dài: " + k.length() + "]";
                System.err.println("[Sanh AI] Gemini từ chối API key. Key đang dùng: " + fingerprint);
                System.err.println("[Sanh AI] Key Gemini hợp lệ thường bắt đầu bằng 'AIza' và dài khoảng 39 ký tự.");
            }
            if (response.statusCode() == 429) {
                System.err.println("[Sanh AI] Đã chạm giới hạn miễn phí của Gemini. Chờ ít phút rồi thử lại.");
            }
            if (response.statusCode() == 404) {
                System.err.println("[Sanh AI] Model '" + properties.getModel() + "' không còn khả dụng.");
                System.err.println("[Sanh AI] Google thường xuyên ngừng hỗ trợ model cũ. Hãy đổi 'gemini.model' "
                        + "trong application.yml sang model mới, xem danh sách tại "
                        + "https://ai.google.dev/gemini-api/docs/models");
            }
            throw new RuntimeException("Gemini API lỗi (" + response.statusCode() + "): " + response.body());
        }

        JsonNode root = mapper.readTree(response.body());
        StringBuilder text = new StringBuilder();
        for (JsonNode part : root.path("candidates").path(0).path("content").path("parts")) {
            text.append(part.path("text").asText(""));
        }

        if (text.length() == 0) {
            // Gemini có thể chặn câu trả lời vì bộ lọc an toàn
            String reason = root.path("candidates").path(0).path("finishReason").asText("");
            if (!reason.isBlank() && !"STOP".equals(reason)) {
                return "Mình chưa trả lời được câu này (lý do: " + reason
                        + "). Bạn thử diễn đạt lại giúp mình nhé.";
            }
        }
        return text.toString();
    }
}