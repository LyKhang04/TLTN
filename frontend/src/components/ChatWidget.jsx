import React, { useEffect, useRef, useState } from "react";
import { MessageCircle, X, Send, Sparkles } from "lucide-react";
import { C } from "../theme";
import { api } from "../api/client";

const GREETING = "Chào bạn 👋 Mình là Sảnh AI. Bạn có thể hỏi mình về hóa đơn, sự cố đã báo, thông báo gần đây, hoặc quy định chung cư.";

export default function ChatWidget({ currentUser }) {
  const [open, setOpen] = useState(false);
  // greeting: true => chỉ hiển thị ở giao diện, KHÔNG gửi lên Anthropic API.
  // Anthropic yêu cầu phần tử đầu tiên của mảng messages phải có role "user",
  // nếu gửi kèm câu chào (role "assistant") thì API sẽ trả về lỗi 400.
  const [messages, setMessages] = useState([{ role: "assistant", content: GREETING, greeting: true }]);
  const [input, setInput] = useState("");
  const [sending, setSending] = useState(false);
  const scrollRef = useRef(null);

  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
    }
  }, [messages, open]);

  const send = async () => {
    const text = input.trim();
    if (!text || sending) return;

    const nextMessages = [...messages, { role: "user", content: text }];
    setMessages(nextMessages);
    setInput("");
    setSending(true);

    try {
      // Bỏ câu chào mở đầu và mọi tin nhắn "assistant" đứng trước lượt "user" đầu tiên,
      // để lịch sử gửi lên API luôn bắt đầu bằng role "user".
      let history = nextMessages
        .slice(0, -1)
        .filter((m) => !m.greeting && (m.role === "user" || m.role === "assistant"))
        .map((m) => ({ role: m.role, content: m.content }));
      const firstUserIndex = history.findIndex((m) => m.role === "user");
      history = firstUserIndex === -1 ? [] : history.slice(firstUserIndex);

      const res = await api.post("/chat", {
        residentId: currentUser?.id,
        message: text,
        history,
      });
      setMessages((prev) => [...prev, { role: "assistant", content: res.reply }]);
    } catch (e) {
      setMessages((prev) => [...prev, { role: "assistant", content: "Xin lỗi, mình chưa kết nối được tới máy chủ. Thử lại giúp mình nhé." }]);
    } finally {
      setSending(false);
    }
  };

  const onKeyDown = (e) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      send();
    }
  };

  return (
    <div style={{ position: "fixed", right: 24, bottom: 24, zIndex: 50 }}>
      {open && (
        <div className="f-body" style={{
          width: 340, height: 460, background: C.paper, borderRadius: 16,
          border: `1px solid ${C.line}`, boxShadow: "0 12px 40px rgba(27,42,74,0.18)",
          display: "flex", flexDirection: "column", marginBottom: 12, overflow: "hidden",
        }}>
          <div style={{ background: C.ink, color: "#fff", padding: "14px 16px", display: "flex", alignItems: "center", justifyContent: "space-between" }}>
            <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
              <div style={{ width: 26, height: 26, borderRadius: 999, background: C.amber, display: "flex", alignItems: "center", justifyContent: "center" }}>
                <Sparkles size={13} color={C.ink} />
              </div>
              <div>
                <div className="f-display" style={{ fontWeight: 600, fontSize: 13.5 }}>Sảnh AI</div>
                <div className="f-mono" style={{ fontSize: 10, color: "#ffffff99" }}>Trợ lý hỗ trợ cư dân</div>
              </div>
            </div>
            <button onClick={() => setOpen(false)} style={{ background: "transparent", border: "none", color: "#ffffffb0", cursor: "pointer" }}>
              <X size={16} />
            </button>
          </div>

          <div ref={scrollRef} className="sanh-scroll" style={{ flex: 1, overflowY: "auto", padding: 14, display: "flex", flexDirection: "column", gap: 10 }}>
            {messages.map((m, idx) => (
              <div key={idx} style={{
                alignSelf: m.role === "user" ? "flex-end" : "flex-start",
                background: m.role === "user" ? C.ink : C.mist,
                color: m.role === "user" ? "#fff" : C.ink,
                padding: "9px 12px", borderRadius: 12, fontSize: 13, lineHeight: 1.5, maxWidth: "85%",
                whiteSpace: "pre-wrap",
              }}>
                {m.content}
              </div>
            ))}
            {sending && (
              <div style={{ alignSelf: "flex-start", color: C.slateLight, fontSize: 12.5, padding: "0 4px" }}>
                Sảnh AI đang trả lời...
              </div>
            )}
          </div>

          <div style={{ borderTop: `1px solid ${C.line}`, padding: 10, display: "flex", gap: 8 }}>
            <textarea
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={onKeyDown}
              placeholder="Nhập câu hỏi..."
              rows={1}
              className="f-body"
              style={{
                flex: 1, resize: "none", border: `1px solid ${C.line}`, borderRadius: 10,
                padding: "9px 12px", fontSize: 13, outline: "none",
              }}
            />
            <button
              onClick={send}
              disabled={sending || !input.trim()}
              style={{
                width: 38, height: 38, borderRadius: 10, border: "none", flexShrink: 0,
                background: C.ink, color: "#fff", cursor: "pointer",
                display: "flex", alignItems: "center", justifyContent: "center",
                opacity: sending || !input.trim() ? 0.5 : 1,
              }}
            >
              <Send size={15} />
            </button>
          </div>
        </div>
      )}

      <button
        onClick={() => setOpen((o) => !o)}
        style={{
          width: 56, height: 56, borderRadius: 999, border: "none", cursor: "pointer",
          background: C.ink, color: "#fff", boxShadow: "0 8px 24px rgba(27,42,74,0.28)",
          display: "flex", alignItems: "center", justifyContent: "center",
        }}
      >
        {open ? <X size={22} /> : <MessageCircle size={22} />}
      </button>
    </div>
  );
}
