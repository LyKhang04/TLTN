package com.sanh.chungcu.chat;

import com.sanh.chungcu.entity.ChatMessage;
import com.sanh.chungcu.entity.User;
import com.sanh.chungcu.repository.ChatMessageRepository;
import com.sanh.chungcu.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
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
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public ChatController(AnthropicClient anthropicClient,
                           ChatContextService contextService,
                           ChatMessageRepository chatMessageRepository,
                           UserRepository userRepository) {
        this.anthropicClient = anthropicClient;
        this.contextService = contextService;
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        if (!anthropicClient.isConfigured()) {
            return ResponseEntity.ok(new ChatResponse(
                    "Chatbot chưa được cấu hình. Ban quản trị hệ thống cần đặt biến môi trường " +
                    "ANTHROPIC_API_KEY trước khi khởi động backend để bật tính năng này."
            ));
        }

        // Nếu việc tổng hợp ngữ cảnh gặp sự cố dữ liệu, chatbot vẫn trả lời được
        // ở chế độ chung thay vì làm hỏng toàn bộ request (HTTP 500).
        String context;
        try {
            context = contextService.buildContext(request.getResidentId());
        } catch (Exception e) {
            context = "Không đọc được dữ liệu cá nhân của cư dân do sự cố hệ thống. "
                    + "Hãy trả lời chung và đề nghị cư dân liên hệ Ban quản lý để tra cứu chính xác.";
        }
        String systemPrompt = String.format(SYSTEM_PROMPT_TEMPLATE, context);

        List<ChatRequest.ChatMessage> turns = new ArrayList<>();
        if (request.getHistory() != null) {
            turns.addAll(request.getHistory());
        }
        ChatRequest.ChatMessage current = new ChatRequest.ChatMessage();
        current.setRole("user");
        current.setContent(request.getMessage());
        turns.add(current);

        // Anthropic Messages API bắt buộc phần tử đầu tiên phải có role "user".
        // Loại bỏ mọi lượt "assistant" đứng đầu (ví dụ câu chào của giao diện)
        // để tránh lỗi HTTP 400 từ phía API.
        turns = normalizeTurns(turns);

        // Lưu câu hỏi của cư dân vào lịch sử chat (bảng chat_messages)
        saveMessage(request.getResidentId(), "user", request.getMessage());

        try {
            String reply = anthropicClient.sendMessage(systemPrompt, turns);
            // Lưu câu trả lời của AI vào lịch sử chat
            saveMessage(request.getResidentId(), "assistant", reply);
            return ResponseEntity.ok(new ChatResponse(reply));
        } catch (Exception e) {
            String errorReply = "Xin lỗi, mình đang gặp sự cố kết nối tới dịch vụ AI. Vui lòng thử lại sau ít phút. " +
                    "(Chi tiết kỹ thuật: " + e.getMessage() + ")";
            saveMessage(request.getResidentId(), "assistant", errorReply);
            return ResponseEntity.ok(new ChatResponse(errorReply));
        }
    }

    /**
     * Chuẩn hoá danh sách lượt hội thoại trước khi gửi tới Anthropic API:
     * bỏ các lượt rỗng và cắt bỏ phần đầu cho tới lượt "user" đầu tiên.
     */
    private List<ChatRequest.ChatMessage> normalizeTurns(List<ChatRequest.ChatMessage> turns) {
        List<ChatRequest.ChatMessage> cleaned = new ArrayList<>();
        for (ChatRequest.ChatMessage t : turns) {
            if (t == null || t.getContent() == null || t.getContent().isBlank()) {
                continue;
            }
            cleaned.add(t);
        }
        int firstUser = -1;
        for (int i = 0; i < cleaned.size(); i++) {
            if ("user".equals(cleaned.get(i).getRole())) {
                firstUser = i;
                break;
            }
        }
        if (firstUser <= 0) {
            return firstUser == 0 ? cleaned : new ArrayList<>();
        }
        return new ArrayList<>(cleaned.subList(firstUser, cleaned.size()));
    }

    /**
     * Ghi một lượt chat (user hoặc assistant) vào bảng chat_messages.
     * Nếu không xác định được cư dân (residentId null hoặc không tồn tại) thì bỏ qua,
     * không chặn luồng trả lời của chatbot.
     */
    private void saveMessage(Integer residentId, String role, String content) {
        if (residentId == null || content == null) {
            return;
        }
        User user = userRepository.findById(residentId).orElse(null);
        if (user == null) {
            return;
        }
        ChatMessage message = new ChatMessage();
        message.setUser(user);
        message.setRole(role);
        message.setContent(content);
        message.setCreatedAt(LocalDateTime.now());
        chatMessageRepository.save(message);
    }
}
