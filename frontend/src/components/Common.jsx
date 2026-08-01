import React from "react";
import { C, STATUS_LABEL, STATUS_COLOR } from "../theme";

export function Shell({ title, eyebrow, action, children }) {
  return (
    <div className="f-body" style={{ flex: 1, display: "flex", flexDirection: "column", minWidth: 0 }}>
      <div style={{
        padding: "20px 32px", borderBottom: `1px solid ${C.line}`,
        display: "flex", alignItems: "center", justifyContent: "space-between",
        background: C.paper,
      }}>
        <div>
          <div className="f-mono" style={{ fontSize: 11, letterSpacing: 1, color: C.slateLight, textTransform: "uppercase", marginBottom: 4 }}>
            {eyebrow}
          </div>
          <h1 className="f-display" style={{ fontSize: 22, fontWeight: 600, color: C.ink, margin: 0 }}>{title}</h1>
        </div>
        {action}
      </div>
      <div className="sanh-scroll" style={{ flex: 1, overflowY: "auto", padding: 32, display: "flex", flexDirection: "column", gap: 20 }}>
        {children}
      </div>
    </div>
  );
}

export function Card({ children, style, className }) {
  return (
    <div className={`card-hover ${className || ""}`} style={{ background: C.paper, border: `1px solid ${C.line}`, borderRadius: 14, ...style }}>
      {children}
    </div>
  );
}

export function Th({ children, style }) {
  return (
    <th className="f-mono" style={{
      textAlign: "left", fontSize: 11, letterSpacing: 0.6, textTransform: "uppercase",
      color: C.slateLight, fontWeight: 600, padding: "0 16px 12px", ...style,
    }}>{children}</th>
  );
}

export function Td({ children, style }) {
  return <td style={{ padding: "14px 16px", borderTop: `1px solid ${C.line}`, fontSize: 14, color: C.ink, ...style }}>{children}</td>;
}

export function StatusBadge({ status }) {
  const s = STATUS_COLOR[status] || { bg: C.mist, fg: C.slate };
  const label = STATUS_LABEL[status] || status;
  return (
    <span className="f-body" style={{
      display: "inline-flex", alignItems: "center", gap: 6,
      background: s.bg, color: s.fg, fontSize: 12, fontWeight: 600,
      padding: "3px 10px", borderRadius: 999,
    }}>
      <span style={{ width: 6, height: 6, borderRadius: 999, background: s.fg }} />
      {label}
    </span>
  );
}

export function PrimaryButton({ children, icon: Icon, onClick, type = "button", disabled }) {
  return (
    <button
      type={type}
      onClick={onClick}
      disabled={disabled}
      className="f-body"
      style={{
        display: "inline-flex", alignItems: "center", gap: 8, background: disabled ? C.slateLight : C.ink,
        color: "#fff", border: "none", borderRadius: 10, padding: "10px 16px", fontSize: 14, fontWeight: 600,
        cursor: disabled ? "not-allowed" : "pointer",
      }}
    >
      {Icon && <Icon size={16} />}
      {children}
    </button>
  );
}

export function TextField({ label, ...props }) {
  return (
    <div style={{ marginBottom: 14 }}>
      <div style={{ fontSize: 12.5, color: C.slate, marginBottom: 6 }}>{label}</div>
      <input
        {...props}
        className="f-body"
        style={{
          width: "100%", border: `1px solid ${C.line}`, borderRadius: 8, padding: "10px 12px",
          fontSize: 13.5, outline: "none", boxSizing: "border-box",
        }}
      />
    </div>
  );
}

export function LoadingBlock({ label = "Đang tải dữ liệu..." }) {
  return <div style={{ padding: 40, textAlign: "center", color: C.slateLight, fontSize: 13.5 }}>{label}</div>;
}

export function ErrorBlock({ message }) {
  return (
    <div style={{ padding: 20, background: C.redSoft, color: C.red, borderRadius: 10, fontSize: 13.5 }}>
      Không tải được dữ liệu: {message}. Kiểm tra backend đã chạy ở http://localhost:8080 chưa.
    </div>
  );
}

export function useApi(fetcher, deps = []) {
  const [data, setData] = React.useState(null);
  const [loading, setLoading] = React.useState(true);
  const [error, setError] = React.useState(null);

  const reload = React.useCallback(() => {
    setLoading(true);
    setError(null);
    fetcher()
      .then(setData)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, deps);

  React.useEffect(() => { reload(); }, [reload]);

  return { data, loading, error, reload };
}
