package com.sanh.chungcu.chat;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "gemini")
@Getter
@Setter
public class GeminiProperties {
    /** Đọc từ biến môi trường GEMINI_API_KEY, xem application.yml */
    private String apiKey;
    /** Model có gói miễn phí. Xem danh sách mới nhất: https://ai.google.dev/gemini-api/docs/models */
    private String model = "gemini-3-flash-preview";
    private String baseUrl = "https://generativelanguage.googleapis.com/v1beta/models";
}