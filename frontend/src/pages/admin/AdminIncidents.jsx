import React from "react";
import { C } from "../../theme";
import { Shell, Card, LoadingBlock, ErrorBlock, useApi } from "../../components/Common";
import { getIncidents } from "../../api/services";

const COLS = [
  { key: "NEW", label: "Mới", tone: C.blue },
  { key: "IN_PROGRESS", label: "Đang xử lý", tone: C.amber },
  { key: "RESOLVED", label: "Đã xử lý", tone: C.green },
];

export default function AdminIncidents() {
  const { data: incidents, loading, error } = useApi(getIncidents);

  return (
    <Shell eyebrow="Vận hành" title="Sự cố & yêu cầu">
      {loading && <LoadingBlock />}
      {error && <ErrorBlock message={error} />}
      {incidents && (
        <div style={{ display: "flex", gap: 16 }}>
          {COLS.map((col) => (
            <div key={col.key} style={{ flex: 1, minWidth: 260 }}>
              <div style={{ display: "flex", alignItems: "center", gap: 8, marginBottom: 12 }}>
                <span style={{ width: 8, height: 8, borderRadius: 999, background: col.tone }} />
                <span className="f-display" style={{ fontWeight: 600, fontSize: 13.5, color: C.ink }}>{col.label}</span>
                <span className="f-mono" style={{ fontSize: 11.5, color: C.slateLight }}>
                  {incidents.filter((i) => i.status === col.key).length}
                </span>
              </div>
              <div style={{ display: "flex", flexDirection: "column", gap: 10 }}>
                {incidents.filter((i) => i.status === col.key).map((i) => (
                  <Card key={i.id} style={{ padding: 14 }}>
                    <div className="f-mono" style={{ fontSize: 11, color: C.slateLight, marginBottom: 6 }}>#{i.id} · {i.category}</div>
                    <div style={{ fontSize: 13.5, color: C.ink, marginBottom: 8 }}>{i.description}</div>
                    <div style={{ display: "flex", justifyContent: "space-between", fontSize: 12, color: C.slate }}>
                      <span className="f-mono">{i.apartment?.code}</span>
                      <span>{i.reporter?.fullName}</span>
                    </div>
                  </Card>
                ))}
              </div>
            </div>
          ))}
        </div>
      )}
    </Shell>
  );
}
