import React, { useState } from "react";
import { KeyRound } from "lucide-react";
import { C } from "../../theme";
import { Shell, Card, TextField, PrimaryButton } from "../../components/Common";
import { changePassword } from "../../api/services";

export default function ResidentAccount({ currentUser }) {
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [message, setMessage] = useState(null);

  const submit = async () => {
    setMessage(null);

    // Kiểm tra phía giao diện trước để báo lỗi nhanh cho người dùng.
    // Backend vẫn kiểm tra lại đầy đủ, không tin tưởng hoàn toàn vào frontend.
    if (!currentPassword || !newPassword) {
      setMessage({ ok: false, text: "Vui lòng nhập đủ mật khẩu hiện tại và mật khẩu mới." });
      return;
    }
    if (newPassword.length < 6) {
      setMessage({ ok: false, text: "Mật khẩu mới phải có ít nhất 6 ký tự." });
      return;
    }
    if (newPassword !== confirmPassword) {
      setMessage({ ok: false, text: "Xác nhận mật khẩu không khớp." });
      return;
    }

    setSubmitting(true);
    try {
      const res = await changePassword({
        userId: currentUser?.id,
        currentPassword,
        newPassword,
      });
      setMessage({ ok: true, text: res.message || "Đổi mật khẩu thành công." });
      setCurrentPassword("");
      setNewPassword("");
      setConfirmPassword("");
    } catch (e) {
      setMessage({ ok: false, text: e.message });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Shell eyebrow="Tài khoản" title="Thông tin & bảo mật">
      <Card style={{ padding: 22, marginBottom: 16 }}>
        <div style={{ fontWeight: 700, marginBottom: 12 }}>Thông tin tài khoản</div>
        <InfoRow label="Họ và tên" value={currentUser?.fullName} />
        <InfoRow label="Tên đăng nhập" value={currentUser?.username} mono />
        <InfoRow label="Email" value={currentUser?.email || "Chưa cập nhật"} />
        <InfoRow label="Điện thoại" value={currentUser?.phone || "Chưa cập nhật"} />
        <InfoRow label="Vai trò" value={currentUser?.roleName} />
      </Card>

      <Card style={{ padding: 22 }}>
        <div style={{ fontWeight: 700, marginBottom: 4 }}>Đổi mật khẩu</div>
        <div style={{ fontSize: 13, color: C.slate, marginBottom: 16 }}>
          Vì lý do an toàn, bạn cần nhập lại mật khẩu hiện tại trước khi đặt mật khẩu mới.
        </div>

        <TextField label="Mật khẩu hiện tại" type="password"
                   value={currentPassword} onChange={(e) => setCurrentPassword(e.target.value)} />
        <TextField label="Mật khẩu mới" type="password"
                   value={newPassword} onChange={(e) => setNewPassword(e.target.value)} />
        <TextField label="Xác nhận mật khẩu mới" type="password"
                   value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)} />

        {message && (
          <div style={{
            margin: "12px 0",
            padding: 12,
            borderRadius: 8,
            fontSize: 13,
            background: message.ok ? C.greenSoft : C.redSoft,
            color: message.ok ? C.green : C.red,
          }}>
            {message.text}
          </div>
        )}

        <PrimaryButton icon={KeyRound} onClick={submit} disabled={submitting}>
          {submitting ? "Đang xử lý..." : "Đổi mật khẩu"}
        </PrimaryButton>
      </Card>
    </Shell>
  );
}

function InfoRow({ label, value, mono }) {
  return (
    <div style={{ display: "flex", padding: "8px 0", borderBottom: `1px solid ${C.line}` }}>
      <div style={{ width: 150, color: C.slate, fontSize: 13 }}>{label}</div>
      <div className={mono ? "f-mono" : ""} style={{ fontSize: 14, fontWeight: 500 }}>{value}</div>
    </div>
  );
}
