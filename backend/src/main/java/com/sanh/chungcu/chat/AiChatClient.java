package com.sanh.chungcu.chat;

import java.util.List;

/**
 * Giao diện chung cho các nhà cung cấp mô hình ngôn ngữ lớn (LLM).
 * Nhờ lớp trừu tượng này, hệ thống có thể đổi giữa Anthropic Claude và
 * Google Gemini chỉ bằng cấu hình, không phải sửa lại ChatController.
 */
public interface AiChatClient {

    /** Tên nhà cung cấp, dùng cho log và thông báo lỗi. */
    String providerName();

    /** Đã có API key hợp lệ hay chưa. */
    boolean isConfigured();

    /**
     * Gửi hội thoại tới mô hình và trả về câu trả lời dạng văn bản.
     *
     * @param systemPrompt vai trò của trợ lý + dữ liệu ngữ cảnh của cư dân
     * @param turns        lịch sử hội thoại, đã bao gồm câu hỏi mới nhất
     */
    String sendMessage(String systemPrompt, List<ChatRequest.ChatMessage> turns) throws Exception;

    /**
     * Làm sạch API key: bỏ khoảng trắng và dấu nháy bao ngoài mà người dùng
     * hay vô tình dán kèm khi khai báo biến môi trường.
     */
    static String sanitizeKey(String key) {
        if (key == null) {
            return null;
        }
        key = key.trim();
        if (key.length() >= 2
                && ((key.startsWith("\"") && key.endsWith("\""))
                 || (key.startsWith("'") && key.endsWith("'")))) {
            key = key.substring(1, key.length() - 1).trim();
        }
        return key.isBlank() ? null : key;
    }
}