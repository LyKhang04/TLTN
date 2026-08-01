import React, { useState } from "react";
import { C } from "../theme";
import { login } from "../api/services";
import { PrimaryButton, TextField } from "../components/Common";

export default function Login({ onSuccess }) {
  const [username, setUsername] = useState("admin");
  const [password, setPassword] = useState("password123");
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const submit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      const user = await login(username, password);
      onSuccess(user);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{
      minHeight: "100vh", display: "flex", alignItems: "center", justifyContent: "center",
      background: C.mist,
    }}>
      <form onSubmit={submit} className="f-body" style={{
        background: C.paper, border: `1px solid ${C.line}`, borderRadius: 16, padding: 32, width: 360,
      }}>
        <div style={{ display: "flex", alignItems: "center", gap: 10, marginBottom: 24 }}>
          <div style={{ width: 22, height: 22, display: "grid", gridTemplateColumns: "1fr 1fr", gap: 2 }}>
            {[0, 1, 2, 3].map((i) => (
              <div key={i} style={{ borderRadius: 2, background: i === 2 ? C.amber : C.line }} />
            ))}
          </div>
          <div>
            <div className="f-display" style={{ fontWeight: 700, fontSize: 17, color: C.ink }}>Sảnh</div>
            <div className="f-mono" style={{ fontSize: 10, color: C.slateLight }}>NỀN TẢNG QUẢN LÝ CHUNG CƯ</div>
          </div>
        </div>

        <TextField label="Tên đăng nhập" value={username} onChange={(e) => setUsername(e.target.value)} />
        <TextField label="Mật khẩu" type="password" value={password} onChange={(e) => setPassword(e.target.value)} />

        {error && (
          <div style={{ background: C.redSoft, color: C.red, fontSize: 12.5, padding: "8px 12px", borderRadius: 8, marginBottom: 14 }}>
            {error}
          </div>
        )}

        <div style={{ width: "100%" }}>
          <PrimaryButton type="submit" disabled={loading}>{loading ? "Đang đăng nhập..." : "Đăng nhập"}</PrimaryButton>
        </div>

        <div className="f-mono" style={{ fontSize: 11, color: C.slateLight, marginTop: 18, lineHeight: 1.6 }}>
          Tài khoản demo: admin / lan.nguyen / hung.tran<br />Mật khẩu: password123
        </div>
      </form>
    </div>
  );
}
