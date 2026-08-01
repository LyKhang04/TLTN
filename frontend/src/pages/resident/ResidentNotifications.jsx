import React from "react";
import { C } from "../../theme";
import { Shell, Card, LoadingBlock, ErrorBlock, useApi } from "../../components/Common";
import { getNotifications } from "../../api/services";

export default function ResidentNotifications() {
  const { data: notifications, loading, error } = useApi(getNotifications);

  return (
    <Shell eyebrow="Cập nhật từ ban quản lý" title="Thông báo">
      {loading && <LoadingBlock />}
      {error && <ErrorBlock message={error} />}
      <div style={{ display: "flex", flexDirection: "column", gap: 12 }}>
        {(notifications || []).map((n) => (
          <Card key={n.id} style={{ padding: 18 }}>
            <div style={{ fontSize: 14.5, fontWeight: 600, color: C.ink, marginBottom: 6 }}>{n.title}</div>
            <div style={{ fontSize: 13, color: C.slate, lineHeight: 1.5 }}>{n.content}</div>
            <div style={{ marginTop: 10 }}>
              <span className="f-mono" style={{ fontSize: 11, color: C.slateLight, background: C.mist, padding: "3px 8px", borderRadius: 6 }}>{n.targetScope}</span>
            </div>
          </Card>
        ))}
      </div>
    </Shell>
  );
}
