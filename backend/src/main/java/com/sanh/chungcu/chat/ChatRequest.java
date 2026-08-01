package com.sanh.chungcu.chat;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChatRequest {
    private Integer residentId;
    private String message;
    /** Lịch sử hội thoại trước đó (không bắt buộc), mỗi phần tử: {role, content} */
    private List<ChatMessage> history;

    @Getter
    @Setter
    public static class ChatMessage {
        private String role;   // "user" hoặc "assistant"
        private String content;
    }
}
