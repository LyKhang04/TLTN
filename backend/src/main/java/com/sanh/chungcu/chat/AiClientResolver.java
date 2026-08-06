package com.sanh.chungcu.chat;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Chọn nhà cung cấp AI cho chatbot "Sảnh AI".
 *
 * Cấu hình bằng thuộc tính ai.provider trong application.yml:
 *   - "gemini"    : luôn dùng Google Gemini
 *   - "anthropic" : luôn dùng Anthropic Claude
 *   - "auto"      : (mặc định) ưu tiên nhà cung cấp nào đã có API key,
 *                   Gemini được xét trước vì có gói miễn phí.
 */
@Service
public class AiClientResolver {

    private final GeminiClient geminiClient;
    private final AnthropicClient anthropicClient;

    @Value("${ai.provider:auto}")
    private String provider;

    public AiClientResolver(GeminiClient geminiClient, AnthropicClient anthropicClient) {
        this.geminiClient = geminiClient;
        this.anthropicClient = anthropicClient;
    }

    @PostConstruct
    public void logSelection() {
        AiChatClient client = resolve();
        if (client == null) {
            System.out.println("[Sanh AI] Chưa cấu hình API key nào. "
                    + "Đặt GEMINI_API_KEY (miễn phí) hoặc ANTHROPIC_API_KEY để bật chatbot.");
        } else {
            System.out.println("[Sanh AI] Chatbot sẽ dùng: " + client.providerName());
        }
    }

    /** Trả về client đang dùng, hoặc null nếu chưa cấu hình key nào. */
    public AiChatClient resolve() {
        String p = provider == null ? "auto" : provider.trim().toLowerCase();

        if ("gemini".equals(p)) {
            return geminiClient.isConfigured() ? geminiClient : null;
        }
        if ("anthropic".equals(p)) {
            return anthropicClient.isConfigured() ? anthropicClient : null;
        }
        // auto: ưu tiên Gemini vì có gói miễn phí
        if (geminiClient.isConfigured()) {
            return geminiClient;
        }
        if (anthropicClient.isConfigured()) {
            return anthropicClient;
        }
        return null;
    }
}