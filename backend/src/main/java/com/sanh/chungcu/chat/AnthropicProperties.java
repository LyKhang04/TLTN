package com.sanh.chungcu.chat;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "anthropic")
@Getter
@Setter
public class AnthropicProperties {
    /** Đọc từ bien moi truong ANTHROPIC_API_KEY, xem application.yml */
    private String apiKey;
    private String model = "claude-sonnet-5";
    private String baseUrl = "https://api.anthropic.com/v1/messages";
}
