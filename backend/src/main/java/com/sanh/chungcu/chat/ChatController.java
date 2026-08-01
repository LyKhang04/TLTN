package com.sanh.chungcu.chat;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final String SYSTEM_PROMPT_TEMPLATE = """
            Bạn là "Sảnh AI" - trợ lý ảo hỗ trợ cư dân của một chung cư tại Việt Nam.
            Trả lời ngắn gọn, thân thiện, xưng "mình", gọi người dùng là "bạn".
            CHỈ sử dụng dữ liệu được cung cấp bên dưới để trả lời các câu hỏi về căn hộ,
            hóa đơn, sự cố, thông báo của cá nhân cư dân này. KHÔNG được bịa số liệu.
            Nếu không có dữ liệu liên quan trong ngữ cảnh, hãy nói rõ là chưa có thông tin
            và đề nghị liên hệ trực tiếp Ban quản lý qua mục "Báo sự cố" hoặc hotline toà nhà.
            Với các câu hỏi chung (quy định chung cư, cách sử dụng ứng dụng, tiện ích...),
            trả lời dựa trên hiểu biết thông thường một cách hữu ích và lịch sự.

            Dữ liệu ngữ cảnh của cư dân đang trò chuyện:
            ---
            %s
            ---
            """;

    private final AnthropicClient anthropicClient;
    private final ChatContextService contextService;

    public ChatController(AnthropicClient anthropicClient, ChatContextService contextService) {
        this.anthropicClient = anthropicClient;
        this.contextService = contextService;
    }

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        if (!anthropicClient.isConfigured()) {
            return ResponseEntity.ok(new ChatResponse(
                    "Chatbot chưa được cấu hình. Ban quản trị hệ thống cần đặt biến môi trường " +
                    "ANTHROPIC_API_KEY trước khi khởi động backend để bật tính năng này."
            ));
        }

        String context = contextService.buildContext(request.getResidentId());
        String systemPrompt = String.format(SYSTEM_PROMPT_TEMPLATE, context);

        List<ChatRequest.ChatMessage> turns = new ArrayList<>();
        if (request.getHistory() != null) {
            turns.addAll(request.getHistory());
        }
        ChatRequest.ChatMessage current = new ChatRequest.ChatMessage();
        current.setRole("user");
        current.setContent(request.getMessage());
        turns.add(current);

        try {
            String reply = anthropicClient.sendMessage(systemPrompt, turns);
            return ResponseEntity.ok(new ChatResponse(reply));
        } catch (Exception e) {
            return ResponseEntity.ok(new ChatResponse(
                    "Xin lỗi, mình đang gặp sự cố kết nối tới dịch vụ AI. Vui lòng thử lại sau ít phút. " +
                    "(Chi tiết kỹ thuật: " + e.getMessage() + ")"
            ));
        }
    }
}
