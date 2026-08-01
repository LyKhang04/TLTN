import React from "react";
import { Building2, Home, Receipt, Wrench, ArrowUpRight } from "lucide-react";
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid } from "recharts";
import { C } from "../../theme";
import { Shell, Card, StatusBadge, LoadingBlock, ErrorBlock, useApi } from "../../components/Common";
import { getDashboardSummary, getIncidents } from "../../api/services";

const REVENUE = [
  { m: "T2", value: 182 }, { m: "T3", value: 195 }, { m: "T4", value: 201 },
  { m: "T5", value: 189 }, { m: "T6", value: 214 }, { m: "T7", value: 226 },
];

function StatCard({ label, value, sub, icon: Icon, tone }) {
  return (
    <Card style={{ padding: 20, flex: "1 1 220px", minWidth: 220 }}>
      <div style={{ display: "flex", alignItems: "flex-start", justifyContent: "space-between" }}>
        <div className="f-mono" style={{ fontSize: 11, letterSpacing: 0.6, color: C.slateLight, textTransform: "uppercase" }}>{label}</div>
        <div style={{ width: 30, height: 30, borderRadius: 8, background: tone + "1A", display: "flex", alignItems: "center", justifyContent: "center" }}>
          <Icon size={15} color={tone} />
        </div>
      </div>
      <div className="f-display" style={{ fontSize: 26, fontWeight: 700, color: C.ink, marginTop: 10 }}>{value}</div>
      <div style={{ fontSize: 12.5, color: C.slate, marginTop: 4 }}>{sub}</div>
    </Card>
  );
}

export default function AdminDashboard() {
  const { data: summary, loading, error } = useApi(getDashboardSummary);
  const { data: incidents } = useApi(getIncidents);

  return (
    <Shell eyebrow="Tổng quan · Ban quản lý" title="Chào buổi sáng, Ban quản lý 👋">
      {loading && <LoadingBlock />}
      {error && <ErrorBlock message={error} />}

      {summary && (
        <div style={{ display: "flex", gap: 16, flexWrap: "wrap" }}>
          <StatCard label="Tổng số căn hộ" value={summary.totalApartments} sub="Toàn bộ toà nhà" icon={Building2} tone={C.blue} />
          <StatCard label="Đang có người ở" value={summary.occupiedApartments} sub="Trên tổng số căn hộ" icon={Home} tone={C.green} />
          <StatCard label="Hóa đơn chưa thu" value={summary.unpaidInvoices} sub="Bao gồm quá hạn" icon={Receipt} tone={C.amber} />
          <StatCard label="Sự cố đang mở" value={summary.openIncidents} sub="Mới + đang xử lý" icon={Wrench} tone={C.red} />
        </div>
      )}

      <div style={{ display: "flex", gap: 16, flexWrap: "wrap" }}>
        <Card style={{ padding: 24, flex: "2 1 420px", minWidth: 380 }}>
          <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: 16 }}>
            <div className="f-display" style={{ fontWeight: 600, color: C.ink, fontSize: 15 }}>Doanh thu phí quản lý (triệu đ)</div>
            <span style={{ fontSize: 12.5, color: C.green, display: "flex", alignItems: "center", gap: 4 }}><ArrowUpRight size={13} /> minh hoạ</span>
          </div>
          <ResponsiveContainer width="100%" height={220}>
            <BarChart data={REVENUE}>
              <CartesianGrid vertical={false} stroke={C.line} />
              <XAxis dataKey="m" tick={{ fontSize: 12, fill: C.slate }} axisLine={{ stroke: C.line }} tickLine={false} />
              <YAxis tick={{ fontSize: 12, fill: C.slate }} axisLine={false} tickLine={false} />
              <Tooltip cursor={{ fill: C.mist }} contentStyle={{ borderRadius: 10, border: `1px solid ${C.line}`, fontSize: 13 }} />
              <Bar dataKey="value" fill={C.blue} radius={[6, 6, 0, 0]} barSize={34} />
            </BarChart>
          </ResponsiveContainer>
        </Card>

        <Card style={{ padding: 24, flex: "1 1 300px", minWidth: 300 }}>
          <div className="f-display" style={{ fontWeight: 600, color: C.ink, fontSize: 15, marginBottom: 16 }}>Sự cố gần đây</div>
          <div style={{ display: "flex", flexDirection: "column", gap: 14 }}>
            {(incidents || []).slice(0, 4).map((i) => (
              <div key={i.id} style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", gap: 8 }}>
                <div>
                  <div style={{ fontSize: 13.5, color: C.ink, fontWeight: 500 }}>{i.description}</div>
                  <div className="f-mono" style={{ fontSize: 11.5, color: C.slateLight, marginTop: 2 }}>
                    {i.apartment?.code} · {i.category}
                  </div>
                </div>
                <StatusBadge status={i.status} />
              </div>
            ))}
          </div>
        </Card>
      </div>
    </Shell>
  );
}
