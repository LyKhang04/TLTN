import React, { useMemo } from "react";
import { AlertCircle, Wrench, CalendarClock } from "lucide-react";
import { C, fmtVnd } from "../../theme";
import { Shell, Card, LoadingBlock, ErrorBlock, useApi } from "../../components/Common";
import { getInvoices, getIncidents, getNotifications } from "../../api/services";

export default function ResidentHome({ currentUser }) {
  const { data: invoices, loading: l1, error: e1 } = useApi(getInvoices);
  const { data: incidents, loading: l2 } = useApi(getIncidents);
  const { data: notifications, loading: l3 } = useApi(getNotifications);

  const myInvoice = useMemo(
    () => (invoices || []).find((i) => i.status !== "PAID"),
    [invoices]
  );
  const myIncident = useMemo(() => (incidents || [])[0], [incidents]);

  return (
    <Shell eyebrow={`Xin chào`} title={`Chào ${currentUser?.fullName || "bạn"} 👋`}>
      {(l1 || l2 || l3) && <LoadingBlock />}
      {e1 && <ErrorBlock message={e1} />}

      <div style={{ display: "flex", gap: 16, flexWrap: "wrap" }}>
        {myInvoice && (
          <Card style={{ padding: 20, flex: "1 1 260px", borderColor: C.amber + "55", background: C.amberSoft }}>
            <div style={{ display: "flex", alignItems: "center", gap: 8, marginBottom: 8 }}>
              <AlertCircle size={16} color={C.amber} />
              <span className="f-display" style={{ fontSize: 13.5, fontWeight: 600, color: C.ink }}>Hóa đơn cần thanh toán</span>
            </div>
            <div className="f-mono" style={{ fontSize: 22, fontWeight: 700, color: C.ink }}>{fmtVnd(myInvoice.totalAmount)}</div>
            <div style={{ fontSize: 12.5, color: C.slate, marginTop: 4 }}>Kỳ {myInvoice.periodMonth}/{myInvoice.periodYear}</div>
          </Card>
        )}
        {myIncident && (
          <Card style={{ padding: 20, flex: "1 1 260px" }}>
            <div style={{ display: "flex", alignItems: "center", gap: 8, marginBottom: 8 }}>
              <Wrench size={16} color={C.blue} />
              <span className="f-display" style={{ fontSize: 13.5, fontWeight: 600, color: C.ink }}>Sự cố đang theo dõi</span>
            </div>
            <div className="f-display" style={{ fontSize: 15, fontWeight: 700, color: C.ink }}>{myIncident.description}</div>
            <div style={{ fontSize: 12.5, color: C.slate, marginTop: 4 }}>{myIncident.status}</div>
          </Card>
        )}
        <Card style={{ padding: 20, flex: "1 1 260px" }}>
          <div style={{ display: "flex", alignItems: "center", gap: 8, marginBottom: 8 }}>
            <CalendarClock size={16} color={C.green} />
            <span className="f-display" style={{ fontSize: 13.5, fontWeight: 600, color: C.ink }}>Tiện ích</span>
          </div>
          <div style={{ fontSize: 12.5, color: C.slate }}>Xem và đặt lịch ở mục "Đặt tiện ích"</div>
        </Card>
      </div>

      <Card style={{ padding: 22 }}>
        <div className="f-display" style={{ fontWeight: 600, color: C.ink, fontSize: 15, marginBottom: 14 }}>Thông báo mới nhất</div>
        <div style={{ display: "flex", flexDirection: "column", gap: 14 }}>
          {(notifications || []).slice(0, 3).map((n) => (
            <div key={n.id} style={{ display: "flex", gap: 12, alignItems: "flex-start" }}>
              <div style={{ width: 8, height: 8, borderRadius: 999, background: C.blue, marginTop: 6, flexShrink: 0 }} />
              <div>
                <div style={{ fontSize: 13.5, fontWeight: 500, color: C.ink }}>{n.title}</div>
                <div style={{ fontSize: 12.5, color: C.slate, marginTop: 2 }}>{n.content}</div>
              </div>
            </div>
          ))}
        </div>
      </Card>
    </Shell>
  );
}
